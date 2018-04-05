package com.taisau.facecardcompare.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.GFace;
import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.model.CompareInfo;
import com.taisau.facecardcompare.ui.BaseActivity;
import com.taisau.facecardcompare.ui.WelcomeActivity;
import com.taisau.facecardcompare.ui.history.HistoryListActivity;
import com.taisau.facecardcompare.ui.history.PrintHistoryActivity;
import com.taisau.facecardcompare.ui.main.adpater.AdsPagerAdapter;
import com.taisau.facecardcompare.ui.main.broadcast.NetBroadCast;
import com.taisau.facecardcompare.ui.main.contract.MainContract;
import com.taisau.facecardcompare.ui.main.dialog.PassDialog;
import com.taisau.facecardcompare.ui.main.presenter.MainPresenter;
import com.taisau.facecardcompare.ui.personlist.GroupActivity;
import com.taisau.facecardcompare.ui.setting.SettingActivity;
import com.taisau.facecardcompare.util.CameraUtils2;
import com.taisau.facecardcompare.util.FileUtils;
import com.taisau.facecardcompare.util.IDCardUtils;
import com.taisau.facecardcompare.util.Preference;
import com.taisau.facecardcompare.util.SoundUtils;
import com.taisau.facecardcompare.widget.FaceFrame;
import com.taisau.facecardcompare.widget.VerticalSeekBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.taisau.facecardcompare.util.Constant.LIB_DIR;

/**
 * Created by whx on 2017/09/01
 * 主页面
 */
public class MainActivity extends BaseActivity implements SurfaceHolder.Callback, MainContract.View, Camera.ErrorCallback, View.OnClickListener {
    private static final String TAG = "MainActivity";
    private MainPresenter presenter;
    //承载画面的surfaceView
    public SurfaceView surfaceView;
    //用于承载预览画面生成图片的数据的holder
    public SurfaceHolder surfaceHolder;
    //相机的辅助工具类
    public CameraUtils2 cameraUtils;
    //底层帧布局
    public FrameLayout bottomLayout;
    //待机画面
    public RelativeLayout restLayout;
    //广告Pager
    private ViewPager pager;
    private static int pagerIndex = 0;
    //人脸框
    public FaceFrame frame;
    private LinearLayout useLayout;
    private LinearLayout compareLayout;
    //身份证信息
    private TextView cardInfo;
    //身份证照片
    private ImageView cardImg;
    private ImageView faceImg;
    //比对结果信息
    private TextView resInfo;
    private ImageView resImg;
    //天气 时间
    private ImageView weatherImg;
    private TextView weather;
    private TextView time;
    private AlertDialog errorDialog;
    private PassDialog passDialog;
    //主副标题
    private TextView main_tittle, sub_tittle;
    private LinearLayout ll_sound;//音量布局
    private VerticalSeekBar seekbar_sound;
    private NetBroadCast netBroadCast = new NetBroadCast(this);
    public Handler handler = new Handler();
    //广告轮播
    private int adsCount = 0;
    private Subscription subscription;//定时轮播的订阅事件
    private int maxSound;
    private boolean isFirstTime = true, isInitSound = true;

