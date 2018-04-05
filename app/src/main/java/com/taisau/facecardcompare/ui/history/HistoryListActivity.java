package com.taisau.facecardcompare.ui.history;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rabbitmq.client.AMQP;
import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.model.HistoryList;
import com.taisau.facecardcompare.model.HistoryListDao;
import com.taisau.facecardcompare.ui.BaseActivity;
import com.taisau.facecardcompare.ui.history.adpter.HistoryAdapter;
import com.taisau.facecardcompare.util.ExcelException;
import com.taisau.facecardcompare.util.ExcelUtils2;
import com.taisau.facecardcompare.util.FileUtils;
import com.taisau.facecardcompare.util.ImgUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class HistoryListActivity extends BaseActivity {
    RecyclerView recyclerView;
    HistoryAdapter adapter;
    List dataList = new ArrayList();
    List showDataList = new ArrayList();
    LinearLayoutManager manager;
    int lastVisibleItem;
    EditText search;
    TextView showSize, allSize;
    private Handler handler = new Handler();
    private long lastInput;
    private long firstInput;
    private boolean isPrepare = false;
    private String search_content = "";
    private static int showLastItem;
    private ProgressDialog dialog;
    private TextView tvDataTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initView();
        initData();
        requestWritePermission();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == WRITE_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("ds>>>", "Write Permission Failed");
                Toast.makeText(this, "You must allow permission write external storage to your mobile device.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    private static final int WRITE_PERMISSION = 0x01;
    private void requestWritePermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
      /*  if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_PERMISSION);
        }*/
    }
    @Override
    protected void onResume() {
        super.onResume();
//        if (search.getText().toString().equals("")) {
//            initData();
//        }
    }


    public void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataList = FaceCardApplication.getApplication().getDaoSession().getHistoryListDao().queryBuilder().orderDesc(HistoryListDao.Properties.Time).list();
                if (dataList.size() <= 20) {
                    showDataList = dataList;
                    showLastItem = dataList.size();
                } else {
                    showDataList = dataList.subList(0, 20);
                    showLastItem = 20;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateData();
                    }
                });
            }
        }).start();


    }

    private void updateData() {
        if (showDataList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            tvDataTips.setVisibility(View.GONE);
            adapter = new HistoryAdapter(HistoryListActivity.this, showDataList);
            recyclerView.setAdapter(adapter);
        } else {
            tvDataTips.setVisibility(View.VISIBLE);
            tvDataTips.setText("没有任何内容");
            recyclerView.setVisibility(View.GONE);
        }
        if (dataList.size() > 20)
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
                    System.out.println("on scroll state change");

                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                        if (showLastItem == dataList.size()) {
                            return;
                        }
                        adapter.changeMoreStatus(1, null);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (showLastItem + 10 < dataList.size()) {
                                    showLastItem = showLastItem + 10;
                                    showDataList = dataList.subList(0, showLastItem);
                                    adapter.changeMoreStatus(0, showDataList);
                                } else {
                                    showLastItem = dataList.size();
                                    adapter.changeMoreStatus(3, dataList);
                                    showDataList = dataList;
                                }
                                recyclerView.scrollToPosition(adapter.getItemCount() - 8);
                                allSize.setText("共有" + dataList.size() + "条记录");
                                showSize.setText("当前已展示" + showDataList.size() + "条记录");
                            }
                        }, 1000);
                    }
                }

                @Override
                public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                    lastVisibleItem = manager.findLastVisibleItemPosition();
                    if (lastVisibleItem + 1 == adapter.getItemCount()) {
                        if (showLastItem == dataList.size()) {
                            return;
                        }
                        adapter.changeMoreStatus(1, null);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (showLastItem + 10 < dataList.size()) {
                                    showLastItem = showLastItem + 10;
                                    showDataList = dataList.subList(0, showLastItem);
                                    adapter.changeMoreStatus(0, showDataList);
                                } else {
                                    showLastItem = dataList.size();
                                    adapter.changeMoreStatus(3, dataList);
                                    showDataList = dataList;
                                }
                                recyclerView.scrollToPosition(adapter.getItemCount() - 8);
                                allSize.setText("共有" + dataList.size() + "条记录");
                                showSize.setText("当前已展示" + showDataList.size() + "条记录");
                            }
                        }, 1000);
                    }
                }

            });
        allSize.setText("共有" + dataList.size() + "条记录");
        showSize.setText("当前已展示" + showDataList.size() + "条记录");
    }

    public void initView() {
        tvDataTips = (TextView) findViewById(R.id.activity_history_null);
        findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.history_to_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.activity_history_list);
        manager = new LinearLayoutManager(HistoryListActivity.this);
        recyclerView.setLayoutManager(manager);

        allSize = (TextView) findViewById(R.id.activity_history_all_size);
        allSize.setText("共有" + dataList.size() + "条记录");
        showSize = (TextView) findViewById(R.id.activity_history_show_size);
        showSize.setText("当前已展示" + showDataList.size() + "条记录");

        findViewById(R.id.history_down_to_excel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(HistoryListActivity.this).setTitle("导出数据")
                        .setMessage("导出EXCEL表格数据到U盘中")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                String path= /*Environment.getExternalStorageDirectory().getAbsolutePath()*/
//                                "/mnt/usb_storage/USB_DISK2/udisk0"+"/比对历史记录.xls";
                                String path = null;
                                File storage = new File("/mnt/usb_storage");
                                if (storage.exists()) {
                                    File[] files = storage.listFiles();
                                    for (File file : files) {
                                        if (file.isDirectory()) {// /mnt/usb_storage/USB_DISK2
                                            for (File file1 : file.listFiles()) {
                                                if (file1.isDirectory()) {// /mnt/usb_storage/USB_DISK2/udisk0
                                                    path = file1.getAbsolutePath();
                                                }
                                            }
                                        }
                                    }
                                }
                                if (path != null) {//如果是读卡器，文件夹存在，可读，但无法写
                                    if (dataList.size() == 0) {
                                        Toast.makeText(HistoryListActivity.this, "未发现历史记录数据", Toast.LENGTH_SHORT).show();
                                    } else {
                                        HistoryListActivity.this.dialog = ProgressDialog.show(HistoryListActivity.this, "数据导出", "正在导出数据，请稍等...", true, false);
                                        exportHistoryList(path);
                                    }
                                } else {
                                    Toast.makeText(HistoryListActivity.this, "未检测到 U 盘，无法导出数据", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        search = (EditText) findViewById(R.id.activity_history_search_edit);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lastInput = System.currentTimeMillis();
                search_content = s.toString();
                if (!isPrepare) {
                    isPrepare = true;
                    firstInput = System.currentTimeMillis();
                    new Thread(searchCatcher).start();
                }
            }
        });
    }

    Runnable searchCatcher = new Runnable() {
        @Override
        public void run() {
            synchronized (this) {
                try {
                    while (isPrepare) {
                        wait(2000);
                        if (lastInput - firstInput <= 1500) {
                            doSearch(search_content);
                        } else {
                            firstInput = lastInput;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void doSearch(String search_content) {
        QueryBuilder builder1 = FaceCardApplication.getApplication().getDaoSession().getHistoryListDao().queryBuilder();
        builder1.where(builder1.or(HistoryListDao.Properties.Person_name.like("%" + search_content + "%"), HistoryListDao.Properties.Id_card.like("%" + search_content + "%")))
                .orderDesc(HistoryListDao.Properties.Time);
        dataList = builder1.list();
        if (dataList.size() <= 20) {
            showDataList = dataList;
            showLastItem = dataList.size();
        } else {
            showDataList = dataList.subList(0, 20);
            showLastItem = 20;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
//                initView();
                updateData();
                isPrepare = false;
            }
        });
    }

    /**
     * 导出历史记录到 U 盘
     */
    private void exportHistoryList(final String usbPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LinkedHashMap<String, String> historyMap = new LinkedHashMap<>();
                historyMap.put("person_name", "姓名");
                historyMap.put("id_card", "身份证号");
                historyMap.put("sex", "性别");
                historyMap.put("ethnic", "民族");
                historyMap.put("birthday", "生日");
                historyMap.put("address", "住址");
                historyMap.put("card_release_org", "发行单位");
                historyMap.put("valid_time", "有效期");
                historyMap.put("face_path", "现场照");
                historyMap.put("card_path", "身份证照片");
                historyMap.put("time", "抓拍时间");
                historyMap.put("com_status", "结果");
                historyMap.put("score", "分数");
                try {
                    String cardPngFilePath, facePngFilePath;
                    Log.d("EXPORT", "历史记录数量 historyList = " + dataList.size());
                    List<HistoryList> list = dataList;
                    int size = list.size();
                    int writeNum = size / 50 + 1;
                    for (int i = 0; i < writeNum; i++) {
                        int from = i * 50;
                        int to = i * 50 + 50 > size ? size : i * 50 + 50;
                        List<HistoryList> historys = list.subList(from, to);
                        Log.d("EXPORT", "historys: " + historys.size() + "," + historys.get(0).getId());
                        for (HistoryList his : historys) {//将jpg转为png导出
                            cardPngFilePath = his.getCard_path().replace("jpg", "png");
                            facePngFilePath = his.getFace_path().replace("jpg", "png");
                            ImgUtils.convertJpgToPng(his.getCard_path(), cardPngFilePath, 50);
                            ImgUtils.convertJpgToPng(his.getFace_path(), facePngFilePath, 50);
                            his.setCard_path(cardPngFilePath);
                            his.setFace_path(facePngFilePath);
                        }
                        ExcelUtils2.listToExcel(historys, historyMap, "比对历史", 0,
                                new File(usbPath + "/比对历史记录" + from + "-" + to + ".xls"));
                        for (HistoryList his : historys) {//导出完后，删除png图片,并把路径改回原本的路径
                            FileUtils.deleteFile(his.getCard_path());
                            FileUtils.deleteFile(his.getFace_path());
                            his.setCard_path(his.getCard_path().replace("png", "jpg"));
                            his.setFace_path(his.getFace_path().replace("png", "jpg"));
                        }
                    }
                    Log.d("EXPORT", "导出完毕");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            HistoryListActivity.this.dialog.dismiss();
                            Toast.makeText(HistoryListActivity.this, "导出 比对历史记录.xls 完成", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (ExcelException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            HistoryListActivity.this.dialog.dismiss();
                            Toast.makeText(HistoryListActivity.this, "导出 比对历史记录.xls 失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }
}
