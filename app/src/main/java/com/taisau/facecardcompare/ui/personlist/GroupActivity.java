package com.taisau.facecardcompare.ui.personlist;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.http.AutoLogin;
import com.taisau.facecardcompare.http.BaseRespose;
import com.taisau.facecardcompare.http.NetClient;
import com.taisau.facecardcompare.model.FaceList;
import com.taisau.facecardcompare.model.GroupJoin;
import com.taisau.facecardcompare.model.GroupList;
import com.taisau.facecardcompare.model.Person;
import com.taisau.facecardcompare.ui.BaseActivity;
import com.taisau.facecardcompare.ui.personlist.adpter.GroupPagerAdapter;
import com.taisau.facecardcompare.util.Constant;
import com.taisau.facecardcompare.util.FeaUtils;
import com.taisau.facecardcompare.util.ImgUtils;
import com.taisau.facecardcompare.util.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static com.taisau.facecardcompare.util.Constant.LIB_DIR;

/**
 * GroupFragment 容器
 */
public class GroupActivity extends BaseActivity implements AutoLogin.AutoLoginListener {
    LinearLayout tab1, tab2;
    ImageView tab_img1, tab_img2;
    TextView tab_text1, tab_text2;
    GroupPagerAdapter adapter;
    ViewPager viewPager;
    ImageView refreshList;
    ProgressDialog dialog;
    List<FaceList> whiteFL = new ArrayList<FaceList>();
    GroupInfoFragment whiteFragment, blackFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    /*    //tab 黑名单
        tab_img1=(ImageView)findViewById(R.id.group_tab_black_img);
        tab_img1.setImageResource(R.mipmap.blacklist);
        tab_text1=(TextView)findViewById(R.id.group_tab_black_text);
        tab_text1.setTextColor(getResources().getColor(R.color.color_132D4E));

        //tab白名单
        tab_img2=(ImageView)findViewById(R.id.group_tab_white_img);
        tab_img2.setImageResource(R.mipmap.whitelist_unclick);
        tab_text2=(TextView)findViewById(R.id.group_tab_white_text);
        tab_text2.setTextColor(getResources().getColor(R.color.color_737373));*/

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        adapter = new GroupPagerAdapter(getSupportFragmentManager(), GroupActivity.this);
        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.group_pager);
        viewPager.setAdapter(adapter);

       /* viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position)
                {
                    case 0:
                        tab_img1.setImageResource(R.mipmap.blacklist);
                        tab_text1.setTextColor(getResources().getColor(R.color.color_132D4E));
                        tab_img2.setImageResource(R.mipmap.whitelist_unclick);
                        tab_text2.setTextColor(getResources().getColor(R.color.color_737373));
                        break;
                    case 1:
                        tab_img2.setImageResource(R.mipmap.whitelist);
                        tab_text2.setTextColor(getResources().getColor(R.color.color_132D4E));
                        tab_img1.setImageResource(R.mipmap.blacklist_unclick);
                        tab_text1.setTextColor(getResources().getColor(R.color.color_737373));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

     /*   findViewById(R.id.group_tab_black).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        findViewById(R.id.group_tab_white).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        */
        findViewById(R.id.group_bar_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(GroupActivity.this)
                        .setTitle("更新名单")
                        .setMessage("是否更新名单？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GroupActivity.this.dialog = ProgressDialog.show(GroupActivity.this, "更新名单", "正在获取人员名单数据...");
//                                dorefreshGroupList();
                                getAllPersonList();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            }
        });
    }

    /**
     * 手动更新全部人员
     */
    void getAllPersonList() {
        System.out.println("开始请求 txt 文件路径");
        NetClient.getInstance().getPersonListAPI().getUpdatePersonInfo(Preference.getServerUrl() + "person/getUpdatePersonInfo",
                Preference.getSid(), Preference.getSession()).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                dialog.setMessage("正在存储人员名单...");
                System.out.println("onResponse:" + response.toString());
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String resStr = response.body().string();
                                System.out.println("onResponse:" + resStr);
                                JSONObject jsonObject = new JSONObject(resStr);
                                String time = jsonObject.getString("time");
                                String url = jsonObject.getString("url");
                                if (url.contains(".txt")) {
//                                    Thread.sleep(Integer.valueOf(time));
                                    downloadPersonFile(url);
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                            Toast.makeText(GroupActivity.this, "返回错误，请重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (JSONException | IOException /*| InterruptedException*/ e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        Toast.makeText(GroupActivity.this, "返回错误，请重试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("onFailure :" + t.toString() + ",currentThread = " + Thread.currentThread());
                dialog.dismiss();
                Toast.makeText(GroupActivity.this, "无法连接到数据平台，无法更新", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void downloadPersonFile(String url) {
        NetClient.getInstance().getPersonListAPI().getPersonFile(url).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, final Response<ResponseBody> response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("开始写");
                        InputStream is = null;
//                        StringBuilder sb = new StringBuilder();
                        byte[] buf = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;
                        String SDPath = Constant.LIB_DIR;
                        try {
                            is = response.body().byteStream();
                            long total = response.body().contentLength();
                            File file = new File(SDPath, "group_data.txt");
                            if (file.exists()) {
                                file.delete();
                            }
                            fos = new FileOutputStream(file);
                            long sum = 0;
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
//                                sb.append(new String(buf,"utf-8"));
//                                sum += len;
//                                int progress = (int) (sum * 1.0f / total * 100);
//                                System.out.println("下载person数据：" + progress);
                            }
                            fos.flush();
                            savePersonData(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(GroupActivity.this, "数据传输中断", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } finally {
                            try {
                                if (is != null)
                                    is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                if (fos != null)
                                    fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                System.out.println("onFailure    Throwable：" + t);
                Toast.makeText(GroupActivity.this, "无法连接到数据平台，无法更新", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void savePersonData(final File file) {
        System.out.println("开始读取并保存到数据库");
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(file));
            String str = null;

            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        savePerson(sb);
    }
    private void savePerson(StringBuilder sb){
        System.out.println("sb = " + sb.toString());
        try {
            JSONObject object = new JSONObject(sb.toString());
            boolean isSuccess = object.getBoolean("success");
            int count = object.getInt("count");
            JSONArray list = object.getJSONArray("list");
            ArrayList<Person> personList = new ArrayList<>();
            if (list.length() > 0) {
                Gson gson = new Gson();
                for (int i = 0; i < list.length(); i++) {
                    Person person = gson.fromJson(list.get(i).toString(), Person.class);
                    personList.add(person);
                }
                System.out.println("personList.size()= " + personList.size());
                FaceCardApplication.getApplication().getDaoSession().getPersonDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getGroupJoinDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getGroupListDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getFaceListDao().deleteAll();
                System.out.println("数据库清空完毕 ");

                List<FaceList> faceLists = new ArrayList<>();
                List<GroupList> groupLists = new ArrayList<>();
                List<Long> groupListsIds = new ArrayList<>();
                List<GroupJoin> groupJoins = new ArrayList<>();
                List<Person> personArrayList = new ArrayList<>();
                for (Person pp : personList) {
                    pp.setSex(pp.getSex().equals("2") ? "女" : "男");
                    List<GroupList> gl = pp.getGroupList();
                    List<FaceList> fl = pp.getFaceList();
                    for (FaceList ff : fl) {
                        ff.setPersonId(pp.getPerson_id());
//                        FaceCardApplication.getApplication().getDaoSession().insertOrReplace(ff);
                        faceLists.add(ff);
                    }
                    for (GroupList gg : gl) {
//                        FaceCardApplication.getApplication().getDaoSession().insertOrReplace(gg);
                        if (!groupListsIds.contains(gg.getGroup_id())) {
                            groupListsIds.add(gg.getGroup_id());
                            groupLists.add(gg);
                        }
                        GroupJoin join = new GroupJoin();
                        join.setId(null);
                        join.setGroup_id(gg.getGroup_id());
                        join.setPerson_id(pp.getPerson_id());
//                        FaceCardApplication.getApplication().getDaoSession().getGroupJoinDao().insert(join);
                        groupJoins.add(join);
                    }
//                    FaceCardApplication.getApplication().getDaoSession().getPersonDao().insertOrReplace(pp);
                    personArrayList.add(pp);
                }
                System.out.println("数据分类完毕  开始批量导入数据库");
                FaceCardApplication.getApplication().getDaoSession().getPersonDao().insertInTx(personArrayList);
                FaceCardApplication.getApplication().getDaoSession().getFaceListDao().insertInTx(faceLists);
                FaceCardApplication.getApplication().getDaoSession().getGroupJoinDao().insertInTx(groupJoins);
                FaceCardApplication.getApplication().getDaoSession().getGroupListDao().insertInTx(groupLists);

                System.out.println("数据库保存完成= ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.getFragments()[0].refreshGroup();
                        Toast.makeText(GroupActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GroupActivity.this, "存储异常", Toast.LENGTH_SHORT).show();
                }
            });
        } finally {
            dialog.dismiss();
        }
    }

    void dorefreshGroupList() {
        NetClient.getInstance().getPersonListAPI().getSendPerson(Preference.getServerUrl() + "person/getSendPerson", Preference.getSid(), Preference.getSession())
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<BaseRespose<Person>>() {
                    @Override
                    public void onCompleted() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                adapter.getFragments()[0].refreshGroup();
                                Toast.makeText(GroupActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(GroupActivity.this, "更新失败"/*+e.getMessage()*/, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNext(BaseRespose<Person> personBaseRespose) {
                        //System.out.println("res pp:"+personBaseRespose.getMsg());
                        FaceCardApplication.getApplication().getDaoSession().getPersonDao().deleteAll();
                        FaceCardApplication.getApplication().getDaoSession().getGroupJoinDao().deleteAll();
                        FaceCardApplication.getApplication().getDaoSession().getGroupListDao().deleteAll();
                        FaceCardApplication.getApplication().getDaoSession().getFaceListDao().deleteAll();
                        // FaceCardApplication.getApplication().getDaoSession().getWhiteFeaDao().deleteAll();

                   /*     FileUtils.deleteDirectory(LIB_DIR+"/white_fea");
                        FileUtils.deleteDirectory(LIB_DIR+"/white_img");
                        File whiteFea=new File(LIB_DIR+"/white_fea");
                        File whiteImg=new File(LIB_DIR+"/white_img");*/

                      /*  if (!whiteFea.exists())
                            whiteFea.mkdir();
                        if (!whiteImg.exists())
                            whiteImg.mkdir();*/

                        failWhiteAdd = 0;

                        List<Person> listP = personBaseRespose.getList();
                        for (Person pp : listP) {
                            boolean isWhite = false;
                            pp.setSex(pp.getSex().equals("2") ? "女" : "男");
                            List<GroupList> gl = pp.getGroupList();
                            List<FaceList> fl = pp.getFaceList();
                            for (FaceList ff : fl) {
                                ff.setPersonId(pp.getPerson_id());
                                FaceCardApplication.getApplication().getDaoSession().insertOrReplace(ff);
                            }
                            for (GroupList gg : gl) {

                                FaceCardApplication.getApplication().getDaoSession().insertOrReplace(gg);
                                GroupJoin join = new GroupJoin();
                                join.setId(null);
                                join.setGroup_id(gg.getGroup_id());
                                join.setPerson_id(pp.getPerson_id());
                                FaceCardApplication.getApplication().getDaoSession().getGroupJoinDao().insert(join);
                                //添加白名单模版
                             /*   if(gg.getGroup_type().equals("white_group")&&!isWhite)
                                {
                                    isWhite=true;
                                }*/
                            }
                        /*   if (isWhite)
                            {
                                whiteFL.addAll(fl);
                            }*/
                            FaceCardApplication.getApplication().getDaoSession().getPersonDao().insertOrReplace(pp);
                        }
                      /*  System.out.println("whiteFL size:"+whiteFL.size());
                        addWhiteFea(whiteFL);*/

                    }
                });

    }

    public Handler handler = new Handler();
    public static int failWhiteAdd = 0;

    public void addWhiteFea(final List<FaceList> fl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                for (final FaceList ff : fl) {
                    File file = null;
                    try {
                        i++;
                        Response<ResponseBody> responsebb = NetClient.getInstance().getPersonListAPI().getImg(ff.getImg_url()).execute();
                        byte[] data = responsebb.body().bytes();
                        if (data != null) {
                            String path = LIB_DIR + "/white_img/" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + "_http_img.jpg";
                            ff.setImg_path(path);
                            file = new File(path);
                            if (!file.exists())
                                file.createNewFile();
                            Bitmap bb = BitmapFactory.decodeByteArray(data, 0, data.length);
                            ImgUtils.getUtils().saveBitmap(bb, path);
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 400, 600, false);
                            ImgUtils.getUtils().saveBitmap(bitmap, path);
                            float bitFea[] = ImgUtils.getUtils().getImgFea(bitmap);
                            if (bitFea != null) {
                                String whiteFea = LIB_DIR + "/white_fea/" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + "_white_fea.txt";
                                FeaUtils.saveFea(whiteFea, bitFea);
                                ff.setFea_path(whiteFea);
                                FaceCardApplication.getApplication().getDaoSession().getFaceListDao().insertOrReplace(ff);
                            }
                            //System.out.println("White Fea size:" + FaceCardApplication.getApplication().getDaoSession().getWhiteFeaDao().loadAll().size());
                            // readFea(LIB_DIR+"/card_fea/fea.txt");
                            else {
                                file.delete();
                                failWhiteAdd++;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupActivity.this, " 添加PersonID为：" + ff.getPersonId() + "白名单特征值失败。第" + failWhiteAdd
                                                + "次添加失败\n失败原因：无法提取模版图片特征值，请确认模版图片有效", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } else {
                            failWhiteAdd++;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(GroupActivity.this, " 添加PersonID为：" + ff.getPersonId() + "白名单特征值失败。第" + failWhiteAdd
                                            + "次添加失败\n失败原因：无法下载到模版图片，请确认设备连接到数据平台", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        if (file != null)
                            file.delete();
                        failWhiteAdd++;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupActivity.this, " 添加PersonID为：" + ff.getPersonId() + "白名单特征值失败。第" + failWhiteAdd
                                        + "次添加失败\n失败原因：无法提取模版图片特征值，没有加载到模版图片", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (Exception e) {
                        failWhiteAdd++;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupActivity.this, " 添加PersonID为：" + ff.getPersonId() + "白名单特征值失败。第" + failWhiteAdd
                                        + "次添加失败\n失败原因：网络连接失败，请检查网络", Toast.LENGTH_LONG).show();
                            }
                        });
                        e.printStackTrace();
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        adapter.getFragments()[0].refreshGroup();
                        adapter.getFragments()[1].refreshGroup();
                        Toast.makeText(GroupActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onLoginFinish(int code) {
        if (code == -1) {
            Toast.makeText(GroupActivity.this, "无效的设备号", Toast.LENGTH_SHORT).show();
            //   Preference.clearUser();
           /* Intent intent=new Intent(GroupActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();*/
        } else if (code == 1) {
            dorefreshGroupList();
        }
    }
}
