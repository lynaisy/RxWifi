package com.lyn.rxwifi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.lyn.library.wifi.WifiEntity;
import com.lyn.library.wifi.WifiProcess;
import com.lyn.library.wifi.WifiProcessInterface;

import java.util.List;

import io.reactivex.functions.Consumer;

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
        initView();
        wifiProcessInterface.startScan()
                .subscribe(new Consumer<List<WifiEntity>>() {
                    @Override
                    public void accept(List<WifiEntity> wifiEntities) throws Exception {
                        wifiAdapter.setList(wifiEntities);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiProcessInterface.stopScan();
    }

    private void initView() {
        stWifi = findViewById(R.id.st_wifi);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        wifiAdapter = new WifiAdapter();
        recyclerView.setAdapter(wifiAdapter);
        wifiProcessInterface.init(this);
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
        checkPermission();
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
