package com.brucewuu.android.qlcy.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brucewuu.android.qlcy.util.io.LogUtils;

import butterknife.ButterKnife;


/**
 * Created by brucewuu on 2015/3/18.
 */
public abstract class BaseFragment extends Fragment {

    protected final String TAG;

    protected abstract int getLayoutId();

    protected abstract void afterViews();

    public BaseFragment() {
        Object o = this;
        TAG = o.getClass().getSimpleName();
        //LogUtils.e("tag:" + TAG);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //LogUtils.e(TAG + "---onAttach---");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //LogUtils.e(TAG + "---onCreate---");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        LogUtils.e(TAG + "---onCreateView---");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //LogUtils.e(TAG + "---onViewCreated---");
        afterViews();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //LogUtils.e(TAG + "---onActivityCreated---");
    }

    @Override
    public void onResume() {
        super.onResume();
        //LogUtils.e(TAG + "---onResume---");
    }

    @Override
    public void onPause() {
        super.onPause();
        //LogUtils.e(TAG + "---onPause---");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        LogUtils.e(TAG + "---onDestroyView---");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG + "---onDestroy---");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //LogUtils.e(TAG + "---onDetach---");
    }

    protected void redirectTo(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        startActivity(intent);
    }

    protected void redirectTo(Class<?> cls, Bundle extras) {
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setClass(getActivity(), cls);
        startActivity(intent);
    }
}
