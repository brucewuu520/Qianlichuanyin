package com.brucewuu.android.qlcy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.brucewuu.android.qlcy.AppContext;
import com.brucewuu.android.qlcy.AppManager;
import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.base.BaseActivity;
import com.brucewuu.android.qlcy.config.AppConfig;
import com.brucewuu.android.qlcy.db.DBDaoFactory;
import com.brucewuu.android.qlcy.fragment.DiscussionFragment;
import com.brucewuu.android.qlcy.fragment.FriendsFragment;
import com.brucewuu.android.qlcy.fragment.GroupFragment;
import com.brucewuu.android.qlcy.fragment.MainFragment;
import com.brucewuu.android.qlcy.model.User;
import com.brucewuu.android.qlcy.util.NetworkUtils;
import com.brucewuu.android.qlcy.util.ObjectUtils;
import com.brucewuu.android.qlcy.util.PreferenceUtil;
import com.brucewuu.android.qlcy.util.SysIntentUtil;
import com.brucewuu.android.qlcy.util.UIHelper;
import com.brucewuu.android.qlcy.util.io.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mcxiaoke.next.task.Failure;
import com.mcxiaoke.next.task.Success;
import com.mcxiaoke.next.task.TaskBuilder;
import com.mcxiaoke.next.task.TaskQueue;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.okhttp.FormEncodingBuilder;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;

import org.brucewuu.http.AppClient;
import org.json.JSONObject;

import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by brucewuu on 15/9/1.
 */
public class MainActivity extends BaseActivity {

    /**
     * Remember the position of the selected item.
     */
    //private static final String STATE_SELECTED_POSITION = "selected_navigation_view_position";
    private int selectedId = R.id.nav_home;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    @Bind(R.id.iv_user_face)
    CircleImageView ivUserFace;

    @Bind(R.id.tv_user_name)
    TextView tvUsername;

    @Bind(R.id.tv_load_tips)
    TextView tvTips;

    private AlertDialog addGroupDialog;
    private AlertDialog createGroupDialog;

    private static User user;

    @Override
    protected int getLayoutId() {
        return R.layout.act_main;
    }

