package com.brucewuu.android.qlcy.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.brucewuu.android.qlcy.R;
import com.yzxIM.data.db.ChatMessage;

/**
 * Created by brucewuu on 15/9/2.
 */
public class ChatListAdapter extends RecyclerArrayAdapter<ChatMessage, ChatListAdapter.MyViewHolder> {

    private static final int MYSELEF_CHAT = -111;

    private AppCompatActivity mActivity;

    public ChatListAdapter(AppCompatActivity activity) {
        super(activity);
        this.mActivity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MYSELEF_CHAT)
            return new MyViewHolder(getInflater().inflate(R.layout.item_chat_myself, parent, false));
        else
            return new MyViewHolder(getInflater().inflate(R.layout.item_chat_other, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getIsFromMyself()) {
            return MYSELEF_CHAT;
        }
        return super.getItemViewType(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
