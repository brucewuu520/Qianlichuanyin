package com.brucewuu.android.qlcy.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.activity.ChatActivity;
import com.brucewuu.android.qlcy.activity.MainActivity;
import com.brucewuu.android.qlcy.adapter.GroupAdapter;
import com.brucewuu.android.qlcy.base.LoadDataFragment;
import com.brucewuu.android.qlcy.config.AppConfig;
import com.brucewuu.android.qlcy.listener.OnItemClickListener;
import com.brucewuu.android.qlcy.model.GroupInfo;
import com.brucewuu.android.qlcy.util.ListUtils;
import com.brucewuu.android.qlcy.util.UIHelper;
import com.brucewuu.android.qlcy.widget.EmptyLayout;
import com.mcxiaoke.next.task.Failure;
import com.mcxiaoke.next.task.Success;
import com.mcxiaoke.next.task.TaskBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.okhttp.FormEncodingBuilder;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.db.ConversationInfo;

import org.brucewuu.http.AppClient;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by 吴昭 on 2015-9-5.
 */
public class GroupFragment extends LoadDataFragment<GroupInfo> implements View.OnClickListener, OnItemClickListener {

    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;

    @Bind(R.id.btn_add_group)
    AppCompatButton btnAddGroup;

    @Bind(R.id.rv_group)
    RecyclerView mRecyclerView;

    private GroupAdapter mAdapter;

    private AlertDialog addGroupDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fm_group;
    }

    @Override
    protected void afterViews() {
        Activity parentActivity = getActivity();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(parentActivity);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        if (mAdapter != null && !mAdapter.isEmpty()) {
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter = new GroupAdapter(this);
            mRecyclerView.setAdapter(mAdapter);
            loadData();
        }
        mAdapter.setOnItemClickListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void loadData() {
        emptyLayout.setEmptyView(EmptyLayout.ON_LOADING);
        load();
    }

    @Override
    protected void initData(List<GroupInfo> data) {
        emptyLayout.setEmptyView(EmptyLayout.DEFAULT);
        if (ListUtils.isEmpty(data)) {
            btnAddGroup.setVisibility(View.VISIBLE);
        } else {
            btnAddGroup.setVisibility(View.GONE);
            mAdapter.addAll(data);
        }
    }

    @Override
    protected void loadError() {
        emptyLayout.setEmptyView(EmptyLayout.LOAD_ERROR);
        emptyLayout.setOnClickListener(this);
    }

    @Override
    protected List<GroupInfo> getData() throws Exception {
        final String url = AppConfig.GET_GROUP + MainActivity.getUser().getId();
        final String response = AppClient.get(url);
        //LogUtils.e("group:" + response);
        return GroupInfo.getGroups(response);
    }

    @Override
    public void onClick(View v) {
        loadData();
    }

    @OnClick(R.id.btn_add_group)
    void addGroup() {
        if (addGroupDialog == null) {
            final Activity parentActivity = getActivity();
            View localView = View.inflate(parentActivity, R.layout.dialog_group, null);
            final MaterialEditText localEditText = (MaterialEditText) localView.findViewById(R.id.et_group);
            localEditText.setHint("输入群组ID");
            this.addGroupDialog = new AlertDialog.Builder(parentActivity)
                    .setTitle(R.string.add_group)
                    .setView(localView)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String groupId = localEditText.getText().toString();
                            if (TextUtils.isEmpty(groupId)) {
                                localEditText.setError("请输入群组ID~");
                                return;
                            }
                            TaskBuilder.create(new Callable<Boolean>() {
                                @Override
                                public Boolean call() throws Exception {
                                    FormEncodingBuilder builder = new FormEncodingBuilder();
                                    builder.add("userId", MainActivity.getUser().getId());
                                    builder.add("groupId", groupId);
                                    final String response = AppClient.post(AppConfig.ADD_GROUP, builder);
                                    JSONObject json = new JSONObject(response);
                                    if (json.has("result") && json.getString("result").equals("0")) {
                                        return true;
                                    }

                                    return false;
                                }
                            }).success(new Success<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result, Bundle bundle) {
                                    if (result) {
                                        UIHelper.showToast("添加群组成功~");
                                        EventBus.getDefault().post(AppConfig.ADD_GROUP_SUCCESS);
                                    } else {
                                        UIHelper.showToast("添加群组失败~");
                                    }
                                }
                            }).failure(new Failure() {
                                @Override
                                public void onFailure(Throwable throwable, Bundle bundle) {
                                    UIHelper.showToast("添加群组失败，请重试~");
                                }
                            }).with(MainActivity.class).start();
                        }
                    })
                    .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
        }
        addGroupDialog.show();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onItemClick(View view, int position) {
        GroupInfo groupInfo = mAdapter.getItem(position);
        ConversationInfo conversationInfo = new ConversationInfo();
        conversationInfo.setTargetId(groupInfo.getGroupid());
        conversationInfo.setCategoryId(CategoryId.GROUP.ordinal());
        conversationInfo.setConversationTitle(groupInfo.getGroupname());

        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(ChatActivity.CONVERSATION, conversationInfo);
        startActivity(intent);
    }

    public void onEvent(String event) {
        if (event.equals(AppConfig.ADD_GROUP_SUCCESS) ||
                event.equals(AppConfig.EXIT_GROUP_SUCCESS)) {
            mAdapter.clear();
            loadData();
            return;
        }
    }
}
