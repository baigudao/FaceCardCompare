package com.taisau.facecardcompare.ui.main.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.taisau.facecardcompare.ui.main.contract.MainContract;

/**
 * Created by Administrator on 2017/8/24 0024.
 */

public class NetBroadCast extends BroadcastReceiver {
    public NetBroadCast() {
        super();
    }

    MainContract.View listener;
    public NetBroadCast(MainContract.View listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.d("mark", "网络状态已经改变");
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if(info != null && info.isAvailable()) {
                String name = info.getTypeName();
                Log.d("mark", "当前网络名称：" + name);
                listener.updateNetworkStatus(true);
            } else {
                Log.d("mark", "没有可用网络");
                listener.updateNetworkStatus(false);
            }
        }
    }
}
