package com.brucewuu.android.qlcy.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.config.AppConfig;
import com.brucewuu.android.qlcy.listener.OnItemClickListener;
import com.brucewuu.android.qlcy.widget.BadgeView;
import com.bumptech.glide.Glide;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.db.ConversationInfo;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by brucewuu on 15/9/1.
 */
public class MessageAdapter extends RecyclerArrayAdapter<ConversationInfo, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = -222;
    private Fragment mFragment;
    private WeakReference<OnItemClickListener> mOnItemClickListener;
    private View header;

    public MessageAdapter(@NonNull Fragment fragment) {
        super(fragment.getActivity());
        this.mFragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER)
            return new HeaderViewHolder(header);
        return new MyViewHolder(getInflater().inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) != VIEW_TYPE_HEADER) {
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            final ConversationInfo conversationInfo = getItem(position);
            int url = 0;
            if (conversationInfo.getCategoryId() == CategoryId.PERSONAL) {
                url = R.mipmap.converse_head2;
            } else if (conversationInfo.getCategoryId() == CategoryId.GROUP) {
                url = R.mipmap.converse_head5;
            } else if (conversationInfo.getCategoryId() == CategoryId.DISCUSSION) {
                url = R.mipmap.converse_head6;
            } else {
                url = R.mipmap.defalut_face;
            }
            Glide.with(mFragment).load(url).crossFade().centerCrop().into(myViewHolder.ivFace);
            myViewHolder.tvName.setText(conversationInfo.getConversationTitle());
            myViewHolder.tvLastMsg.setText(conversationInfo.getDraftMsg());
            if (conversationInfo.getMsgUnRead() > 0) {
                myViewHolder.mBadgeView.setText(String.valueOf(conversationInfo.getMsgUnRead()));
                myViewHolder.mBadgeView.show();
            } else {
                myViewHolder.mBadgeView.hide();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && header != null) {
            return VIEW_TYPE_HEADER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (header != null)
            return super.getItemCount() + 1;
        return super.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        if (position == 0 && header != null)
            return position;
        return super.getItemId(position);
    }

    @Override
    public ConversationInfo getItem(int position) {
        if (header != null)
            return super.getItem(position - 1);
        return super.getItem(position);
    }

    public void addHeaderView() {
        if (this.header == null)
            this.header = getInflater().inflate(R.layout.layout_no_message, null);
    }

    public void removeHeaderView() {
        this.header = null;
    }

    public boolean hasHeaderView() {
        return this.header != null;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
            final BadgeView badgeView = ButterKnife.findById(itemView, R.id.bv_msg_count);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(AppConfig.NO_MSG_TO_FRIENDS);
                    if (badgeView.isShown())
                        badgeView.hide();
                }
            });
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.iv_msg_face)
        CircleImageView ivFace;

        @Bind(R.id.tv_msg_name)
        TextView tvName;

        @Bind(R.id.tv_last_msg)
        TextView tvLastMsg;

        @Bind(R.id.bv_msg_count)
        BadgeView mBadgeView;

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
