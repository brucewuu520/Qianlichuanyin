package com.brucewuu.android.qlcy.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.yzxIM.data.db.DiscussionInfo;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 吴昭 on 2015-9-5.
 */
public class DiscusssionAdapter extends RecyclerArrayAdapter<DiscussionInfo, DiscusssionAdapter.MyViewHolder> {

    private Fragment mFragment;
    private WeakReference<OnItemClickListener> mOnItemClickListener;

    public DiscusssionAdapter(@NonNull Fragment fragment) {
        super(fragment.getActivity());
        this.mFragment = fragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(getInflater().inflate(R.layout.item_discussion, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        DiscussionInfo discussionInfo = getItem(position);
        Glide.with(mFragment).load(R.mipmap.converse_head6).crossFade().into(holder.ivFace);
        holder.tvName.setText(discussionInfo.getDiscussionName());
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.iv_discussion_face)
        CircleImageView ivFace;

        @Bind(R.id.tv_discussion_name)
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
