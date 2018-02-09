package com.lyn.library.wifi;

/**
 * Created by Administrator on 2016/12/10.
 */

public enum WifiStateEnum {
    STATE_NONE,
    STATE_CONNECTED,
    STATE_SAVED,
    STATE_CONNECTING,
    STATE_OBTAINING_IPADDR,
    STATE_FAIL;


    public static String getState(WifiStateEnum wifiStateEnum) {
        switch (wifiStateEnum) {
            case STATE_NONE:
                return "";
            case STATE_CONNECTED:
                return "已连接";
            case STATE_SAVED:
                return "已保存";
            case STATE_CONNECTING:
                return "正在连接";
            case STATE_OBTAINING_IPADDR:
                return "正在获取ip";
            case STATE_FAIL:
                return "身份验证出现问题";
            default:
                return "";
        }
    }
}
