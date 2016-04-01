package com.erlema.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.erlema.bean.CircleImageView;
import com.erlema.bean.Comments;
import com.erlema.bean.MyGoods;
import com.erlema.bean.MyUser;
import com.erlema.bean.TagChexkBox;
import com.erlema.config.Constants;
import com.erlema.util.MyUtils;
import com.erlema.adapter.fundcommentsAdapter;
import com.erlema.adapter.fundcommentsAdapter.onAvatorClickListener;
import com.erlema.bean.myActionBar;
import com.erlema.event.ChatEvent;
import com.erlema.fragment.ToolbarFragmentWrite;
import com.example.erlema.R;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonIcon;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.event.EventBus;

public class GoodsDetailsActivity extends FragmentActivity {

    private SwipeRefreshLayout sw;
    private myActionBar myctionBar;
    private ViewPager mViewPager;
    private ListView mListView;
    private LinearLayout tagLayout;//标签容器
    private ImageView tool_comm;
    private Button tool_buy;
    private ButtonIcon tool_star;
    private ImageView[] imageViews;
    private TextView index, zamCount, commentsCount, price, sellerName, sellerSchool,
            sellerCredibility, goodstitle, goodsDescrib, time;
    private CircleImageView sellerAcator;
    private List<Comments> comments;
    private fundcommentsAdapter comAdapter;
    private MyUser localUser;
    String goodsId = null;
    private int headCount = 3;
    String userId, userName, answerTo;
    String title = null;
    MyGoods goods = null;
    MyUser seller = null;
    String[] imgs = null;
    private boolean isMe = false;
    private boolean isBuyer = false;

    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        super.onCreate(arg0);
        setContentView();
        initViews();
        initListeners();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Constants.RESULT_DELETE) {
                String[] imgs=this.goods.getImgUrl();
            //删除
                MyGoods goods = new MyGoods();
                goods.setObjectId(goodsId);
                  for (int i = 0; i < imgs.length; i++) {
                      BmobFile file = new BmobFile();
                      file.setUrl(imgs[i]);
                      file.delete(getApplicationContext(), null);
                  }
                goods.delete(getApplicationContext(), new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        deleteComments(goodsId);
                        GoodsDetailsActivity.this.finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Snackbar.make(tool_buy, "error:" + s, Snackbar.LENGTH_SHORT).show();
                    }
                });
            } else if (resultCode == Constants.RESULT_COMPLETE) {
                if (goods.getStatus() != 3) {
                    final String id = data.getStringExtra("userID");
                    final String name = data.getStringExtra("userName");
                    goods.setBuyer(id);
                    goods.setStatus(Constants.GOODS_STATUS_SALD);
                    goods.update(getApplicationContext(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            BmobIMUserInfo info = new BmobIMUserInfo(id, name, null);
                            BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
                                @Override
                                public void done(BmobIMConversation c, BmobException e) {
                                    if (e == null) {
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("c", c);
                                        bundle.putString("goodsName", goods.getTitle());
                                        bundle.putBoolean("done", true);
                                        bundle.putString("BuyerId", id);
                                        bundle.putString("goodsId", goods.getObjectId());
                                        startActivity(ChatActivity.class, bundle, false);
                                        GoodsDetailsActivity.this.finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), e.getMessage() + "(" + e.getErrorCode() + ")", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            MyUtils.showTost(GoodsDetailsActivity.this, "网络连接失败，请稍候再试");
                        }
                    });
                } else {
                    Snackbar.make(tool_buy, "交易已完成", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void deleteComments(final String goodsId) {
        BmobQuery<Comments> bq = new BmobQuery<Comments>();
        bq.addWhereEqualTo("goodsID", goodsId);
        bq.findObjects(getApplicationContext(), new FindListener<Comments>() {
            @Override
            public void onSuccess(List<Comments> list) {
                List<BmobObject> lists = new ArrayList<BmobObject>();
                for (int i = 0; i < list.size(); i++) {
                    Comments com = new Comments();
                    com.setObjectId(list.get(i).getObjectId());
                    lists.add(com);
                }
                new BmobObject().deleteBatch(getApplicationContext(),
                        lists, new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                //批量删除只支持50个，递归删除
                                deleteComments(goodsId);
                            }

                            @Override
                            public void onFailure(int i, String s) {

                            }
                        });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    public void setContentView() {
        setContentView(R.layout.activity_goods_details);
        localUser = BmobUser.getCurrentUser(getApplicationContext(),
                MyUser.class);
    }

    @SuppressWarnings("deprecation")
    public void initViews() {
        goodsId = getIntent().getStringExtra("goodsId");
        MyUtils.showLog("ttt", goodsId + "");
        getGoods();
        myctionBar = (myActionBar) findViewById(R.id.myActionBar);
        title = getIntent().getStringExtra("GoodsTitle");
        // l = (LinearLayout) findViewById(R.id.floadbar);
        zamCount = (TextView) findViewById(R.id.tool_zam_count);
        commentsCount = (TextView) findViewById(R.id.toolbar_com_count);
        tool_comm = (ImageView) findViewById(R.id.toolbar_comments);
        tool_buy = (Button) findViewById(R.id.tool_buy);
        tool_star = (ButtonIcon) findViewById(R.id.tool_zam);
        if (!zamed) {
            tool_star.setDrawableIcon(getResources().getDrawable(
                    R.drawable.zan_no));
        }
        mListView = (ListView) findViewById(R.id.details_istView);
        sw = (SwipeRefreshLayout) findViewById(R.id.details_swp);
        sw.setColorScheme(R.color.main_color, R.color.red, R.color.green);
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                sw.setRefreshing(true);
            }
        });
        List<String> cos = localUser.getCollection();
        for (String string : cos) {
            if (string.equals(goodsId)) {
                tool_star.setDrawableIcon(getResources().getDrawable(
                        R.drawable.zan_yes));
                zamed = true;
                break;
            }
        }
    }

    private void getSeller(String id) {
        BmobQuery<MyUser> bq = new BmobQuery<MyUser>();
        bq.getObject(getApplicationContext(), id, new GetListener<MyUser>() {

            @Override
            public void onFailure(int arg0, String arg1) {

            }

            @Override
            public void onSuccess(MyUser arg0) {
                seller = arg0;
                if (seller.getAvator() != null) {
                    Glide.with(GoodsDetailsActivity.this)
                            .load(seller.getAvator()).into(sellerAcator);
                }
                sellerName.setText(seller.getUsername());
                sellerSchool.setText(seller.getSchool());
                sellerCredibility.setText("友善度:" + seller.getCredibility());
            }
        });
    }

    private void getGoods() {
        BmobQuery<MyGoods> bq = new BmobQuery<MyGoods>();
        bq.getObject(getApplicationContext(), goodsId,
                new GetListener<MyGoods>() {

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        MyUtils.showTost(GoodsDetailsActivity.this, "物品已删除或网络存在问题");
                    }

                    @Override
                    public void onSuccess(MyGoods arg0) {
                        goods = arg0;
                        imgs = goods.getImgUrl();
                        imageViews = new ImageView[imgs.length];
                        index.setText("1/" + imgs.length);
                        getSeller(goods.getOwerID());
                        if (localUser.getObjectId().equals(goods.getOwerID())) {
                            isMe = true;
                            tool_buy.setText("管理");
                            if (goods.getStatus() == 2) {
                                tool_buy.setText("交易进行中");
                            }
                            if (goods.getStatus() == 3) {
                                tool_buy.setText("交易完成");
                            }
                        } else if (localUser.getObjectId().equals(goods.getBuyer())) {
                            isBuyer = true;
                            tool_buy.setText("评价");
                            if (goods.getStatus() == 3) {
                                tool_buy.setText("已评价");
                            }
                        }
                        goodstitle.setText(goods.getTitle());
                        price.setText("￥" + goods.getPirce());
                        String tags = "";
                        List<Integer> spposition = new ArrayList<Integer>();
                        spposition.add(0);
                        String[] deg = new String[]{"五成新以下", "7成新", "8成新",
                                "9成新", "9.5成新", "全新"};
                        tags += " " + deg[goods.getDegree()];
                        TagChexkBox chexkBox = new TagChexkBox(getApplicationContext());
                        chexkBox.setText(tags);
                        chexkBox.setSelect(false);
                        chexkBox.setClickable(false);
                        tagLayout.addView(chexkBox);
                        for (int i = 0; i < goods.getTags().size(); i++) {
                            TagChexkBox chexkBox1 = new TagChexkBox(getApplicationContext());
                            chexkBox1.setText(goods.getTags().get(i));
                            chexkBox1.setSelect(false);
                            tagLayout.addView(chexkBox1);
                        }

                        time.setText(MyUtils.getTimeElapse(goods.getCreatedAt()));
                        goodsDescrib.setText(goods.getDescrib() == null
                                || goods.getDescrib().trim().equals("") ? "该用户很懒，什么描述都没有留下"
                                : goods.getDescrib());
                        zamCount.setText(goods.getStarCount() + "");
                        mViewPager.setAdapter(new myPagerAdapter());
                        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

                            @Override
                            public void onPageSelected(int arg0) {

                                index.setText(arg0 + 1 + "/" + imgs.length);
                            }

                            @Override
                            public void onPageScrolled(int arg0, float arg1, int arg2) {

                            }

                            @Override
                            public void onPageScrollStateChanged(int arg0) {
                            }
                        });
                    }
                });
    }

    boolean zamed = false;
    View mcontent;

    @SuppressWarnings("deprecation")
    public void initListeners() {
        sw.setOnRefreshListener(onRefreshListener);
        myctionBar.setButtonVisible(true, false);
        myctionBar.setTitleText("物品详情");
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //点击用户资料
                if (position == 1 && seller != null) {
                    Intent intent = new Intent(GoodsDetailsActivity.this,
                            AboutUserActivity.class);
                    intent.putExtra("userId", seller.getObjectId());
                    startActivity(intent);
                }
                //点击评论
                if (comments.size() > 0 && position >= headCount && !sw.isRefreshing()) {
                    answerTo = comments.get(position - headCount).getUsername();
                    openInputWindow();
                }
            }
        });

		/*
         * 评论按钮
		 */
        tool_comm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                answerTo = null;
                openInputWindow();
                /*
                 * 滑动到评论最后一条
				 */
                mListView.setSelection(comAdapter.getCount());
            }
        });

		/*
         * 购买按钮
		 */
        tool_buy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //添加对话
                if (seller != null) {
                    if (isMe) {//自己发布的
                        if (goods.getStatus() <= 2) {//交易未完成
                            Bundle bundle = new Bundle();
                            bundle.putStringArray("buyers", goods.getWantBuyers().toArray(new String[]{}));
                            Intent in = new Intent(GoodsDetailsActivity.this, ManageGoodsActivity.class);
                            in.putExtra("buyers", bundle);
                            startActivityForResult(in, 0);
                        } else {//交易完成
                            Bundle bundle = new Bundle();
                            bundle.putStringArray("buyers", new String[]{goods.getBuyer()});
                            Intent in = new Intent(GoodsDetailsActivity.this, ManageGoodsActivity.class);
                            in.putExtra("buyers", bundle);
                            startActivityForResult(in, 0);
                        }
                    } else if (isBuyer) {
                        new AlertDialog.Builder(GoodsDetailsActivity.this)
                                .setTitle("请评价")
                                .setMessage("若选择差评将扣除对方友善度，该操作不可逆！")
                                .setPositiveButton("好评", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (goods.getStatus() != 3) {
                                            goods.setStatus(3);
                                            goods.update(getApplicationContext(), new UpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    localUser.addBuys(goodsId);
                                                    localUser.update(getApplicationContext(), new UpdateListener() {
                                                        @Override
                                                        public void onSuccess() {
                                                            BmobIMUserInfo info = new BmobIMUserInfo(seller.getObjectId(), seller.getUsername(), seller.getAvator());
                                                            BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
                                                                @Override
                                                                public void done(BmobIMConversation c, BmobException e) {
                                                                    if (e == null) {
                                                                        Bundle bundle = new Bundle();
                                                                        bundle.putSerializable("c", c);
                                                                        bundle.putString("goodsName", goods.getTitle());
                                                                        bundle.putBoolean("adone", true);
                                                                        bundle.putString("goodsId", goods.getObjectId());
                                                                        startActivity(ChatActivity.class, bundle, false);
                                                                        pushToHim(seller.getUsername(), "有交易完成啦，快到到消息中心查看吧~", Constants.ACTION_MESSAGECENTER);
                                                                        GoodsDetailsActivity.this.finish();
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(), e.getMessage() + "(" + e.getErrorCode() + ")", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onFailure(int i, String s) {

                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onFailure(int i, String s) {

                                                }
                                            });
                                        } else {
                                            Snackbar.make(tool_buy, "已经评价过啦！", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton("差评", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (goods.getStatus() != 3) {
                                            //test对应你刚刚创建的云端逻辑名称
                                            String cloudCodeName = "chapin";
                                            JSONObject params = new JSONObject();
                                            //name是上传到云端的参数名称，值是bmob，云端逻辑可以通过调用request.body.name获取这个值
                                            try {
                                                params.put("objectId", seller.getObjectId());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            //创建云端逻辑对象
                                            AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
                                            //异步调用云端逻辑
                                            cloudCode.callEndpoint(GoodsDetailsActivity.this, cloudCodeName, params, new CloudCodeListener() {
                                                @Override
                                                public void onSuccess(Object o) {
                                                    Toast.makeText(getApplicationContext(), "扣除对方信誉度“1”", Toast.LENGTH_LONG).show();
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {
                                                }
                                            });


                                            goods.setStatus(3);
                                            goods.update(getApplicationContext());
                                            localUser.addBuys(goodsId);
                                            localUser.update(getApplicationContext(), new UpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    BmobIMUserInfo info = new BmobIMUserInfo(seller.getObjectId(), seller.getUsername(), seller.getAvator());
                                                    BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
                                                        @Override
                                                        public void done(BmobIMConversation c, BmobException e) {
                                                            if (e == null) {
                                                                Bundle bundle = new Bundle();
                                                                bundle.putSerializable("c", c);
                                                                bundle.putString("goodsName", goods.getTitle());
                                                                bundle.putBoolean("adone", true);
                                                                bundle.putString("goodsId", goods.getObjectId());
                                                                startActivity(ChatActivity.class, bundle, false);
                                                                pushToHim(seller.getUsername(), "有交易完成,对方对您差评，快到到消息中心查看吧~", Constants.ACTION_MESSAGECENTER);
                                                                GoodsDetailsActivity.this.finish();
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), e.getMessage() + "(" + e.getErrorCode() + ")", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onFailure(int i, String s) {

                                                }
                                            });
                                        } else {
                                            Snackbar.make(tool_buy, "已经评价过啦！", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .show();
                    } else {
                        Dialog d = new Dialog(GoodsDetailsActivity.this, "警告", "向对方发出购买请求吗?");
                        d.addCancelButton("取消");
                        d.setOnAcceptButtonClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                //如果需要更新用户资料，开发者只需要传新的info进去就可以了
                                BmobIMUserInfo info = new BmobIMUserInfo(seller.getObjectId(), seller.getUsername(), seller.getAvator());
                                BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
                                    @Override
                                    public void done(BmobIMConversation c, BmobException e) {
                                        if (e == null) {
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("c", c);
                                            bundle.putString("goodsName", goods.getTitle());
                                            bundle.putBoolean("wantBuy", true);
                                            bundle.putString("goodsId", goods.getObjectId());
                                            startActivity(ChatActivity.class, bundle, false);
                                            GoodsDetailsActivity.this.finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), e.getMessage() + "(" + e.getErrorCode() + ")", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                pushToHim(seller.getUsername(), "有人想买你的东西，快到到消息中心查看吧~", Constants.ACTION_MESSAGECENTER);
                                goods.addBuyer(localUser.getObjectId());
                                goods.update(getApplicationContext());
                            }
                        });
                        d.show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "网络异常或者对方账户异常,请稍后再试", Toast.LENGTH_SHORT).show();
                }
            }
        });

		/*
         * 点赞按钮
		 */
        tool_star.setOnClickListener(new OnClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (zamed) {
                    tool_star.setDrawableIcon(getResources().getDrawable(
                            R.drawable.zan_no));
                    if (goods != null) {
                        goods.increment("starCount", -1);
                        goods.update(getApplicationContext());
                    }
                    int zam = Integer.valueOf(zamCount.getText() + "");
                    zamCount.setText(--zam + "");
                    localUser.removeCollection(goodsId);
                    localUser.update(getApplicationContext());
                    zamed = false;
                } else {
                    tool_star.setDrawableIcon(getResources().getDrawable(
                            R.drawable.zan_yes));
                    if (goods != null) {
                        goods.increment("starCount", 1);
                        goods.update(getApplicationContext());
                    }
                    int zam = Integer.valueOf(zamCount.getText() + "");
                    zamCount.setText(++zam + "");
                    localUser.addCollection(goodsId);
                    localUser.update(getApplicationContext());
                    zamed = true;
                }
            }
        });

        // public void click() {
        // Intent viewIma=new Intent(getApplicationContext(),
        // ImageActivity.class);
        // viewIma.putExtra("ImageUrls", imgs);
        // startActivity(viewIma);
        // }
    }


    @SuppressWarnings("deprecation")
    public void initData() {
        comments = new ArrayList<Comments>();

        comAdapter = new fundcommentsAdapter(GoodsDetailsActivity.this, comments);
        mListView.setAdapter(comAdapter);
        View imgViewpager = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.detail_img, mListView, false);
        index = (TextView) imgViewpager.findViewById(R.id.detail_index);
        mViewPager = (ViewPager) imgViewpager
                .findViewById(R.id.details_viewpager);
        mListView.addHeaderView(imgViewpager);// 图片viewpager
        View v = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.seller_ifo, null, false);
        sellerAcator = (CircleImageView) v.findViewById(R.id.seller_avatar);
        price = (TextView) v.findViewById(R.id.seller_price);
        sellerName = (TextView) v.findViewById(R.id.seller_name);
        sellerSchool = (TextView) v.findViewById(R.id.tv_school);
        sellerCredibility = (TextView) v.findViewById(R.id.seller_credibility);
        goodstitle = (TextView) v.findViewById(R.id.seller_title);
        mcontent = v;
        mListView.addHeaderView(v);// 卖方个人信息
        View v1 = LayoutInflater.from(GoodsDetailsActivity.this).inflate(
                R.layout.goods_ifo, null, false);
        time = (TextView) v1.findViewById(R.id.seller_time);
        goodsDescrib = (TextView) v1.findViewById(R.id.seller_discribe);
        // final RelativeLayout relativeLayout=(RelativeLayout)
        // findViewById(R.id.centent);
        // relativeLayout.addView(v1);
        mListView.addHeaderView(v1);// 标签及描述信息
        tagLayout = (LinearLayout) v1.findViewById(R.id.tags);
        View emptyView = LayoutInflater.from(GoodsDetailsActivity.this).inflate(R.layout.none, null, false);
        mListView.setEmptyView(emptyView);
        // mcontent=mListView.getChildAt(1);
        mListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= 1) {
                    // ShowToast("...");
                    // mListView.removeHeaderView(mcontent);
                    // ((ListView)mcontent.getParent()).removeHeaderView(mcontent);
                    // l.addView(mcontent);

                } else {
                    if (mcontent != null) {
                        // l.removeAllViews();
                        // mListView.addHeaderView(mcontent);
                    }
                }
            }
        });
        getComments();
    }

    OnRefreshListener onRefreshListener = new OnRefreshListener() {

        @Override
        public void onRefresh() {
            mListView.setEnabled(false);
            reflashComments(false);
        }
    };

    /*
     * ViewPagerAdapter
     */
    private class myPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = new ImageView(getApplicationContext());
            iv.setScaleType(ScaleType.CENTER_CROP);
            Glide.with(GoodsDetailsActivity.this).load(imgs[position])
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(iv);
            imageViews[position] = iv;
            final int index = position;
            imageViews[position].setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent viewIma = new Intent(getApplicationContext(),
                            ImageActivity.class);
                    viewIma.putExtra("ImageUrls", imgs);
                    viewIma.putExtra("index", index);
                    startActivity(viewIma);
                }
            });
            container.addView(imageViews[position]);
            return imageViews[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews[position]);
        }

        @Override
        public int getCount() {
            return imgs == null ? 0 : imgs.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    boolean srollToBottom = false;

    public void reflashComments(Boolean bottom) {
        comments.clear();
        comAdapter.notifyDataSetChanged();
        srollToBottom = bottom;
        getComments();
    }

    /*
     * 获取评论数据
     */
    private void getComments() {
        BmobQuery<Comments> bq = new BmobQuery<Comments>();
        bq.addWhereEqualTo("goodsID", goodsId);
        bq.order("createdAt");
        bq.findObjects(GoodsDetailsActivity.this, new FindListener<Comments>() {

            @Override
            public void onSuccess(List<Comments> arg0) {
                commentsCount.setText(arg0.size() + "");
                for (Comments c : arg0) {
                    comments.add(c);
                }
                MyUtils.showLog("comments", "success" + comments.size());
                comAdapter.notifyDataSetChanged();
                if (srollToBottom) {
                    mListView.setSelection(comAdapter.getCount());
                }
                mListView.setEnabled(true);
                sw.setRefreshing(false);
                srollToBottom = false;
                comAdapter.SetOnAvatorClickListener(new onAvatorClickListener() {

                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(GoodsDetailsActivity.this,
                                AboutUserActivity.class);
                        intent.putExtra("userId", comments.get(position).getUserId());
                        MyUtils.showLog("about", comments.get(position).toString() + "goodsDetails" + comments.get(position).getUserId());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onError(int arg0, String arg1) {
                MyUtils.showLog("comments", arg1);
                mListView.setEnabled(true);
                sw.setRefreshing(false);
                srollToBottom = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(ChatEvent event) {
        //如果需要更新用户资料，开发者只需要传新的info进去就可以了
        BmobIM.getInstance().startPrivateConversation(event.info, new ConversationListener() {
            @Override
            public void done(BmobIMConversation c, BmobException e) {
                if (e == null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("c", c);
                    startActivity(ChatActivity.class, bundle, false);
                } else {
                    Toast.makeText(getApplicationContext(), e.getMessage() + "(" + e.getErrorCode() + ")", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pushToHim(String name, String msg, final int action) {
        BmobQuery<MyUser> bq = new BmobQuery<MyUser>();
        bq.addWhereEqualTo("username", name);
        final String text = msg;
        bq.findObjects(getApplicationContext(),
                new FindListener<MyUser>() {

                    @Override
                    public void onSuccess(List<MyUser> arg0) {
                        if (arg0.size() > 0) {
                            String installationId = arg0
                                    .get(0)
                                    .getInstallationID();
                            if (installationId != null) {
                                BmobPushManager bmobPush = new BmobPushManager(
                                        getApplicationContext());
                                BmobQuery<BmobInstallation> query = BmobInstallation
                                        .getQuery();
                                query.addWhereEqualTo(
                                        "installationId",
                                        installationId);
                                bmobPush.setQuery(query);
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("alert", text);
                                    jsonObject.put("action", action);
                                    bmobPush.pushMessage(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(int arg0,
                                        String arg1) {

                    }
                });

    }

    public void startActivity(Class<? extends Activity> target, Bundle bundle, boolean finish) {
        Intent intent = new Intent();
        intent.setClass(this, target);
        if (bundle != null)
            intent.putExtra(getPackageName(), bundle);
        startActivity(intent);
        if (finish)
            finish();
    }

    /*
     * 打开输入框
     */
    private void openInputWindow() {
        ToolbarFragmentWrite fragment = ToolbarFragmentWrite.newInstance(
                userId, userName, answerTo, goodsId);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.toolbar, fragment);
        ft.addToBackStack(null);
        ft.commit();
        /*
         * 打开输入法
		 */
        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public String getSellerName() {
        if (seller != null) {
            return seller.getUsername();
        } else {
            return "";
        }
    }
}
