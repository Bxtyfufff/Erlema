package com.erlema.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

import com.erlema.activity.GoodsDetailsActivity;
import com.erlema.activity.MainActivity;
import com.erlema.activity.RegisterActivity;
import com.erlema.activity.WebviewActivity;
import com.erlema.adapter.BackHandledInterface;
import com.erlema.adapter.TopNewsAdapter;
import com.erlema.bean.MyGoods;
import com.erlema.bean.MyRecyclViewAdapter;
import com.erlema.bean.MyRecyclViewAdapter.onRecClickListener;
import com.erlema.bean.MySchools;
import com.erlema.bean.MySubject;
import com.erlema.bean.MyUser;
import com.erlema.config.Constants;
import com.erlema.util.MyUtils;
import com.erlema.bean.MyViewFlipper;
import com.erlema.bean.OnRecvScrollListener;
import com.example.erlema.R;
import com.jude.rollviewpager.RollPagerView;

public class FoundFragment extends BaseFragment implements OnClickListener {
    protected BackHandledInterface mBackHandledInterface;
    private RecyclerView myRecycle;
    private RollPagerView rp_topNews;
    boolean once = true;
    private LinearLayout topbar;
    private LinearLayout catebar;
    private SwipeRefreshLayout sw;
    private static int gotcount = 0;
    private List<MyGoods> data = new ArrayList<MyGoods>();
    MyRecyclViewAdapter adapter;
    private int headCount = 0;
    private Button bytime, bydegree, bycount, bypricedown, bycate;
    private Spinner sp_location;
    private SearchView sv_search;
    private String order = "-createdAt";
    private String timeUp = "+createdAt";
    private String timeDown = "-createdAt";
    private String hotUp = "+clickCount";
    private String hotDown = "-clickCount";
    private String priceUp = "+pirce";//笔误
    private String priceDown = "-pirce";
    private String degUp = "+degree";
    private String degDown = "-degree";
    boolean price, hot, time, degree;
    private AppBarLayout appBarLayout;
    private ArrayAdapter<String> spSchooladapter1;
    private MyUser login_user;
    private List<String> urls = new ArrayList<>();
    private List<MySubject> subjectlist;
    private TopNewsAdapter newsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.found_layout, container,
                false);
        myRecycle = (RecyclerView) rootView.findViewById(R.id.fund_recycle);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.main_color));
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof BackHandledInterface)) {
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        } else {
            this.mBackHandledInterface = (BackHandledInterface) getActivity();
        }
        login_user = BmobUser
                .getCurrentUser(getActivity(), MyUser.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        //告诉FragmentActivity，当前Fragment在栈顶
        mBackHandledInterface.setSelectedFragment(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void initViews() {
        initRoolViewPager();
        topbar = (LinearLayout) getActivity().findViewById(R.id.topbar);
        // catesp = (Spinner) getActivity().findViewById(R.id.cate_sp);
        bycate = (Button) getActivity().findViewById(R.id.bycate);
        initSpinner();
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        initSearchVIew();
        catebar = (LinearLayout) getActivity().findViewById(R.id.cate);
        sw = (SwipeRefreshLayout) getActivity().findViewById(R.id.fund_sw);
        sw.setColorScheme(R.color.main_color, R.color.red, R.color.green);
        sw.post(new Runnable() {
            @Override
            public void run() {
                sw.setRefreshing(true);
            }
        });
        adapter = new MyRecyclViewAdapter(data, getActivity());
        myRecycle.setItemAnimator(new DefaultItemAnimator());
        // myRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        StaggeredGridLayoutManager sgl = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 2);
        glm.setSpanSizeLookup(new SpanSizeLookup() {

            @Override
            public int getSpanSize(int arg0) {
                return arg0 == 0 ? 1 : 1;
            }
        });
        myRecycle.setLayoutManager(glm);
        bytime = (Button) getActivity().findViewById(R.id.bytime);
        bytime.getPaint().setFakeBoldText(true);
        bydegree = (Button) getActivity().findViewById(R.id.degree);
        bycount = (Button) getActivity().findViewById(R.id.bycount);
        bypricedown = (Button) getActivity().findViewById(R.id.bypricedown);

        myRecycle.setAdapter(adapter);
    }

    private void initSpinner() {
        sp_location = (Spinner) getActivity().findViewById(R.id.sp_school);
        sp_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    appBarLayout.setExpanded(false);
                }
                ReflashData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        schools.add(login_user.getSchool());
        spSchooladapter1 = new ArrayAdapter<String>(
                getContext(), R.layout.myspinnerlayout, schools);
        sp_location.setAdapter(spSchooladapter1);
        getSchool();
    }

    private void initSearchVIew() {
        sv_search = (SearchView) getActivity().findViewById(R.id.searchView);
        sv_search.setOnSearchClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                appBarLayout.setExpanded(false);
                bycate.setVisibility(View.GONE);
                sp_location.setVisibility(View.GONE);
            }
        });
        sv_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        sv_search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                bycate.setVisibility(View.VISIBLE);
                sp_location.setVisibility(View.VISIBLE);
                ReflashData();
                return false;
            }
        });
    }

    private void search(final String query) {
        if (query != null && !"".equals(query)) {
            sw.setRefreshing(true);
            gotcount = 0;
            adapter.DeleteAll();
            BmobQuery<MyGoods> b1 = new BmobQuery<>();
            b1.addWhereContains("title", query);
            BmobQuery<MyGoods> b2 = new BmobQuery<>();
            b2.addWhereContains("school", query);
            BmobQuery<MyGoods> b3 = new BmobQuery<>();
            b3.addWhereContains("describ", query);
            List<BmobQuery<MyGoods>> bqs = new ArrayList<>();
            bqs.add(b1);
            bqs.add(b2);
            bqs.add(b3);
            BmobQuery<MyGoods> mainBq = new BmobQuery<MyGoods>();
            mainBq.order(order);
            mainBq.or(bqs);
            mainBq.addWhereEqualTo("status", Constants.GOODS_STATUS_NORMAL);//只加载正常状态的物品
            // query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);// 先从缓存读取,否则网络
            // query.addWhereEqualTo("school", login_user.getSchool());// 返回同一个学校/
            mainBq.setLimit(9);// ///////////测试使用的实际为10

            mainBq.findObjects(getActivity(), new FindListener<MyGoods>() {

                @Override
                public void onSuccess(List<MyGoods> arg0) {
                    // ShowToast(arg0.size()+"");
                    if (arg0.size() == 0) {
                        Snackbar.make(bycate, "没有找到\"" + query + "\"相关内容，去发现看看？", Snackbar.LENGTH_LONG).show();
                    } else {
                        MyUtils.showLog("indext", arg0.size() + "");
                        for (int i = 0; i < arg0.size(); i++) {
                            adapter.AddItem(gotcount + i, arg0.get(i));
                        }
                        gotcount += arg0.size();
                    }
                    sw.setRefreshing(false);
                }

                @Override
                public void onError(int arg0, String arg1) {
                    MyUtils.showLog("indext", arg0 + arg1);
                    sw.setRefreshing(false);
                    if (9009 != arg0) {
                        ShowToast("网络错误");
                    }
                }
            });
        }
    }

    private void initRoolViewPager() {
        rp_topNews = (RollPagerView) getActivity().findViewById(R.id.rp_topNews);
        newsAdapter = new TopNewsAdapter(getActivity(), rp_topNews, urls);
        newsAdapter.setOnTopNEwsClicklistener(new TopNewsAdapter.onTopnewsClicklistener() {
            @Override
            public void onClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putString("url", subjectlist.get(position).getLink_url());
                startActivity(WebviewActivity.class, bundle, false);
            }
        });
        rp_topNews.setAdapter(newsAdapter);
        getSubject();
    }

    private void getSubject() {
        BmobQuery<MySubject> bq = new BmobQuery<>();
        bq.findObjects(getContext(), new FindListener<MySubject>() {
            @Override
            public void onSuccess(final List<MySubject> list) {
                MyUtils.showLog("aaaaaaaa", list.size() + "   aaaa");
                subjectlist=list;
                for (int i = 0; i < list.size(); i++) {
                    urls.add(list.get(i).getImg_url());
                }
                newsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    View footer;
    boolean isIdle;
    int mScrollY;

    @Override
    public void initListeners() {
        sw.setOnRefreshListener(mOnRefreshListener);
        bycate.setOnClickListener(new mcateButtonClickLisener());
        bytime.setOnClickListener(this);
        bydegree.setOnClickListener(this);
        bycount.setOnClickListener(this);
        bypricedown.setOnClickListener(this);
        adapter.SetOnRecClickListener(new onRecClickListener() {

            @Override
            public void onClick(int position) {
                MyGoods g = data.get(position - headCount);
                // 点击计数
                g.increment("clickCount");
                g.update(getActivity());
                Intent todetails = new Intent(getActivity(),
                        GoodsDetailsActivity.class);
                todetails.putExtra("goodsId", g.getObjectId());
                todetails.putExtra("GoodsTitle", g.getTitle());
                todetails.putExtra("GoodsImgs", g.getImgUrl());
                startActivity(todetails);
            }
        });
        myRecycle.setOnScrollListener(new OnRecvScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mScrollY += dy;
                // show or hide header view
                if (mScrollY > 6) {
//					hideHeader();
                } else {
//					showHeader();
                }
            }

            private void showHeader() {
                topbar.setVisibility(View.GONE);
                catebar.setVisibility(View.VISIBLE);
            }

            private void hideHeader() {
                topbar.setVisibility(View.VISIBLE);
                catebar.setVisibility(View.GONE);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // isIdle = newState == RecyclerView.SCROLL_STATE_IDLE;
                    mScrollY = 0;
                }
            }

            @Override
            public void onBottom() {
                super.onBottom();
                MyUtils.showLog("fund", "bottom");
                GetData();
            }

        });
    }

    OnRefreshListener mOnRefreshListener = new OnRefreshListener() {

        @Override
        public void onRefresh() {
            // myListView.setEnabled(false);
            ReflashData();
        }
    };
    private MyViewFlipper viewFlipper;
    ImageView mark0, mark1, mark2, mark3;
    private int cate;

    @Override
    public void initData() {
        gotcount = 0;
        ReflashData();
    }

    boolean bool = true;

    private void GetData() {
        BmobQuery<MyGoods> query = new BmobQuery<MyGoods>();
        MyUser login_user = BmobUser
                .getCurrentUser(getActivity(), MyUser.class);
        query.order(order);
        MyUtils.showLog("fsdf", cate - 1 + "");
        if (cate > 0) {
            query.addWhereEqualTo("cate", cate - 1);
        }
        SharedPreferences preferences = getActivity().getPreferences(0x0000);
        // final BmobGeoPoint point = new BmobGeoPoint(preferences.getFloat(
        // "longitude", (float) 112.934755), preferences.getFloat(
        // "latitude", (float) 24.52065));
        // query.addWhereNear("geopoint", point);
        query.addWhereLessThanOrEqualTo("status", Constants.GOODS_STATUS_SALD);//只加载正常状态的物品
        // query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);// 先从缓存读取,否则网络
        query.addWhereEqualTo("school", sp_location.getSelectedItem().toString());// 返回同一个学校/
        query.setSkip(gotcount);
        if (bool) {
            query.setLimit(9);// ///////////测试使用的实际为10
        } else {
            query.setLimit(10);// ///////////测试使用的实际为10
        }
        bool = !bool;

        query.findObjects(getActivity(), new FindListener<MyGoods>() {

            @Override
            public void onSuccess(List<MyGoods> arg0) {
                // ShowToast(arg0.size()+"");
                if (arg0.size() == 0) {
                    if (gotcount == 0) {
                        Snackbar.make(bycate, "没有数据了", Snackbar.LENGTH_LONG).show();
                    } else {
//                        ShowToast("没有更多了...");
                    }
                } else {
                    MyUtils.showLog("indext", arg0.size() + "");
                    for (int i = 0; i < arg0.size(); i++) {
//						data.add(arg0.get(i));
                        adapter.AddItem(gotcount + i, arg0.get(i));
                    }
                    gotcount += arg0.size();
                    // adapter.notifyDataSetChanged();

                    // myListView.setEnabled(true);
                }
                sw.setRefreshing(false);
                // myListView.removeFooterView(footer);
            }

            @Override
            public void onError(int arg0, String arg1) {
                MyUtils.showLog("indext", arg0 + arg1);
                sw.setRefreshing(false);
                sw.setRefreshing(false);
                if (9009 != arg0) {
//                    ShowToast("网络错误");
                    Snackbar.make(bycate, "网络错误", Snackbar.LENGTH_LONG).show();
                    // SnackBar snb = new SnackBar(getActivity(), arg1, "ok",
                    // new View.OnClickListener() {
                    //
                    // @Override
                    // public void onClick(View v) {
                    // // sw.setRefreshing(false);
                    // }
                    // });
                    // snb.setDismissTimer(60000);
                    // snb.setCanceledOnTouchOutside(false);
                    // snb.show();
                }
                // if (footer != null) {
                // myListView.removeFooterView(footer);
                // }
            }
        });
    }

    public void ReflashData() {
        sw.setRefreshing(true);
        data.clear();
        gotcount = 0;
        adapter.DeleteAll();
        GetData();
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        resetSize();
        switch (v.getId()) {
            case R.id.bycount:
                bycount.getPaint().setFakeBoldText(true);
                if (hot) {
                    bycount.setText("热度↑");
                    order = hotUp;
                } else {
                    order = hotDown;
                    bycount.setText("热度↓");
                }
                hot = !hot;
                break;
            case R.id.bypricedown:
                bypricedown.getPaint().setFakeBoldText(true);
                if (price) {
                    order = priceUp;
                    bypricedown.setText("价格↑");
                } else {
                    order = priceDown;
                    bypricedown.setText("价格↓");
                }
                price = !price;
                break;
            case R.id.bytime:
                bytime.getPaint().setFakeBoldText(true);
                if (time) {
                    order = timeUp;
                    bytime.setText("时间↑");
                } else {
                    order = timeDown;
                    bytime.setText("时间↓");
                }
                time = !time;
                break;
            case R.id.degree:
                bydegree.getPaint().setFakeBoldText(true);
                if (degree) {
                    order = degUp;
                    bydegree.setText("成色↑");
                } else {
                    order = degDown;
                    bydegree.setText("成色↓");
                }
                degree = !degree;
                break;
            default:
                break;
        }
        appBarLayout.setExpanded(false);
        sw.setRefreshing(true);
        ReflashData();
    }

    private void checkOrder() {
        if (!price) {
            order = priceUp;
            bypricedown.setText("价格↑");
        } else {
            order = priceDown;
            bypricedown.setText("价格↓");
        }
        if (!time) {
            order = timeUp;
            bytime.setText("时间↑");
        } else {
            order = timeDown;
            bytime.setText("时间↓");
        }
        if (!hot) {
            bycount.setText("热度↑");
            order = hotUp;
        } else {
            order = hotDown;
            bycount.setText("热度↓");
        }
        if (!degree) {
            order = degUp;
            bydegree.setText("成色↑");
        } else {
            order = degDown;
            bydegree.setText("成色↓");
        }
    }

    private void resetSize() {
        bycount.getPaint().setFakeBoldText(false);
        bytime.getPaint().setFakeBoldText(false);
        bydegree.getPaint().setFakeBoldText(false);
        bypricedown.getPaint().setFakeBoldText(false);
        checkOrder();
    }

    int[] cates;
    PopupWindow morePop;
    Button[] bt_cates;

    private class mcateButtonClickLisener implements OnClickListener {

        @Override
        public void onClick(View v) {
            appBarLayout.setExpanded(false);
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.popu_list, null);
            cates = new int[]{R.id.categ0, R.id.categ1, R.id.categ2,
                    R.id.categ3, R.id.categ4, R.id.categ5, R.id.categ6,
                    R.id.categ7, R.id.categ8};
            bt_cates = new Button[9];
            mcatelistener mclick = new mcatelistener();
            for (int i = 0; i < bt_cates.length; i++) {
                bt_cates[i] = (Button) view.findViewById(cates[i]);
                bt_cates[i].setOnClickListener(mclick);
            }
            // 注入
            morePop = new PopupWindow(view);
            morePop.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            morePop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            morePop.setTouchable(true);
            morePop.setFocusable(true);
            morePop.setOutsideTouchable(true);
            morePop.setBackgroundDrawable(new BitmapDrawable());
            // 动画效果 从顶部弹下
            // morePop.setAnimationStyle(R.style.MenuPop);
            // morePop.showAsDropDown(tx_cate, 0, -dip2px(this, 2.0F));
//            morePop.showAsDropDown(rp_topNews);
            morePop.showAtLocation(topbar, Gravity.LEFT, 0, 0);
        }

        private class mcatelistener implements OnClickListener {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.categ0:
                    case R.id.categ1:
                    case R.id.categ2:
                    case R.id.categ3:
                    case R.id.categ4:
                    case R.id.categ5:
                    case R.id.categ6:
                    case R.id.categ7:
                    case R.id.categ8:
                        for (int i = 0; i <= 8; i++) {
                            if (v.getId() == cates[i]) {
                                morePop.dismiss();
                                bycate.setText("" + bt_cates[i].getText());
                                cate = i;
                                sw.setRefreshing(true);
                                ReflashData();
                                break;
                            }
                        }
                        break;

                    default:
                        break;
                }
            }
        }
    }

    /**
     * 返回关闭搜索框
     *
     * @return
     */
    public boolean onBackPressed() {
        if (!FoundFragment.this.isHidden() && sv_search != null && !bycate.isShown()) {
            sv_search.onActionViewCollapsed();
            sv_search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            bycate.setVisibility(View.VISIBLE);
            sp_location.setVisibility(View.VISIBLE);
            ReflashData();
            return true;
        }
        return false;
    }

    private List<String> schools = new ArrayList<>();

    private void getSchool() {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        final BmobGeoPoint point = new BmobGeoPoint(preferences.getFloat(
                "longitude", (float) 112.934755), preferences.getFloat(
                "latitude", (float) 24.52065));
        BmobQuery<MySchools> bmobQuery = new BmobQuery<MySchools>();
        bmobQuery.addWhereNear("geopoint", point);
        bmobQuery.setLimit(5); // 获取最接近用户地点的5条数据
        bmobQuery.findObjects(getContext(),
                new FindListener<MySchools>() {

                    @Override
                    public void onSuccess(List<MySchools> arg0) {
                        schools.clear();
                        schools.add(login_user.getSchool());
                        for (int i = 0; i < arg0.size(); i++) {
                            if (!arg0.get(i).getSchoolname().equals(schools.get(0))) {
                                schools.add(arg0.get(i).getSchoolname());
                            }
                            Log.i("school", arg0.get(i).getSchoolname());
                        }
                        ((MainActivity) getActivity()).setSchools(schools);
                        spSchooladapter1.notifyDataSetChanged();
                        sp_location.setAdapter(spSchooladapter1);
                    }

                    @Override
                    public void onError(int arg0, String arg1) {
                        Log.i("getschools", arg1);
                    }
                });
    }


}
