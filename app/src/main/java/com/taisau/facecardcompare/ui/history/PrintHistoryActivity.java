package com.taisau.facecardcompare.ui.history;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.model.HistoryList;
import com.taisau.facecardcompare.ui.BaseActivity;

public class PrintHistoryActivity extends BaseActivity{
    long his_id;
    TextView time;
    ImageView cardImg,faceImg;
    TextView name,idcard,birthday,sex,address,validTime,ethnic;
    ImageView resImg;
    TextView resInfo;
  //  EditText input_mobile,input_company,input_person;
   // TextView add_vistor;
  //  RelativeLayout vistor_layout;
  //  ImageView vistor_img;
  //  TextView vistor_name_id;
 //   TextView result;
  //  Button print;
    HistoryList historyList;
    boolean isAdd=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_history);
        findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
        initData();

    }

    public void initView(){
        time=(TextView)findViewById(R.id.print_time);
        cardImg=(ImageView)findViewById(R.id.print_card_img);
        faceImg=(ImageView)findViewById(R.id.print_face_img);
        name=(TextView)findViewById(R.id.print_info_name2);
        idcard=(TextView)findViewById(R.id.print_info_card2);
        birthday=(TextView)findViewById(R.id.print_info_birthday2);
        sex=(TextView)findViewById(R.id.print_info_sex2);
        ethnic=(TextView)findViewById(R.id.print_info_ethnic2);
        address=(TextView)findViewById(R.id.print_info_address2);
        validTime=(TextView)findViewById(R.id.print_info_valid_time2);
        resImg=(ImageView)findViewById(R.id.print_compare_info_img);
        resInfo=(TextView)findViewById(R.id.print_compare_info_res);

        /*input_mobile=(EditText)findViewById(R.id.print_edit_mobile);
        input_company=(EditText)findViewById(R.id.print_edit_company);
        input_person=(EditText)findViewById(R.id.print_edit_person);
        add_vistor=(TextView)findViewById(R.id.print_add_vistor);
        vistor_layout=(RelativeLayout)findViewById(R.id.print_vistor_layout);
        vistor_img=(ImageView)findViewById(R.id.print_vistor_img);
        vistor_name_id=(TextView)findViewById(R.id.print_vistor_name_id);
        result=(TextView)findViewById(R.id.print_result);*/

    }
    public void initData(){
        his_id=getIntent().getLongExtra("his_id",0);
        historyList=FaceCardApplication.getApplication().getDaoSession().getHistoryListDao().load(his_id);
        time.setText("时间："+ DateFormat.format("yyyy-MM-dd HH:mm:ss",historyList.getTime()).toString());
        cardImg.setImageBitmap(BitmapFactory.decodeFile(historyList.getCard_path()));
        faceImg.setImageBitmap(BitmapFactory.decodeFile(historyList.getFace_path()));
        name.setText(historyList.getPerson_name());
        idcard.setText(historyList.getId_card());
        birthday.setText(historyList.getBirthday());
        ethnic.setText(historyList.getEthnic());
        sex.setText(historyList.getSex());
        address.setText(historyList.getAddress());
        validTime.setText(historyList.getValid_time());
        //result.setText(historyList.getCom_status());
        if (historyList.getCom_status().contains("通过"))
            resInfo.setText("分值:"+historyList.getScore());
        else
            resInfo.setVisibility(View.GONE);
        if (historyList.getCom_status().contains("黑名单人员"))
            resImg.setImageResource(R.mipmap.his_compare_contact_cast);
        else if (historyList.getCom_status().contains("年龄不符合要求"))
            resImg.setImageResource(R.mipmap.his_compare_age_error);
        else if (historyList.getCom_status().contains("证件过期"))
            resImg.setImageResource(R.mipmap.his_compare_out_date);
        else if (historyList.getCom_status().contains("比对失败 分值"))
            resImg.setImageResource(R.mipmap.his_compare_error_man);
       /* add_vistor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(PrintHistoryActivity.this, PrintSelectActivity.class),1001);
            }
        });*/
      /* vistor_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(PrintHistoryActivity.this, PrintSelectActivity.class),1001);
            }
        });
        findViewById(R.id.print_history_print_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PrintHistoryActivity.this, PrintActivity.class);
                intent.putExtra("his_id",his_id);
                intent.putExtra("white_id",white_id);
                intent.putExtra("visitor_mobile",input_mobile.getText().toString());
                intent.putExtra("visitor_count",input_person.getText().toString());
                intent.putExtra("visitor_company",input_company.getText().toString());
                startActivity(intent);
            }
        });*/
    }


    private long white_id=-1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
         /*   if (vistor_layout.getVisibility()==View.GONE){
                vistor_layout.setVisibility(View.VISIBLE);
                add_vistor.setVisibility(View.GONE);
            }
            white_id=data.getLongExtra("pID",0);
            Person whiteList=FaceCardApplication.getApplication().getDaoSession().getPersonDao().load(data.getLongExtra("pID",0));
            vistor_name_id.setText("姓名："+whiteList.getPerson_name()+"\nID:"+whiteList.getPerson_id());
            List<FaceList>list=FaceCardApplication.getApplication().getDaoSession().getFaceListDao().queryBuilder().where(FaceListDao.Properties.PersonId.eq(whiteList.getPerson_id())).list();
            if (list.size()>0) {
                Glide.with(PrintHistoryActivity.this).load(list.get(0).getImg_url())
                        .centerCrop()
                        .transform(new GlideCircleTransform(PrintHistoryActivity.this))
                        .dontAnimate()
                        .placeholder(R.drawable.bg_oval_gray)
                        .error(R.drawable.bg_oval_gray)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(vistor_img);
            }*/
        }
    }
}
