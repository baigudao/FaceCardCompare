package com.taisau.facecardcompare.ui.reg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.GFace;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.ui.WelcomeActivity;
import com.taisau.facecardcompare.util.Preference;

import java.util.Hashtable;

import static com.taisau.facecardcompare.util.Constant.LIB_DIR;

public class RegisterActivity extends AppCompatActivity {
    private static RegisterActivity mInstance;

    public static RegisterActivity getWel() {
        return mInstance;
    }

    EditText  key;
    ProgressDialog dialog;
    ImageView QR_img;
    boolean httpFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        setContentView(R.layout.activity_register);
        GFace.loadModel(LIB_DIR+"/face_GFace6/dnew.dat",LIB_DIR+"/face_GFace6/anew.dat",LIB_DIR+"/face_GFace7/db.dat",LIB_DIR+"/face_GFace7/p.dat");
        System.out.println(GFace.GetSn("TS"));
        key = (EditText) findViewById(R.id.reg_edit_key);
        int width1 = 500; // 图像宽度
        int height1 = 500; // 图像高度
        Bitmap bitmap;
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(GFace.GetSn("TS"),
                    BarcodeFormat.QR_CODE, width1, height1);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        System.out.println("matrix w:"+matrix.getWidth()+" h:"+matrix.getHeight());
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }
        bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        QR_img = (ImageView) findViewById(R.id.reg_QR_img);
        QR_img.setImageBitmap(bitmap);
        findViewById(R.id.reg_button_ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, WelcomeActivity.class));
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
        //System.out.println("wel resume");
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (Preference.getSid() != null && !Preference.getSid().equals("") && Preference.getServerUrl() != null/* && Preference.getMachineKey() != null*/) {
              startActivity(new Intent(RegisterActivity.this, com.taisau.facecardcompare.ui.main.MainActivity.class));
        }
    }
}
