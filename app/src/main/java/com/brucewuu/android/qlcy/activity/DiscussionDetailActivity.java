package com.brucewuu.android.qlcy.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brucewuu.android.qlcy.AppContext;
import com.brucewuu.android.qlcy.AppManager;
import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.adapter.RecyclerArrayAdapter;
import com.brucewuu.android.qlcy.base.SwipeBackActivity;
import com.brucewuu.android.qlcy.util.UIHelper;
import com.brucewuu.android.qlcy.util.io.LogUtils;
import com.mcxiaoke.next.task.Failure;
import com.mcxiaoke.next.task.SimpleTaskCallback;
import com.mcxiaoke.next.task.Success;
import com.mcxiaoke.next.task.TaskBuilder;
import com.mcxiaoke.next.task.TaskCallable;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.task.TaskQueue;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yzxIM.IMManager;
import com.yzxIM.data.db.DiscussionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by brucewuu on 15/9/6.
 */
public class DiscussionDetailActivity extends SwipeBackActivity {

    public static final String DISCUSSION_ID = "discussion_id";
    public static final String USER_ID = "user_id";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.rv_discussion_member)
    RecyclerView mRecyclerView;

    @Bind(R.id.tv_discussion_name)
    TextView tvName;

    private String discussionId;
    private String userId;

    private DiscussionInfo discussionInfo;

    private DiscussionMemberAdapter mAdapter;

    private AlertDialog addMemberDialog;
    private AlertDialog clearMessageDialog;
    private AlertDialog deleteDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.act_discussion_detail;
    }

    @Override
    protected void afterViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        discussionId = getIntent().getStringExtra(DISCUSSION_ID);
        userId = getIntent().getStringExtra(USER_ID);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.setHasFixedSize(false);
        mAdapter = new DiscussionMemberAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        TaskQueue.getDefault().add(getCallable(), callback, this);
    }

    @OnClick(R.id.rl_change_discussion_name)
    void changeName() {
        if (addMemberDialog == null) {
            View localView = View.inflate(this, R.layout.dialog_group, null);
            final MaterialEditText localEditText = (MaterialEditText) localView.findViewById(R.id.et_group);
            localEditText.setHint("输入新的昵称");
            this.addMemberDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.create_group)
                    .setView(localView)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String groupName = localEditText.getText().toString();
                            if (TextUtils.isEmpty(groupName)) {
                                localEditText.setError("请输入群组名~");
                                return;
                            } else if (groupName.equals(tvName.getText().toString())) {
                                return;
                            }
                            showProgressDialog("正在修改...");
                            TaskBuilder.create(new Callable<Boolean>() {
                                @Override
                                public Boolean call() throws Exception {
                                    return IMManager.getInstance(AppContext.getInstance()).modifyDiscussionTitle(discussionId, groupName);
                                }
                            }).success(new Success<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result, Bundle bundle) {
                                    dismissProgressDialog();
                                    if (result) {
                                        tvName.setText(groupName);
                                    } else {
                                        UIHelper.showToast("修改失败~");
                                    }
                                }
                            }).failure(new Failure() {
                                @Override
                                public void onFailure(Throwable throwable, Bundle bundle) {
                                    dismissProgressDialog();
                                    UIHelper.showToast("修改失败，请重试~");
                                }
                            }).with(DiscussionDetailActivity.this).start();
                        }
                    })
                    .setNegativeButton(R.string.cancle, null).create();
        }
        addMemberDialog.show();
    }

    @OnClick(R.id.label_clear_message)
    void clearMessage() {
        if (clearMessageDialog == null) {
            clearMessageDialog = new AlertDialog.Builder(this)
                    .setMessage("清空此讨论组的历史消息?")
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            IMManager.getInstance(AppContext.getInstance())
                                    .clearMessages(IMManager.getInstance(AppContext.getInstance())
                                            .getConversation(discussionId));
                        }
                    })
                    .setNegativeButton(R.string.cancle, null).create();
        }
        clearMessageDialog.show();
    }

    @OnClick(R.id.btn_exit_discussion)
    void exitAndDelete() {
        if (deleteDialog == null) {
            deleteDialog = new AlertDialog.Builder(this)
                    .setMessage("删除并退出后，将不再接受此讨论组信息!")
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boolean flag = IMManager.getInstance(AppContext.getInstance())
                                    .quitDiscussionGroup(discussionId);
                            if (flag) {
                                AppManager.getInstance().finishActivity(ChatActivity.class);
                                finish();
                            } else {
                                UIHelper.showToast("退出失败，请重试~");
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancle, null).create();
        }
        deleteDialog.show();
    }

    final TaskCallback<List<String>> callback = new SimpleTaskCallback<List<String>>() {
        @Override
        public void onTaskSuccess(List<String> results, Bundle extras) {
            LogUtils.e("results:" + results.size());
            mAdapter.addAll(results);
            tvName.setText(discussionInfo.getDiscussionName());
        }

        @Override
        public void onTaskFailure(Throwable ex, Bundle extras) {

        }
    };

    private Callable<List<String>> getCallable() {
        return new TaskCallable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                discussionInfo = IMManager.getInstance(AppContext.getInstance()).getDiscussionInfo(discussionId);
                ArrayList memberList = new ArrayList();
                if (discussionInfo != null) {
                    LogUtils.e("member::" + discussionInfo.getDiscussionMembers());
                    String[] members = discussionInfo.getDiscussionMembers().split(",");
                    for (int i = 0, length = members.length; i < length; i++) {
                        if (members[i].equals(userId)) {
                            memberList.add("我");
                        } else {
                            memberList.add(members[i]);
                        }
                    }
                    if (discussionInfo.getOwnerId().equals(userId)) {
                        memberList.add("+");
                        memberList.add("-");
                    } else {
                        memberList.add("+");
                    }
                }

                return memberList;
            }
        };
    }

    @Override
    protected void onDestroy() {
        TaskQueue.getDefault().cancelAll(this);
        super.onDestroy();
    }

    class DiscussionMemberAdapter extends RecyclerArrayAdapter<String, DiscussionMemberAdapter.MyViewHolder> {

        private boolean deleting = false;

        public DiscussionMemberAdapter(Context context) {
            super(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LogUtils.e("---onCreateViewHolder--:" + viewType);
            return new MyViewHolder(getInflater().inflate(R.layout.item_discussion_member, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            LogUtils.e("---onBindViewHolder--:" + position);
            String member = getItem(position);
            if (member.equals("+")) {
                holder.ivFace.setImageResource(R.drawable.add_member);
                holder.tvName.setVisibility(View.GONE);
                holder.flag.setVisibility(View.GONE);
            } else if (member.equals("-")) {
                holder.ivFace.setImageResource(R.drawable.delete_member);
                holder.tvName.setVisibility(View.GONE);
                holder.flag.setVisibility(View.GONE);
            } else if (deleting) {
                holder.ivFace.setImageResource(R.drawable.person);
                holder.tvName.setVisibility(View.VISIBLE);
                holder.flag.setVisibility(View.VISIBLE);
            } else {
                holder.ivFace.setImageResource(R.drawable.person);
                holder.tvName.setVisibility(View.VISIBLE);
                holder.flag.setVisibility(View.GONE);
            }
        }

        public void setDeleting(boolean isDeleting) {
            this.deleting = isDeleting;
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @Bind(R.id.iv_member_face)
            CircleImageView ivFace;

            @Bind(R.id.view_delete_flag)
            View flag;

            @Bind(R.id.tv_member_name)
            TextView tvName;

            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                String member = getItem(getAdapterPosition());
                if (member.equals("+")) {

                } else if (member.equals("-")) {

                } else if (deleting) {

                } else {

                }
            }
        }
    }
}
