package com.lyn.rxwifi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyn.library.wifi.WifiEntity;
import com.lyn.library.wifi.WifiStateEnum;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/2/9.
 */

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {

    private OnClickItemListener onClickItemListener;
    private List<WifiEntity> wifiEntityList;
    private int[] lockSignalWifiRes = {R.drawable.ic_signal_wifi_1_bar_lock_black_36dp, R.drawable.ic_signal_wifi_2_bar_lock_black_36dp,
            R.drawable.ic_signal_wifi_3_bar_lock_black_36dp, R.drawable.ic_signal_wifi_4_bar_lock_black_36dp};
    private int[] SignalWifiRes = {R.drawable.ic_signal_wifi_1_bar_black_36dp, R.drawable.ic_signal_wifi_2_bar_black_36dp,
            R.drawable.ic_signal_wifi_3_bar_black_36dp, R.drawable.ic_signal_wifi_4_bar_black_36dp};

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_wifi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvName.setText(wifiEntityList.get(position).getWifiName());
        WifiEntity wifiEntity = wifiEntityList.get(position);
        holder.tvName.setText(wifiEntity.getWifiName());
        holder.tvState.setText(WifiStateEnum.getState(wifiEntity.getWifiState()));

        switch (wifiEntity.getWifiSecurityMode()) {
            case WPA2:
            case WPA_AND_WPA2:
                holder.ivSignalWifi.setImageResource(lockSignalWifiRes[wifiEntity.getWifiStrength()]);
                break;
            default:
                holder.ivSignalWifi.setImageResource(SignalWifiRes[wifiEntity.getWifiStrength()]);
        }
        if (onClickItemListener != null) {
            holder.wifi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItemListener.onClickItem(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return wifiEntityList == null ? 0 : wifiEntityList.size();
    }

    public void setList(List<WifiEntity> wifiEntityList) {
        this.wifiEntityList = wifiEntityList;
        notifyDataSetChanged();
    }

    public List<WifiEntity> getWifiEntityList() {
        return wifiEntityList;
    }

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.onClickItemListener = listener;
    }

    public interface OnClickItemListener {
        void onClickItem(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_state)
        TextView tvState;
        @BindView(R.id.iv_signal_wifi)
        ImageView ivSignalWifi;
        @BindView(R.id.wifi)
        RelativeLayout wifi;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

}
