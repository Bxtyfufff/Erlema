package com.erlema.fragment;

import com.erlema.activity.ChatActivity;
import com.erlema.adapter.ConversationAdapter;
import com.erlema.bean.MyGoods;
import com.erlema.bean.MyUser;
import com.erlema.bean.myActionBar;
import com.erlema.util.MyUtils;
import com.example.erlema.R;
import com.gc.materialdesign.widgets.Dialog;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.util.Logger;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.greenrobot.event.EventBus;

public class MessageFragment extends BaseFragment {
    protected View rootView = null;
    private myActionBar myactionbar;
    RecyclerView rc_view;
    SwipeRefreshLayout sw_refresh;
    ConversationAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden==false){
            sw_refresh.setRefreshing(true);
            query();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.message_layout, container, false);
        rc_view = (RecyclerView) rootView.findViewById(R.id.rc_view);
        sw_refresh = (SwipeRefreshLayout) rootView.findViewById(R.id.sw_refresh);
        sw_refresh.setColorScheme(R.color.main_color, R.color.red, R.color.green);
        adapter = new ConversationAdapter();
        rc_view.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity());
        rc_view.setLayoutManager(layoutManager);
        sw_refresh.setEnabled(true);
        setListener();
        return rootView;
    }
    private void setListener() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                sw_refresh.setRefreshing(true);
                query();
            }
        });
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });
        adapter.setOnRecyclerViewListener(new ConversationAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("c", adapter.getItem(position));
                startActivity(ChatActivity.class, bundle, false);
            }

            @Override
            public boolean onItemLongClick(final int position) {
                //以下两种方式均可以删除会话
//                BmobIM.getInstance().deleteConversation(adapter.getItem(position).getConversationId());
                Dialog d = new Dialog(getActivity(), "警告", "删除该对话？");
                d.addCancelButton("取消");
                d.setOnAcceptButtonClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        BmobIM.getInstance().deleteConversation(adapter.getItem(position));
                        adapter.remove(position);
                    }
                });
                d.show();
                return true;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        sw_refresh.setRefreshing(true);
        if (BmobUser.getCurrentUser(getContext())!=null){
            query();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 查询本地会话
     */
    public void query() {
        adapter.bindDatas(BmobIM.getInstance().loadAllConversation());
        adapter.notifyDataSetChanged();
        sw_refresh.setRefreshing(false);
        if (adapter.getItemCount()==0){
            Snackbar.make(sw_refresh, "暂时没有新消息", Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    public void onEventMainThread(OfflineMessageEvent event) {
        Log.i("message", "onEventMainThread_OfflineMessageEvent");
        //重新刷新列表
        adapter.bindDatas(BmobIM.getInstance().loadAllConversation());
//        adapter.notifyDataSetChanged();
    }

    /**
     * 注册消息接收事件
     *
     * @param event 1、与用户信息相关的开放给开发者维护，SDK内部只存储用户信息
     *              2、开发者获取到信息后，可调用SDK内部提供的方法更新会话
     */
    public void onEventMainThread(final MessageEvent event) {
        Log.i("message","onEventMainThread");
        final BmobIMMessage msg = event.getMessage();
        final BmobIMConversation conversation = event.getConversation();
        String username = msg.getBmobIMUserInfo().getName();
        String title = conversation.getConversationTitle();
        MyUtils.showLog("message",username+" "+title);
        //sdk内部，将新会话的会话标题用objectId表示，因此需要比对用户名和会话标题--单聊，后续会根据会话类型进行判断
        //这里只是提供一种思路，来告诉开发者如何更新会话信息，
        //用户信息的更新可参照SearchUserActivity类中的BmobIM.getInstance().startPrivateConversation方法，传入最新的用户信息即可
        if (!username.equals(title)) {
            BmobQuery<MyUser> query = new BmobQuery<>();
            query.addWhereEqualTo("objectId", msg.getFromId());
            query.findObjects(getContext(), new FindListener<MyUser>() {
                @Override
                public void onSuccess(List<MyUser> list) {
                    if (list != null && list.size() > 0) {
                        MyUser s = list.get(0);
                        conversation.setConversationIcon(s.getAvator());
                        conversation.setConversationTitle(s.getUsername());
                        BmobIMUserInfo info=new BmobIMUserInfo(msg.getFromId(),s.getUsername(),s.getAvator());
                        MyUser user=BmobUser.getCurrentUser(getContext(),MyUser.class);
                        BmobIMUserInfo info2=new BmobIMUserInfo(msg.getToId(),user.getUsername(),user.getAvator());
                        List<BmobIMUserInfo> list1=new ArrayList<BmobIMUserInfo>();
                        list1.add(info);
                        list1.add(info2);
                        BmobIM.getInstance().updateBatchUserInfo(list1);
                        //更新会话资料
                        BmobIM.getInstance().updateConversation(conversation);
                        //获取本地消息并刷新列表
                        adapter.bindDatas(BmobIM.getInstance().loadAllConversation());
                    }
                }

                @Override
                public void onError(int i, String s) {
//                    listener.internalDone(new BmobException(i,s));
                }
            });
        } else {
            //获取本地消息并刷新列表
            adapter.bindDatas(BmobIM.getInstance().loadAllConversation());
            adapter.notifyDataSetChanged();
        }

    }
    @Override
    public void initViews() {
        myactionbar = (myActionBar) getActivity().findViewById(
                R.id.myActionBar2);
        myactionbar.setButtonVisible(false, false);
        myactionbar.setTitleText("消息");
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
