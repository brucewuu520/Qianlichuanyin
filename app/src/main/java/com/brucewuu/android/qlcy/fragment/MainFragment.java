package com.brucewuu.android.qlcy.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.activity.ChatActivity;
import com.brucewuu.android.qlcy.adapter.MessageAdapter;
import com.brucewuu.android.qlcy.base.LoadDataFragment;
import com.brucewuu.android.qlcy.config.AppConfig;
import com.brucewuu.android.qlcy.listener.OnItemClickListener;
import com.brucewuu.android.qlcy.util.ListUtils;
import com.brucewuu.android.qlcy.util.io.LogUtils;
import com.brucewuu.android.qlcy.widget.EmptyLayout;
import com.yzxIM.IMManager;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.listener.IConversationListener;

import java.util.List;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * Created by brucewuu on 15/9/1.
 */
public class MainFragment extends LoadDataFragment<ConversationInfo> implements View.OnClickListener,
        IConversationListener, OnItemClickListener {

    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;

    @Bind(R.id.rv_chat)
    RecyclerView mRecyclerView;

    private MessageAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fm_main;
    }

    @Override
    protected void afterViews() {
        Activity parentActivity = getActivity();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(parentActivity);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MessageAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        IMManager.getInstance(parentActivity).setConversationListener(this);
        emptyLayout.setEmptyClickListener(this);
        EventBus.getDefault().register(this);

        loadData();
    }

    @Override
    protected void loadData() {
        emptyLayout.setEmptyView(EmptyLayout.ON_LOADING);
        load();
    }

    @Override
    protected void initData(List<ConversationInfo> data) {
        emptyLayout.setEmptyView(EmptyLayout.DEFAULT);
        if (ListUtils.isEmpty(data)) {
            mAdapter.addHeaderView();
        } else {
            mAdapter.addAll(data);
        }
    }

    @Override
    protected void loadError() {
        if (mAdapter.isEmpty()) {
            emptyLayout.setEmptyView(EmptyLayout.LOAD_ERROR);
        } else {
            emptyLayout.setEmptyView(EmptyLayout.DEFAULT);
        }
    }

    @Override
    protected List<ConversationInfo> getData() throws Exception {
        return IMManager.getInstance(getActivity()).getConversationList();
    }

    @Override
    public void onCreateConversation(ConversationInfo conversationInfo) {
        LogUtils.e("onCreateConversation:" + conversationInfo);
        if (conversationInfo != null) {
            if (mAdapter.hasHeaderView())
                mAdapter.removeHeaderView();
            mAdapter.add(conversationInfo);
        }
    }

    @Override
    public void onDeleteConversation(ConversationInfo conversationInfo) {
        LogUtils.e("onDeleteConversation:" + conversationInfo);
        if (conversationInfo == null)
            return;
        if (conversationInfo == null)
            return;
        List<ConversationInfo> items = mAdapter.getAllItems();
        for (ConversationInfo info : items) {
            if (conversationInfo.getTargetId().equals(info.getTargetId())) {
                mAdapter.remove(info);
                break;
            }
        }
        if (mAdapter.isEmpty()) {
            mAdapter.addHeaderView();
        }
    }

    @Override
    public void onUpdateConversation(ConversationInfo conversationInfo) {
        LogUtils.e("onUpdateConversation:" + "---madapter==" + mAdapter);
        if (conversationInfo == null)
            return;
        List<ConversationInfo> items = mAdapter.getAllItems();
        for (int i = 0, size = items.size(); i < size; i++) {
            ConversationInfo info = items.get(i);
            if (conversationInfo.getTargetId().equals(info.getTargetId())) {
                LogUtils.e("indexof:" + i);
                info.setMsgUnRead(conversationInfo.getMsgUnRead());
                info.setConversationTitle(conversationInfo.getConversationTitle());
                info.setDraftMsg(conversationInfo.getDraftMsg());
                info.setLastTime(conversationInfo.getLastTime());
                info.setTopTime(conversationInfo.getTopTime());
                info.setIsTop(conversationInfo.getIsTop());
                mAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        loadData();
    }

    @Override
    public void onItemClick(View view, int position) {
        ConversationInfo conversationInfo = mAdapter.getItem(position);
        if (conversationInfo.getUnreadCount() > 0) {
            conversationInfo.setMsgUnRead(0);
            mAdapter.notifyItemChanged(position);
        }
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(ChatActivity.CONVERSATION, mAdapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    public void onEventMainThread(String event) {
        LogUtils.e("--onEvent:" + event);
        if (event.equals(AppConfig.CONNECT_SUCCESS)) {

            return;
        }
    }
}