    private void initView() {
        bottomLayout = (FrameLayout) findViewById(R.id.main_preview_back_frameLayout);
        restLayout = (RelativeLayout) findViewById(R.id.main_rest_view);
        /*
         * 底部布局
         */
        useLayout = (LinearLayout) findViewById(R.id.main_using_tips);
        compareLayout = (LinearLayout) findViewById(R.id.main_compare_info);
        RelativeLayout bottomMenuLayout = (RelativeLayout) findViewById(R.id.main_info_layout);
        bottomMenuLayout.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                if (x > 0 && x < 80 && y > 560 && y < 900)
                    findViewById(R.id.main_right_menu).setVisibility(View.VISIBLE);
                else
                    findViewById(R.id.main_right_menu).setVisibility(View.GONE);
                return false;
            }
        });

        findViewById(R.id.main_right_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FaceCardApplication.getApplication().getDaoSession().getHistoryListDao().count() == 0) {
                    Toast.makeText(MainActivity.this, "没有历史记录", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, PrintHistoryActivity.class);
                intent.putExtra("his_id", Preference.getHisLastId());
                inputPasswordDialog(intent);
            }
        });
        findViewById(R.id.main_right_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPasswordDialog(new Intent(MainActivity.this, GroupActivity.class));
            }
        });
        findViewById(R.id.main_right_btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPasswordDialog(new Intent(MainActivity.this, HistoryListActivity.class));
            }
        });
        findViewById(R.id.main_right_btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPasswordDialog(new Intent(MainActivity.this, SettingActivity.class));
            }
        });
        /*
         *  compare info比对信息
         */
        pager = (ViewPager) findViewById(R.id.main_ads_pager);
        faceImg = (ImageView) findViewById(R.id.main_compare_img1);
        cardImg = (ImageView) findViewById(R.id.main_compare_img2);
        resInfo = (TextView) findViewById(R.id.main_compare_info_res);
        resInfo.setVisibility(View.GONE);
        resImg = (ImageView) findViewById(R.id.main_compare_info_img);
        resImg.setVisibility(View.GONE);
        cardInfo = (TextView) findViewById(R.id.main_compare_info_cardInfo);
        main_tittle = (TextView) findViewById(R.id.main_main_tittle);
        sub_tittle = (TextView) findViewById(R.id.main_sub_tittle);
        weatherImg = (ImageView) findViewById(R.id.main_tips_weather_img);
        weather = (TextView) findViewById(R.id.main_tips_weather_text);
        time = (TextView) findViewById(R.id.main_tips_time_text);
        /*
         * 人脸框架
         */
        frame = new FaceFrame(this);
        ImageView net = (ImageView) findViewById(R.id.main_net_status);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            net.setImageResource(R.mipmap.net_online);
        } else {
            net.setImageResource(R.mipmap.net_offline);
        }
        ll_sound = (LinearLayout) findViewById(R.id.ll_sound);
        findViewById(R.id.iv_sound_status).setOnClickListener(this);
        findViewById(R.id.imgBtn_main_sound_add).setOnClickListener(this);
        findViewById(R.id.imgBtn_main_sound_decrease).setOnClickListener(this);
        seekbar_sound = (VerticalSeekBar) findViewById(R.id.seekbar_sound);
        seekbar_sound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isFirstTime) {
                    isFirstTime = false;
                    return;
                }
                presenter.updateSoundStatus(isInitSound, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SoundUtils.init(this);
        maxSound = SoundUtils.getInstance().getMaxVolume(AudioManager.STREAM_MUSIC);
        seekbar_sound.setMax(maxSound);
        String currentVolume = Preference.getDevVolume();
        if (currentVolume == null) {
            Log.d(TAG, "initView:   currentVolume == null");
            seekbar_sound.setProgress(7);
        } else {
            Log.d(TAG, "initView: maxSound=" + maxSound + ",currentVolume=" + currentVolume);
            seekbar_sound.setProgress(Integer.valueOf(currentVolume));
        }
        isInitSound = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_sound_status://音量喇叭
                if (ll_sound.getVisibility() == View.GONE) {
                    ll_sound.setVisibility(View.VISIBLE);
                } else {
                    ll_sound.setVisibility(View.GONE);
                }
                break;
            case R.id.imgBtn_main_sound_add://音量加
                presenter.updateSoundStatus(false, -1);
                break;
            case R.id.imgBtn_main_sound_decrease://音量减
                presenter.updateSoundStatus(false, -2);
                break;

        }
    }

    private void initIDCardReader() {
        IDCardUtils.getUtils().initIDCardReader(presenter.getCardDetectListener(), this);
        IDCardUtils.getUtils().setFilepath(LIB_DIR);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int ret = GFace.loadModel(LIB_DIR + "/face_GFace6/dnew.dat", LIB_DIR + "/face_GFace6/anew.dat", LIB_DIR + "/face_GFace7/db.dat", LIB_DIR + "/face_GFace7/p.dat");
        if (ret == -1) {
            synchronized (this) {
                Toast.makeText(this, "算法初始化失败", Toast.LENGTH_LONG).show();
                try {
                    wait(1500);
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, WelcomeActivity.class);
                    intent.putExtra("exit_flag", "exit_id");
                    MainActivity.this.startActivity(intent);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        File apk = new File(LIB_DIR + "/FaceCompare.apk");
        if (apk.exists())
            FileUtils.deleteFile(apk.getAbsolutePath());
        setContentView(R.layout.activity_main);
        //初始化Present
        presenter = new MainPresenter(this);
        //初始化VIEW
        initView();
        //初始化读卡器
        initIDCardReader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        useLayout.setVisibility(View.VISIBLE);
        compareLayout.setVisibility(View.GONE);
        presenter.updateAdsTitle();
        presenter.updateAdsSubitle();
        presenter.updateAdsPath();
        presenter.updateUserName();
        //初始化白名单
        //开启读卡器
        IDCardUtils.getUtils().setIDCardReaderRun();
        //开启摄像头
        cameraUtils = new CameraUtils2(this, this);
        surfaceView = (SurfaceView) findViewById(R.id.main_camera_preview);
        surfaceHolder = surfaceView.getHolder();
        // 为surfaceHolder添加一个回调监听器
        //用于监听surfaceView的变化
        surfaceHolder.addCallback(this);
        surfaceView.setKeepScreenOn(true);
        if (cameraUtils.getCamera() != null) {
            try {
                cameraUtils.getCamera().stopPreview();
                //绑定摄像头到surfaceView和Holder上
                //  cameraUtils.getCamera().autoFocus(null);
                cameraUtils.getCamera().setPreviewDisplay(surfaceHolder);
                cameraUtils.getCamera().setPreviewCallback(/*MainActivity.this*/presenter.getPreviewCallback());
                cameraUtils.getCamera().startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            presenter.doAutoLogin();
            presenter.initTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "输入服务器地址有误,请重新设置", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            inputPasswordDialog(intent);
        }

        registerReceiver(netBroadCast, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.stopTime();
        if (cameraUtils != null) {
            cameraUtils.releaseCamera();
            cameraUtils = null;
        }
        IDCardUtils.getUtils().setIDCardReaderStop();
        if (subscription != null) {
            subscription.unsubscribe();
        }
        unregisterReceiver(netBroadCast);
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IDCardUtils.getUtils().closeIDCardReader();
        Log.e(TAG, "MainActivity      onDestroy: ");
    }

    private void inputPasswordDialog(final Intent intent) {
        if (passDialog == null) {
            passDialog = new PassDialog(MainActivity.this);
            passDialog.setStopListener(new PassDialog.onStopListener() {
                @Override
                public void onDialogStop() {
                    presenter.setRunDetect(true);
                    IDCardUtils.getUtils().setIDCardReaderRun();
                }
            });
        }
        IDCardUtils.getUtils().setIDCardReaderStop();
        presenter.setRunDetect(false);
        passDialog.setTitle("验证密码");
        passDialog.setMessage("请输入密码，验证成功才能跳转页面");
        passDialog.setYesOnclickListener("确定", new PassDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                String password = passDialog.getPass();
                if (password.equals("6116")) {
                    passDialog.dismiss();
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    hideSystemUi();
                }
            }
        });
        passDialog.setNoOnclickListener("取消", new PassDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                hideSystemUi();
                passDialog.dismiss();
                passDialog.setPass("");
            }
        });
        passDialog.show();
    }


    //surfaceView生成时会调用此函数
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        if (cameraUtils.getCamera() != null) {
            try {
                cameraUtils.getCamera().stopPreview();
                //绑定摄像头到surfaceView和Holder上
                //  cameraUtils.getCamera().autoFocus(null);
                cameraUtils.getCamera().setPreviewDisplay(surfaceHolder);
                cameraUtils.getCamera().setPreviewCallback(/*MainActivity.this*/presenter.getPreviewCallback());
                cameraUtils.getCamera().startPreview();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //surfaceView销毁时会调用此函数此时释放摄像头
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void updateNetworkStatus(boolean netEnable) {
        ((ImageView) findViewById(R.id.main_net_status)).setImageResource(
                netEnable ? (R.mipmap.net_online) : (R.mipmap.net_offline));
    }


    @Override
    public void updateTimeStatus(String time) {
        this.time.setText(time);
    }

    @Override
    public void updateAdsTitle(String title) {
        main_tittle.setText(title);
    }

    @Override
    public void updateAdsSubtitle(String subtitle) {
        sub_tittle.setText(subtitle);
    }


    @Override
    public void updateAdsImage(String[] paths) {
        if (paths.length == 1 && paths[0] == null) {
            adsCount = 1;
        } else {
            for (String path : paths) {
                Log.d(TAG, "updateAdsImage: paths[i] = " + path);
            }
            if (paths.length < 1) {
                adsCount = 1;
            } else {
                adsCount = paths.length;
            }
        }

        LayoutInflater inflater = getLayoutInflater();
        ImageView[] view = new ImageView[adsCount];
        ArrayList<ImageView> viewList = new ArrayList<>(adsCount);// 将要分页显示的View装入数组中
        final ViewGroup viewGroup = null;
        for (int i = 0; i < adsCount; i++) {
            view[i] = (ImageView) inflater.inflate(R.layout.content_main_ads, viewGroup);
            if (i == 0) {//第一页放置默认或者客户设置的广告
                if (Preference.getAdsPath() == null || Preference.getAdsPath().equals("")) {
                    view[i].setImageResource(R.mipmap.main_ads_default);
                } else {
                    view[i].setImageBitmap(BitmapFactory.decodeFile(Preference.getAdsPath()));
                }
            } else {//剩下的放置下发的广告
                view[i].setImageBitmap(BitmapFactory.decodeFile(paths[i]));
            }
            viewList.add(view[i]);
        }
        AdsPagerAdapter adsPagerAdapter = new AdsPagerAdapter(viewList);
        pager.setAdapter(adsPagerAdapter);
        pagerIndex = 0;
        pager.setCurrentItem(pagerIndex);
        if (adsCount < 2) {
            return;
        }
        subscription = Observable.interval(12, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (pagerIndex < (adsCount - 1)) {
                            pagerIndex++;
                        } else {
                            pagerIndex = 0;
                        }
                        Log.d(TAG, "call: pagerIndex=" + pagerIndex);
                        pager.setCurrentItem(pagerIndex);
                    }
                });
    }


    @Override
    public void updateFaceFrame(long[] position, int pic_width, int pic_height) {
        frame.createFrame(bottomLayout, position, pic_width, pic_height);
    }

    @Override
    public void updateUserName(String result) {
        //custom_name.setText(result);
    }

    @Override
    public void showToastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    /*可重构代码
        1.switch放入present完成
        2.此处优化switch语句
          不写break复用部分选项
     */
    @Override
    public void updateCompareInfo(CompareInfo info) {
        switch (info.getType())
        {
            case CLEAR_All:
                useLayout.setVisibility(View.VISIBLE);
                resImg.setVisibility(View.GONE);
                compareLayout.setVisibility(View.GONE);
                faceImg.setImageBitmap(null);
                cardImg.setImageBitmap(null);
                cardInfo.setText(null);
                resInfo.setText(null);
                resInfo.setVisibility(View.GONE);
                resImg.setVisibility(View.GONE);
                break;
            case CARD_WITH_TEXT:
                useLayout.setVisibility(View.GONE);
                compareLayout.setVisibility(View.VISIBLE);
                faceImg.setImageBitmap(null);
                cardImg.setImageBitmap(info.getCard());
                cardInfo.setText(info.getInfoResult());
                resInfo.setText(null);
                resInfo.setVisibility(View.GONE);
                resImg.setVisibility(View.GONE);
                break;
            case FACE_WITH_TEXT_PASS:
                useLayout.setVisibility(View.GONE);
                compareLayout.setVisibility(View.VISIBLE);
                faceImg.setImageBitmap(info.getReal());
                cardInfo.setText(info.getInfoResult());
                resInfo.setText(info.getScoreResult());
                resImg.setVisibility(View.VISIBLE);
                resImg.setImageResource(info.getResultID());
                resInfo.setVisibility(View.VISIBLE);
                resInfo.setTextColor(Color.GREEN);
                break;
            case FACE_WITH_TEXT_FAIL:
                useLayout.setVisibility(View.GONE);
                compareLayout.setVisibility(View.VISIBLE);
                faceImg.setImageBitmap(info.getReal());
                cardInfo.setText(info.getInfoResult());
                resInfo.setVisibility(View.GONE);
                resImg.setVisibility(View.VISIBLE);
                resImg.setImageResource(info.getResultID());
                break;
            case ONLY_TEXT:
                useLayout.setVisibility(View.GONE);
                compareLayout.setVisibility(View.VISIBLE);
                cardInfo.setText(info.getInfoResult());
                resInfo.setVisibility(View.GONE);
                resImg.setVisibility(View.GONE);
                break;
            case ERROR_TEXT:
                useLayout.setVisibility(View.GONE);
                compareLayout.setVisibility(View.VISIBLE);
                faceImg.setImageBitmap(null);
                cardImg.setImageBitmap(null);
                cardInfo.setText(info.getInfoResult());
                resInfo.setVisibility(View.GONE);
                resImg.setVisibility(View.GONE);
                break;
            case WHITE_RES:

        }
    }



    @Override
    public void updateSoundStatus(int soundNum) {
        int current = seekbar_sound.getProgress();
        Log.d(TAG, "updateSoundStatus: current = " + current + ",soundNum=" + soundNum);
        switch (soundNum) {
            case 0: //静音
                seekbar_sound.setProgress(0);
                break;
            case -1: //音量 +1

                if (current != maxSound) {
                    seekbar_sound.setProgress(current + 1);
                }

                break;
            case -2: //音量 -1
                if (current != 0) {
                    seekbar_sound.setProgress(current - 1);
                }
                break;
            case -3: //音量最大
                if (current != maxSound) {
                    seekbar_sound.setProgress(maxSound);
                }
                break;
            default: // 直接设置音量

                break;
        }
    }

    @Override
    public void reLoad() {
        finish();
    }


    @Override
    public void onError(int error, Camera camera) {
        if (error == 100) {
            errorDialog = new AlertDialog.Builder(this).setTitle("摄像头失效")
                    .setMessage("请确认摄像头是否有效，或者重新插拔摄像头点击确定键重新初始化\n提示:5秒后会自动进行初始化工作")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (errorDialog != null && errorDialog.isShowing()) {
                                errorDialog.dismiss();
                                if (cameraUtils != null) {
                                    cameraUtils.releaseCamera();
                                    cameraUtils = null;
                                    cameraUtils = new CameraUtils2(MainActivity.this, MainActivity.this);
                                    try {
                                        cameraUtils.getCamera().stopPreview();
                                        cameraUtils.getCamera().setPreviewDisplay(surfaceHolder);
                                        cameraUtils.getCamera().setPreviewCallback(presenter.getPreviewCallback());
                                        cameraUtils.getCamera().startPreview();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                surfaceView.setVisibility(View.GONE);
                                surfaceView.setVisibility(View.VISIBLE);
                            }
                        }
                    })
                    .setNegativeButton("关闭应用", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, WelcomeActivity.class);
                            intent.putExtra("exit_flag", "exit_id");
                            MainActivity.this.startActivity(intent);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (errorDialog != null && errorDialog.isShowing()) {
                    errorDialog.dismiss();
                    if (cameraUtils != null) {
                        cameraUtils.releaseCamera();
                        cameraUtils = null;
                        cameraUtils = new CameraUtils2(MainActivity.this, MainActivity.this);
                        try {
                            cameraUtils.getCamera().stopPreview();
                            //绑定摄像头到surfaceView和Holder上
                            //  cameraUtils.getCamera().autoFocus(null);
                            cameraUtils.getCamera().setPreviewDisplay(surfaceHolder);
                            cameraUtils.getCamera().setPreviewCallback(presenter.getPreviewCallback());
                            cameraUtils.getCamera().startPreview();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    surfaceView.setVisibility(View.GONE);
                    surfaceView.setVisibility(View.VISIBLE);
                }
            }
        }, 5000);
    }

}
