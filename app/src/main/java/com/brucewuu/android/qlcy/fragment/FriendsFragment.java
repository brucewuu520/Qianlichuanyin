package com.brucewuu.android.qlcy.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.adapter.FriendsAdapter;
import com.brucewuu.android.qlcy.base.LoadDataFragment;
import com.brucewuu.android.qlcy.config.AppConfig;
import com.brucewuu.android.qlcy.db.DBDaoFactory;
import com.brucewuu.android.qlcy.listener.OnItemClickListener;
import com.brucewuu.android.qlcy.model.User;
import com.brucewuu.android.qlcy.util.ListUtils;
import com.brucewuu.android.qlcy.util.io.LogUtils;
import com.brucewuu.android.qlcy.widget.EmptyLayout;
import com.mcxiaoke.next.task.Success;
import com.mcxiaoke.next.task.TaskBuilder;
import com.squareup.okhttp.FormEncodingBuilder;

import org.brucewuu.http.AppClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.Bind;

/**
 * Created by brucewuu on 15/9/2.
 */
public class FriendsFragment extends LoadDataFragment<User> implements OnItemClickListener {

    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;

    @Bind(R.id.rv_friends)
    RecyclerView mRecyclerView;

    private FriendsAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fm_friends;
    }

    @Override
    protected void afterViews() {
        Activity parentActivity = getActivity();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(parentActivity);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        if (mAdapter == null) {
            mAdapter = new FriendsAdapter(this);
            mRecyclerView.setAdapter(mAdapter);
            loadData();
        } else {
            mRecyclerView.setAdapter(mAdapter);
        }
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void loadData() {
        emptyLayout.setEmptyView(EmptyLayout.ON_LOADING);
        load();
    }

    @Override
    protected void initData(List<User> data) {
        if (ListUtils.isEmpty(data)) {
            addFriends();
        } else {
            emptyLayout.setEmptyView(EmptyLayout.DEFAULT);
            mAdapter.addAll(data);
        }
    }

    @Override
    protected void loadError() {
        emptyLayout.setEmptyView(EmptyLayout.NO_DATA);
    }

    @Override
    protected List<User> getData() throws Exception {
        return DBDaoFactory.getFriendsDao().getFriends();
    }

    private void addFriends() {
        TaskBuilder.create(new Callable<List<User>>() {
            @Override
            public List<User> call() throws Exception {
                List<User> friends = new ArrayList<>();
                for (int i = 0, length = AppConfig.FRIENDS.length; i < length; i++) {
                    FormEncodingBuilder builder = new FormEncodingBuilder();
                    builder.add("phone", AppConfig.FRIENDS[i]);
                    builder.add("nickname", AppConfig.NAMES[i]);
                    String responce = AppClient.post(AppConfig.REGISTER_URL, builder);
                    User user = User.parseJson(responce);
                    LogUtils.e("user===" + responce);
                    if (user != null && !TextUtils.isEmpty(user.getImtoken())) {
                        friends.add(user);
                    }
                }
                return friends;
            }
        }).success(new Success<List<User>>() {
            @Override
            public void onSuccess(List<User> results, Bundle bundle) {
                emptyLayout.setEmptyView(EmptyLayout.DEFAULT);
                mAdapter.addAll(results);
            }
        }).with(mCaller).start();
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
