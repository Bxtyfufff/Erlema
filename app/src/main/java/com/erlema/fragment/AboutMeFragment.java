package com.erlema.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;

import com.bumptech.glide.Glide;
import com.erlema.activity.AuthActivity;
import com.erlema.activity.LoginActivity;
import com.erlema.activity.SettingActivity;
import com.erlema.bean.MyFragmentPagerAdapter;
import com.erlema.bean.MyUser;
import com.erlema.util.MyUtils;
import com.erlema.bean.myActionBar;
import com.erlema.bean.myActionBar.mClickListener;
import com.example.erlema.R;
import com.gc.materialdesign.widgets.Dialog;

public class AboutMeFragment extends BaseFragment {
    private RecyclerView mytopicview;
    private myActionBar myActionBar;
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentList;
    private ImageView cursor;
    private TextView view1, view2, view3, view4;
    private int currIndex;// 当前页卡编号
    private float bmpW;// 横线图片宽度
    private float offset;// 图片移动的偏移量
    private LinearLayout aboutCreadibility;
    private TextView nickname, realname, tel, school, creadibility, auth, time;
    private ImageView avator;
    private FragmentActivity context;
    MyUser currentUser;
    boolean isme;
    private String userId;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden&&isme) {
            refreshIfo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isme) {
            refreshIfo();
        }
    }

    public static AboutMeFragment newInstance(String id) {
        AboutMeFragment fragment = new AboutMeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId", id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            MyUtils.showLog("about", "" + userId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_me__layout, container,
                false);
        return rootView;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void initViews() {
        MyUser local = BmobUser.getCurrentUser(getActivity(), MyUser.class);
        if (local.getObjectId().equals(userId)) {
            currentUser = local;
            isme = true;
        }
        aboutCreadibility = (LinearLayout) getActivity().findViewById(
                R.id.aboutcreadibility);
        myActionBar = (com.erlema.bean.myActionBar) getActivity().findViewById(
                R.id.myactionbar);
        myActionBar.setTitleText("个人信息");
        myActionBar.setButtonVisible(!isme, isme);
        nickname = (TextView) getActivity().findViewById(R.id.menickname);
        realname = (TextView) getActivity().findViewById(R.id.merealname);
        tel = (TextView) getActivity().findViewById(R.id.metel);
        school = (TextView) getActivity().findViewById(R.id.meschool);
        creadibility = (TextView) getActivity()
                .findViewById(R.id.mecredibility);
        auth = (TextView) getActivity().findViewById(R.id.meauthed);
        time = (TextView) getActivity().findViewById(R.id.mecreadat);
        avator = (ImageView) getActivity().findViewById(R.id.seller_avatar);

    }

    @Override
    public void onAttach(Activity activity) {
        context = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void initListeners() {
        avator.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isme) {
                    startActivity(AuthActivity.class, null, false);
                }
            }
        });
        aboutCreadibility.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ShowToast("友善度：收到一个差评扣1，默认为100");
            }
        });
        myActionBar.setmListener(new mClickListener() {

            @Override
            public void onRightClick() {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }

            @Override
            public void onLeftClick() {

            }
        });
    }

    @Override
    public void initData() {
        InitTextView();
        InitCursor();
        InitViewPager();
    }

    private void UpdataUserData(String snick, String sreal, String stel,
                                String ssch, int scread, boolean autu, String creat, String url) {
        nickname.setText(snick);
        if (realname != null) {
            realname.setText("实名：" + sreal);
        }
        if (stel != null) {
            tel.setText("电话：" + stel);
        }
        school.setText("学校：" + ssch);
        creadibility.setText(scread + "");
        if (autu) {
            auth.setText("已认证");
        } else {
            auth.setText("未认证");
        }
        time.setText(creat.substring(0, 11) + "创建");
        if (avator != null) {
            Glide.with(this).load(url).placeholder(R.drawable.ic_default_avatar_big_normal).override(61, 61).into(avator);
        }
    }


    /*
     * 初始化标签名
     */
    public void InitTextView() {
        view1 = (TextView) getActivity().findViewById(R.id.tv_guid1);
        view2 = (TextView) getActivity().findViewById(R.id.tv_guid2);
        view3 = (TextView) getActivity().findViewById(R.id.tv_guid3);
        view4 = (TextView) getActivity().findViewById(R.id.tv_guid4);

        view1.setOnClickListener(new txListener(0));
        view2.setOnClickListener(new txListener(1));
        view3.setOnClickListener(new txListener(2));
        view4.setOnClickListener(new txListener(3));
    }

    public class txListener implements OnClickListener {
        private int index = 0;

        public txListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    }

    /*
     * 初始化图片的位移像素
     */
    public void InitCursor() {
        // 初始化动画
        cursor = (ImageView) getActivity().findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor)
                .getWidth();// 获取图片宽度

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 4 - bmpW) / 2;// 计算偏移量

        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);// 设置动画初始位置
    }

    /*
     * 初始化ViewPager
     */
    public void InitViewPager() {
        mPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        fragmentList = new ArrayList<Fragment>();
        if (isme) {
            refreshIfo();
            Fragment f1, f2, f3, f4;
            f1 = mySendGoodsFragment.newInstance(isme, userId);
            f2 = myQiuFragment.newInstance(isme, userId);
            f3 = myBuyedGoodsFragment.newInstance(currentUser.getObjectId());
            f4 = myCollecteddGoodsFragment.newInstance(isme, userId);
            fragmentList.add(f1);
            fragmentList.add(f2);
            fragmentList.add(f3);
            fragmentList.add(f4);
            // 给ViewPager设置适配器
            mPager.setAdapter(new MyFragmentPagerAdapter(context
                    .getSupportFragmentManager(), fragmentList));
            mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
            mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
        } else {
            BmobQuery<MyUser> bq = new BmobQuery<MyUser>();
            bq.getObject(getActivity(), userId, new GetListener<MyUser>() {

                @Override
                public void onFailure(int arg0, String arg1) {
                    ShowToast("信息获取出错( ▼-▼ )");
                }

                @Override
                public void onSuccess(MyUser arg0) {
                    MyUtils.showLog("aboutmeFragment", "success" + arg0.toString());
                    currentUser = arg0;
                    refreshIfo();
                    Fragment f1, f2, f3, f4;
                    f1 = mySendGoodsFragment.newInstance(isme, userId);
                    f2 = myQiuFragment.newInstance(isme, userId);
                    f3 = myBuyedGoodsFragment.newInstance(currentUser.getObjectId());
                    f4 = myCollecteddGoodsFragment.newInstance(isme, userId);
                    fragmentList.add(f1);
                    fragmentList.add(f2);
                    fragmentList.add(f3);
                    fragmentList.add(f4);
                    // 给ViewPager设置适配器
                    mPager.setAdapter(new MyFragmentPagerAdapter(context
                            .getSupportFragmentManager(), fragmentList));
                    mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
                    mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
                }
            });
        }


    }

    private void refreshIfo() {
        BmobQuery<MyUser> bq = new BmobQuery<MyUser>();
        bq.getObject(getActivity(), userId,
                new GetListener<MyUser>() {

                    @Override
                    public void onFailure(int arg0, String arg1) {

                    }

                    @Override
                    public void onSuccess(MyUser arg0) {
                        UpdataUserData(arg0.getUsername(), arg0.getrelName(),
                                arg0.getMobilePhoneNumber(), arg0.getSchool(),
                                arg0.getCredibility(), arg0.getAuthed(),
                                arg0.getCreatedAt(), arg0.getAvator());
                    }
                });
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {
        private float one = offset * 2 + bmpW;// 两个相邻页面的偏移量
        private TextView[] tvs = {view1, view2, view3, view4};

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @SuppressLint("NewApi")
        @Override
        public void onPageSelected(int arg0) {
            Animation animation = new TranslateAnimation(one * currIndex, arg0
                    * one, 0, 0);// 平移动画
            currIndex = arg0;
            animation.setFillAfter(true);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);// 动画持续时间0.2秒
            cursor.startAnimation(animation);// 是用ImageView来显示动画的
        }
    }

}
