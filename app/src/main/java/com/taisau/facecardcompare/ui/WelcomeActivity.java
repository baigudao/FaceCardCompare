package com.taisau.facecardcompare.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.http.BaseRespose;
import com.taisau.facecardcompare.http.NetClient;
import com.taisau.facecardcompare.model.Device;
import com.taisau.facecardcompare.ui.main.MainActivity;
import com.taisau.facecardcompare.util.Preference;
import com.taisau.facecardcompare.widget.RegionNumberEditText;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.taisau.facecardcompare.http.Config.DEV_SIGN;

public class WelcomeActivity extends BaseActivity {

    EditText dev_no;
    RegionNumberEditText port, ip1, ip2, ip3, ip4;
    ProgressDialog dialog;
    private volatile boolean httpFlag = false;
    private String ipStr, portStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ip1 = (RegionNumberEditText) findViewById(R.id.welcom_edit_ip1);
        ip2 = (RegionNumberEditText) findViewById(R.id.welcom_edit_ip2);
        ip3 = (RegionNumberEditText) findViewById(R.id.welcom_edit_ip3);
        ip4 = (RegionNumberEditText) findViewById(R.id.welcom_edit_ip4);
        ip1.setRegion(255, 0);
        ip1.setTextWatcher();
        ip2.setRegion(255, 0);
        ip2.setTextWatcher();
        ip3.setRegion(255, 0);
        ip3.setTextWatcher();
        ip4.setRegion(255, 0);
        ip4.setTextWatcher();
        ip1.setText("183");
        ip2.setText("62");
        ip3.setText("194");
        ip4.setText("234");

        port = (RegionNumberEditText) findViewById(R.id.welcom_edit_port);
        port.setTextWatcher();
        port.setRegion(1024 * 64, 0);
        findViewById(R.id.btn_network_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("https://www.baidu.com");
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        dev_no = (EditText) findViewById(R.id.welcom_edit_account);
        findViewById(R.id.welcome_button_ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ip1.getText().toString().equals("") || ip2.getText().toString().equals("") || ip3.getText().toString().equals("") || ip4.getText().toString().equals("")) {
                    Toast.makeText(WelcomeActivity.this, "没有输入平台ip", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(ip1.getText().toString());
                    buffer.append(".");
                    buffer.append(ip2.getText().toString());
                    buffer.append(".");
                    buffer.append(ip3.getText().toString());
                    buffer.append(".");
                    buffer.append(ip4.getText().toString());
                    ipStr = buffer.toString();
                }
                if (port.getText().toString().equals("")) {
                    Toast.makeText(WelcomeActivity.this, "没有输入端口号", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    portStr = port.getText().toString();
                }
                Preference.setServerIp(ipStr);
                Preference.setServerPort(portStr);
                Preference.setServerUrl("http://" + ipStr + ":" + portStr + "/FaceNew/");
                if (dev_no.getText() != null && !dev_no.getText().toString().equals("")) {
                    dialog = ProgressDialog.show(WelcomeActivity.this, "登录", "正在登录中...", true, true, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            httpFlag = false;
                            dialog.dismiss();
                        }
                    });
                    httpFlag = true;
                    try {
                        NetClient.getInstance().getDeviceAPI().deviceLogin(Preference.getServerUrl() + "user/login", dev_no.getText().toString(), DEV_SIGN)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<BaseRespose<Device>>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                        dialog.dismiss();
                                        if (httpFlag)
                                            Toast.makeText(WelcomeActivity.this, "无法连接到网络", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onNext(BaseRespose<Device> device) {
                                        dialog.dismiss();
                                        System.out.println("loginfo:" + device.getMsg() + "  code:" + device.getCode());
                                        if (httpFlag) {
                                            if (device.isSuccess()) {
                                                Toast.makeText(WelcomeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                                Preference.setSession(device.getSession());
                                                Preference.setSid(device.getSid());
                                                Preference.setDevSno(dev_no.getText().toString());
                                                Preference.setBlackWarning("true");
                                                Preference.setAgeWarning("false");
                                                Preference.setAliveCheck("false");
                                                Preference.setScoreRank("easy");
                                                Preference.setVoiceTips("false");
                                                Preference.setNoFaceCount("20");
                                                Preference.setAgeWarningMAX("18");
                                                Preference.setAgeWarningMIN("0");
                                                Preference.setHisLastId(0);
                                                Preference.setHisFirstId(1);
                                                Preference.setCustomName(getResources().getString(R.string.tips_gonganju_tittle));
                                                Preference.setMainTittle(getResources().getString(R.string.main_tittle1));
                                                Preference.setSubTittle(getResources().getString(R.string.main_tittle2));
                                                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                                FaceCardApplication.getApplication().bindMqService();
                                            }
                                            else {
                                                if (device.getCode().equals("10002"))
                                                    new AlertDialog.Builder(WelcomeActivity.this)
                                                            .setTitle("提示")
                                                            .setMessage("设备已登录")
                                                            .show();
                                                else
                                                    new AlertDialog.Builder(WelcomeActivity.this)
                                                            .setTitle("提示")
                                                            .setMessage("无效的设备号")
                                                            .show();
                                            }
                                        }
                                    }
                                });
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(WelcomeActivity.this, "输入服务器网址非法，请检查ip地址和端口号", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(WelcomeActivity.this, "没有填写设备号", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getStringExtra("exit_flag") != null) {
            if (intent.getStringExtra("exit_flag").equals("exit_id"))
                finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (Preference.getSid() != null && !Preference.getSid().equals("") && Preference.getServerUrl() != null) {
            FaceCardApplication.getApplication().bindMqService();
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        }
    }

}
