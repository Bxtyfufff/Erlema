package com.erlema.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.erlema.R;
import com.erlema.adapter.ChatAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.v3.exception.BmobException;

/**
 * 聊天界面
 *
 * @author :smile
 * @project:ChatActivity
 * @date :2016-01-25-18:23
 */
public class ChatActivity extends BaseActivity {

    LinearLayout ll_chat;

    SwipeRefreshLayout sw_refresh;

    RecyclerView rc_view;
    boolean wantBuy =false;
    boolean done=false;//交易完成
    boolean adone=false;//评论完成
    EditText edit_msg;

    Button btn_chat_send;
    private com.erlema.bean.myActionBar myActionBar;
    ChatAdapter adapter;
    protected LinearLayoutManager layoutManager;
    BmobIMConversation c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_chat);
    }

    @Override
    public void initViews() {
        c = BmobIMConversation.obtain(BmobIMClient.getInstance(), (BmobIMConversation) getBundle().getSerializable("c"));
        edit_msg= (EditText) findViewById(R.id.edit_msg);
        btn_chat_send= (Button) findViewById(R.id.btn_chat_send);
        myActionBar = (com.erlema.bean.myActionBar) findViewById(R.id.chat_myactionbar);
        myActionBar.setButtonVisible(true, false);
        myActionBar.setTitleText(c.getConversationTitle().toString());
        sw_refresh= (SwipeRefreshLayout) findViewById(R.id.sw_refresh);
        sw_refresh.setColorScheme(R.color.main_color, R.color.red, R.color.green);
        sw_refresh.setEnabled(true);
        layoutManager = new LinearLayoutManager(this);
        rc_view= (RecyclerView) findViewById(R.id.rc_view);
        rc_view.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(this);
        rc_view.setAdapter(adapter);
        ll_chat= (LinearLayout) findViewById(R.id.ll_chat);
        ll_chat.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll_chat.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                sw_refresh.setRefreshing(true);
                //自动刷新
                queryMessages(null);
            }
        });
        //下拉加载
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobIMMessage msg = adapter.getFirstMessage();
                queryMessages(msg);
            }
        });
    }

    private void processExtraData() {
        wantBuy =getBundle().getBoolean("wantBuy", false);
        done=getBundle().getBoolean("done",false);
        adone=getBundle().getBoolean("adone",false);
    }

    @Override
    public void initListeners() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);//must store the new intent unless getIntent() will return the old one
        initData();
        super.onNewIntent(intent);
    }

    @Override
    public void initData() {
        processExtraData();
        edit_msg.post(new Runnable() {
            @Override
            public void run() {
                //点击了买买买
                if (wantBuy){
                    sendBuyMessage();
                }
                if (adone){//评价完成
                    BmobIMTextMessage msg = new BmobIMTextMessage();
                    msg.setContent("确认交易完成"+getBundle().get("goodsName"));
                    //可设置额外信息
                    Map<String, Object> map = new HashMap<>();
                    map.put("level", "2");//随意增加信息
                    map.put("goodsId", getBundle().getString("goodsId"));//随意增加信息
                    msg.setExtraMap(map);
                    c.sendMessage(msg, new MessageSendListener() {
                        @Override
                        public void onStart(BmobIMMessage msg) {
                            super.onStart(msg);
                            adapter.addMessage(msg);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void done(BmobIMMessage msg, BmobException e) {
                            if (e == null) {
//                    Logger.i("send success");
                                adapter.notifyDataSetChanged();
                    scrollToBottom();
//                        edit_msg.setText("");
                            } else {
//                    Logger.e(e);
                            }
                        }
                    });
                    scrollToBottom();
                    done=false;
                }
                if (done){//完成交易
                    BmobIMTextMessage msg = new BmobIMTextMessage();
                    msg.setContent("请确认交易："+getBundle().get("goodsName"));
                    //可设置额外信息
                    Map<String, Object> map = new HashMap<>();
                    map.put("level", "2");//随意增加信息
                    map.put("goodsId", getBundle().getString("goodsId"));//随意增加信息
                    msg.setExtraMap(map);
                    c.sendMessage(msg, new MessageSendListener() {
                        @Override
                        public void onStart(BmobIMMessage msg) {
                            super.onStart(msg);
                            adapter.addMessage(msg);
//                    adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void done(BmobIMMessage msg, BmobException e) {
                            if (e == null) {
//                    Logger.i("send success");
                                adapter.notifyDataSetChanged();
                    scrollToBottom();
//                        edit_msg.setText("");
                            } else {
//                    Logger.e(e);
                            }
                        }
                    });
                    scrollToBottom();
                    done=false;
                }
            }
        });
        scrollToBottom();
    }

    private void sendBuyMessage() {
            Log.i("buy", "wantBuy");
            BmobIMTextMessage msg = new BmobIMTextMessage();
            msg.setContent("想买"+getBundle().get("goodsName"));
            //可设置额外信息
            Map<String, Object> map = new HashMap<>();
            map.put("level", "0");//随意增加信息
            map.put("goodsId", getBundle().getString("goodsId"));//随意增加信息
        msg.setExtraMap(map);
            c.sendMessage(msg, new MessageSendListener() {
                @Override
                public void onStart(BmobIMMessage msg) {
                    super.onStart(msg);
                    adapter.addMessage(msg);
//                    adapter.notifyDataSetChanged();
                }

                @Override
                public void done(BmobIMMessage msg, BmobException e) {
                    if (e == null) {
//                    Logger.i("send success");
                        adapter.notifyDataSetChanged();
                    scrollToBottom();
//                        edit_msg.setText("");
                    } else {
//                    Logger.e(e);
                    }
                }
            });
            scrollToBottom();
            wantBuy=false;
    }

    /**
     * 首次加载，可设置msg为null，下拉刷新的时候，默认取消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列
     *
     * @param msg
     */
    public void queryMessages(BmobIMMessage msg) {
        c.queryMessages(msg, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                sw_refresh.setRefreshing(false);
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        adapter.addMessages(list);
                        adapter.notifyDataSetChanged();
                    }
                } else {
//                    toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                }
            }
        });
    }

    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
    }

    public void onSendClick(View view) {
        scrollToBottom();
        send();
    }

    private void send() {
        String text = edit_msg.getText().toString();
        if (TextUtils.isEmpty(text.trim())) {
            Toast.makeText(getApplicationContext(), "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobIMTextMessage msg = new BmobIMTextMessage();
        msg.setContent(text);
        //可设置额外信息
        Map<String, Object> map = new HashMap<>();
        map.put("level", "1");//随意增加信息
        msg.setExtraMap(map);
        c.sendMessage(msg, new MessageSendListener() {
            @Override
            public void onStart(BmobIMMessage msg) {
                super.onStart(msg);
                adapter.addMessage(msg);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {
//                    Logger.i("send success");
                    adapter.notifyDataSetChanged();
//                    scrollToBottom();
                    edit_msg.setText("");
                } else {
//                    Logger.e(e);
                }
                scrollToBottom();
            }
        });
        scrollToBottom();
    }

    /**
     * 接收到聊天消息
     *
     * @param event
     */
    public void onEventMainThread(MessageEvent event) {
        if (c != null && event != null && c.getConversationId().equals(event.getConversation().getConversationId())) {//如果是当前会话的消息
            BmobIMMessage msg = event.getMessage();
            adapter.addMessage(msg);
//            adapter.notifyDataSetChanged();
            adapter.notifyItemInserted(adapter.getItemCount());
            //更新该会话下面的已读状态
            c.updateReceiveStatus(msg);
            scrollToBottom();
        }
    }

    @Override
    protected void onDestroy() {
        //更新此会话的所有消息为已读状态
        c.updateLocalCache();
        super.onDestroy();
    }
}
