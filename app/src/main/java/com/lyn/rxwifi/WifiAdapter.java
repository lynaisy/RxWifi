package com.lyn.rxwifi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.lyn.library.wifi.WifiEntity;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2018/2/9.
 */

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {
    private List<WifiEntity> wifiEntityList;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_wifi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvName.setText(wifiEntityList.get(position).getWifiName());
    }

    @Override
    public int getItemCount() {
        return wifiEntityList == null ? 0 : wifiEntityList.size();
    }

    public void setList(List<WifiEntity> wifiEntityList) {
        this.wifiEntityList = wifiEntityList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
