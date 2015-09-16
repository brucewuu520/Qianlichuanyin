package com.brucewuu.android.qlcy.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.activity.MainActivity;
import com.brucewuu.android.qlcy.model.User;
import com.brucewuu.android.qlcy.util.DateUtil;
import com.brucewuu.android.qlcy.util.io.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.MSGTYPE;
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

        if (message.getMsgType() == MSGTYPE.MSG_DATA_TEXT) {
            holder.tvMessage.setText(message.getContent());
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.messageImage.setVisibility(View.GONE);
            holder.voliceUnread.setVisibility(View.GONE);
            holder.tvVoliceTime.setVisibility(View.GONE);
        } else if (message.getMsgType() == MSGTYPE.MSG_DATA_IMAGE) {
            holder.tvMessage.setVisibility(View.GONE);
            holder.messageImage.setVisibility(View.VISIBLE);
            holder.voliceUnread.setVisibility(View.GONE);
            holder.tvVoliceTime.setVisibility(View.GONE);
            Glide.with(mActivity).load(message.getContent()).placeholder(R.drawable.default_img)
                    .crossFade().into(holder.messageImage);
        } else if (message.getMsgType() == MSGTYPE.MSG_DATA_VOICE) {
            holder.tvMessage.setVisibility(View.GONE);
            holder.messageImage.setVisibility(View.VISIBLE);
            holder.tvVoliceTime.setVisibility(View.VISIBLE);
            holder.tvVoliceTime.setText(message.getContent() + "\"");
            if (message.getIsFromMyself())
                holder.messageImage.setImageResource(R.drawable.right_audio3);
            else
                holder.messageImage.setImageResource(R.drawable.left_audio3);

            if (message.getReadStatus() == ChatMessage.MSG_STATUS_UNREAD) {
                holder.voliceUnread.setVisibility(View.VISIBLE);
            } else {
                holder.voliceUnread.setVisibility(View.GONE);
            }
        } else if (message.getMsgType() == MSGTYPE.MSG_DATA_VIDEO){

        } else {
            holder.tvMessage.setText(message.getContent());
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.messageImage.setVisibility(View.GONE);
            holder.voliceUnread.setVisibility(View.GONE);
        }

        LogUtils.e("msg--send---status:" + message.getSendStatus());
        if (message.getIsFromMyself()) { // 自己发的信息
            if (message.getSendStatus() == ChatMessage.MSG_STATUS_INPROCESS) {
                holder.viewState.setVisibility(View.VISIBLE);
                holder.pbSend.setVisibility(View.VISIBLE);
                holder.ivFail.setVisibility(View.GONE);
            } else if (message.getSendStatus() == ChatMessage.MSG_STATUS_SUCCESS) {
                holder.viewState.setVisibility(View.GONE);
            } else if (message.getSendStatus() == ChatMessage.MSG_STATUS_FAIL) {
                holder.viewState.setVisibility(View.VISIBLE);
                holder.pbSend.setVisibility(View.GONE);
                holder.ivFail.setVisibility(View.VISIBLE);
            }
        } else { // 接收的信息
            if (message.getCategoryId() == CategoryId.PERSONAL) {
                holder.tvName.setVisibility(View.INVISIBLE);
            } else {
                holder.tvName.setVisibility(View.VISIBLE);
                holder.tvName.setText(message.getSenderId());
            }
        }

        String time = "";
        if (position > 0) {
            long lastTime = getItem(position - 1).getSendTime();
            if (message.getSendTime() - lastTime > 2 * 60 * 1000) {
                time = DateUtil.friendlyDate(message.getSendTime());
            }
        } else {
            time = DateUtil.friendlyDate(message.getSendTime());
        }

        if (!TextUtils.isEmpty(time)) {
            holder.tvTime.setText(time);
            holder.tvTime.setVisibility(View.VISIBLE);
        } else {
            holder.tvTime.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getIsFromMyself()) {
            return MYSELEF_CHAT;
        }
        return super.getItemViewType(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_message_time)
        TextView tvTime;

        @Bind(R.id.iv_face)
        CircleImageView ivFace;

        @Bind(R.id.tv_name)
        TextView tvName;

        @Bind(R.id.tv_chat_messsage)
        TextView tvMessage;

        @Bind(R.id.iv_message)
        ImageView messageImage;

        @Bind(R.id.message_volice_time)
        TextView tvVoliceTime;

        @Bind(R.id.iv_volice_unread)
        ImageView voliceUnread;

        @Bind(R.id.fl_message_state)
        View viewState;

        @Bind(R.id.pb_send)
        ProgressBar pbSend;

        @Bind(R.id.iv_send_error)
        ImageView ivFail;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
