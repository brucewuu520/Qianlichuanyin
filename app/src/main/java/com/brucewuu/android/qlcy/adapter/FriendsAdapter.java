package com.brucewuu.android.qlcy.adapter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.activity.MainActivity;
import com.brucewuu.android.qlcy.config.AppConfig;
import com.brucewuu.android.qlcy.db.DBDaoFactory;
import com.brucewuu.android.qlcy.listener.OnItemClickListener;
import com.brucewuu.android.qlcy.model.User;
import com.brucewuu.android.qlcy.util.UIHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mcxiaoke.next.task.Failure;
import com.mcxiaoke.next.task.Success;
import com.mcxiaoke.next.task.TaskBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.brucewuu.http.AppClient;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by brucewuu on 15/9/2.
 */
public class FriendsAdapter extends RecyclerArrayAdapter<User, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = -333;

    private Fragment mFragment;
    private WeakReference<OnItemClickListener> mOnItemClickListener;

    public FriendsAdapter(@NonNull Fragment fragment) {
        super(fragment.getActivity());
        this.mFragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER)
            return new HeaderViewHolder(getInflater().inflate(R.layout.view_add_friends, parent, false));
        return new MyViewHolder(getInflater().inflate(R.layout.item_friends, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) != VIEW_TYPE_HEADER) {
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            final User user = getItem(position);
            Glide.with(mFragment).load(user.getPortraituri()).placeholder(R.mipmap.defalut_face)
                    .error(R.mipmap.defalut_face)
                    .crossFade()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            myViewHolder.ivFriend.setImageDrawable(resource);
                        }
                    });
            myViewHolder.tvName.setText(user.getNickname());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (isEmpty())
            return 0;
        return super.getItemCount() + 1;
    }

    @Override
    public User getItem(int position) {
        return super.getItem(position - 1);
    }

    private AlertDialog addFriendsDialog;

    class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public HeaderViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (addFriendsDialog == null) {
                View localView = getInflater().inflate(R.layout.dialog_group, null);
                final MaterialEditText localEditText = (MaterialEditText) localView.findViewById(R.id.et_group);
                localEditText.setHint("输入联系人ID");
                addFriendsDialog = new AlertDialog.Builder(mFragment.getActivity())
                        .setTitle("添加好友")
                        .setView(localView)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String userId = localEditText.getText().toString();
                                if (TextUtils.isEmpty(userId)) {
                                    localEditText.setError("请输入联系人ID");
                                    return;
                                }
                                if (userId.equals(MainActivity.getUser().getId())) {
                                    UIHelper.showToast("不能添加自己~");
                                }
                                boolean flag = false;
                                for (User user : getAllItems()) {
                                    if (userId.equals(user.getId())) {
                                        UIHelper.showToast("已经是你的好友了~");
                                        flag = true;
                                        break;
                                    }
                                }
                                if (flag)
                                    return;
                                TaskBuilder.create(new Callable<User>() {
                                    @Override
                                    public User call() throws Exception {
                                        final String respones = AppClient.get(AppConfig.LOGIN_URL + "?phone=" + userId);
                                        return User.parseJson(respones);
                                    }
                                }).success(new Success<User>() {
                                    @Override
                                    public void onSuccess(User result, Bundle bundle) {
                                        if (result != null && !TextUtils.isEmpty(result.getImtoken())) {
                                            result.setId(result.getPhone());
                                            add(result);
                                            DBDaoFactory.getFriendsDao().save(result);
                                        } else {
                                            UIHelper.showToast("该用户不存在~");
                                        }
                                    }
                                }).failure(new Failure() {
                                    @Override
                                    public void onFailure(Throwable throwable, Bundle bundle) {
                                        UIHelper.showToast("添加失败，请重试~");
                                    }
                                }).with(FriendsAdapter.class).serial(true).start();
                            }
                        }).create();
            }
            addFriendsDialog.show();
        }
    }

    final class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.iv_friends_face)
        CircleImageView ivFriend;

        @Bind(R.id.tv_friends_name)
        TextView tvName;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null && mOnItemClickListener.get() != null)
                mOnItemClickListener.get().onItemClick(v, getAdapterPosition());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = new WeakReference<>(listener);
    }
}
