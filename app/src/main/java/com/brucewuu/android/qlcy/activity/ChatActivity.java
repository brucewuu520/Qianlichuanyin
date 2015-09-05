package com.brucewuu.android.qlcy.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.adapter.ChatListAdapter;
import com.brucewuu.android.qlcy.base.LoadDataActivity;
import com.brucewuu.android.qlcy.config.AppConfig;
import com.brucewuu.android.qlcy.model.User;
import com.brucewuu.android.qlcy.util.AndroidUtils;
import com.brucewuu.android.qlcy.util.ListUtils;
import com.brucewuu.android.qlcy.util.RecyclerViewHelper;
import com.brucewuu.android.qlcy.util.UIHelper;
import com.brucewuu.android.qlcy.util.io.LogUtils;
import com.mcxiaoke.next.task.Failure;
import com.mcxiaoke.next.task.Success;
import com.mcxiaoke.next.task.TaskBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.okhttp.FormEncodingBuilder;
import com.yzxIM.IMManager;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.MSGTYPE;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DiscussionChat;
import com.yzxIM.data.db.GroupChat;
import com.yzxIM.data.db.SingleChat;
import com.yzxIM.listener.MessageListener;

import org.brucewuu.http.AppClient;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import de.greenrobot.event.EventBus;

/**
 * Created by brucewuu on 15/9/2.
 */
public class ChatActivity extends LoadDataActivity<ChatMessage> implements MessageListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String CONVERSATION = "conversation";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.mSwipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.rv_chat_list)
    RecyclerView mRecyclerView;

    @Bind(R.id.et_chat_message)
    MaterialEditText etMessage;

    @Bind(R.id.btn_chat_more)
    ImageButton ibtnMore;

    @Bind(R.id.btn_chat_send)
    AppCompatButton btnSend;

    private ChatListAdapter mAdapter;

    private ConversationInfo conversationInfo;
    private User user;

    private int allCount = 0;
    private static final int hasCount = 20;

    @Override
    protected int getLayoutId() {
        return R.layout.act_chat;
    }

    @Override
    protected void afterViews() {
        LogUtils.e("--ChatListAdapter==" + mAdapter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        conversationInfo = (ConversationInfo) getIntent().getSerializableExtra(CONVERSATION);
        user = MainActivity.getUser();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setVerticalScrollBarEnabled(false);
        mAdapter = new ChatListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        IMManager.getInstance(this).setSendMsgListener(this);

        loadData();

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AndroidUtils.hideSoftKeyboard(ChatActivity.this, etMessage);
                return false;
            }
        });
    }

    @Override
    protected void loadData() {
        UIHelper.setRefreshing(mSwipeRefreshLayout, true);
        load();
    }

    @Override
    protected void initData(List<ChatMessage> data) {
        UIHelper.setRefreshing(mSwipeRefreshLayout, false);
        if (!ListUtils.isEmpty(data)) {
            if (mAdapter.isEmpty()) {
                mAdapter.addAll(data);
                conversationInfo.clearMessagesUnreadStatus();
                mRecyclerView.scrollToPosition(mAdapter.getCount() - 1);
            } else {
                mAdapter.addAll(0, data);
            }
        }
    }

    @Override
    protected void loadError() {
        UIHelper.setRefreshing(mSwipeRefreshLayout, false);
    }

    @Override
    protected List<ChatMessage> getData() throws Exception {
        if (allCount == 0)
            allCount = conversationInfo.getAllMessage().size();
        return conversationInfo.getLastestMessages(mAdapter.getCount(), mAdapter.getCount() + hasCount);
    }

    @Override
    public void onRefresh() {
        if (mAdapter.getCount() < allCount) {
            load();
        } else {
            UIHelper.setRefreshing(mSwipeRefreshLayout, false);
        }
    }

    @Override
    public void onSendMsgRespone(ChatMessage chatMessage) {
        LogUtils.e("onSendMsgRespone---" + chatMessage.getContent());
    }

    @Override
    public void onReceiveMessage(List list) {
        LogUtils.e("onReceiveMessage---" + ListUtils.getSize(list));
        if (!ListUtils.isEmpty(list)) {
            boolean isLastVisiable = RecyclerViewHelper.findLastVisibleItemPosition(mRecyclerView) == RecyclerViewHelper.getItemCount(mRecyclerView) -1 ? true : false;
            mAdapter.addAll(list);
            if (isLastVisiable) {
                mRecyclerView.scrollToPosition(mAdapter.getCount() - 1);
            }
        }
    }

    @Override
    public void onDownloadAttachedProgress(String s, String s1, int i, int i1) {
        LogUtils.e("onDownloadAttachedProgress---");
    }

    @OnTextChanged(value = R.id.et_chat_message, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void inputMobile(Editable editable) {
        if (editable.length() > 0) {
            ibtnMore.setVisibility(View.GONE);
            btnSend.setVisibility(View.VISIBLE);
        } else {
            ibtnMore.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_chat_send)
    void sendMessage() {
        ChatMessage chatMessage;
        if (conversationInfo.getCategoryId() == CategoryId.GROUP) {
            chatMessage = new GroupChat();
        } else if (conversationInfo.getCategoryId() == CategoryId.DISCUSSION) {
            chatMessage = new DiscussionChat();
        } else {
            chatMessage = new SingleChat();
        }
        chatMessage.setTargetId(conversationInfo.getTargetId());
        chatMessage.setSenderId(user.getId());
        chatMessage.setMsgType(MSGTYPE.MSG_DATA_TEXT);//设置消息类型为文本
        chatMessage.setContent(etMessage.getText().toString());//设置消息内容
        if (IMManager.getInstance(this).sendmessage(chatMessage)) {//发送消息成功返回true
            LogUtils.e("----------发送成功------");
            //发送成功后把消息添加到消息列表中，收到消息发送回调后刷新界面
            mAdapter.add(chatMessage);
            etMessage.setText("");
            mRecyclerView.scrollToPosition(mAdapter.getCount() - 1);
        } else {
            LogUtils.e("----------发送失败------");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;
        if (conversationInfo.getCategoryId() == CategoryId.PERSONAL) {
            menuItem = menu.add(0, R.id.menu_chat, 0, "");
            menuItem.setIcon(R.drawable.ic_menu_one);
        }
        else {
            menuItem = menu.add(0, R.id.menu_chat, 0, "");
            menuItem.setIcon(R.drawable.ic_menu_some);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_chat) {
            if (conversationInfo.getCategoryId() == CategoryId.GROUP) {
                new AlertDialog.Builder(this).setTitle("退出群组")
                .setMessage("确认退出该群组？")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TaskBuilder.create(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                FormEncodingBuilder builder = new FormEncodingBuilder();
                                builder.add("userId", user.getId());
                                builder.add("groupId", conversationInfo.getTargetId());
                                final String response = AppClient.post(AppConfig.EXIT_GROUP, builder);
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("result") && jsonObject.getString("result").equals("0"))
                                    return true;
                                return false;
                            }
                        }).success(new Success<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result, Bundle bundle) {
                                if (result) {
                                    UIHelper.showToast("退出成功~");
                                    EventBus.getDefault().post(AppConfig.EXIT_GROUP_SUCCESS);
                                } else {
                                    UIHelper.showToast("操作失败~");
                                }
                            }
                        }).failure(new Failure() {
                            @Override
                            public void onFailure(Throwable throwable, Bundle bundle) {
                                UIHelper.showToast("操作失败,请重试~");
                            }
                        }).with(mCaller).start();
                    }
                }).setNegativeButton(R.string.cancle, null).show();
            } else {

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
