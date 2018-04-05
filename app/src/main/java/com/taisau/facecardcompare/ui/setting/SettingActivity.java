package com.taisau.facecardcompare.ui.setting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.em.DialogCase;
import com.taisau.facecardcompare.model.UpgradePKG;
import com.taisau.facecardcompare.ui.BaseActivity;
import com.taisau.facecardcompare.ui.WelcomeActivity;
import com.taisau.facecardcompare.ui.setting.compare.CompareSettingActivity;
import com.taisau.facecardcompare.ui.setting.device.DeviceSettingActivity;
import com.taisau.facecardcompare.ui.setting.display.DisplaySettingActivity;
import com.taisau.facecardcompare.ui.setting.listener.OnDownLoadListener;
import com.taisau.facecardcompare.ui.setting.network.NetworkSettingActivity;
import com.taisau.facecardcompare.ui.setting.presenter.SettingPresenter;
import com.taisau.facecardcompare.ui.setting.view.ISettingView;

import java.io.File;
import java.io.IOException;

import static com.taisau.facecardcompare.util.Constant.LIB_DIR;

public class SettingActivity extends BaseActivity implements View.OnClickListener, ISettingView {
    SettingPresenter presenter = new SettingPresenter(this);
    TextView appVersion;
    ImageView iconNewVersion;
    UpgradePKG pkg;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (version != null)
            presenter.checkVersion(version);
        updateAppVersion(version);
    }


    public void initView() {
        findViewById(R.id.rl_display_setting).setOnClickListener(this);
        findViewById(R.id.rl_compare_setting).setOnClickListener(this);
        findViewById(R.id.rl_device_setting).setOnClickListener(this);
        findViewById(R.id.rl_network_setting).setOnClickListener(this);
        findViewById(R.id.rl_update_setting).setOnClickListener(this);
        findViewById(R.id.rl_restore_default).setOnClickListener(this);
        findViewById(R.id.rl_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_setting_title)).setText("设置");
        iconNewVersion = (ImageView) findViewById(R.id.iv_new_version);
        appVersion = (TextView) findViewById(R.id.tv_app_version);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_display_setting:
                startActivity(new Intent(SettingActivity.this, DisplaySettingActivity.class));
                break;
            case R.id.rl_compare_setting:
                startActivity(new Intent(SettingActivity.this, CompareSettingActivity.class));
                break;
            case R.id.rl_device_setting:
                startActivity(new Intent(SettingActivity.this, DeviceSettingActivity.class));
                break;
            case R.id.rl_network_setting:
                startActivity(new Intent(SettingActivity.this, NetworkSettingActivity.class));
                break;
            case R.id.rl_update_setting:
                if (iconNewVersion.getVisibility() == View.VISIBLE) {
                    setAlertDialogShow(DialogCase.UPDATE_DIALOG);
                } else {
                    Toast.makeText(SettingActivity.this, "未检测到新版本", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_restore_default:
                setAlertDialogShow(DialogCase.WARNING_DIALOG);
                break;

        }
    }

    @Override
    public void updateAppVersion(final String version) {//有新版本就传filePath
        appVersion.setText(version);
    }

    @Override
    public void setRestoreDefaultSuccess() {
        Toast.makeText(SettingActivity.this, "恢复默认值成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setNewImgShow(boolean isShow, UpgradePKG pkg) {

        if (isShow)
            iconNewVersion.setVisibility(View.VISIBLE);
        else
            iconNewVersion.setVisibility(View.INVISIBLE);
        this.pkg = pkg;
    }

    @Override
    public void setAlertDialogShow(DialogCase dialogCase) {
        switch (dialogCase) {
            case UPDATE_DIALOG:
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("提示")
                        .setMessage("有新版本,是否更新？" + pkg.getFile_path() + ((pkg.getRemark() != null) && (!pkg.getRemark().equals("")) ? "\n新版本特性：\n" + pkg.getRemark() : ""))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DownLoadApkAction action = new DownLoadApkAction();
                                        action.downloadApk(SettingActivity.this, pkg.getFile_path(), listener);
                                    }
                                }
                        )
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            case WARNING_DIALOG:
                new AlertDialog.Builder(SettingActivity.this).setTitle("初始化设备")
                        .setMessage("警告：初始化设备将会初始化掉所有功能")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.clearData();
                                progressDialog = new ProgressDialog(SettingActivity.this);
                                progressDialog.setCancelable(false);
                                progressDialog.setMessage("正在初始化...");
                                progressDialog.show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
        }
    }

    @Override
    public void toastMsg(String msg) {
        Toast.makeText(SettingActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clearDataComplete() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.cancel();
                }
                startActivity(new Intent(SettingActivity.this, WelcomeActivity.class));
            }
        });

    }

    private OnDownLoadListener listener = new OnDownLoadListener() {
        @Override
        public void onDownloadFinish() {
            String path = LIB_DIR + "/FaceCompare.apk";
            installAPK(Uri.fromFile(new File(path)));
        }

        @Override
        public void onDownloadFail(String msg) {

        }
    };

    private void installAPK(Uri uri) {
    /*    Uri packageURI = Uri.parse("package:com.taisau.facecardcompare");
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        startActivity(uninstallIntent);*/
        // 通过Intent安装APK文件
        try {
            String[] command = {"chmod", "777", LIB_DIR + "/FaceCompare.apk"};
            ProcessBuilder builder1 = new ProcessBuilder(command);
            builder1.start();
            //    ShellUtils.execCommand("pm install -r mnt/internal_sd/caffe_mobile/FaceCompare.apk",false);
           /* Runtime.getRuntime().exec("sh");
            Runtime.getRuntime().exec("pm uninstall com.taisau.facecardcompare");
            Runtime.getRuntime().exec("sh pm install -r mnt/internal_sd/caffe_mobile/FaceCompare.apk");
            Runtime.getRuntime().exec("sh reboot");*/
         /*   builder2.command(new String[]{"pm install -r "+LIB_DIR + "/FaceCompare.apk"});
            builder2.start();*/
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
        Intent intents = new Intent();
        intents.setAction(Intent.ACTION_VIEW);
        // 如果不加上这句的话在apk安装完成之后点击单开会崩溃
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intents.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intents);
        //System.exit(-1);
    }

}