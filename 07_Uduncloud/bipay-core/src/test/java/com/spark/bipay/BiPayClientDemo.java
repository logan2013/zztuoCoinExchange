package com.spark.bipay;

import com.spark.bipay.constant.CoinType;
import com.spark.bipay.entity.Address;
import com.spark.bipay.http.ResponseMessage;
import com.spark.bipay.http.client.BiPayClient;

import java.math.BigDecimal;


public class BiPayClientDemo {

    public static void main(String[] args) throws Exception {
        BiPayClient client = new BiPayClient("http://47.52.142.109","100078","fcf09ae5177ee7eb5a778d77f0566cda");
        /*ResponseMessage<Address> resp = client.createCoinAddress(CoinType.ETH,"http://bipay-gateway.caymanex.pro/bipay/notify");
        System.out.println(resp.getMessage());
        Address address = resp.getData();
        System.out.println(address.getAddress());*/
        //System.out.println(client.queryTransaction());
        //USDT:1hUVFoSXeAxX3cgmZHN4bkyjcNk48RFrz
        //ETH:0x4965c31fbe2f630eb19b0d45b5df776bfffeaa25
        System.out.println(client.transfer("123456", new BigDecimal("0.001"),CoinType.ETH,"0x4965c31fbe2f630eb19b0d45b5df776bfffeaa25","http://bipay-gateway.caymanex.pro/bipay/notify"));
        //System.out.println(client.transfer("123456", new BigDecimal("0.2"),CoinType.USDT,"1hUVFoSXeAxX3cgmZHN4bkyjcNk48RFrz","http://bipay-gateway.caymanex.pro/bipay/notify"));

    }


}
