package com.erlema.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

import com.bumptech.glide.Glide;
import com.erlema.adapter.fundcommentsAdapter;
import com.erlema.bean.CircleImageView;
import com.erlema.bean.Comments;
import com.erlema.bean.GoodsWanted;
import com.erlema.bean.MyUser;
import com.erlema.fragment.ToolbarFragmentWrite;
import com.erlema.util.MyUtils;
import com.erlema.bean.wantedmcommentsAdapter;
import com.erlema.bean.myActionBar;
import com.erlema.config.Constants;
import com.example.erlema.R;

public class WantedGoodsDetailsActivity extends FragmentActivity {
    private SwipeRefreshLayout sw;
    private myActionBar myactionBar;
    private ListView mListView;
    private FrameLayout toolBar;
    private wantedmcommentsAdapter comAdapter;
    private List<Comments> comments;
    private TextView index, price, sellerName, sellerSchool, sellerCredibility, goodstitle,
            goodsDescrib, degree, time;
    private CircleImageView sellerAcator;
    private String wandedId;
    String answerTo;
    private MyUser localUser;
    private ToolbarFragmentWrite toolbarFragmentWrite;

    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        super.onCreate(arg0);
        setContentView();
        initViews();
        initListeners();
        initData();
    }

    public void setContentView() {
        setContentView(R.layout.activity_wanted_goods_details);
        localUser = BmobUser.getCurrentUser(getApplicationContext(),
                MyUser.class);
        // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, Constants.Bmob_APPID);
    }

    public void initViews() {
        wandedId = getIntent().getStringExtra("goodsId");
        MyUtils.showLog("ttt", wandedId + "" + getIntent().toString());
        myactionBar = (myActionBar) findViewById(R.id.myActionBar);
        myactionBar.setTitleText("求购详情");
        mListView = (ListView) findViewById(R.id.details_istView);
        sellerAcator = (CircleImageView) findViewById(R.id.seller_avatar);
        price = (TextView) findViewById(R.id.seller_price);
        sellerName = (TextView) findViewById(R.id.seller_name);
        sellerSchool = (TextView) findViewById(R.id.tv_school);
        sellerCredibility = (TextView) findViewById(R.id.seller_credibility);
        goodstitle = (TextView) findViewById(R.id.seller_title);
        time = (TextView) findViewById(R.id.seller_time);
        goodsDescrib = (TextView) findViewById(R.id.seller_discribe);
        sw = (SwipeRefreshLayout) findViewById(R.id.details_swp);
        sw.setColorScheme(R.color.main_color, R.color.red, R.color.green);
        toolBar = (FrameLayout) findViewById(R.id.toolbar);
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                sw.setRefreshing(true);
            }
        });
    }

    public void initListeners() {
        sw.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                mListView.setEnabled(false);
                reflashComments(false);
                getGoods();
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                answerTo = comments.get(position).getUsername();
                if (toolbarFragmentWrite != null) {
                    toolbarFragmentWrite.setHint("回复：" + answerTo);
                }
                openInputWindow();
            }
        });
    }

    public void initData() {
        comments = new ArrayList<Comments>();
        comAdapter = new wantedmcommentsAdapter(getApplicationContext(), comments);
        mListView.setAdapter(comAdapter);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        toolbarFragmentWrite = ToolbarFragmentWrite.newInstance(localUser.getObjectId(), localUser.getUsername(),
                answerTo, getGoodsId());
        transaction.replace(R.id.toolbar, toolbarFragmentWrite);
        transaction.commit();
        reflashComments(false);
        getGoods();
    }

    boolean srollToBottom = false;

    public void reflashComments(Boolean bottom) {// 刷新并滑动到底部
        comments.clear();
        getComments();
        srollToBottom = bottom;
    }

    /*
     * 获取评论数据
     */
    private void getComments() {
        BmobQuery<Comments> bq = new BmobQuery<Comments>();
        bq.addWhereEqualTo("goodsID", wandedId);
        bq.order("createdAt");
        bq.findObjects(getApplicationContext(), new FindListener<Comments>() {

            @Override
            public void onSuccess(List<Comments> arg0) {
                for (Comments c : arg0) {
                    comments.add(c);
                }
                MyUtils.showLog("comments", "success" + comments.size());
                comAdapter.notifyDataSetChanged();
                MyUtils.showLog("comments", comAdapter.getCount() - 1 + "");
                if (srollToBottom) {
                    mListView.setSelection(comAdapter.getCount());
                }
                comAdapter.SetOnAvatorClickListener(new wantedmcommentsAdapter.onAvatorClickListener() {
                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(WantedGoodsDetailsActivity.this,
                                AboutUserActivity.class);
                        intent.putExtra("userId", comments.get(position).getUserId());
                        startActivity(intent);
                    }
                });
                sw.setRefreshing(false);
                mListView.setEnabled(true);
                srollToBottom = false;
            }

            @Override
            public void onError(int arg0, String arg1) {
                MyUtils.showLog("comments", arg1);
                sw.setRefreshing(false);
                mListView.setEnabled(true);
            }
        });
    }

    MyUser seller;

    private void getSeller(String id) {
        BmobQuery<MyUser> bq = new BmobQuery<MyUser>();
        bq.getObject(getApplicationContext(), id, new GetListener<MyUser>() {

            @Override
            public void onFailure(int arg0, String arg1) {

            }

            @SuppressLint("NewApi")
            @Override
            public void onSuccess(MyUser arg0) {
                seller = arg0;
                if (seller.getAvator() != null) {
                    if (!WantedGoodsDetailsActivity.this.isDestroyed()) {
                        Glide.with(WantedGoodsDetailsActivity.this)
                                .load(seller.getAvator()).into(sellerAcator);
                    }
                }
                sellerAcator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击用户资料
                        if (seller != null) {
                            Intent intent = new Intent(WantedGoodsDetailsActivity.this,
                                    AboutUserActivity.class);
                            intent.putExtra("userId", seller.getObjectId());
                            startActivity(intent);
                        }
                    }
                });
                sellerName.setText(seller.getUsername());
                sellerSchool.setText(seller.getSchool());
                sellerCredibility.setText("友善度:" + seller.getCredibility());
            }
        });
    }

    GoodsWanted goods;

    private void getGoods() {
        BmobQuery<GoodsWanted> bq = new BmobQuery<GoodsWanted>();
        bq.getObject(getApplicationContext(), wandedId,
                new GetListener<GoodsWanted>() {

                    @Override
                    public void onFailure(int arg0, String arg1) {

                    }

                    @Override
                    public void onSuccess(GoodsWanted arg0) {
                        goods = arg0;
                        getSeller(goods.getOwerID());
                        goodstitle.setText(goods.getTitle());
                        price.setText("￥" + goods.getPirce_low() + "~"
                                + goods.getPirce_high());
                        time.setText(MyUtils.getTimeElapse(goods.getCreatedAt()));
                        goodsDescrib.setText(goods.getDescrib() == null
                                || goods.getDescrib().trim().equals("") ? "该用户很懒，什么描述都没有留下"
                                : goods.getDescrib());
                    }
                });
    }

    /*
     * 获取当前 goodsID
     */
    public String getGoodsId() {
        return wandedId;
    }

    /*
     * 获取answerTo(名字)
     */
    public String getAnswerTo() {
        return answerTo;
    }

    private void openInputWindow() {
        /*
		 * 打开输入法
		 */
        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public String getSellerName() {
        if (seller!=null){
            return seller.getUsername();
        }else {
            return "";
        }
    }
}
