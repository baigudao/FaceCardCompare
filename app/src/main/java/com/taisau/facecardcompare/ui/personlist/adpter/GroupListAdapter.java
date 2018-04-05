package com.taisau.facecardcompare.ui.personlist.adpter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.model.GroupJoin;
import com.taisau.facecardcompare.model.GroupJoinDao;
import com.taisau.facecardcompare.model.GroupList;
import com.taisau.facecardcompare.ui.history.PrintStartResult;
import com.taisau.facecardcompare.ui.personlist.PersonListActivity;
import com.taisau.facecardcompare.util.ColorUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class GroupListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final List<GroupList> list;
    private Context ctx;
    private GroupList group;
    private boolean isAddPrint=false;

    private PrintStartResult startResult;
    public void setAddPrint(boolean addPrint,PrintStartResult startResult) {
        this.startResult=startResult;
        isAddPrint = addPrint;
    }

    public  GroupListAdapter(Context ctx, List<GroupList> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(ctx, R.layout.item_sample_manage, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        SampleViewHolder vh = new SampleViewHolder(view);
        vh.setIsRecyclable(true);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        SampleViewHolder vh = (SampleViewHolder) holder;
        int c = Color.parseColor(ColorUtil.getRandomColor());
        group = list.get(position);
        vh.groupName.setText(group.getGroup_name());
        List<GroupJoin>joins=FaceCardApplication.getApplication().getDaoSession().getGroupJoinDao().queryBuilder().where(GroupJoinDao.Properties.Group_id.eq(group.getGroup_id())).list();
        vh.groupCount.setText(joins.size()+"人");
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(ctx,"pos:"+position+" id:"+list.get(position).getGroup_id(),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.setClass(ctx, PersonListActivity.class);
                if (isAddPrint)
                    intent.putExtra("isAdd",true);
                else
                    intent.putExtra("isAdd",false);
                if (group.getGroup_type().equals("black_group"))
                    intent.putExtra("flag",0);
                if (group.getGroup_type().equals("white_group"))
                    intent.putExtra("flag",1);
                intent.putExtra("group_name",list.get(position).getGroup_name());
                intent.putExtra("group_id",list.get(position).getGroup_id());
                if (isAddPrint)
                    startResult.doStartResult(intent);
                else
                    ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SampleViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView groupCount;
        public SampleViewHolder(View itemView) {
            super(itemView);
            groupName=(TextView) itemView.findViewById(R.id.tv_group_name);
            groupCount=(TextView)itemView.findViewById(R.id.tv_group_count);
        }
    }
}
