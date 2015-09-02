package com.brucewuu.android.qlcy.activity;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.adapter.ChatListAdapter;
import com.brucewuu.android.qlcy.base.LoadDataActivity;
import com.brucewuu.android.qlcy.util.ListUtils;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by brucewuu on 15/9/2.
 */
public class ChatActivity extends LoadDataActivity<ChatMessage> {

    public static final String CONVERSATION = "conversation";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.rv_chat_list)
    RecyclerView mRecyclerView;

    private ChatListAdapter mAdapter;

    private ConversationInfo conversationInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.act_chat;
    }

    @Override
    protected void afterViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        conversationInfo = (ConversationInfo) getIntent().getSerializableExtra(CONVERSATION);
        if (conversationInfo != null) {
            //需要获取最近的20条聊天记录
            List<ChatMessage> currentMsgList = new ArrayList<>();
            currentMsgList = conversationinfo.getLastestMessages(0, 20);
            //清除会话未读消息
            conversationinfo.clearMessagesUnreadStatus();
            //清除会话聊天记录
            conversationinfo.clearMessages();
            //设置会话是否置顶
            conversationinfo.setIsTop(Boolean isTop);

        }
    }

    @Override
    protected void loadData() {
        loadData();
    }

    @Override
    protected void initData(List<ChatMessage> data) {
        if (!ListUtils.isEmpty(data)) {

            conversationInfo.clearMessagesUnreadStatus();
        }
    }

    @Override
    protected void loadError() {

    }

    @Override
    protected List<ChatMessage> getData() throws Exception {
        return conversationInfo.getLastestMessages(0, 20);
    }
}
