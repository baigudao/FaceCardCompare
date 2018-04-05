package com.taisau.facecardcompare.ui.personlist.adpter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.taisau.facecardcompare.ui.personlist.GroupInfoFragment;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class GroupPagerAdapter extends FragmentPagerAdapter {
    private GroupInfoFragment fragments[]=new GroupInfoFragment[1];
    private Context mContext;
    public GroupPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public GroupInfoFragment getItem(int position) {
        fragments[position]=new GroupInfoFragment(position);
        return fragments[position];
    }

    public GroupInfoFragment[] getFragments() {
        return fragments;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
