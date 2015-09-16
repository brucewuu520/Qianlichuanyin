package com.brucewuu.android.qlcy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.brucewuu.android.qlcy.AppContext;
import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.adapter.ChatListAdapter;
import com.brucewuu.android.qlcy.base.LoadDataActivity;
import com.brucewuu.android.qlcy.config.AppConfig;
import com.brucewuu.android.qlcy.listener.OnSoftKeyboardListener;
import com.brucewuu.android.qlcy.model.User;
import com.brucewuu.android.qlcy.util.AndroidUtils;
import com.brucewuu.android.qlcy.util.ListUtils;
import com.brucewuu.android.qlcy.util.RecyclerViewHelper;
import com.brucewuu.android.qlcy.util.UIHelper;
import com.brucewuu.android.qlcy.util.io.LogUtils;
import com.brucewuu.android.qlcy.widget.KeyboardRelativeLayout;
import com.brucewuu.android.qlcy.widget.MessageEditText;
import com.mcxiaoke.next.task.Async;
import com.mcxiaoke.next.task.Failure;
import com.mcxiaoke.next.task.Success;
import com.mcxiaoke.next.task.TaskBuilder;
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

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import de.greenrobot.event.EventBus;

/**
 * Created by brucewuu on 15/9/2.
 */
public class ChatActivity extends LoadDataActivity<ChatMessage> implements MessageListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String CONVERSATION = "conversation";

    @Bind(R.id.rr_chat_ui)
    KeyboardRelativeLayout chatUi;

    @Bind(R.id.mSwipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.rv_chat_list)
    RecyclerView mRecyclerView;

    @Bind(R.id.et_chat_message)
    MessageEditText etMessage;

    @Bind(R.id.btn_chat_more)
    ImageButton ibtnMore;

    @Bind(R.id.btn_chat_send)
    AppCompatButton btnSend;

    @Bind(R.id.ibtn_chat_volice)
    ImageButton btnChatType;

    @Bind(R.id.btn_record)
    Button btnRecord;

    private ChatListAdapter mAdapter;

    private ConversationInfo conversationInfo;
    private User user;

    private int allCount = 0;
    private static final int hasCount = 20;

    public static final String path = AndroidUtils.getCacheDirectory(AppContext.getInstance()).getAbsolutePath();
    private IMManager imManager;

    private Handler messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_chat;
    }

    @Override
    protected void afterViews() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        imManager = IMManager.getInstance(this);
        messageHandler = new MessageHandler(this);

        conversationInfo = (ConversationInfo) getIntent().getSerializableExtra(CONVERSATION);
        user = MainActivity.getUser();
        actionBar.setTitle(conversationInfo.getConversationTitle());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setVerticalScrollBarEnabled(false);
        mAdapter = new ChatListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        imManager.setSendMsgListener(this);

        loadData();

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AndroidUtils.hideSoftKeyboard(ChatActivity.this, etMessage);
                return false;
            }
        });
        chatUi.setOnSoftKeyboardListener(new OnSoftKeyboardListener() {
            @Override
            public void onShow() {
                LogUtils.e("----soft keyboard  is show");
                mRecyclerView.scrollToPosition(mAdapter.getCount() - 1);
            }

            @Override
            public void onHidden() {
                LogUtils.e("----soft keyboard  is hidden");
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
        Message message = messageHandler.obtainMessage();
        message.obj = chatMessage;
        message.what = 2;
        messageHandler.sendMessage(message);
    }

    @Override
    public void onReceiveMessage(List list) {
        LogUtils.e("onReceiveMessage---" + ListUtils.getSize(list));
        Message message = messageHandler.obtainMessage();
        message.obj = list;
        message.what = 3;
        messageHandler.sendMessage(message);
    }

    @Override
    public void onDownloadAttachedProgress(String s, String s1, int i, int i1) {
        LogUtils.e("onDownloadAttachedProgress---");
    }

    @OnTextChanged(value = R.id.et_chat_message, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void inputMobile(Editable editable) {
        if (editable.toString().trim().length() > 0) {
            ibtnMore.setVisibility(View.GONE);
            btnSend.setVisibility(View.VISIBLE);
        } else {
            ibtnMore.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.GONE);
        }
    }

    @OnEditorAction(R.id.et_chat_message)
    boolean actionSend(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            if (TextUtils.isEmpty(etMessage.getText().toString()))
                return false;
            sendMessage();
        }
        return false;
    }

    @OnClick(R.id.ibtn_chat_volice)
    void changeChatType() {
        if (btnRecord.getVisibility() == View.GONE) {
            AndroidUtils.hideSoftKeyboard(this, etMessage);
            etMessage.setVisibility(View.GONE);
            btnRecord.setVisibility(View.VISIBLE);
            btnChatType.setBackgroundResource(R.drawable.key_up);
        } else {
            btnRecord.setVisibility(View.GONE);
            etMessage.setVisibility(View.VISIBLE);
            etMessage.requestFocus();
            btnChatType.setBackgroundResource(R.drawable.record_up);
            AndroidUtils.showSoftKeyboard(this, etMessage);
        }
    }

    @OnClick(R.id.btn_chat_send)
    void sendMessage() {
        Async.start(new Runnable() {
            @Override
            public void run() {
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
                chatMessage.setMsgType(MSGTYPE.MSG_DATA_TEXT.ordinal());//设置消息类型为文本
                chatMessage.setContent(etMessage.getText().toString());//设置消息内容
                messageHandler.sendEmptyMessage(-1);
                if (imManager.sendmessage(chatMessage)) { // 发送消息
                    Message message = messageHandler.obtainMessage();
                    message.what = 1;
                    message.obj = chatMessage;
                    messageHandler.sendMessage(message);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;
        if (conversationInfo.getCategoryId() == CategoryId.PERSONAL) {
            menuItem = menu.add(0, R.id.menu_chat, 0, "");
            menuItem.setIcon(R.drawable.ic_menu_one);
        } else {
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
                                showProgressDialog("正在退出...");
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
                                        dismissProgressDialog();
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
                                        dismissProgressDialog();
                                        UIHelper.showToast("操作失败,请重试~");
                                    }
                                }).with(this).start();
                            }
                        }).setNegativeButton(R.string.cancle, null).show();
            } else if (conversationInfo.getCategoryId() == CategoryId.DISCUSSION) {
                Intent intent = new Intent(this, DiscussionDetailActivity.class);
                intent.putExtra(DiscussionDetailActivity.DISCUSSION_ID, conversationInfo.getTargetId());
                intent.putExtra(DiscussionDetailActivity.USER_ID, user.getId());
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void clearText() {
        etMessage.setText("");
    }

    public void addMessage(ChatMessage chatMessage) {
        mAdapter.add(chatMessage);
        mRecyclerView.scrollToPosition(mAdapter.getCount() - 1);
    }

    public void responseMessage(ChatMessage chatMessage) {
        for (int i = mAdapter.getCount() - 1; i >= 0; i--) {
            if (mAdapter.getItem(i).getMsgid().equals(chatMessage.getMsgid())) {
                mAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    public void receiveMessage(List messageList) {
        boolean isLastVisiable = RecyclerViewHelper.findLastVisibleItemPosition(mRecyclerView) == mAdapter.getItemCount() - 1 ? true : false;
        mAdapter.addAll(messageList);
        if (isLastVisiable) {
            mRecyclerView.scrollToPosition(mAdapter.getCount() - 1);
        }
    }

    static class MessageHandler extends Handler {

        private WeakReference<ChatActivity> mChatActivity;

        public MessageHandler(ChatActivity activity) {
            this.mChatActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    mChatActivity.get().clearText();
                    break;
                case 1: // 发出消息
                    // 发送后把消息添加到消息列表中，收到消息发送回调后刷新界面
                    mChatActivity.get().addMessage((ChatMessage) msg.obj);
                    break;
                case 2: // 发送消息成功或失败
                    mChatActivity.get().responseMessage((ChatMessage) msg.obj);
                    break;
                case 3: // 接收到消息
                    mChatActivity.get().receiveMessage((List) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }
}
