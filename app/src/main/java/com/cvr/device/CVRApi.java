//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cvr.device;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.os.Message;

import com.sdt.Common;
import com.sdt.Sdtapi;

public class CVRApi {
    Sdtapi sdta;
    Handler MyHandler;
    Common common;
    boolean init_suss = false;
    Activity m_mact;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @SuppressLint({"NewApi"})
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action)) {
                UsbDevice msg = intent.getParcelableExtra("device");

                String deviceName = msg.getDeviceName();
                if(msg != null && msg.equals(deviceName)) {
                    Message msg1 = new Message();
                    msg1.what = 2;
                    msg1.obj = "USB设备拔出，应用程序即将关闭。";
                    CVRApi.this.MyHandler.sendMessage(msg1);
                }
            } else {
                CVRApi.this.common.getClass();
                if("com.android.USB_PERMISSION".equals(action)) {
                    Message msg2 = new Message();
                    msg2.what = 1;
                    msg2.obj = "USB设备无权限";
                    CVRApi.this.MyHandler.sendMessage(msg2);
                }
            }

        }
    };

    public CVRApi(Handler handler) {
        this.MyHandler = handler;
        this.common = new Common();
    }

    public boolean CVR_Init(Activity mact) {
        try {
            this.m_mact = mact;
            IntentFilter e1 = new IntentFilter();
            e1.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
            mact.registerReceiver(this.mUsbReceiver, e1);
            this.sdta = new Sdtapi(mact);
        } catch (Exception var3) {
            var3.printStackTrace();
            if(var3.getCause() == null) {
                (new Thread() {
                    public void run() {
                        Message msg = new Message();
                        msg.what = 2;
                        msg.obj = "USB设备异常或无连接，应用程序即将关闭。";
                        CVRApi.this.MyHandler.sendMessage(msg);
                    }
                }).start();
            } else {
                (new Thread() {
                    public void run() {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "USB设备未授权，弹出请求授权窗口后，请点击\"确定\"继续";
                        CVRApi.this.MyHandler.sendMessage(msg);
                    }
                }).start();
            }

            this.init_suss = false;
            return false;
        }

        (new Thread() {
            public void run() {
                Message msg = new Message();
                msg.what = 0;
                msg.obj = "设备连接成功";
                CVRApi.this.MyHandler.sendMessage(msg);
            }
        }).start();
        this.init_suss = true;
        return true;
    }

    public void UnInit() {
        this.m_mact.unregisterReceiver(this.mUsbReceiver);
    }

    public String CVR_GetSAM_ID() {
        String sam_id_str = "";
        if(this.init_suss) {
            byte[] recData = new byte[70];
            int n = this.sdta.SDT_GetSAMID(recData);
            if(n == 144) {
                sam_id_str = Utility.AnalyzeSAM(recData);
            }
        }

        return sam_id_str;
    }

    public int CVR_Authenticate() {
        if(!this.init_suss) {
            return -1;
        } else {
            int ret = this.sdta.SDT_StartFindIDCard();
            if(ret != 159) {
                return ret;
            } else {
                ret = this.sdta.SDT_SelectIDCard();
                return ret != 144?ret:0;
            }
        }
    }

    public int CVR_Read_Content(IDCardInfo ici) {
        if(!this.init_suss) {
            return -1;
        } else {
            boolean ret = false;
            int[] puiCHMsgLen = new int[1];
            int[] puiPHMsgLen = new int[1];
            int[] puiFPMsgLen = new int[1];
            byte[] pucCHMsg = new byte[256];
            byte[] pucPHMsg = new byte[1024];
            byte[] pucFPMsg = new byte[1024];
            int ret1 = this.sdta.SDT_ReadBaseFPMsg(pucCHMsg, puiCHMsgLen, pucPHMsg, puiPHMsgLen, pucFPMsg, puiFPMsgLen);
            if(ret1 != 144) {
                return ret1;
            } else {
                try {
                    ici.setwltdata(pucPHMsg);
                  //  ici.setFpDate(pucFPMsg);
                    Utility.PersonInfoUtoG(pucCHMsg, ici);
                   /* Log.e("LUAN", "文字：" + ByteUtlis.bytesToHexStr(pucCHMsg));
                    Log.e("LUAN", "照片：" + ByteUtlis.bytesToHexStr(pucPHMsg));*/
                  //  Log.e("LUAN", "指纹：" + ByteUtlis.bytesToHexStr(pucFPMsg));
                    return 0;
                } catch (Exception var10) {
                    return -2;
                }
            }
        }
    }

    public int Unpack(String filepath, byte[] wltdata, byte[] bmpdata) {
        byte ret = -1;

        try {
            int ret1 = Utility.PersonInfoPic(filepath, wltdata, bmpdata);
            return ret1;
        } catch (Exception var6) {
            return ret;
        }
    }
}
