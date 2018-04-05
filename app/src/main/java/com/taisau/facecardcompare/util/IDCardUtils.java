package com.taisau.facecardcompare.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.cvr.device.CVRApi;
import com.cvr.device.IDCardInfo;
import com.cvr.device.USBMsg;
import com.taisau.facecardcompare.listener.OnCardDetectListener;
import com.taisau.facecardcompare.ui.WelcomeActivity;

import java.text.SimpleDateFormat;

/**
 * Created by llxtnt on 17-6-5.
 */

public class IDCardUtils {

    private static IDCardUtils utils = new IDCardUtils();
    private CVRApi api;
    private static boolean isRun = false;
    private static String filepath;
    private OnCardDetectListener listener;
    private Activity activity;
    private AlertDialog dialog;


    @SuppressLint({"HandlerLeak", "SimpleDateFormat", "DefaultLocale"})
    Handler MyHandler = new Handler() {
        public void handleMessage(Message msg) {
            // System.out.println("msg:"+msg.obj.toString());
            switch (msg.what) {
                case USBMsg.USB_DeviceConnect:// 设备连接
                 //   System.out.println("device connect");
                    break;
                case USBMsg.USB_DeviceOffline:// 设备断开
                 //   System.out.println("device offline");
                    break;
                case USBMsg.ReadIdCardSusse:
                    IDCardInfo ic = (IDCardInfo) msg.obj;
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
                    String cardimgPath = "temp_mod.bmp";
                    String info[] = new String[]{"", ic.getPeopleName(), ic.getSex(), ic.getPeople(), df.format(ic.getBirthDay()),ic.getAddr()
                            , ic.getIDCard(), ic.getDepartment(), ic.getStrartDate().replace(".", "") + "-" + ic.getEndDate().replace(".", ""), ""
                            , filepath + "/" + cardimgPath};
                  //  System.out.println("start decode img");
                    try {
                        byte[] bmpdata = new byte[38862];
                        int ret = api.Unpack(filepath + "/" + cardimgPath, ic.getwltdata(), bmpdata);// 照片解码;
                        if (ret != 0) {// 读卡失败
                            msg.what = USBMsg.ReadIdCardFail;
                            msg.obj = "照片解码失败！";
                            MyHandler.sendMessage(msg);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                //    System.out.println("end decode img");
                    listener.onDetectCard(info);
                    break;
                case USBMsg.ReadIdCardFail:
                 //   System.out.println("read fail");
                    break;
                case USBMsg.DeviceConfirmFail:
                 //   System.out.println("confirm fail");
                    break;
                default:
                    break;
            }
        }
    };

    public static IDCardUtils getUtils() {
        return utils;
    }

    private static boolean isInit = false;

    public void initIDCardReader(final OnCardDetectListener listener, final Activity activity) {
        api = new CVRApi(MyHandler);
        this.listener = listener;
        this.activity = activity;
        if (api.CVR_Init(activity)) {
            isInit = true;
            setIDCardReaderRun();
            // Toast.makeText(activity, "init success", Toast.LENGTH_SHORT).show();
        } else {
            isInit = false;
            if (CameraUtils2.camera!=null)
                MyHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog = new AlertDialog.Builder(activity).setTitle("读卡器初始化失败")
                                .setMessage("请确认已经插好读卡器，或者重新插拔读卡器点击确定键重新初始化")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        initIDCardReader(listener, activity);
                                    }
                                })
                                .setNegativeButton("关闭应用", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setClass(activity, WelcomeActivity.class);
                                        intent.putExtra("exit_flag", "exit_id");
                                        activity.startActivity(intent);
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                });
            MyHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                    initIDCardReader(listener, activity);
                }
            }, 5000);
        }
    }

    public boolean setIDCardReaderRun() {
        if (isRun == false) {
            isRun = true;
            if (isInit) {
                new Thread() {
                    public void run() {
                        synchronized (this) {
                            while (isRun) {
                                try {
                                    int ret = -1;
                                    Message msg = MyHandler.obtainMessage();
                                    ret = api.CVR_Authenticate();// 卡认证
                                    if (ret != 0) {// 卡认证失败
                                        if (ret==144)
                                        {
                                            setIDCardReaderStop();
                                            closeIDCardReader();
                                            initIDCardReader(listener,activity);
                                            setIDCardReaderRun();
                                            break;
                                        }
                                        msg.what = USBMsg.DeviceConfirmFail;
                                        msg.obj = "卡认证失败！";
                                        MyHandler.sendMessage(msg);

                                    } else {
                                        IDCardInfo ic = new IDCardInfo();
                                        ret = api.CVR_Read_Content(ic);// 读卡
                                        if (ret != 0) {// 读卡失败
                                            msg.what = USBMsg.ReadIdCardFail;
                                            msg.obj = "读卡失败！";
                                            MyHandler.sendMessage(msg);
                                            // return;
                                        } else {
                                            isRun = false;
                                            msg.what = USBMsg.ReadIdCardSusse;
                                            msg.obj = ic;
                                            MyHandler.sendMessage(msg);
                                        }
                                    }
                                } catch (Exception e) {
                                    isInit = false;
                                    isRun = false;
                                    initIDCardReader(IDCardUtils.this.listener, IDCardUtils.this.activity);
                                    e.printStackTrace();
                                }
                                try {
                                    wait(500);
                                } catch (Exception e) {

                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }.start();
            } else
                isRun = false;
            return true;
        } else
            return false;
    }


    public boolean setIDCardReaderStop() {
        if (isRun) {
            isRun = false;
            return true;
        } else
            return false;
    }

    public void closeIDCardReader() {
        if (isRun == false)
            api.UnInit();
    }

    public static boolean isRun() {
        return isRun;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        IDCardUtils.filepath = filepath;
    }

    /**
     * 指纹 指位代码
     *
     * @param FPcode
     * @return
     */
    String GetFPcode(int FPcode) {
        switch (FPcode) {
            case 11:
                return "右手拇指";
            case 12:
                return "右手食指";
            case 13:
                return "右手中指";
            case 14:
                return "右手环指";
            case 15:
                return "右手小指";
            case 16:
                return "左手拇指";
            case 17:
                return "左手食指";
            case 18:
                return "左手中指";
            case 19:
                return "左手环指";
            case 20:
                return "左手小指";
            case 97:
                return "右手不确定指位";
            case 98:
                return "左手不确定指位";
            case 99:
                return "其他不确定指位";
            default:
                return "未知";
        }
    }
}
