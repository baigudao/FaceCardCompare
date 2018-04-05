package com.taisau.facecardcompare.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.model.GroupList;
import com.taisau.facecardcompare.model.GroupListDao;
import com.taisau.facecardcompare.ui.BaseActivity;
import com.taisau.facecardcompare.ui.personlist.adpter.GroupListAdapter;

import java.util.List;

import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;

public class PrintSelectActivity extends BaseActivity implements PrintStartResult {
    private GroupListAdapter adapter;
    private List<GroupList> list;
    private int flag = 1;
    private FamiliarRecyclerView recyclerView;

    @Override
    public void doStartResult(Intent intent) {
        startActivityForResult(intent,1001);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_select);
        list = FaceCardApplication.getApplication().getDaoSession().getGroupListDao().queryBuilder()
                .where(GroupListDao.Properties.Group_type.eq("white_group"))
                .list();
        recyclerView = (FamiliarRecyclerView)findViewById(R.id.group_frv);
        adapter = new GroupListAdapter(PrintSelectActivity.this, list);
        recyclerView.setAdapter(adapter);
        adapter.setAddPrint(true,this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.print_select_bar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("选择访问人");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

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


}
