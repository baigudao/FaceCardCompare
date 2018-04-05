package com.taisau.facecardcompare.ui.personlist.adpter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.model.FaceList;
import com.taisau.facecardcompare.model.FaceListDao;
import com.taisau.facecardcompare.model.Person;
import com.taisau.facecardcompare.ui.history.PrintStartResult;
import com.taisau.facecardcompare.ui.personlist.PersonInfoActivity;
import com.taisau.facecardcompare.widget.GlideCircleTransform;

import java.util.List;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class PersonListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_FOOTER = 1, TYPE_ITEM = 0, TYPE_NOMORE = 2;
    public static int load_more_status = 0;
    public Context context;
    private boolean isAddPrint;
    private PrintStartResult startResult;
    List list;

    public PersonListAdapter(Context context, List<String> list, boolean isAddPrint, PrintStartResult startResult) {
        this.isAddPrint = isAddPrint;
        this.startResult = startResult;
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //进行判断显示类型，来创建返回不同的View
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.content_person_list_layout, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            PersonInfoView personInfoView = new PersonInfoView(view);
            return personInfoView;
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.recyle_load_more, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            LoadMore loadMore = new LoadMore(view);
            return loadMore;
        } else if (viewType == TYPE_NOMORE) {
            View view = LayoutInflater.from(context).inflate(R.layout.recyle_load_nomore, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            NoMore noMore = new NoMore(view);
            return noMore;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PersonInfoView) {
            final Person person = (Person) list.get(position);
            PersonInfoView infoView = (PersonInfoView) holder;
            ((PersonInfoView) holder).name.setText("姓名：" + person.getPerson_name() + "\nID：" + person.getPerson_id());
            String imgUrl = "";
            List<FaceList> fl = FaceCardApplication.getApplication().getDaoSession().getFaceListDao().queryBuilder().where(FaceListDao.Properties.PersonId.eq(person.getPerson_id())).list();
            if (fl.size() > 0)
                imgUrl = fl.get(0).getImg_url();
            if (imgUrl != null && !imgUrl.equals(""))
                Glide.with(context).load(imgUrl).centerCrop().transform(new GlideCircleTransform(context)).dontAnimate().placeholder(R.drawable.bg_oval_gray).error(R.drawable.bg_oval_gray)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(infoView.avatar);
            else
                infoView.avatar.setVisibility(View.INVISIBLE);
            final String finalImgUrl = imgUrl;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PersonInfoActivity.class);
                    intent.putExtra("person_id", person.getPerson_id());
                    intent.putExtra("name", person.getPerson_name());
                    intent.putExtra("img_url", finalImgUrl);
                    intent.putExtra("id_card", person.getId_card());
                    if (isAddPrint)
                        intent.putExtra("isAdd", true);
                    else
                        intent.putExtra("isAdd", false);
                    if (person.getMobile() != null && !person.getMobile().equals(""))
                        intent.putExtra("mobile", person.getMobile());
                    else
                        intent.putExtra("mobile", "未填写");
                    if (person.getQq() != null && !person.getQq().equals(""))
                        intent.putExtra("qq", person.getQq());
                    else
                        intent.putExtra("qq", "未填写");
                    if (person.getEmail() != null && !person.getEmail().equals(""))
                        intent.putExtra("email", person.getEmail());
                    else
                        intent.putExtra("email", "未填写");
                    if (isAddPrint)
                        startResult.doStartResult(intent);
                    else
                        context.startActivity(intent);
                }
            });
        }
        if (holder instanceof LoadMore) {
            LoadMore loadMore = (LoadMore) holder;
            switch (load_more_status) {
                case 0:
                    loadMore.itemView.setVisibility(View.GONE);
                    break;
                case 1:
                    loadMore.itemView.setVisibility(View.VISIBLE);
                    break;
            }
        }
        if (holder instanceof NoMore) {
            NoMore noMore = (NoMore) holder;
        }
    }


    @Override
    public int getItemCount() {

        if (list.size() >= 20) {
            return list.size() + 1;
        } else {
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

    public class PersonInfoView extends RecyclerView.ViewHolder {
        TextView name;
        ImageView avatar;

        public PersonInfoView(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.person_list_name_id);
            avatar = (ImageView) itemView.findViewById(R.id.person_list_avatar);
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

    public void changeMoreStatus(int status, List list) {
        if (list != null)
            this.list = list;
        load_more_status = status;
        notifyDataSetChanged();
    }
}
