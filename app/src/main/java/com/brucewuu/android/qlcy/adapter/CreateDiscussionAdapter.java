package com.brucewuu.android.qlcy.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.listener.OnItemClickListener;
import com.brucewuu.android.qlcy.model.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 吴昭 on 2015-9-5.
 */
public class CreateDiscussionAdapter extends RecyclerArrayAdapter<User, CreateDiscussionAdapter.MyViewHolder> {

    private AppCompatActivity mActivity;
    private WeakReference<OnItemClickListener> mOnItemClickListener;

    public CreateDiscussionAdapter(AppCompatActivity activity) {
        super(activity);
        this.mActivity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(getInflater().inflate(R.layout.item_create_discussion, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        User user = getItem(position);
        Glide.with(mActivity).load(user.getPortraituri()).placeholder(R.mipmap.defalut_face)
                .error(R.mipmap.defalut_face)
                .crossFade()
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        holder.ivFace.setImageDrawable(resource);
                    }
                });
        holder.tvName.setText(user.getNickname());
        if (user.getResult() != null && user.getResult().equals("0"))
            holder.ivCheck.setSelected(true);
        else
            holder.ivCheck.setSelected(false);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.iv_check)
        ImageView ivCheck;

        @Bind(R.id.iv_friends_face)
        CircleImageView ivFace;

        @Bind(R.id.tv_friends_name)
        TextView tvName;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null && mOnItemClickListener.get() != null)
                mOnItemClickListener.get().onItemClick(v, getAdapterPosition());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = new WeakReference<>(listener);
    }
}
