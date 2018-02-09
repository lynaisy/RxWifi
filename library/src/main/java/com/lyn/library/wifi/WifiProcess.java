package com.lyn.library.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @author liu.yn
 */
public class WifiProcess implements WifiProcessInterface {

    /**
     * android自带的WifiManager
     */
    private WifiManager mWifiManager;
    /**
     * Android 对WiFi电源管理的代码主要在WifiService.java中。如果应用程序想在屏幕被关掉后继续使用WiFi则可以调用 acquireWifiLock来锁住WiFi，
     * 该操作会阻止WiFi进入睡眠状态。当应用程序不再使用WiFi时需要调用 releaseWifiLock来释放WiFi。之后WiFi可以进入睡眠状态以节省电源。
     */
    private WifiLock mWifiLock;
    private Context mContext;
    private boolean disableOthers = true;
    private WifiInfoTransform wifiInfoTransform;
    private BroadcastReceiver wifiResultReceiver;
    private ObservableEmitter<List<WifiEntity>> wifiResultEmitter;

    private WifiProcess() {
    }

    private static class InstanceHolder {
        private static WifiProcessInterface INSTANCE = new WifiProcess();
    }

    public static WifiProcessInterface getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 初始化各种对象
     *
     * @param context
     */
    @Override
    public void init(Context context) {
        mContext = context;
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
        if (wifiInfoTransform == null) {
            wifiInfoTransform = new WifiInfoTransform();
            wifiInfoTransform.init(mWifiManager, this);
        }
    }

    /**
     * 打开wifi
     */
    @Override
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭wifi
     */
    @Override
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 判断wifi是否可用
     *
     * @return
     */
    @Override
    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 检查当前WIFI状态
     *
     * @return WIFI_STATE_DISABLING    0   WIFI网卡正在关闭
     * WIFI_STATE_DISABLED     1   WIFI网卡不可用
     * WIFI_STATE_ENABLING     2   WIFI网卡正在打开
     * WIFI_STATE_ENABLED      3   WIFI网卡可用
     * WIFI_STATE_UNKNOWN      4   WIFI网卡状态不可知
     */
    @Override
    public int getCheckState() {
        return mWifiManager.getWifiState();
    }

    /**
     * 锁定WifiLock,能够阻止wifi进入睡眠状态，使wifi一直处于活跃状态
     *
     * @param tag
     */
    @Override
    public void acquireWifiLock(String tag) {
        if (mWifiLock == null) {
            mWifiLock = mWifiManager.createWifiLock(tag);
        }
        mWifiLock.acquire();
    }

    /**
     * 解锁WifiLock
     */
    @Override
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock != null && mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }

    /**
     * 开始扫描wifi
     *
     * @return
     */
    @Override
    public Observable<List<WifiEntity>> startScan() {
        stopScan();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        return Observable.create(new ObservableOnSubscribe<List<WifiEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<WifiEntity>> emitter) throws Exception {
                if (wifiResultReceiver != null) {
                    emitter.onError(new Exception("wifiResultReceiver already exist"));
                }
                wifiResultEmitter = emitter;
                wifiResultReceiver = getScanResultReceiver();
                mContext.registerReceiver(wifiResultReceiver, filter);
                mWifiManager.startScan();
            }
        });
    }

    /**
     * 停止扫描wifi
     */
    @Override
    public void stopScan() {
        if (wifiResultEmitter != null) {
            wifiResultEmitter.onComplete();
            wifiResultEmitter = null;
        }
        if (wifiResultReceiver != null) {
            mContext.unregisterReceiver(wifiResultReceiver);
            wifiResultReceiver = null;
        }
    }


    /**
     * 根据netId连接已保存的wifi
     *
     * @param netId
     */
    @Override
    public void connectWifi(int netId) {
        mWifiManager.enableNetwork(netId, disableOthers);
    }

    /**
     * 连接知道账号密码和加密方式的wifi
     *
     * @param ssid
     * @param password
     * @param type
     */
    @Override
    public int connectWifi(String ssid, String password, WifiSecutityEnum type) {
        WifiConfiguration wifiConfiguration = createWifiInfo(ssid, password, type);
        int netId = mWifiManager.addNetwork(wifiConfiguration);
        if (netId != -1) {
            mWifiManager.enableNetwork(netId, disableOthers);
        }
        return netId;
    }

    /**
     * 断开当前wifi
     */
    @Override
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    /**
     * 根据netId删除保存的wifi
     *
     * @param netId
     */
    @Override
    public void removeWifi(int netId) {
        mWifiManager.removeNetwork(netId);
    }

    /**
     * 判断指定的wifi是否保存
     *
     * @param SSID
     * @return
     */
    @Override
    public WifiConfiguration isWifiSave(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    /**
     * 是否处于已连接或正在连接的状态
     *
     * @return
     */
    @Override
    public boolean isConnectedOrConnecting() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * 判断是否与指定wifi连接或正在连接
     *
     * @param SSID
     * @return
     */
    @Override
    public boolean isGivenWifiConnectedOrConnecting(String SSID) {
        return isConnectedOrConnecting() && wifiInfoTransform.getCurentWifiSSID().equals(SSID);
    }


    /**
     * 是否处于wifi连接的状态
     *
     * @return
     */
    @Override
    public boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo.isConnected();
    }

    /**
     * 判断是否与指定wifi连接
     *
     * @param ssid
     * @return
     */
    @Override
    public boolean isGivenWifiConnect(String ssid) {
        return isWifiConnected() && wifiInfoTransform.getCurentWifiSSID().equals(ssid);
    }


    /**
     * 得到已经保存过的wifi配置信息
     *
     * @return
     */
    @Override
    public List<WifiConfiguration> getConfiguredNetworks() {
        return mWifiManager.getConfiguredNetworks();
    }


    /**
     * 得到WifiInfoTransform实例
     *
     * @return
     */
    @Override
    public WifiInfoTransform getWifiInfoTransform() {
        return wifiInfoTransform;
    }


    /**
     * 创建有密码的wifi的config
     *
     * @param ssid
     * @param password
     * @param type
     * @return
     */
    private WifiConfiguration createWifiInfo(String ssid, String password, WifiSecutityEnum type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
        // WifiConfiguration tempConfig = this.isWifiSave(SSID);
        WifiConfiguration tempConfig = isWifiSave(ssid);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        switch (type) {
            case NOON:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case WEP:
                config.hiddenSSID = true;
                config.wepKeys[0] = "\"" + password + "\"";
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case WPA:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                if (password.length() != 0) {
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = password;
                    } else {
                        config.preSharedKey = '"' + password + '"';
                    }
                }
                break;
            case WPA_AND_WPA2:
                config.preSharedKey = "\"" + password + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.status = WifiConfiguration.Status.ENABLED;
                break;
            default:
                break;
        }
        return config;
    }


    private BroadcastReceiver getScanResultReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                    List<ScanResult> wifiList = mWifiManager.getScanResults();
                    List<WifiEntity> wifiEntityList = wifiInfoTransform.get(wifiList);
                    wifiResultEmitter.onNext(wifiEntityList);
                    mWifiManager.startScan();
                }
            }
        };
    }
}
