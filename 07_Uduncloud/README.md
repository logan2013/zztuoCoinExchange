## 什么是优盾钱包

优盾钱包是一款多功能企业级数字资产钱包管理系统，可以为交易所提供多钱包配置、一键归集交易所钱包余额、一键对接比特币、以太坊接口等功能。如果你在使用本交易所系统时，讨厌搭建各个币种节点（因为太耗费硬盘和同步时间了），你可以选择使用优盾钱包，可以为你提供更加便捷的对接体验。

 **优盾钱包-官网** ：[https://www.uduncloud.com/](http://www.uduncloud.com/)    
 **优盾钱包-开发者中心** ：[https://www.uduncloud.com/product-introduction](http://www.uduncloud.com/product-introduction)

## 如何对接优盾钱包  

1. 复制本目录下的项目到00_Framework中  
2. 到优盾钱包官网申请账户  
3. 修改wallet项目下的application.properties文件的配置  
> bipay.merchantId=100000  
> bipay.merchantKey=  
> bipay.gateway=http://127.0.0.1  
4. 重新编译生成执行jar包文件  
5. 运行系统即可  

## 关于钱包对接问题

 **1. 对接优盾钱包后，用户提现如何处理**   
答：对接钱包后，用户实际提现审核将放在优盾钱包的系统中，提供mac os、windows、app端供管理员使用  

 **2. EOS与XRP类型的钱包有针对性处理吗**   
答：有。针对EOS和XRP的钱包充值提现处理，请参考优盾钱包开发者中心。  