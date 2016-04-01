package com.erlema.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.erlema.bean.MyUser;
import com.erlema.util.MyUtils;
import com.erlema.util.TimeUtil;
import com.example.erlema.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

public class ConversationHolder extends BaseViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public ImageView iv_recent_avatar;
    public TextView tv_recent_name;
    public TextView tv_recent_msg;
    public TextView tv_recent_time;
    public TextView tv_recent_unread;
    private Context context;
    ConversationAdapter.OnRecyclerViewListener onRecyclerViewListener;

    public ConversationHolder(Context context, ViewGroup root, ConversationAdapter.OnRecyclerViewListener onRecyclerViewListener) {
        super(context, root, R.layout.item_conversation);
        this.context = context;
        this.onRecyclerViewListener = onRecyclerViewListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        iv_recent_avatar = (ImageView) itemView.findViewById(R.id.iv_recent_avatar);
        tv_recent_name = (TextView) itemView.findViewById(R.id.tv_recent_name);
        tv_recent_msg = (TextView) itemView.findViewById(R.id.tv_recent_msg);
        tv_recent_time = (TextView) itemView.findViewById(R.id.tv_recent_time);
        tv_recent_unread = (TextView) itemView.findViewById(R.id.tv_recent_unread);
    }

    boolean isFirst = true;

    @Override
    public void bindData(Object o) {
        final BmobIMConversation conversation = (BmobIMConversation) o;
        List<BmobIMMessage> msgs = conversation.getMessages();
        //会话标题
        if (msgs != null && msgs.size() > 0) {
            BmobIMMessage lastMsg = msgs.get(0);
            tv_recent_msg.setText(lastMsg.getContent());
            tv_recent_time.setText(TimeUtil.getChatTime(false, lastMsg.getCreateTime()));
            tv_recent_name.setText(conversation.getConversationTitle());
            //乱码解决
            if ( BmobUser.getCurrentUser(getContext()).getObjectId().equals(lastMsg.getToId())) {
                BmobQuery<MyUser> bq = new BmobQuery<>();
                bq.addQueryKeys("username,user_ico");
                bq.getObject(getContext(), lastMsg.getFromId(), new GetListener<MyUser>() {
                    @Override
                    public void onSuccess(MyUser myUser) {
                        if (tv_recent_name != null) {
                            isFirst = false;
                            tv_recent_name.setText(myUser.getUsername());
                            conversation.setConversationTitle(myUser.getUsername());
                            conversation.setConversationIcon(myUser.getAvator());
                            conversation.update();
                            BmobIM.getInstance().updateConversation(conversation);
                            Glide.with(context).load(myUser.getAvator()).override(200, 200).placeholder(R.drawable.ic_default_avatar_big_normal)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_recent_avatar);
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            } else {
                BmobQuery<MyUser> bq = new BmobQuery<>();
                bq.addQueryKeys("username,user_ico");
                bq.getObject(getContext(), lastMsg.getToId(), new GetListener<MyUser>() {
                    @Override
                    public void onSuccess(MyUser myUser) {
                        if (tv_recent_name != null) {
                            isFirst = false;
                            tv_recent_name.setText(myUser.getUsername());
                            conversation.setConversationTitle(myUser.getUsername());
                            conversation.setConversationIcon(myUser.getAvator());
                            BmobIM.getInstance().updateConversation(conversation);
                            Glide.with(context).load(myUser.getAvator()).override(200, 200).placeholder(R.drawable.ic_default_avatar_big_normal)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_recent_avatar);
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
                tv_recent_name.setText(conversation.getConversationTitle());
            }
        }
        //会话图标
//      ViewUtil.setAvatar(conversation.getConversationIcon(), R.mipmap.head, iv_recent_avatar);
        Glide.with(context).load(conversation.getConversationIcon()).placeholder(R.drawable.ic_default_avatar_big_normal)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_recent_avatar);
        //查询指定未读消息数
        long unread = BmobIM.getInstance().getUnReadCount(conversation.getConversationId());
        if (unread > 0) {
            tv_recent_unread.setVisibility(View.VISIBLE);
            tv_recent_unread.setText(" " + String.valueOf(unread) + " ");
        } else {
            tv_recent_unread.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (onRecyclerViewListener != null) {
            onRecyclerViewListener.onItemClick(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (onRecyclerViewListener != null) {
            onRecyclerViewListener.onItemLongClick(getAdapterPosition());
        }
        return true;
    }
}