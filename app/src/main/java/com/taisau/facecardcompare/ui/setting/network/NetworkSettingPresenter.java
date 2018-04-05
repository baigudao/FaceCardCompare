package com.taisau.facecardcompare.ui.setting.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.SparseArray;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Administrator on 2017-08-16
 */

public class NetworkSettingPresenter {

    INetworkSettingView iNetworkSettingView;
    INetworkSettingModel iNetworkSettingModel;
    Context context;

    public NetworkSettingPresenter(INetworkSettingView view,Context context) {
        this.iNetworkSettingView = view;
        iNetworkSettingModel=new NetWorkSettingModel(context);
        this.context=context;
    }

    SparseArray<String> getCurrentAddress() {
        SparseArray<String> s = new SparseArray<>();
        try {
          s=iNetworkSettingModel.getCurrentAddress(s);
        } catch (Exception e) {
            e.printStackTrace();
            s.clear();
            s.append(0, "192.168.1.168");
            s.append(1, "255.255.255.0");
            s.append(2, "192.168.1.1");
            s.append(3, "192.168.1.1");
        }
        return s;
    }

    void setAddressChange(int position,String address) {
        try {
            iNetworkSettingModel.setAddressChange(address);
            iNetworkSettingView.showChangeResult(position, address);
        } catch (Exception e) {
            e.printStackTrace();
            iNetworkSettingView.showChangeResult(position, null);
        }
    }
    public int getNetConnectType(){
        //获取网络连接管理者
        ConnectivityManager connectionManager = (ConnectivityManager)
                context.getSystemService(CONNECTIVITY_SERVICE);
        //获取网络的状态信息，有下面三种方式
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo==null)
            return -2;
        if (networkInfo.getState()== NetworkInfo.State.DISCONNECTED||networkInfo.getState()== NetworkInfo.State.DISCONNECTING)
            return -1;
        System.out.println("net type:"+networkInfo.getTypeName()+" int:"+networkInfo.getType());
        return networkInfo.getType();
    }

}
