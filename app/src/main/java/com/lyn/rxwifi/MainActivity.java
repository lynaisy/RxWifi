package com.lyn.rxwifi;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.lyn.library.wifi.WifiEntity;
import com.lyn.library.wifi.WifiProcess;
import com.lyn.library.wifi.WifiProcessInterface;
import com.lyn.library.wifi.WifiSecutityEnum;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @author liuyn
 *         主流程已经通了
 *         todo 连接一些细节没做，比如状态切换，密码输入错误处理等
 */
public class MainActivity extends AppCompatActivity {
    private Switch stWifi;
    private RecyclerView recyclerView;
    private WifiProcessInterface wifiProcessInterface = WifiProcess.getInstance();
    private WifiAdapter wifiAdapter;
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiProcessInterface.init(this);
        initView();
        checkPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiProcessInterface.startScan()
                .subscribe(new Consumer<List<WifiEntity>>() {
                    @Override
                    public void accept(List<WifiEntity> wifiEntities) throws Exception {
                        //todo 得到wifi列表，下面是自己的业务逻辑
                        wifiAdapter.setList(wifiEntities);
                    }
                });
        wifiAdapter.setOnClickItemListener(new WifiAdapter.OnClickItemListener() {
            @Override
            public void onClickItem(int position) {
                List<WifiEntity> wifiEntities = wifiAdapter.getWifiEntityList();
                WifiEntity clickWifiEntity = wifiEntities.get(position);
                switch (wifiEntities.get(position).getWifiState()) {
                    case STATE_NONE:
                        if (clickWifiEntity.getWifiSecurityMode().equals("")) {
                            wifiProcessInterface.connectWifi(clickWifiEntity.getWifiName(), "", clickWifiEntity.getWifiSecurityMode());
                        } else {
                            creatDialogConnectWifi(clickWifiEntity.getWifiName(), clickWifiEntity.getWifiSecurityMode());
                        }
                        break;
                    case STATE_CONNECTED:
                        //todo 查看已经连接的wifi信息
                        break;
                    case STATE_SAVED:
                        //todo 查看已经保存的wifi信息
                        break;
                    default:
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        wifiProcessInterface.stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 创建连接wifi的dialog
     * todo 连接失败的逻辑
     *
     * @param wifiName
     * @param wifiSecutityEnum
     */
    private void creatDialogConnectWifi(final String wifiName, final WifiSecutityEnum wifiSecutityEnum) {
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle(wifiName)
                .setView(et)
                .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wifiProcessInterface.connectWifi(wifiName, et.getText().toString(), wifiSecutityEnum);
                    }
                })
                .create()
                .show();
    }

    private void initView() {
        stWifi = findViewById(R.id.st_wifi);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        wifiAdapter = new WifiAdapter();
        recyclerView.setAdapter(wifiAdapter);
        stWifi.setChecked(wifiProcessInterface.isWifiEnabled());
        stWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wifiProcessInterface.openWifi();
                } else {
                    wifiProcessInterface.closeWifi();
                }
            }
        });
    }

    /**
     * android 6.0以上 获取wifi列表需要定位权限
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }
}
