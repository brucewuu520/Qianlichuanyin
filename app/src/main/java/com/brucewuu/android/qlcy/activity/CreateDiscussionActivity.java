package com.brucewuu.android.qlcy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.brucewuu.android.qlcy.AppContext;
import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.adapter.CreateDiscussionAdapter;
import com.brucewuu.android.qlcy.base.LoadDataActivity;
import com.brucewuu.android.qlcy.db.DBDaoFactory;
import com.brucewuu.android.qlcy.listener.OnItemClickListener;
import com.brucewuu.android.qlcy.model.User;
import com.brucewuu.android.qlcy.util.ListUtils;
import com.brucewuu.android.qlcy.util.UIHelper;
import com.brucewuu.android.qlcy.util.io.LogUtils;
import com.brucewuu.android.qlcy.widget.EmptyLayout;
import com.mcxiaoke.next.task.Failure;
import com.mcxiaoke.next.task.Success;
import com.mcxiaoke.next.task.TaskBuilder;
import com.yzxIM.IMManager;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DiscussionInfo;
import com.yzxIM.listener.DiscussionGroupCallBack;
import com.yzxtcp.data.UcsReason;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.Bind;

/**
 * Created by 吴昭 on 2015-9-5.
 */
public class CreateDiscussionActivity extends LoadDataActivity<User> implements View.OnClickListener,
        OnItemClickListener, DiscussionGroupCallBack {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;

    @Bind(R.id.rv_create_discussion_friends)
    RecyclerView mRecyclerView;

    private CreateDiscussionAdapter mAdapter;

    private ArrayList<User> selectList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.act_create_discussion;
    }

    @Override
    protected void afterViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new CreateDiscussionAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        emptyLayout.setEmptyClickListener(this);
        IMManager.getInstance(this).setDiscussionGroup(this);

        loadData();
    }

    @Override
    protected void loadData() {
        emptyLayout.setEmptyView(EmptyLayout.ON_LOADING);
        load();
    }

    @Override
    protected void initData(List<User> data) {
        emptyLayout.setEmptyView(EmptyLayout.DEFAULT);
        if (ListUtils.isEmpty(data)) {
            emptyLayout.setEmptyView(EmptyLayout.LOAD_ERROR);
        } else {
            mAdapter.addAll(data);
        }
    }

    @Override
    protected void loadError() {
        emptyLayout.setEmptyView(EmptyLayout.LOAD_ERROR);
    }

    @Override
    protected List<User> getData() throws Exception {
        return DBDaoFactory.getFriendsDao().getFriends();
    }

    @Override
    public void onClick(View v) {
        loadData();
    }

    @Override
    public void onItemClick(View view, int position) {
        User user = mAdapter.getItem(position);
        if (user.getResult() == null || !user.getResult().equals("0")) { // 没被选中
            user.setResult("0");
            selectList.add(user);
        } else {
            Iterator<User> iterator = selectList.iterator();
            while (iterator.hasNext()) {
                if (user.getId().equals(iterator.next().getId())) {
                    iterator.remove();
                    user.setResult("1");
                    break;
                }
            }
        }
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_confirm) {
            if (selectList.size() == 0) {
                UIHelper.showToast("请至少选择一个好友~");
            } else {
                TaskBuilder.create(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(MainActivity.getUser().getNickname())
                                .append(",");
                        ArrayList<String> memberList = new ArrayList<>();
                        for (User user : selectList) {
                            stringBuilder.append(user.getNickname()).append(",");
                            memberList.add(user.getId());
                        }

                        String discussionName = stringBuilder.substring(0, stringBuilder.length() - 1);
                        LogUtils.e("---discussionName==" + discussionName + "--melist" + memberList.size());

                        return IMManager.getInstance(AppContext.getInstance()).createDiscussionGroup(discussionName, memberList);
                    }
                }).success(new Success<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result, Bundle bundle) {
                        if (result) {

                        } else {
                            UIHelper.showToast("创建失败，请重试~");
                        }
                    }
                }).failure(new Failure() {
                    @Override
                    public void onFailure(Throwable throwable, Bundle bundle) {
                        UIHelper.showToast("创建失败，请重试~");
                    }
                }).with(mCaller).serial(true).start();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectList.clear();
    }

    @Override
    public void onCreateDiscussion(UcsReason ucsReason, DiscussionInfo discussionInfo) {
        UIHelper.showToast("当前创建的~");
        if (ucsReason.getReason() == 0) { // 创建成功
            ConversationInfo info = IMManager.getInstance(AppContext.getInstance()).getConversation(discussionInfo.getDiscussionId());
            if (null != info) {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra(ChatActivity.CONVERSATION, info);
                startActivity(intent);
                finish();
            } else {
                LogUtils.e("获得讨论组会话为空");
            }
        } else {
            UIHelper.showToast("创建失败，请重试~");
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
}
