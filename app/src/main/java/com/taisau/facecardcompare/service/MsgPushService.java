package com.taisau.facecardcompare.service;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.google.gson.Gson;
import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.http.AutoLogin;
import com.taisau.facecardcompare.http.BaseRespose;
import com.taisau.facecardcompare.http.NetClient;
import com.taisau.facecardcompare.listener.MsgPushListener;
import com.taisau.facecardcompare.model.FaceList;
import com.taisau.facecardcompare.model.GroupJoin;
import com.taisau.facecardcompare.model.GroupJoinDao;
import com.taisau.facecardcompare.model.GroupList;
import com.taisau.facecardcompare.model.GroupListDao;
import com.taisau.facecardcompare.model.Person;
import com.taisau.facecardcompare.model.PersonDao;
import com.taisau.facecardcompare.thread.MsgPushThreadManager;
import com.taisau.facecardcompare.util.Constant;
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
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Subscriber;
import rx.schedulers.Schedulers;


/**
 * Created by ds  on 2017/1/4 13:24
 */

public class MsgPushService extends Service implements MsgPushListener {
    private Intent msgIntent = null;
    private PendingIntent msgPendingIntent = null;
    private NotificationManager manager;
    private boolean is_close_sound;
    private final IBinder binder = new MQBinder();
    private Handler handler = new Handler();

    public void startMsgService() {
        MsgPushThreadManager.getInstance().runMsgPushService(this, this);
    }

    public void stopMsgService() {
        MsgPushThreadManager.getInstance().stopMsgPushThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }


    public class MQBinder extends Binder {
        public MsgPushService getService() {
            return MsgPushService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MsgPushThreadManager.getInstance().stopMsgPushThread();
        return super.onUnbind(intent);
    }

