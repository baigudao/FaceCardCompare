package com.taisau.facecardcompare.ui.setting;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.taisau.facecardcompare.model.UpgradePKG;
import com.taisau.facecardcompare.ui.setting.listener.OnDownLoadListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

import static com.taisau.facecardcompare.util.Constant.LIB_DIR;

/**
 * Created by Administrator on 2016/9/29 0029.
 */

public class DownLoadApkAction {
    private int signal=0;
    private String msg="未知错误";
    private OnDownLoadListener listener;
    private UpgradePKG info;
    private String url;
    private Context context;
    private int progress=0;
    private ProgressDialog dialog;
    private boolean isRun=false;
    public void downloadApk(Context context, String url, OnDownLoadListener listener){
        this.listener=listener;
        this.url=url;
        this.context=context;
        dialog=new ProgressDialog(context);
        dialog.setMax(100);
        dialog.setProgressStyle(
                ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("下载中...");
        dialog.setProgress(0);
        dialog.show();
       // runcheckUpdate();
        if (!isRun) {
            isRun=true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runUpdate();
                }
            }).start();
        }
    }
    private void runUpdate(){
        byte[] data = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        HttpURLConnection conn = null;
        FileOutputStream fos = null;
        try {// 为了测试取消连接
            // Thread.sleep(5000);
            // http联网可以用httpClient或java.net.url
            URL url = new URL(this.url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(1000 * 30);
            conn.setReadTimeout(1000 * 30);
            conn.setRequestMethod("POST");
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                File file = new File(LIB_DIR, "FaceCompare.apk");
                if (!file.exists())
                    file.createNewFile();
                fos = new FileOutputStream(file);
                is = conn.getInputStream();
                byte[] buffer = new byte[1024 * 8];
                int size = 0;
                long total=conn.getContentLength();
                long sum=0;
                while ((size = is.read(buffer)) >= 1) {
                    fos.write(buffer, 0, size);
                    sum += size;
                    progress = (int) (sum * 1.0f / total * 100);
                    downloadHandler.sendMessage(new Message());
                }
                fos.flush();
                signal=1;
                finishHandler.sendMessage(new Message());
            }else if (responseCode==404)
            {
                msg="平台未找到更新文件，请检查文件是否存在";
                signal=0;
                finishHandler.sendMessage(new Message());
            }
            else {
                msg = "更新失败，请联系客服人员";
                signal = 0;
                finishHandler.sendMessage(new Message());
            }
        }
        catch (SocketTimeoutException e)
        {
            e.printStackTrace();
            msg="服务器访问超时，请稍后重试";
            signal=0;
            finishHandler.sendMessage(new Message());
        }
        catch (SocketException e)
        {
            e.printStackTrace();
            msg="服务器访问失败，请检查网络";
            signal=0;
            finishHandler.sendMessage(new Message());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            msg="文件未找到，请检查文件是否存在";
            signal=0;
            finishHandler.sendMessage(new Message());
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {

            }
            try {
            if (baos != null) {baos.close();}
                conn = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Handler downloadHandler=new Handler(){
        @Override
        public void handleMessage(Message mg) {
            super.handleMessage(mg);
            //   isRun=false;
            if (progress>100)
                dialog.dismiss();
            else
            {
                dialog.setProgress(progress);
            }
        }
    };

    public Handler finishHandler=new Handler(){
        @Override
        public void handleMessage(Message mg) {
            super.handleMessage(mg);
            isRun=false;
            switch (signal)
            {
                case 0:
                    dialog.dismiss();
                    listener.onDownloadFail(msg);
                    break;
                case 1:
                    dialog.dismiss();
                    listener.onDownloadFinish();
                    break;
            }
        }
    };
}
