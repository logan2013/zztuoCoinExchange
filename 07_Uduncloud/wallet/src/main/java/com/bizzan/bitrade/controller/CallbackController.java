package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.service.WithdrawRecordService;
import com.bizzan.bitrade.util.MessageResult;
import com.spark.bipay.entity.Trade;
import com.spark.bipay.http.client.BiPayClient;
import com.spark.bipay.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequestMapping("wallet")
public class CallbackController {
    @Autowired
    private BiPayClient biPayClient;
    
    @Autowired
    private MemberWalletService walletService;
    
    @Autowired
    private WithdrawRecordService withdrawRecordService;
    
    @Autowired
    private CoinService coinService;
    
    private Logger logger = LoggerFactory.getLogger(CallbackController.class);

    @RequestMapping("/bipay/notify")
    public synchronized String tradeCallback(@RequestParam("timestamp")String timestamp,
                                @RequestParam("nonce")String nonce,
                                @RequestParam("body")String body,
                                @RequestParam("sign")String sign) throws Exception {
        logger.info("timestamp:{},nonce:{},sign:{},body:{}",timestamp,nonce,sign,body);
        if(!HttpUtil.checkSign(biPayClient.getMerchantKey(),timestamp,nonce,body,sign)){
            return "error";
        }
        Trade trade = JSONObject.parseObject(body,Trade.class);
        logger.info("trade:{}",trade);
        //TODO 业务处理
        if(trade.getTradeType() == 1){
            logger.info("=====收到充币通知======");
            logger.info("address:{},amount:{},coinType:{},fee:{}",trade.getAddress(),trade.getAmount(),trade.getCoinType(),trade.getFee());
            //金额为最小单位，需要转换,包括amount和fee字段
            BigDecimal amount = trade.getAmount().divide(BigDecimal.TEN.pow(trade.getDecimals()),8, RoundingMode.DOWN);
            BigDecimal fee = trade.getFee().divide(BigDecimal.TEN.pow(trade.getDecimals()),8, RoundingMode.DOWN);
            logger.info("amount={},fee={}",amount.toPlainString(),fee.toPlainString());

            String txid = trade.getTxId();
            String address = trade.getAddress();
            Coin coin = biPayService.convert2Coin(trade);
            logger.info("coin={}", coin);
            if (coin != null
                    && walletService.findDeposit(address, txid) == null
                    && amount.compareTo(coin.getMinRechargeAmount()) >= 0) {
                MessageResult mr = walletService.recharge(coin, address, amount, txid);
                logger.info("wallet recharge result:{}", mr);
            }
        }
        else if(trade.getTradeType() == 2){
            logger.info("=====收到提币处理通知=====");
            logger.info("address:{},amount:{},coinType:{},businessId:{}",trade.getAddress(),trade.getAmount(),trade.getCoinType(),trade.getBusinessId());
            Long withdrawId = Long.parseLong(trade.getBusinessId());
            Coin coin = biPayService.convert2Coin(trade);
            WithdrawRecord withdrawRecord=withdrawRecordService.findOne(withdrawId);
            if(withdrawRecord==null){
                return "error";
            }
            String txid = trade.getTxId();
            //转账失败，状态变回等待放币
            if(trade.getStatus() == 1){
                logger.info("审核通过，转账中");
                //TODO: 提币交易已发出，理提币订单状态，扣除提币资金
                if(withdrawRecord.getStatus() == WithdrawStatus.FAIL
                        || withdrawRecord.getStatus() == WithdrawStatus.SUCCESS){
                    return "error";
                }
                withdrawRecordService.withdrawSuccess(withdrawId, txid);
            }
            else if(trade.getStatus() == 2){
                logger.info("审核不通过");
                //TODO: 处理提币订单状态，订单号为 businessId
                if(withdrawRecord.getStatus() == WithdrawStatus.FAIL
                        || withdrawRecord.getStatus() == WithdrawStatus.SUCCESS){
                    return "error";
                }
                withdrawRecordService.withdrawFail(withdrawId);
            }
            else if(trade.getStatus() == 3){
                logger.info("提币已到账");
                //TODO: 提币已到账，可以向提币用户发出通知
                withdrawRecordService.updateWithrawTxid(withdrawId,trade.getTxId());
            }
        }
        return "success";
    }

    @RequestMapping("/bipay/reset-coin")
    public String mockRegSuccess(String coinName){
        Coin coin = coinService.findOne(coinName);
        if(coin != null) {
            kafkaTemplate.send("reset-wallet", JSON.toJSONString(coin));
        }
        return "success";
    }
}