    //   private int failWhiteAdd=0;
    //  List<FaceList> whiteFL=new ArrayList<FaceList>();
    @Override
    public void OnMsgPushFinish(String str) {
        try {
            JSONObject object = new JSONObject(str);
            String path = object.getString("path");
            JSONObject params = object.getJSONObject("params");
            JSONObject personId = params.getJSONObject("personId");
            if (personId.has("add")) {
                String add = personId.getString("add");
                if (!add.equals("")) {
                    if (!path.contains(".txt")) {
                        String[] addArray = add.split(",");
                        StringBuffer personIdList = new StringBuffer();
                        for (String content : addArray) {
                            personIdList.append(content.substring(2, content.length()));
                            if (!content.equals(addArray[addArray.length - 1]))
                                personIdList.append(",");

                        }
                        String sid = Preference.getSid();
                        String session = Preference.getSession();
                        doAddBlackPerson(path, sid, session, personIdList.toString(), addArray);
                    } else {
                        downloadPersonFile(path);
                    }
                }
            }
            if (personId.has("remove")) {
                String remove = personId.getString("remove");
                if (!remove.equals("")) {
                    String[] removeArray = remove.split(",");
                    for (String content : removeArray) {
                        String type = content.substring(0, 1);
                        String idcard = content.substring(2, content.length());
                        if (type.equals("2")) {
                            List<Person> list = FaceCardApplication.getApplication().getDaoSession().getPersonDao().queryBuilder().where(PersonDao.Properties.Id_card.eq(idcard)).build().list();
                            for (Person person : list) {
                                List<GroupJoin> joins = FaceCardApplication.getApplication().getDaoSession().getGroupJoinDao().queryBuilder().where(GroupJoinDao.Properties.Person_id.eq(person.getPerson_id())).list();
                                for (GroupJoin join : joins) {
                                    List<GroupList> groups = FaceCardApplication.getApplication().getDaoSession().getGroupListDao().queryBuilder().where(GroupListDao.Properties.Group_id.eq(join.getGroup_id())).list();
                                    for (GroupList group : groups) {
                                        List<GroupJoin> groupJoins = FaceCardApplication.getApplication().getDaoSession().getGroupJoinDao().queryBuilder()
                                                .where(GroupJoinDao.Properties.Group_id.eq(group.getGroup_id()), GroupJoinDao.Properties.Person_id.notEq(person.getPerson_id())).list();
                                        if (groupJoins.size() == 0)
                                            FaceCardApplication.getApplication().getDaoSession().getGroupListDao().delete(group);
                                    }
                                    FaceCardApplication.getApplication().getDaoSession().getGroupJoinDao().delete(join);
                                }
                                FaceCardApplication.getApplication().getDaoSession().getPersonDao().delete(person);
                            /*     List<FaceList> face=FaceCardApplication.getApplication().getDaoSession().getFaceListDao().queryBuilder().where(FaceListDao.Properties.PersonId.eq(person.getPerson_id())).list();
                            for (FaceList ff:face)
                            {
                                if (ff.getImg_path()!=null)
                                    FileUtils.deleteFile(ff.getImg_path());
                                if (ff.getFea_path()!=null)
                                    FileUtils.deleteFile(ff.getFea_path());
                                FaceCardApplication.getApplication().getDaoSession().getFaceListDao().delete(ff);
                            }
                          //  List<>*/
                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnMsgFail(String msg) {

    }

    @Override
    public void OnMsgGetServiceError(String msg) {
        MsgPushThreadManager.getInstance().stopMsgPushThread();
        stopForeground(true);
        stopSelf();
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MsgPushThreadManager.getInstance().stopMsgPushThread();
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
                        byte[] buf = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;
                        String SDPath = Constant.LIB_DIR;
                        try {
                            is = response.body().byteStream();
                            long total = response.body().contentLength();
                            File file = new File(SDPath, "data.txt");
                            if (file.exists()) {
                                file.delete();
                            }
                            fos = new FileOutputStream(file);
                            long sum = 0;
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                                sum += len;
//                                int progress = (int) (sum * 1.0f / total * 100);
//                                System.out.println("下载person数据：" + progress);
                            }
                            fos.flush();
                            savePersonData(file);
                        } catch (Exception e) {
                            e.printStackTrace();
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
                Toast.makeText(MsgPushService.this, "无法连接到数据平台，无法下载模版", Toast.LENGTH_SHORT).show();
                System.out.println("无法下载模版");
            }
        });
    }

    public void savePersonData(final File file) {
        System.out.println(Thread.currentThread());
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
                //pp.getPerson_id() 为空说明数据库没存储
                //pp.getGroupList() 获取gg列表
                // 每个gg.getGroup_id()  不空就更新，空就插入   FaceCardApplication.getApplication().getDaoSession().getGroupListDao().update(gg);insert(gg)
                List<FaceList> faceLists = new ArrayList<>();
                List<GroupList> groupLists = new ArrayList<>();
                List<Long> groupListsIds = new ArrayList<>();
                List<GroupJoin> groupJoins = new ArrayList<>();
                List<Person> personArrayList = new ArrayList<>();
//                for (Person pp : personList) {
//                    if (FaceCardApplication.getApplication().getDaoSession().getPersonDao().load(pp.getPerson_id()) == null) {
//                        pp.setSex(pp.getSex().equals("2") ? "女" : "男");
//                        List<GroupList> gl = pp.getGroupList();
//                        for (GroupList gg : gl) {
//                            if (FaceCardApplication.getApplication().getDaoSession().getGroupListDao().load(gg.getGroup_id()) != null)
//                                FaceCardApplication.getApplication().getDaoSession().getGroupListDao().update(gg);
//                            else
//                                FaceCardApplication.getApplication().getDaoSession().getGroupListDao().insert(gg);
//                            GroupJoin join = new GroupJoin();
//                            join.setId(null);
//                            join.setGroup_id(gg.getGroup_id());
//                            join.setPerson_id(pp.getPerson_id());
//                            FaceCardApplication.getApplication().getDaoSession().getGroupJoinDao().insertOrReplace(join);
//                        }
//                        if (FaceCardApplication.getApplication().getDaoSession().getPersonDao().load(pp.getPerson_id()) != null)
//                            FaceCardApplication.getApplication().getDaoSession().getPersonDao().update(pp);
//                        else
//                            FaceCardApplication.getApplication().getDaoSession().getPersonDao().insert(pp);
//                    }
//                }
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
                FaceCardApplication.getApplication().getDaoSession().getPersonDao().insertOrReplaceInTx(personArrayList);
                FaceCardApplication.getApplication().getDaoSession().getFaceListDao().insertOrReplaceInTx(faceLists);
                FaceCardApplication.getApplication().getDaoSession().getGroupJoinDao().insertOrReplaceInTx(groupJoins);
                FaceCardApplication.getApplication().getDaoSession().getGroupListDao().insertOrReplaceInTx(groupLists);
                System.out.println("下发Person  数据库保存完成= ");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MsgPushService.this, "获取到下发人员模版", Toast.LENGTH_SHORT).show();
                        System.out.println("获取到下发人员模版");
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void doAddBlackPerson(final String path, final String sid, final String session, final String personIdList, final String[] addArray) {
        NetClient.getInstance().getPersonListAPI().getSendPerson(path, sid, session, personIdList, "" + 0, "" + addArray.length)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<BaseRespose<Person>>() {
                    @Override
                    public void onCompleted() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MsgPushService.this, "获取到下发人员模版", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MsgPushService.this, "无法连接到数据平台，无法下载模版", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNext(BaseRespose<Person> personBaseRespose) {
                        //System.out.println("res pp:"+personBaseRespose.getMsg());
                        //whiteFL.clear();
                         /*       File whiteFea=new File(LIB_DIR+"/white_fea");
                                File whiteImg=new File(LIB_DIR+"/white_img");*/
                            /*    if (!whiteFea.exists())
                                    whiteFea.mkdir();
                                if (!whiteImg.exists())
                                    whiteImg.mkdir();*/
                        //       failWhiteAdd=0;

                        if (!personBaseRespose.isSuccess()) {
                            if (personBaseRespose.getCode().equals("40004")) {
                                AutoLogin.autologin(new AutoLogin.AutoLoginListener() {
                                    @Override
                                    public void onLoginFinish(int code) {
                                        doAddBlackPerson(path, sid, session, personIdList, addArray);
                                    }
                                });
                                return;
                            }
                        }
                        List<Person> listP = personBaseRespose.getList();
                        if (listP != null) {
                            for (Person pp : listP) {
                                if (FaceCardApplication.getApplication().getDaoSession().getPersonDao().load(pp.getPerson_id()) == null) {
                                    //  boolean isWhite=false;
                                    pp.setSex(pp.getSex().equals("2") ? "女" : "男");
                                    List<GroupList> gl = pp.getGroupList();
                                   /* List<FaceList>fl=pp.getFaceList();
                                    for(FaceList ff:fl)
                                    {
                                        ff.setPersonId(pp.getPerson_id());
                                        FaceCardApplication.getApplication().getDaoSession().getFaceListDao().insertOrReplace(ff);
                                    }*/
                                    for (GroupList gg : gl) {
                                        if (FaceCardApplication.getApplication().getDaoSession().getGroupListDao().load(gg.getGroup_id()) != null)
                                            FaceCardApplication.getApplication().getDaoSession().getGroupListDao().update(gg);
                                        else
                                            FaceCardApplication.getApplication().getDaoSession().getGroupListDao().insert(gg);
                                        GroupJoin join = new GroupJoin();
                                        join.setId(null);
                                        join.setGroup_id(gg.getGroup_id());
                                        join.setPerson_id(pp.getPerson_id());
                                        FaceCardApplication.getApplication().getDaoSession().getGroupJoinDao().insertOrReplace(join);
                                        //添加白名单模版
                               /*         if(gg.getGroup_type().equals("white_group")&&!isWhite)
                                        {
                                            isWhite=true;
                                        }*/
                                    }
                              /*      if (isWhite) {
                                        for (FaceList ff:fl)
                                            if (FaceCardApplication.getApplication().getDaoSession().getFaceListDao().load(ff.getFace_id())!=null)
                                                whiteFL.add(ff);
                                    }*/
                                    if (FaceCardApplication.getApplication().getDaoSession().getPersonDao().load(pp.getPerson_id()) != null)
                                        FaceCardApplication.getApplication().getDaoSession().getPersonDao().update(pp);
                                    else
                                        FaceCardApplication.getApplication().getDaoSession().getPersonDao().insert(pp);
                                }
                            }
                        }
                    }
                });
    }
  /*  public void addWhiteFea(final List<FaceList> fl){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                for (final FaceList ff:fl)
                {
                    File file = null;
                    try {
                        i++;
                        System.out.println("add white "+i);
                        Response<ResponseBody> responsebb = NetClient.getInstance().getPersonListAPI().getImg(ff.getImg_url()).execute();
                        byte[] data = responsebb.body().bytes();
                        if(data!=null){
                            System.out.println("have data size:"+data.length);
                            String path = LIB_DIR + "/white_img/" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + "_http_img.jpg";
                            ff.setImg_path(path);
                            file = new File(path);
                            if (!file.exists())
                                file.createNewFile();
                            Bitmap bb= BitmapFactory.decodeByteArray(data,0,data.length);
                            ImgUtils.getUtils().saveBitmap(bb,path);
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
                                System.out.println("特征值为空");
                                file.delete();
                                failWhiteAdd++;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MsgPushService.this," 添加PersonID为："+ff.getPersonId()+"白名单特征值失败。第"+failWhiteAdd
                                                +"次添加失败\n失败原因：无法提取模版图片特征值，请确认模版图片有效",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                        else {
                            failWhiteAdd++;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MsgPushService.this," 添加PersonID为："+ff.getPersonId()+"白名单特征值失败。第"+failWhiteAdd
                                            +"次添加失败\n失败原因：无法下载到模版图片，请确认设备连接到数据平台",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                    catch (NullPointerException e)
                    {
                        e.printStackTrace();
                        if (file!=null)
                            file.delete();
                        failWhiteAdd++;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MsgPushService.this," 添加PersonID为："+ff.getPersonId()+"白名单特征值失败。第"+failWhiteAdd
                                        +"次添加失败\n失败原因：无法提取模版图片特征值，没有加载到模版图片",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    catch (Exception e) {
                        failWhiteAdd++;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MsgPushService.this," 添加PersonID为："+ff.getPersonId()+"白名单特征值失败。第"+failWhiteAdd
                                        +"次添加失败\n失败原因：网络连接失败，请检查网络",Toast.LENGTH_LONG).show();
                            }
                        });
                        e.printStackTrace();
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MsgPushService.this,"更新成功",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
    Handler handler=new Handler();*/
}
