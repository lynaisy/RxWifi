package com.lyn.library.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/9.
 */

public class WifiInfoTransform {
    private WifiManager mWifiManager;
    private WifiProcess wifiProcess;

    public void init(WifiManager wifiManager, WifiProcess wifiProcess) {
        mWifiManager = wifiManager;
        this.wifiProcess = wifiProcess;
    }

    /**
     * 得到当前连接wifi的SSID
     * *
     */
    public String getCurentWifiSSID() {
        String ssid = "";
        ssid = mWifiManager.getConnectionInfo().getSSID();
        if ("\"".equals(ssid.substring(0, 1))
                && "\"".equals(ssid.substring(ssid.length() - 1))) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    /**
     * wifi频率
     *
     * @param freq
     * @return
     */
    private String getWifiStringFrequency(int freq) {
        if (freq > 2400 && freq < 2500) {
            return 2.4 + "GHz";
        } else {
            return 5.0 + "GHz";
        }
    }

    /**
     * wifi安全模式
     *
     * @param wifiSecurityMode
     * @return
     */
    private WifiSecutityEnum getWifiSecurityMode(String wifiSecurityMode) {
        if (wifiSecurityMode.contains(WifiConstants.KEY_WPA_MODE) && wifiSecurityMode.contains(WifiConstants.KEY_WPA2_MODE)) {
            return WifiSecutityEnum.WPA_AND_WPA2;
        }
        if (!wifiSecurityMode.contains(WifiConstants.KEY_WPA_MODE) && wifiSecurityMode.contains(WifiConstants.KEY_WPA2_MODE)) {
            return WifiSecutityEnum.WPA2;
        }
        if (wifiSecurityMode.contains(WifiConstants.KEY_WPA_MODE) && !wifiSecurityMode.contains(WifiConstants.KEY_WPA2_MODE)) {
            return WifiSecutityEnum.WPA;
        }
        if (wifiSecurityMode.contains(WifiConstants.WEP_MODE)) {
            return WifiSecutityEnum.WEP;
        }
        return WifiSecutityEnum.NOON;
    }

    /**
     * 获得wifi连接速度
     *
     * @return
     */
    public String getWifiSpeed() {
        return mWifiManager.getConnectionInfo().getLinkSpeed() + "Mbps";
    }


    /**
     * 得到List<WifiEntity>
     *
     * @param wifiList
     * @return
     */
    public List<WifiEntity> get(List<ScanResult> wifiList) {
        List<WifiEntity> wifiEntityList = new ArrayList<>();
        for (int i = 0; i < wifiList.size(); i++) {
            WifiEntity wifiEntity = new WifiEntity();
            wifiEntity.setWifiName(wifiList.get(i).SSID);
            wifiEntity.setWifiConnFrequency(getWifiStringFrequency(wifiList.get(i).frequency));
            wifiEntity.setWifiStrength(WifiManager.calculateSignalLevel(wifiList.get(i).level, 4));
            wifiEntity.setWifiSecurityMode(getWifiSecurityMode(wifiList.get(i).capabilities));
            wifiEntity.setWifiSpeed(getWifiSpeed());
            wifiEntity.setWifiState(WifiStateEnum.STATE_NONE);
            wifiEntity.setNetId(-1);    //-1表示 不是保存的wifi
            WifiConfiguration savedWifi = wifiProcess.isWifiSave(wifiList.get(i).SSID);
            if (savedWifi != null) {//已保存
                wifiEntity.setNetId(savedWifi.networkId);
                wifiEntity.setWifiState(WifiStateEnum.STATE_SAVED);
            }
            if (wifiProcess.isGivenWifiConnectedOrConnecting(wifiList.get(i).SSID)) {//已连接
                wifiEntity.setWifiState(WifiStateEnum.STATE_CONNECTING);
                if (wifiProcess.isGivenWifiConnect(wifiList.get(i).SSID)) {//已连接
                    wifiEntity.setWifiState(WifiStateEnum.STATE_CONNECTED);
                }
            }
            wifiEntityList.add(wifiEntity);
        }

        //把已连接和已保存的放在最前面
        int count = 0;
        for (int i = 0; i < wifiEntityList.size(); i++) {
            WifiEntity wifiEntity = null;
            switch (wifiEntityList.get(i).getWifiState()) {
                case STATE_CONNECTING:
                    System.out.println(wifiEntityList.get(i).getWifiName() + "STATE_CONNECTING");
                    wifiEntity = wifiEntityList.remove(i);
                    wifiEntityList.add(0, wifiEntity);
                    count++;
                    break;
                case STATE_CONNECTED:
                    System.out.println(wifiEntityList.get(i).getWifiName() + "STATE_CONNECTED");
                    wifiEntity = wifiEntityList.remove(i);
                    wifiEntityList.add(0, wifiEntity);
                    count++;
                    break;
                case STATE_SAVED:
                    wifiEntity = wifiEntityList.remove(i);
                    wifiEntityList.add(count, wifiEntity);
                    count++;
                    break;
                default:
                    break;
            }
        }
        return wifiEntityList;
    }

    /**
     * 得到MAC地址
     *
     * @return
     */
    public String getMacAddress() {
        return mWifiManager.getConnectionInfo().getMacAddress();
    }

    /**
     * 得到接入点的BSSID
     *
     * @return
     */
    public String getBSSID() {
        return mWifiManager.getConnectionInfo().getBSSID();
    }

    /**
     * 得到IP地址
     *
     * @return
     */
    public int getIPAddress() {
        return mWifiManager.getConnectionInfo().getIpAddress();
    }

    /**
     * 得到连接的netId
     *
     * @return
     */
    public int getNetworkId() {
        return mWifiManager.getConnectionInfo().getNetworkId();
    }

    /**
     * 得到WifiInfo的所有信息包
     *
     * @return
     */
    public String getWifiInfo() {
        return mWifiManager.getConnectionInfo().toString();
    }
}
