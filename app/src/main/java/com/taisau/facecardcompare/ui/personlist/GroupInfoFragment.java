package com.taisau.facecardcompare.ui.personlist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.model.GroupList;
import com.taisau.facecardcompare.model.GroupListDao;
import com.taisau.facecardcompare.ui.personlist.adpter.GroupListAdapter;

import java.util.List;

import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;

/**
 * Created by Administrator on 2017/4/11 0011.
 */

public class GroupInfoFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private GroupListAdapter adapter;
    private List<GroupList> list;
    private int flag = -1;
    private FamiliarRecyclerView recyclerView;
    public GroupInfoFragment() {
    }
    @SuppressLint("ValidFragment")
    public GroupInfoFragment(int sectionNumber) {
        flag = sectionNumber;
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_list, container, false);
        switch (flag) {
            case 0:
                list = FaceCardApplication.getApplication().getDaoSession().getGroupListDao().queryBuilder()
                        .where(GroupListDao.Properties.Group_type.eq("black_group"))
                        .list();
                break;
            case 1:
                list = FaceCardApplication.getApplication().getDaoSession().getGroupListDao().queryBuilder()
                        .where(GroupListDao.Properties.Group_type.eq("white_group"))
                        .list();
                break;
        }
        recyclerView = (FamiliarRecyclerView) rootView.findViewById(R.id.group_frv);
        adapter = new GroupListAdapter(this.getActivity(), list);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    public void refreshGroup() {
        switch (flag) {
            case 0:
                list = FaceCardApplication.getApplication().getDaoSession().getGroupListDao().queryBuilder()
                        .where(GroupListDao.Properties.Group_type.eq("black_group"))
                        .list();
                break;
            case 1:
                list = FaceCardApplication.getApplication().getDaoSession().getGroupListDao().queryBuilder()
                        .where(GroupListDao.Properties.Group_type.eq("white_group"))
                        .list();
                break;
        }
        adapter = new GroupListAdapter(GroupInfoFragment.this.getActivity(), list);
        recyclerView.setAdapter(adapter);
    }
}