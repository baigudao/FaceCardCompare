package com.taisau.facecardcompare.ui.personlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.ui.BaseActivity;
import com.taisau.facecardcompare.widget.GlideCircleTransform;

public class PersonInfoActivity extends BaseActivity {
    private static boolean isAdd = false;
    ImageView add_btn;
    ImageView avatar;
    TextView name;
    TextView idCard;
    TextView mobile;
    TextView qq;
    TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        isAdd = getIntent().getBooleanExtra("isAdd", false);
        System.out.println("isAdd:" + isAdd);
        initView();
        initData();
    }

    public void initView() {
        add_btn = (ImageView) findViewById(R.id.personinfo_add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("pID", getIntent().getLongExtra("person_id", 0));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        if (!isAdd)
            add_btn.setVisibility(View.GONE);
        avatar = (ImageView) findViewById(R.id.personinfo_face_img);
        name = (TextView) findViewById(R.id.personinfo_name);
        idCard = (TextView) findViewById(R.id.personinfo_idcard);
        mobile = (TextView) findViewById(R.id.personinfo_social_mobile2);
        qq = (TextView) findViewById(R.id.personinfo_social_qq2);
        email = (TextView) findViewById(R.id.personinfo_social_email2);
    }

    public void initData() {
        name.setText("姓名：" + getIntent().getStringExtra("name"));
        idCard.setText("身份证号：" + getIntent().getStringExtra("id_card"));
        mobile.setText(getIntent().getStringExtra("mobile"));
        qq.setText(getIntent().getStringExtra("qq"));
        email.setText(getIntent().getStringExtra("email"));
        if (getIntent().getStringExtra("img_url") != null && !getIntent().getStringExtra("img_url").equals(""))
            Glide.with(PersonInfoActivity.this).load(getIntent().getStringExtra("img_url"))
                    .centerCrop()
                    .transform(new GlideCircleTransform(PersonInfoActivity.this))
                    .dontAnimate()
                    .placeholder(R.drawable.bg_oval_gray)
                    .error(R.drawable.bg_oval_gray)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(avatar);
        else
            avatar.setVisibility(View.INVISIBLE);
    }


}
