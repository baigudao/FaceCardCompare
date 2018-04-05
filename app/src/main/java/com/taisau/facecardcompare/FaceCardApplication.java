package com.taisau.facecardcompare;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;
import com.taisau.facecardcompare.model.DaoMaster;
import com.taisau.facecardcompare.model.DaoSession;
import com.taisau.facecardcompare.service.MsgPushService;
import com.taisau.facecardcompare.util.CrashHandler;
import com.taisau.facecardcompare.util.FileUtils;
import com.taisau.facecardcompare.util.Preference;

import org.greenrobot.greendao.database.Database;

import java.io.File;

import static com.taisau.facecardcompare.util.Constant.LIB_DIR;

/**
 * Created by Administrator on 2017/2/28 0028.
 */

public class FaceCardApplication extends Application {
    public static final String TAG="FaceCardApplication";
    //优化单例模式
    private static FaceCardApplication application;
    public static FaceCardApplication getApplication() {
        return application;
    }

    //数据库存储
    private DaoSession daoSession;
    public DaoSession getDaoSession() {
        return daoSession;
    }

    private volatile static long historyID;

    public static long getHistoryID() {
        return historyID;
    }

    public static void setHistoryID(long historyID) {
        FaceCardApplication.historyID = historyID;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        File fileLibDir = new File(LIB_DIR);
        File fileFace6 = new File(LIB_DIR + "/face_GFace6");
        File fileFace7 = new File(LIB_DIR + "/face_GFace7");
        File cardFea = new File(LIB_DIR + "/card_fea");
        File faceFea = new File(LIB_DIR + "/face_fea");
        File faceImg = new File(LIB_DIR + "/face_img");
        File cardImg = new File(LIB_DIR + "/card_img");
        File whiteImg = new File(LIB_DIR + "/white_img");
        File whiteFea = new File(LIB_DIR + "/white_fea");
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "facecard_com");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        if (!fileLibDir.exists())
            fileLibDir.mkdir();
        if (!fileFace6.exists())
            fileFace6.mkdir();
        if (!fileFace7.mkdir())
            fileFace7.mkdir();
        if (!cardFea.mkdir())
            cardFea.mkdir();
        if (!faceFea.mkdir())
            faceFea.mkdir();
        if (!faceImg.mkdir())
            faceImg.mkdir();
        if (!cardImg.mkdir())
            cardImg.mkdir();
        if (!whiteImg.exists())
            whiteImg.mkdir();
        if (!whiteFea.exists())
            whiteFea.mkdir();
        if (getDaoSession().getHistoryListDao().count() == 0) {
            Preference.setHisFirstId(1);
            Preference.setHisLastId(0);
        }
        System.out.println("count:"+getDaoSession().getHistoryListDao().count());
        FileUtils.moveConfigFile(this, R.raw.base, LIB_DIR + "/base.dat");
        FileUtils.moveConfigFile(this, R.raw.license, LIB_DIR + "/license.lic");
        FileUtils.moveConfigFile(this, R.raw.anew, LIB_DIR + "/face_GFace6/anew.dat");
        FileUtils.moveConfigFile(this, R.raw.dnew, LIB_DIR + "/face_GFace6/dnew.dat");
        FileUtils.moveConfigFile(this, R.raw.db, LIB_DIR + "/face_GFace7/db.dat");
        FileUtils.moveConfigFile(this, R.raw.p, LIB_DIR + "/face_GFace7/p.dat");
        if (Preference.getHisLastId() > 0)
            historyID = Preference.getHisLastId();
        else
            historyID = 1;
        if (!BuildConfig.DEBUG) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());
        }
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    private MsgPushService service;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG,"MsgService Start");
            service = ((MsgPushService.MQBinder) iBinder).getService();
            service.startMsgService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
        }
    };

    public void bindMqService() {
        if (service != null) {
            unbindService(connection);
        }
        bindService(new Intent(this, MsgPushService.class), connection, Context.BIND_AUTO_CREATE);
    }

    public void setMqServiceRestart() {
        if (service == null) {
            bindService(new Intent(this, MsgPushService.class), connection, Context.BIND_AUTO_CREATE);
        } else {
            service.stopMsgService();
            service.startMsgService();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unbindService(connection);
    }
}
