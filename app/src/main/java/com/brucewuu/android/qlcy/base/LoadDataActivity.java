package com.brucewuu.android.qlcy.base;

import android.os.Bundle;

import com.mcxiaoke.next.task.SimpleTaskCallback;
import com.mcxiaoke.next.task.TaskCallable;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.task.TaskQueue;

import java.util.List;

/**
 * Created by brucewuu on 15/7/17.
 */
public abstract class LoadDataActivity<T> extends SwipeBackActivity {

    protected abstract void loadData();

    protected abstract void initData(List<T> data);

    protected abstract void loadError();

    protected abstract List<T> getData() throws Exception;

    protected final void load() {
        TaskQueue.getDefault().add(callable, callback, this);
    }

    protected final void loadSerially() {
        TaskQueue.getDefault().addSerially(callable, callback, this);
    }

    final TaskCallback<List<T>> callback = new SimpleTaskCallback<List<T>>() {
        @Override
        public void onTaskSuccess(List<T> result, Bundle extras) {
            initData(result);
        }

        @Override
        public void onTaskFailure(Throwable ex, Bundle extras) {
            loadError();
        }
    };

    final TaskCallable<List<T>> callable = new TaskCallable<List<T>>() {
        @Override
        public List<T> call() throws Exception {
            return getData();
        }
    };

    @Override
    protected void onDestroy() {
        TaskQueue.getDefault().cancelAll(this);
        super.onDestroy();
    }
}