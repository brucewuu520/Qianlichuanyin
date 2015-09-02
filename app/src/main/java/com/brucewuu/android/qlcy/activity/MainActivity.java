package com.brucewuu.android.qlcy.activity;

import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.brucewuu.android.qlcy.AppContext;
import com.brucewuu.android.qlcy.AppManager;
import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.base.BaseActivity;
import com.brucewuu.android.qlcy.config.AppConfig;
import com.brucewuu.android.qlcy.db.DBDaoFactory;
import com.brucewuu.android.qlcy.fragment.FriendsFragment;
import com.brucewuu.android.qlcy.fragment.MainFragment;
import com.brucewuu.android.qlcy.model.User;
import com.brucewuu.android.qlcy.util.NetworkUtils;
import com.brucewuu.android.qlcy.util.ObjectUtils;
import com.brucewuu.android.qlcy.util.PreferenceUtil;
import com.brucewuu.android.qlcy.util.SysIntentUtil;
import com.brucewuu.android.qlcy.util.io.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;

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
    private static final String STATE_SELECTED_POSITION = "selected_navigation_view_position";
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
            AppManager.getInstance().AppExit();
            redirectTo(LoginActivity.class);
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
                public void onLogin(UcsReason reason) {
                    LogUtils.e("msg:" + reason.getMsg() + "--reason:" + reason.getReason());
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                break;
            case R.id.nav_logout:
                UCSManager.disconnect(); // 注销函数
                PreferenceUtil.clear();
                DBDaoFactory.getFriendsDao().deleteAll();
                AppManager.getInstance().AppExit();
                redirectTo(LoginActivity.class);
                break;
            case R.id.nav_app_source:
                break;
            case R.id.nav_about_author:
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
            SysIntentUtil.gotoNetworkSetting(this);
        else {
            UCSManager.connect(user.getImtoken(), new ILoginListener() {
                @Override
                public void onLogin(UcsReason reason) {
                    LogUtils.e("msg:" + reason.getMsg() + "--reason:" + reason.getReason());
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
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEvent(String event) {
        if (event.equals(AppConfig.NO_MSG_TO_FRIENDS)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new FriendsFragment()).commit();
            mNavigationView.getMenu().findItem(R.id.nav_friends).setChecked(true);
            selectedId = R.id.nav_friends;
        }
    }
}
