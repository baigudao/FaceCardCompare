package com.taisau.facecardcompare.ui.history.adpter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.model.HistoryList;
import com.taisau.facecardcompare.ui.history.PrintHistoryActivity;
import com.taisau.facecardcompare.widget.GlideCircleTransform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_FOOTER=1,TYPE_ITEM=0,TYPE_NOMORE=2;
    public static int load_more_status=0;
    public Context context;
    List list=new ArrayList();
    public HistoryAdapter(Context context,List<String> list) {
        load_more_status=0;
        this.list = list;
        this.context=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //进行判断显示类型，来创建返回不同的View
        if(viewType==TYPE_ITEM){

            View view=LayoutInflater.from(context).inflate(R.layout.content_history_list_layout,parent,false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            HistoryView historyView=new HistoryView(view);

            return historyView;
        }else if(viewType==TYPE_FOOTER){
            View view=LayoutInflater.from(context).inflate(R.layout.recyle_load_more,parent,false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            LoadMore loadMore=new LoadMore(view);
            return loadMore;
        }
        else if (viewType==TYPE_NOMORE)
        {
            View view=LayoutInflater.from(context).inflate(R.layout.recyle_load_nomore,parent,false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            NoMore noMore=new NoMore(view);
            return noMore;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HistoryView)
        {
            final HistoryList person=(HistoryList) list.get(position);
            HistoryView historyView=(HistoryView)holder;
            historyView.name.setText("姓名："+person.getPerson_name());
            historyView.idcard.setText("身份证号："+person.getId_card());
            historyView.result.setText(person.getCom_status());
            historyView.time.setText("时间："+ DateFormat.format("yyyy-MM-dd HH:mm:ss",person.getTime()));
            Glide.with(context).load(new File(person.getFace_path())).centerCrop().transform(new GlideCircleTransform(context)).dontAnimate().placeholder(R.drawable.bg_oval_gray).error(R.drawable.bg_oval_gray)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(historyView.avatar);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, PrintHistoryActivity.class);
                    intent.putExtra("his_id",person.getId());
                    context.startActivity(intent);
                }
            });
        }
        if (holder instanceof LoadMore) {
            LoadMore loadMore = ( LoadMore) holder;
            switch (load_more_status) {
                case 0:
                    loadMore.itemView.setVisibility(View.GONE);
                    break;
                case 1:
                    loadMore.itemView.setVisibility(View.VISIBLE);
                    break;
            }
        }
        if (holder instanceof NoMore)
        {
            NoMore noMore=(NoMore)holder;
        }
    }


    @Override
    public int getItemCount() {

        if (list.size()>=20) {
            return list.size() + 1;
        }
        else
        {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position+1== getItemCount()&&list.size()>=20) {
            if (load_more_status==3)
                return TYPE_NOMORE;
            else
                return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public class HistoryView extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name;
        TextView idcard;
        TextView result;
        TextView time;
        public HistoryView(View itemView) {
            super(itemView);
            avatar=(ImageView)itemView.findViewById(R.id.history_list_avatar);
            name=(TextView)itemView.findViewById(R.id.history_list_name);
            idcard=(TextView)itemView.findViewById(R.id.history_list_card_id);
            result=(TextView)itemView.findViewById(R.id.history_list_result);
            time=(TextView)itemView.findViewById(R.id.history_list_time);
        }
    }

    public class LoadMore extends RecyclerView.ViewHolder {
        public LoadMore(View itemView) {
            super(itemView);
        }
    }

    public class NoMore extends RecyclerView.ViewHolder {
        public NoMore(View itemView) {
            super(itemView);
        }
    }

    public void changeMoreStatus(int status,List list){
        if (list!=null)
            this.list=list;
        load_more_status=status;
        notifyDataSetChanged();
    }
}
