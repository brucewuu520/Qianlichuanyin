package com.brucewuu.android.qlcy.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brucewuu.android.qlcy.AppContext;
import com.brucewuu.android.qlcy.AppManager;
import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.base.SwipeBackActivity;
import com.brucewuu.android.qlcy.util.ListUtils;
import com.brucewuu.android.qlcy.util.UIHelper;
import com.brucewuu.android.qlcy.util.io.LogUtils;
import com.brucewuu.android.qlcy.widget.ScrollGridView;
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
import butterknife.OnItemClick;

/**
 * Created by brucewuu on 15/9/6.
 */
public class DiscussionDetailActivity extends SwipeBackActivity {

    public static final String DISCUSSION_ID = "discussion_id";
    public static final String USER_ID = "user_id";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.gv_discussion_member)
    ScrollGridView mGridView;

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
            mAdapter = new DiscussionMemberAdapter(DiscussionDetailActivity.this, results);
            mGridView.setAdapter(mAdapter);
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

    @OnItemClick(R.id.gv_discussion_member)
    void onItemClick(final int position) {
        final String member = mAdapter.getItem(position);
        if (member.equals("+")) {

        } else if (member.equals("-")) {
            if (mAdapter.isDeleting()) {
                mAdapter.setDeleting(false);
            } else {
                mAdapter.setDeleting(true);
            }
            mAdapter.notifyDataSetChanged();
        } else if (mAdapter.isDeleting()) {
            showProgressDialog("正在删除...");
            TaskBuilder.create(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    List<String> list = new ArrayList<>();
                    list.add(member);
                    return IMManager.getInstance(AppContext.getInstance())
                            .delDiscussionGroupMember(discussionId, list);
                }
            }).success(new Success<Boolean>() {
                @Override
                public void onSuccess(Boolean bool, Bundle bundle) {
                    dismissProgressDialog();
                    if (bool) {
                        mAdapter.removeAt(position);
                    }
                }
            }).failure(new Failure() {
                @Override
                public void onFailure(Throwable throwable, Bundle bundle) {
                    dismissProgressDialog();
                    UIHelper.showToast("删除失败，请重试~");
                }
            }).with(DiscussionDetailActivity.this).start();
        } else {

        }
    }

    static class DiscussionMemberAdapter extends BaseAdapter {

        private List<String> items;
        private boolean deleting = false;
        private Context mContext;

        public DiscussionMemberAdapter(Context mContext, List<String> items) {
            this.mContext = mContext;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_discussion_member, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

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

            return convertView;
        }

        @Override
        public int getCount() {
            return ListUtils.getSize(items);
        }

        @Override
        public String getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public boolean isDeleting() {
            return this.deleting;
        }

        public void setDeleting(boolean isDeleting) {
            this.deleting = isDeleting;
        }

        public void removeAt(int position) {
            items.remove(position);
        }

        class ViewHolder {

            @Bind(R.id.iv_member_face)
            ImageView ivFace;

            @Bind(R.id.view_delete_flag)
            ImageView flag;

            @Bind(R.id.tv_member_name)
            TextView tvName;

            public ViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
