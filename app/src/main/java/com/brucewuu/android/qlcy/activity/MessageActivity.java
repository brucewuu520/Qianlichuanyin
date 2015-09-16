package com.brucewuu.android.qlcy.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.base.SwipeBackActivity;

/**
 * Created by brucewuu on 15/9/8.
 */
public class MessageActivity extends SwipeBackActivity {

    RecyclerView mRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.act_message;
    }

    @Override
    protected void afterViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_chat_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }
}
