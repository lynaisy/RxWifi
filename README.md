# RxWifi
基于RX 2.x的wifi操作库
## 功能
- 扫描wifi；
- 开关wifi；
- 显示wifi列表；
- 显示wifi强度；
- 连接wifi。
## 代码示例
        wifiProcessInterface.startScan()
                .subscribe(new Consumer<List<WifiEntity>>() {
                    @Override
                    public void accept(List<WifiEntity> wifiEntities) throws Exception {
                        //todo 得到wifi列表，下面是自己的业务逻辑         
                    }
                });
### 成果展示
![](https://i.imgur.com/Hc1B78b.jpg)