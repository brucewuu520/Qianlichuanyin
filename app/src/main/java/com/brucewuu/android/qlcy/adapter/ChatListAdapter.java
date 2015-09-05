package com.brucewuu.android.qlcy.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.activity.MainActivity;
import com.brucewuu.android.qlcy.model.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.db.ChatMessage;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by brucewuu on 15/9/2.
 */
public class ChatListAdapter extends RecyclerArrayAdapter<ChatMessage, ChatListAdapter.MyViewHolder> {

    private static final int MYSELEF_CHAT = -111;

    private AppCompatActivity mActivity;
    private User user;

    public ChatListAdapter(AppCompatActivity activity) {
        super(activity);
        this.mActivity = activity;
        this.user = MainActivity.getUser();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MYSELEF_CHAT)
            return new MyViewHolder(getInflater().inflate(R.layout.item_chat_myself, parent, false));
        else
            return new MyViewHolder(getInflater().inflate(R.layout.item_chat_other, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ChatMessage message = getItem(position);
        if (message.getIsFromMyself()) {
            Glide.with(mActivity).load(user.getPortraituri()).placeholder(R.mipmap.defalut_face)
                    .error(R.mipmap.defalut_face)
                    .crossFade()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            holder.ivFace.setImageDrawable(resource);
                        }
                    });
        } else if (message.getCategoryId() == CategoryId.PERSONAL) {
            Glide.with(mActivity).load(R.mipmap.converse_head2)
                    .crossFade()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            holder.ivFace.setImageDrawable(resource);
                        }
                    });
        } else if (message.getCategoryId() == CategoryId.GROUP) {
            Glide.with(mActivity).load(R.mipmap.converse_head5)
                    .crossFade()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            holder.ivFace.setImageDrawable(resource);
                        }
                    });
        } else {
            Glide.with(mActivity).load(R.mipmap.converse_head6)
                    .crossFade()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            holder.ivFace.setImageDrawable(resource);
                        }
                    });
        }

        holder.tvTime.setText(String.valueOf(message.getSendTime()));
        holder.tvMessage.setText(message.getContent());
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getIsFromMyself()) {
            return MYSELEF_CHAT;
        }
        return super.getItemViewType(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_received_time)
        TextView tvTime;

        @Bind(R.id.iv_chat_face)
        CircleImageView ivFace;

        @Bind(R.id.tv_chat_messsage)
        TextView tvMessage;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
