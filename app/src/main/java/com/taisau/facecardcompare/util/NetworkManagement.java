package com.taisau.facecardcompare.util;

/**
 * Created by ds  on 2017/2/4 13:48
 */

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NetworkManagement {
    public static final String CTWAP = "ctwap";
    public static final String CTNET = "ctnet";
    public static final String CMWAP = "cmwap";
    public static final String CMNET = "cmnet";
    public static final String NET_3G = "3gnet";
    public static final String WAP_3G = "3gwap";
    public static final String UNIWAP = "uniwap";
    public static final String UNINET = "uninet";

    public static final int TYPE_CT_WAP = 5;
    public static final int TYPE_CT_NET = 6;
    public static final int TYPE_CT_WAP_2G = 7;
    public static final int TYPE_CT_NET_2G = 8;

    public static final int TYPE_CM_WAP = 9;
    public static final int TYPE_CM_NET = 10;
    public static final int TYPE_CM_WAP_2G = 11;
    public static final int TYPE_CM_NET_2G = 12;

    public static final int TYPE_CU_WAP = 13;
    public static final int TYPE_CU_NET = 14;
    public static final int TYPE_CU_WAP_2G = 15;
    public static final int TYPE_CU_NET_2G = 16;

    public static final int TYPE_OTHER = 17;
    public static Uri PREFERRED_APN_URI = Uri
            .parse("content://telephony/carriers/preferapn");

    /** 没有网络 */
    public static final int TYPE_NET_WORK_DISABLED = 0;

    /** wifi网络 */
    public static final int TYPE_WIFI = 4;
    private static ConnectivityManager connectivityManager;
    private static NetworkInfo networkInfo;

    public NetworkManagement(Context context) {
        createConnectivityManager(context);
        createNetworkInfo();
        // NetworkInfo [] networkInfos =
        // connectivityManager.getAllNetworkInfo();
    }

    public static void createConnectivityManager(Context context) {
        connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static void createNetworkInfo() {
        // TelephonyManager telephonyManager =
        // (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        // int networkType = telephonyManager.getNetworkType();
        networkInfo = connectivityManager.getActiveNetworkInfo();
    }

    /**
     * 判断是否为漫游
     *
     * @param context
     * @return
     */
    public static boolean isRoam(Context context) {
        if (connectivityManager == null) {
            createConnectivityManager(context);
            createNetworkInfo();
        }
        if (networkInfo == null)
            return false;
        if (!networkInfo.isConnected())
            return false;
        return networkInfo.isRoaming();
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        createConnectivityManager(context);
        createNetworkInfo();
        if (networkInfo == null)
            return false;
        if (!networkInfo.isConnected())
            return false;
        return networkInfo.isConnected();
    }

    /***
     * 判断Network具体类型（联通移动wap，电信wap，其他net）
     *
     * */
    public static int checkNetworkType(Context mContext) {
        try {
            final ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo mobNetInfoActivity = connectivityManager
                    .getActiveNetworkInfo();
            if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable())
                // 注意一：
                // NetworkInfo 为空或者不可以用的时候正常情况应该是当前没有可用网络，
                // 但是有些电信机器，仍可以正常联网，
                // 所以当成net网络处理依然尝试连接网络。
                // （然后在socket中捕捉异常，进行二次判断与用户提示）。
                return TYPE_NET_WORK_DISABLED;
            // NetworkInfo不为null开始判断是网络类型
            int netType = mobNetInfoActivity.getType();
            if (netType == ConnectivityManager.TYPE_WIFI)
                // wifi net处理
                return TYPE_WIFI;
            if (netType == ConnectivityManager.TYPE_MOBILE) {
                // 注意二：
                // 判断是否电信wap:
                // 不要通过getExtraInfo获取接入点名称来判断类型，
                // 因为通过目前电信多种机型测试发现接入点名称大都为#777或者null，
                // 电信机器wap接入点中要比移动联通wap接入点多设置一个用户名和密码,
                // 所以可以通过这个进行判断！
                boolean is3G = isFastMobileNetwork(mContext);
                final Cursor c = mContext.getContentResolver().query(
                        PREFERRED_APN_URI, null, null, null, null);
                if (c != null) {
                    c.moveToFirst();
                    final String user = c.getString(c.getColumnIndex("user"));
                    if (!TextUtils.isEmpty(user)) {
                        if (user.startsWith(CTWAP))
                            return is3G ? TYPE_CT_WAP : TYPE_CT_WAP_2G;
                        if (user.startsWith(CTNET))
                            return is3G ? TYPE_CT_NET : TYPE_CT_NET_2G;
                    }
                }
                c.close();
                // 注意三：
                // 判断是移动联通wap:
                // 其实还有一种方法通过getString(c.getColumnIndex("proxy")获取代理ip
                // 来判断接入点，10.0.0.172就是移动联通wap，10.0.0.200就是电信wap，但在
                // 实际开发中并不是所有机器都能获取到接入点代理信息，例如魅族M9 （2.2）等...
                // 所以采用getExtraInfo获取接入点名字进行判断
                String netMode = mobNetInfoActivity.getExtraInfo();
                Log.i("", "==================netmode:" + netMode);
                if (netMode != null) {
                    // 通过apn名称判断是否是联通和移动wap
                    netMode = netMode.toLowerCase();
                    if (netMode.equals(CMWAP))
                        return is3G ? TYPE_CM_WAP : TYPE_CM_WAP_2G;
                    if (netMode.equals(CMNET))
                        return is3G ? TYPE_CM_NET : TYPE_CM_NET_2G;
                    if (netMode.equals(NET_3G) || netMode.equals(UNINET))
                        return is3G ? TYPE_CU_NET : TYPE_CU_NET_2G;
                    if (netMode.equals(WAP_3G) || netMode.equals(UNIWAP))
                        return is3G ? TYPE_CU_WAP : TYPE_CU_WAP_2G;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return TYPE_OTHER;
        }

        return TYPE_OTHER;

    }

    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;

        }
    }

    /**
     * 打开设置网络界面
     * */
    public static void setNetworkMethod(Context context) {
        Intent intent = null;
        // 判断手机系统的版本 即API大于10 就是3.0或以上版本
        // intent = new Intent(Settings.ACTION_APN_SETTINGS);
        // startActivity(intent);
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(
                    Settings.ACTION_WIRELESS_SETTINGS);
            context.startActivity(intent);
            return;
        }
        intent = new Intent();
        ComponentName component = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(component);
        intent.setAction("android.intent.action.VIEW");
        context.startActivity(intent);
    }

    /**
     * 打开wifi设置
     */
    public static void setwifi(Context context) {
        // 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
        if (android.os.Build.VERSION.SDK_INT > 10) {
            context.startActivity(new Intent(
                    Settings.ACTION_SETTINGS));
            return;
        }
        context.startActivity(new Intent(
                Settings.ACTION_WIRELESS_SETTINGS));
    }

    public static void setSet(Context context) {
        // 　com.android.settings.AccessibilitySettings 辅助功能设置
        // 　　com.android.settings.ActivityPicker 选择活动
        // 　　com.android.settings.ApnSettings APN设置
        // 　　com.android.settings.ApplicationSettings 应用程序设置
        // 　　com.android.settings.BandMode 设置GSM/UMTS波段
        // 　　com.android.settings.BatteryInfo 电池信息
        // 　　com.android.settings.DateTimeSettings 日期和坝上旅游网时间设置
        // 　　com.android.settings.DateTimeSettingsSetupWizard 日期和时间设置
        // 　　com.android.settings.DevelopmentSettings 应用程序设置=》开发设置
        // 　　com.android.settings.DeviceAdminSettings 设备管理器
        // 　　com.android.settings.DeviceInfoSettings 关于手机
        // 　　com.android.settings.Display 显示——设置显示字体大小及预览
        // 　　com.android.settings.DisplaySettings 显示设置
        // 　　com.android.settings.DockSettings 底座设置
        // 　　com.android.settings.IccLockSettings SIM卡锁定设置
        // 　　com.android.settings.InstalledAppDetails 语言和键盘设置
        // 　　com.android.settings.LanguageSettings 语言和键盘设置
        // 　　com.android.settings.LocalePicker 选择手机语言
        // 　　com.android.settings.LocalePickerInSetupWizard 选择手机语言
        // 　　com.android.settings.ManageApplications 已下载（安装）软件列表
        // 　　com.android.settings.MasterClear 恢复出厂设置
        // 　　com.android.settings.MediaFormat 格式化手机闪存
        // 　　com.android.settings.PhysicalKeyboardSettings 设置键盘
        // 　　com.android.settings.PrivacySettings 隐私设置
        // 　　com.android.settings.ProxySelector 代理设置
        // 　　com.android.settings.RadioInfo 手机信息
        // 　　com.android.settings.RunningServices 正在运行的程序（服务）
        // 　　com.android.settings.SecuritySettings 位置和安全设置
        // 　　com.android.settings.Settings 系统设置
        // 　　com.android.settings.SettingsSafetyLegalActivity 安全信息
        // 　　com.android.settings.SoundSettings 声音设置
        // 　　com.android.settings.TestingSettings 测试——显示手机信息、电池信息、使用情况统计、Wifi
        // information、服务信息
        // 　　com.android.settings.TetherSettings 绑定与便携式热点
        // 　　com.android.settings.TextToSpeechSettings 文字转语音设置
        // 　　com.android.settings.UsageStats 使用情况统计
        // 　　com.android.settings.UserDictionarySettings 用户词典
        // 　　com.android.settings.VoiceInputOutputSettings 语音输入与输出设置
        // 　　com.android.settings.WirelessSettings 无线和网络设置
        // Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        // context.startActivity(intent);
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 移动网络开关
     */
    private void toggleMobileData(Context context, boolean enabled) {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        Class<?> conMgrClass = null; // ConnectivityManager类
        Field iConMgrField = null; // ConnectivityManager类中的字段
        Object iConMgr = null; // IConnectivityManager类的引用
        Class<?> iConMgrClass = null; // IConnectivityManager类
        Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法

        try {
            // 取得ConnectivityManager类
            conMgrClass = Class.forName(conMgr.getClass().getName());
            // 取得ConnectivityManager类中的对象mService
            iConMgrField = conMgrClass.getDeclaredField("mService");
            // 设置mService可访问
            iConMgrField.setAccessible(true);
            // 取得mService的实例化类IConnectivityManager
            iConMgr = iConMgrField.get(conMgr);
            // 取得IConnectivityManager类
            iConMgrClass = Class.forName(iConMgr.getClass().getName());
            // 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
                    "setMobileDataEnabled", Boolean.TYPE);
            // 设置setMobileDataEnabled方法可访问
            setMobileDataEnabledMethod.setAccessible(true);
            // 调用setMobileDataEnabled方法
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
