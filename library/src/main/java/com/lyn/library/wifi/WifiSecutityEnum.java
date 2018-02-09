package com.lyn.library.wifi;


/**
 * Created by Administrator on 2016/12/10.
 */

public enum  WifiSecutityEnum {
    NOON,
    WPA2,
    WPA_AND_WPA2,
    WPA,
    WEP;
    public static String getWifiSecutity(WifiSecutityEnum wifiSecutityEnum){
        switch (wifiSecutityEnum) {
            case NOON:
                return "";
            case WPA2:
                return WifiConstants.WPA2_MODE;
            case WPA_AND_WPA2:
                return WifiConstants.WPA_MODE + "/" + WifiConstants.WPA2_MODE;
            case WPA:
                return WifiConstants.WPA_MODE;
            default:
                return "";
        }
    }
}