    @Override
    protected void afterViews() {
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        EventBus.getDefault().register(this);

        user = User.parseJson(PreferenceUtil.getUserInfo());
        LogUtils.e("user:" + user.getId() + user.getPortraituri());

        if (user == null) {
            Intent intent = new Intent("com.brucewuu.android.qlcy.LoginActivity");
            startActivity(intent);
            AppManager.getInstance().AppExit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new MainFragment()).commit();
            setupDrawerContent(mNavigationView);
            Glide.with(this).load(user.getPortraituri()).placeholder(R.mipmap.defalut_face).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    ivUserFace.setImageDrawable(resource);
                }
            });
            tvUsername.setText(user.getNickname());

            UCSManager.connect(user.getImtoken(), new ILoginListener() {
                @Override
                public void onLogin(final UcsReason reason) {
                    LogUtils.e("msg:" + reason.getMsg() + "--reason:" + reason.getReason());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (reason.getReason() == 0) { // 登入成功
                                LogUtils.e("---登入成功---");
                                tvTips.setVisibility(View.GONE);
                                EventBus.getDefault().post(AppConfig.CONNECT_SUCCESS);
                            } else if (NetworkUtils.isNotConnected(AppContext.getInstance())) {  // 登入失败
                                tvTips.setVisibility(View.VISIBLE);
                                tvTips.setText("网络连接不可用~");
                                tvTips.setTag(-1);
                            } else {
                                tvTips.setVisibility(View.VISIBLE);
                                tvTips.setText("连接失败,点击重试~");
                                tvTips.setTag(-2);
                            }
                        }
                    });
                }
            });
        }

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_add_group: // 加入群组
                addGroupDialog();
                return true;
            case R.id.menu_create_group: // 创建群组
                createGroup();
                return true;
            case R.id.menu_create_discussion: // 创建讨论组
                redirectTo(CreateDiscussionActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 加入群组
     */
    private void addGroupDialog() {
        if (addGroupDialog == null) {
            View localView = View.inflate(this, R.layout.dialog_group, null);
            final MaterialEditText localEditText = (MaterialEditText) localView.findViewById(R.id.et_group);
            localEditText.setHint("输入群组ID");
            this.addGroupDialog = new AlertDialog.Builder(this)
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
                                    builder.add("userId", user.getId());
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
                            }).with(this).serial(true).start();
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

    /**
     * 创建群组
     */
    private void createGroup() {
        if (createGroupDialog == null) {
            View localView = View.inflate(this, R.layout.dialog_group, null);
            final MaterialEditText localEditText = (MaterialEditText) localView.findViewById(R.id.et_group);
            localEditText.setHint("输入群组名称");
            this.createGroupDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.create_group)
                    .setView(localView)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String groupName = localEditText.getText().toString();
                            if (TextUtils.isEmpty(groupName)) {
                                localEditText.setError("请输入群组名~");
                                return;
                            }
                            TaskBuilder.create(new Callable<Boolean>() {
                                @Override
                                public Boolean call() throws Exception {
                                    FormEncodingBuilder builder = new FormEncodingBuilder();
                                    builder.add("userId", user.getId());
                                    builder.add("groupName", groupName);
                                    final String response = AppClient.post(AppConfig.CREATE_GROPU, builder);
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
                                        UIHelper.showToast("创建群组成功~");
                                        EventBus.getDefault().post(AppConfig.ADD_GROUP_SUCCESS);
                                    } else {
                                        UIHelper.showToast("创建群组失败~");
                                    }
                                }
                            }).failure(new Failure() {
                                @Override
                                public void onFailure(Throwable throwable, Bundle bundle) {
                                    UIHelper.showToast("创建群组失败，请重试~");
                                }
                            }).with(this).start();
                        }
                    })
                    .setNegativeButton(R.string.cancle, null).create();
        }
        createGroupDialog.show();
    }

    private void setupDrawerContent(NavigationView mNavigationView) {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                navigationItemChange(menuItem.getItemId());
                return true;
            }
        });

        mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
    }

    private void navigationItemChange(final @IdRes int id) {
        if (id == selectedId)
            return;
        selectedId = id;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (id) {
            case R.id.nav_home:
                transaction.replace(R.id.main_container, new MainFragment());
                break;
            case R.id.nav_friends:
                transaction.replace(R.id.main_container, new FriendsFragment());
                break;
            case R.id.nav_group:
                transaction.replace(R.id.main_container, new GroupFragment());
                break;
            case R.id.nav_discussion:
                transaction.replace(R.id.main_container, new DiscussionFragment());
                break;
            case R.id.nav_logout:
                UCSManager.disconnect(); // 注销函数
                PreferenceUtil.clear();
                DBDaoFactory.getFriendsDao().deleteAll();
                Intent intent = new Intent("com.brucewuu.android.qlcy.LoginActivity");
                startActivity(intent);
                AppManager.getInstance().AppExit();
                break;
            case R.id.nav_app_source:
                break;
            case R.id.nav_about_author:
                redirectTo(SettingActivity.class);
                break;
            default:
                transaction.replace(R.id.main_container, new MainFragment());
                break;
        }

        transaction.commit();
    }

    @OnClick(R.id.tv_load_tips)
    void reLoad() {
        int flag = ObjectUtils.toInt(tvTips.getTag());
        tvTips.setVisibility(View.GONE);
        if (flag == -1)
            SysIntentUtil.goSettings(this);
        else {
            UCSManager.connect(user.getImtoken(), new ILoginListener() {
                @Override
                public void onLogin(final UcsReason reason) {
                    LogUtils.e("msg:" + reason.getMsg() + "--reason:" + reason.getReason());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (reason.getReason() == 0) { // 登入成功
                                LogUtils.e("---登入成功---");
                                tvTips.setVisibility(View.GONE);
                                EventBus.getDefault().post(AppConfig.CONNECT_SUCCESS);
                            } else if (NetworkUtils.isNotConnected(AppContext.getInstance())) {  // 登入失败
                                tvTips.setVisibility(View.VISIBLE);
                                tvTips.setText("网络连接不可用~");
                                tvTips.setTag(-1);
                            } else {
                                tvTips.setVisibility(View.VISIBLE);
                                tvTips.setText("连接失败,点击重试~");
                                tvTips.setTag(-2);
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(networkChangeReceiver);
        TaskQueue.getDefault().cancelAll(MainActivity.class);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        SysIntentUtil.goHome(this);
    }

    public static User getUser() {
        return user;
    }

    final BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.e("---receive:" + intent.getAction());
            if (NetworkUtils.isConnected(context) && tvTips.getVisibility() == View.VISIBLE) {
                tvTips.setTag(1);
                reLoad();
            }
        }
    };

    public void onEventMainThread(String event) {
        if (event.equals(AppConfig.NO_MSG_TO_FRIENDS)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new FriendsFragment()).commit();
            mNavigationView.getMenu().findItem(R.id.nav_friends).setChecked(true);
            selectedId = R.id.nav_friends;
        }
    }
}
