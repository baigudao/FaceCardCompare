package com.taisau.facecardcompare.ui.setting;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;

import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.ui.setting.adapter.AdsAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdsSelectActivity extends AppCompatActivity {
    private GridView ads_grid;
    private AdsAdapter adsAdapter;
    private Button save;
    private List<String>adsPathList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.ads_select_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("广告图片管理");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adsPathList=new ArrayList<String>();
        for (int i=0;i<100;i++)
            adsPathList.add(Environment.getExternalStorageDirectory().getAbsolutePath()+"/123test.jpg");
        ads_grid=(GridView)findViewById(R.id.ads_grid);
        adsAdapter=new AdsAdapter(adsPathList,AdsSelectActivity.this);
        System.out.println(adsAdapter.getCount());
        ads_grid.setAdapter(adsAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ads_manage, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
