package com.brucewuu.android.qlcy.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.listener.OnItemClickListener;
import com.brucewuu.android.qlcy.model.GroupInfo;
import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 吴昭 on 2015-9-5.
 */
public class GroupAdapter extends RecyclerArrayAdapter<GroupInfo, GroupAdapter.MyViewHolder> {

    private Fragment mFragment;
    private WeakReference<OnItemClickListener> mOnItemClickListener;

    public GroupAdapter(@NonNull Fragment fragment) {
        super(fragment.getActivity());
        this.mFragment = fragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(getInflater().inflate(R.layout.item_group, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        GroupInfo groupInfo = getItem(position);
        Glide.with(mFragment).load(R.mipmap.converse_head5).crossFade().into(holder.ivFace);
        holder.tvName.setText(groupInfo.getGroupname());
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.iv_group_face)
        CircleImageView ivFace;

        @Bind(R.id.tv_group_name)
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
