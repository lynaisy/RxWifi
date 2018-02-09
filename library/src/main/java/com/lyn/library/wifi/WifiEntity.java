package com.lyn.library.wifi;

/**
 * Created by Administrator on 2016/12/9.
 */

public class WifiEntity {
    private int netId;
    private String wifiName;
    private WifiStateEnum wifiState;
    private int wifiStrength;
    private String wifiConnFrequency;
    private WifiSecutityEnum wifiSecurityMode;
    private String wifiSpeed;

    public int getNetId() {
        return netId;
    }

    public void setNetId(int netId) {
        this.netId = netId;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public WifiStateEnum getWifiState() {
        return wifiState;
    }

    public void setWifiState(WifiStateEnum wifiState) {
        this.wifiState = wifiState;
    }

    public int getWifiStrength() {
        return wifiStrength;
    }

    public void setWifiStrength(int wifiStrength) {
        this.wifiStrength = wifiStrength;
    }

    public String getWifiConnFrequency() {
        return wifiConnFrequency;
    }

    public void setWifiConnFrequency(String wifiConnFrequency) {
        this.wifiConnFrequency = wifiConnFrequency;
    }

    public WifiSecutityEnum getWifiSecurityMode() {
        return wifiSecurityMode;
    }

    public void setWifiSecurityMode(WifiSecutityEnum wifiSecurityMode) {
        this.wifiSecurityMode = wifiSecurityMode;
    }

    public String getWifiSpeed() {
        return wifiSpeed;
    }

    public void setWifiSpeed(String wifiSpeed) {
        this.wifiSpeed = wifiSpeed;
    }
}
