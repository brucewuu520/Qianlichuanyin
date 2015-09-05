package com.brucewuu.android.qlcy.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brucewuu.android.qlcy.AppContext;
import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.activity.ChatActivity;
import com.brucewuu.android.qlcy.activity.CreateDiscussionActivity;
import com.brucewuu.android.qlcy.adapter.DiscusssionAdapter;
import com.brucewuu.android.qlcy.base.LoadDataFragment;
import com.brucewuu.android.qlcy.listener.OnItemClickListener;
import com.brucewuu.android.qlcy.util.ListUtils;
import com.brucewuu.android.qlcy.util.UIHelper;
import com.brucewuu.android.qlcy.widget.EmptyLayout;
import com.yzxIM.IMManager;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DiscussionInfo;
import com.yzxIM.listener.DiscussionGroupCallBack;
import com.yzxtcp.data.UcsReason;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 吴昭 on 2015-9-5.
 */
public class DiscussionFragment extends LoadDataFragment<DiscussionInfo> implements DiscussionGroupCallBack,
        OnItemClickListener, View.OnClickListener {

    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;

    @Bind(R.id.btn_create_discussion)
    AppCompatButton btnCreateDiscussion;

    @Bind(R.id.rv_discussion)
    RecyclerView mRecyclerView;

    private DiscusssionAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fm_discussion;
    }

    @Override
    protected void afterViews() {
        Activity parentActivity = getActivity();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(parentActivity);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        if (mAdapter == null) {
            mAdapter = new DiscusssionAdapter(this);
            mRecyclerView.setAdapter(mAdapter);
            loadData();
        } else {
            mRecyclerView.setAdapter(mAdapter);
        }
        mAdapter.setOnItemClickListener(this);
        IMManager.getInstance(parentActivity).setDiscussionGroup(this);
    }

    @Override
    protected void loadData() {
        emptyLayout.setEmptyView(EmptyLayout.ON_LOADING);
        load();
    }

    @Override
    protected void initData(List<DiscussionInfo> data) {
        emptyLayout.setEmptyView(EmptyLayout.DEFAULT);
        if (ListUtils.isEmpty(data)) {
            btnCreateDiscussion.setVisibility(View.VISIBLE);
        } else {
            btnCreateDiscussion.setVisibility(View.GONE);
            mAdapter.addAll(data);
        }
    }

    @Override
    protected void loadError() {
        emptyLayout.setEmptyView(EmptyLayout.LOAD_ERROR);
        emptyLayout.setOnClickListener(this);
    }

    @Override
    protected List<DiscussionInfo> getData() throws Exception {
        return IMManager.getInstance(AppContext.getInstance()).getAllDiscussionInfos();
    }

    @Override
    public void onCreateDiscussion(UcsReason ucsReason, DiscussionInfo discussionInfo) {
        UIHelper.showToast("--onCreateDiscussion");
        if (ucsReason.getReason() == 0) { // 创建成功
            if (discussionInfo != null) {
                if (mAdapter.isEmpty()) {
                    emptyLayout.setEmptyView(EmptyLayout.DEFAULT);
                    btnCreateDiscussion.setVisibility(View.GONE);
                }
                mAdapter.insert(discussionInfo, 0);
            }
        }
    }

    @Override
    public void onDiscussionAddMember(UcsReason ucsReason) {

    }

    @Override
    public void onDiscussionDelMember(UcsReason ucsReason) {

    }

    @Override
    public void onQuiteDiscussion(UcsReason ucsReason) {

    }

    @Override
    public void onClick(View v) {

    }

    @OnClick(R.id.btn_create_discussion)
    void createDiscussion() {
        redirectTo(CreateDiscussionActivity.class);
    }

    @Override
    public void onItemClick(View view, int position) {
        DiscussionInfo discussionInfo = mAdapter.getItem(position);
        ConversationInfo conversationInfo = IMManager.getInstance(AppContext.getInstance()).getConversation(discussionInfo.getDiscussionId());
        if (conversationInfo == null) {
            UIHelper.showToast("该讨论组已被解散~");
            return;
        }
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(ChatActivity.CONVERSATION, conversationInfo);
        startActivity(intent);
    }
}
