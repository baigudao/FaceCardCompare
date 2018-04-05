package com.taisau.facecardcompare.ui.personlist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.model.GroupJoin;
import com.taisau.facecardcompare.model.GroupJoinDao;
import com.taisau.facecardcompare.model.Person;
import com.taisau.facecardcompare.model.PersonDao;
import com.taisau.facecardcompare.ui.BaseActivity;
import com.taisau.facecardcompare.ui.history.PrintStartResult;
import com.taisau.facecardcompare.ui.personlist.adpter.PersonListAdapter;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class PersonListActivity extends BaseActivity implements PrintStartResult{
    EditText search;
    ImageView search_img;
    private static long group_id;
    private static int flag;
    RecyclerView recyclerView;
    PersonListAdapter adapter;
    List dataList;
    List showDataList;
    LinearLayoutManager manager;
    int lastVisibleItem;
    private static int showLastItem;
    private boolean isAddPrint;
    private long lastInput;
    private long firstInput;
    private boolean isPrepare=false;
    private String search_content="";
    private Handler handler=new Handler();

    @Override
    public void doStartResult(Intent intent) {
        startActivityForResult(intent,1001);
    }

    //滑动监听类
    RecyclerView.OnScrollListener listener=new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState ==RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 ==adapter.getItemCount()) {
                if (showLastItem==dataList.size())
                {
                    return;
                }
                adapter.changeMoreStatus(1,null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (showLastItem+10<dataList.size()) {
                            showLastItem = showLastItem + 10;
                            showDataList=dataList.subList(0,showLastItem);
                            adapter.changeMoreStatus(0,showDataList);
                        }
                        else {
                            showLastItem = dataList.size();
                            adapter.changeMoreStatus(3,dataList);
                        }
                        recyclerView.scrollToPosition(adapter.getItemCount()-8);
                    }
                }, 1000);
            }
        }

        @Override
        public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView,dx, dy);
            lastVisibleItem =manager.findLastVisibleItemPosition();
            if (lastVisibleItem + 1 ==adapter.getItemCount()) {
                if (showLastItem==dataList.size())
                {
                    return;
                }
                adapter.changeMoreStatus(1,null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (showLastItem+10<dataList.size()) {
                            showLastItem = showLastItem + 10;
                            showDataList=dataList.subList(0,showLastItem);
                            adapter.changeMoreStatus(0,showDataList);
                        }
                        else {
                            showLastItem = dataList.size();
                            adapter.changeMoreStatus(3,dataList);
                            showDataList=dataList;
                        }
                        recyclerView.scrollToPosition(adapter.getItemCount()-8);
                    }
                }, 1000);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);
        group_id=getIntent().getLongExtra("group_id",0);
        flag=getIntent().getIntExtra("flag",-1);
        isAddPrint=getIntent().getBooleanExtra("isAdd",false);
        findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
        initView();
        search=(EditText)findViewById(R.id.personList_search_edit);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("after change");
                lastInput=System.currentTimeMillis();
                search_content=s.toString();
                if (!isPrepare) {
                    System.out.println("start prepare");
                    isPrepare=true;
                    firstInput=System.currentTimeMillis();
                    new Thread(searchCatcher).start();
                }
            }
        });

    }

    public void initData(){
        switch (flag)
        {
            case 0:
                QueryBuilder<Person>builderB=FaceCardApplication.getApplication().getDaoSession().getPersonDao().queryBuilder();
                builderB.join(GroupJoin.class, GroupJoinDao.Properties.Person_id)
                        .where(GroupJoinDao.Properties.Group_id.eq(group_id));
                dataList=builderB.list();
                break;
            case 1:
                QueryBuilder<Person>builderW=FaceCardApplication.getApplication().getDaoSession().getPersonDao().queryBuilder();
                builderW.join(GroupJoin.class, GroupJoinDao.Properties.Person_id)
                        .where(GroupJoinDao.Properties.Group_id.eq(group_id));
                dataList=builderW.list();
                break;
        }
       /* for (int i=0;i<100;i++)
        {
            dataList.add(dataList.get(0));
        }*/
        if (dataList.size()<=20)
        {
            showDataList=dataList;
            showLastItem=dataList.size();
        }
        else
        {
            showDataList=dataList.subList(0,20);
            showLastItem=20;
        }
    }

    public void initView()
    {
        recyclerView=(RecyclerView)findViewById(R.id.personList_list);
        manager=new LinearLayoutManager(PersonListActivity.this);
        recyclerView.setLayoutManager(manager);
        adapter=new PersonListAdapter(PersonListActivity.this,showDataList,isAddPrint,this);
        recyclerView.setAdapter(adapter);
        if (dataList.size()>20)
            recyclerView.addOnScrollListener(listener);
    }

    public void doSearch(String search_content){
        switch (flag)
        {
            case 0:
                QueryBuilder<Person>builderB=FaceCardApplication.getApplication().getDaoSession().getPersonDao().queryBuilder();
                builderB.join(GroupJoin.class, GroupJoinDao.Properties.Person_id)
                        .where(GroupJoinDao.Properties.Group_id.eq(group_id),builderB.or(PersonDao.Properties.Person_name.like("%"+search_content+"%"),PersonDao.Properties.Id_card.like("%"+search_content+"%")));
                dataList=builderB.list();
                break;
            case 1:
                QueryBuilder<Person>builderW=FaceCardApplication.getApplication().getDaoSession().getPersonDao().queryBuilder();
                builderW.join(GroupJoin.class, GroupJoinDao.Properties.Person_id)
                        .where(GroupJoinDao.Properties.Group_id.eq(group_id),builderW.or(PersonDao.Properties.Person_name.like("%"+search_content+"%"),PersonDao.Properties.Id_card.like("%"+search_content+"%")));
                dataList=builderW.list();
                break;
        }
        if (dataList.size()<=20)
        {
            showDataList=dataList;
            showLastItem=dataList.size();
        }
        else
        {
            showDataList=dataList.subList(0,20);
            showLastItem=20;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                initView();
                isPrepare=false;
            }
        });
    }

    Runnable searchCatcher=new Runnable() {
        @Override
        public void run() {
         synchronized (this)
         {
             try {
                 while (isPrepare) {
                     wait(2000);
                     if (lastInput - firstInput <=1500) {
                         //do search
                         doSearch(search_content);
                     } else {
                         firstInput = lastInput;
                     }
                 }
             } catch (InterruptedException e) {

             }
         }
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1001&&resultCode==RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
