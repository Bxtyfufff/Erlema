package com.erlema.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.erlema.adapter.BackHandledInterface;
import com.erlema.bean.MyBmobInstallation;
import com.erlema.bean.MySchools;
import com.erlema.bean.MyUser;
import com.erlema.fragment.BaseFragment;
import com.erlema.fragment.Mainfragment;
import com.erlema.util.MyUtils;
import com.erlema.config.Constants;
import com.erlema.fragment.AboutMeFragment;
import com.erlema.fragment.AddFragment;
import com.erlema.fragment.FoundFragment;
import com.erlema.fragment.IndexFragment;
import com.erlema.fragment.MessageFragment;
import com.example.erlema.R;
import com.gc.materialdesign.widgets.Dialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends BaseActivity implements OnClickListener, Mainfragment.OnFragmentInteractionListener
        , BackHandledInterface {
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private boolean isPush = false;
    private FoundFragment mBackHandedFragment;
    private boolean hadIntercept;
    private IndexFragment indexFragment;
    private FoundFragment foundFragment;
    private AddFragment addFragment;
    private MessageFragment messageFragment;
    private AboutMeFragment aboutMeFragment;
    private FragmentManager fm;
    private RelativeLayout indexLayout, foundLayout, addLayout, messageLayout,
            aboutMeLayout;
    private ImageView indexIv, foundIv, addIv, messageIv, aboutMeIv;
    private Button add;
    private static ImageView indexRed;
    private static ImageView foundRed;
    private static ImageView addRed;
    private static ImageView messageRed;
    private static ImageView aboutMeRed;
    private List<String> schools;//附近的学校
    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView();
        initViews();
        initListeners();
        initData();
    }

    /*
     * LocationClientOption类，该类用来设置定位SDK的定位方式
     */
    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
        mLocationClient.registerLocationListener(myListener); // 注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        // option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("gprs");// 可选，默认gcj02，设置返回的定位结果坐标系
        int span = 0;
        option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 可选，默认false,设置是否使用gps
        option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);//must store the new intent unless getIntent() will return the old one
        processExtraData();
    }

    public void setContentView() {
        setContentView(R.layout.activity_main);
        // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, Constants.Bmob_APPID);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        EventBus.getDefault().register(this);
        checkRedPoint();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        MyUtils.showLog("main", requestCode + "  " + resultCode);
        if (requestCode == 99) {
            new Handler().post(new Runnable() {

                @Override
                public void run() {
                    SelectTap(3);

                }
            });
        }
        if (requestCode == 200) {
            SelectTap(0);
        }
    }

    public void initViews() {
        localUser = BmobUser.getCurrentUser(getApplicationContext(),
                MyUser.class);
        // 查找更新包（新版本）
        BmobUpdateAgent.update(this);
        BmobUpdateAgent.setUpdateOnlyWifi(false);// 所有网络
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus,
                                         UpdateResponse updateInfo) {
                // 根据updateStatus来判断更新是否成功
                MyUtils.showLog("updata", updateStatus + "：" + updateInfo);
            }

        });
        //静默下载更新
        BmobUpdateAgent.silentUpdate(this);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, Constants.Bmob_APPID);
        indexLayout = (RelativeLayout) findViewById(R.id.idex_layout);
        foundLayout = (RelativeLayout) findViewById(R.id.found_layout);
        addLayout = (RelativeLayout) findViewById(R.id.add_layout);
        messageLayout = (RelativeLayout) findViewById(R.id.message_layout);
        aboutMeLayout = (RelativeLayout) findViewById(R.id.about_me_layout);

        indexIv = (ImageView) findViewById(R.id.idex_img);
        foundIv = (ImageView) findViewById(R.id.found_img);
        addIv = (ImageView) findViewById(R.id.add_img);
        messageIv = (ImageView) findViewById(R.id.message_img);
        aboutMeIv = (ImageView) findViewById(R.id.about_img);

        indexRed = (ImageView) findViewById(R.id.idex_red_point);
        foundRed = (ImageView) findViewById(R.id.found_red_point);
        addRed = (ImageView) findViewById(R.id.add_red_point);
        messageRed = (ImageView) findViewById(R.id.message_red_point);
        aboutMeRed = (ImageView) findViewById(R.id.about_red_point);

        add = (Button) findViewById(R.id.add);
    }

    public void initListeners() {
        add.setOnClickListener(this);
        indexLayout.setOnClickListener(this);
        foundLayout.setOnClickListener(this);
        addLayout.setOnClickListener(this);
        messageLayout.setOnClickListener(this);
        aboutMeLayout.setOnClickListener(this);
        initRealTImeData();
    }

    /**
     * 注册消息接收事件
     *
     * @param event
     */
    public void onEventMainThread(MessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    public void onEventMainThread(OfflineMessageEvent event) {
        checkRedPoint();
    }

    private void checkRedPoint() {
        MyUser user = BmobUser.getCurrentUser(getApplicationContext(), MyUser.class);
        if (user != null) {
            if (BmobIM.getInstance().getAllUnReadCount() > 0) {
                messageRed.setVisibility(View.VISIBLE);
            } else {
                messageRed.setVisibility(View.GONE);
            }
        } else {
            startActivity(new Intent(getApplicationContext(),
                    LoginActivity.class));
            this.finish();
        }
    }

    BmobUser local;

    public void initData() {
        checkLogin();
        processExtraData();
        initLocation();
        mLocationClient.start();
    }

    private void checkLogin() {
        local = BmobUser.getCurrentUser(getApplicationContext());
        if (local != null) {
            // ShowToast("欢迎回来,"
            // + BmobUser.getCurrentUser(getApplicationContext())
            // .getUsername());
            updateInstallation();
            SelectTap(0);
            BmobIM.connect(local.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        Log.i("mainActivity", "connect success");
                    } else {
                        Log.i("mainActivity", e.getErrorCode() + "/" + e.getMessage());
                    }
                }
            });
            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                @Override
                public void onChange(ConnectionStatus status) {
                    Log.i("mainActivity", status.getMsg());
                }
            });
        } else {
            startActivity(new Intent(getApplicationContext(),
                    LoginActivity.class));
            this.finish();
        }
    }

    //处理通知栏点击传过来的值
    private void processExtraData() {
        isPush = getIntent().getBooleanExtra("isPush", false);
        MyUtils.showLog("mainActivity", isPush + "ispush");
        if (isPush) {
            messageRed.post(new Runnable() {
                @Override
                public void run() {
                    checkRedPoint();
                    SelectTap(3);
                }
            });
        }
    }

    private void updateInstallation() {
        BmobQuery<MyBmobInstallation> query = new BmobQuery<MyBmobInstallation>();
        query.addWhereEqualTo("installationId",
                BmobInstallation.getInstallationId(this));
        query.findObjects(this, new FindListener<MyBmobInstallation>() {

            @Override
            public void onSuccess(List<MyBmobInstallation> object) {
                // TODO Auto-generated method stub
                if (object.size() > 0) {
                    MyBmobInstallation mbi = object.get(0);
                    mbi.setUid(local.getObjectId());
                    mbi.update(getApplicationContext(), new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            MyUtils.showLog("bmob", "设备信息更新成功");
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            MyUtils.showLog("bmob", "设备信息更新失败:" + msg);
                        }
                    });
                }
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
            }
        });
        MyUser u = BmobUser.getCurrentUser(getApplicationContext(),
                MyUser.class);
        ;
        u.setInstallationID(BmobInstallation.getInstallationId(this));
        u.update(getApplicationContext(), null);
    }

    private boolean acceptAdd = false;
    MyUser localUser;

    @Override
    public void onClick(View v) {
        checkRedPoint();
        switch (v.getId()) {
            case R.id.idex_layout:
                SelectTap(0);
                //双击主页刷新
                if (foundFragment != null && !foundFragment.isHidden()) {
                    foundFragment.ReflashData();
                }
                break;
            case R.id.found_layout:
                SelectTap(1);
                break;
            case R.id.add_layout:
                // SelectTap(2);
                break;
            case R.id.message_layout:
                SelectTap(3);
                break;
            case R.id.about_me_layout:
                SelectTap(4);
                break;
            case R.id.add:
                // 判断是否认证
                Boolean authed = BmobUser.getCurrentUser(getApplicationContext(),
                        MyUser.class).getAuthed();
                BmobQuery<MyUser> bq = new BmobQuery<MyUser>();
                bq.getObject(getApplicationContext(), localUser.getObjectId(),
                        new GetListener<MyUser>() {

                            @Override
                            public void onFailure(int arg0, String arg1) {
                                Toast.makeText(getApplicationContext(),"网络错误，请重试",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(MyUser arg0) {
                                acceptAdd = arg0.getAuthed();
                                MyUser u = new MyUser();
                                u.setAuthed(acceptAdd);
                                u.setCollection(arg0.getCollection());
                                u.setBuys(arg0.getBuys());
                                u.update(getApplicationContext(),
                                        arg0.getObjectId(), null);
                                if (acceptAdd) {
                                    startActivity(new Intent(MainActivity.this, AddActivity.class));
                                    // ToActivity(AddActivity.class);
                                } else {
                                    Dialog d = new Dialog(MainActivity.this, "认证用户才能发布！", "现在就去认证？");
                                    d.addCancelButton("取消");
                                    d.setOnAcceptButtonClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            SelectTap(4);
                                        }
                                    });
                                    d.show();
                                }
                            }
                        });
                // MyUser u=new MyUser();
                // u.setUsername(localUser.getUsername());
                // u.update(getApplicationContext(), localUser.getObjectId(), null);
//                if (authed || acceptAdd) {
//                    startActivity(new Intent(MainActivity.this, AddActivity.class));
//                    // ToActivity(AddActivity.class);
//                } else {
//                    Dialog d = new Dialog(MainActivity.this, "认证用户才能发布！", "现在就去认证？");
//                    d.addCancelButton("取消");
//                    d.setOnAcceptButtonClickListener(new OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            SelectTap(4);
//                        }
//                    });
//                    d.show();
//                }
                break;
            default:
                break;
        }
    }

    Mainfragment mainFragment;

    private void SelectTap(int i) {
        fm = getSupportFragmentManager();
        FragmentTransaction ts1 = fm.beginTransaction();
        hideFragment(ts1);// 隐藏所有fragment
        FragmentTransaction ts = fm.beginTransaction();
        switch (i) {
            case 0:
                indexIv.setImageResource(R.drawable.iconfont_shouyes);
//                if (mainFragment == null) {
//                    mainFragment = Mainfragment.newInstance("ff0","fsf");
//                    ts.add(R.id.main_fragment, mainFragment);
//                } else {
//                    // ts.replace(R.id.main_fragment ,foundFragment);
//                    ts.show(mainFragment);
//                }
                indexIv.setImageResource(R.drawable.iconfont_shouyes);
                if (foundFragment == null) {
                    foundFragment = new FoundFragment();
                    ts.add(R.id.main_fragment, foundFragment);
                } else {
                    // ts.replace(R.id.main_fragment ,foundFragment);
                    ts.show(foundFragment);
                }
                break;
            case 1:
                foundIv.setImageResource(R.drawable.iconfont_chaxuns);
                if (indexFragment == null) {
                    indexFragment = new IndexFragment();
                    ts.add(R.id.main_fragment, indexFragment);
                } else {
                    ts.show(indexFragment);
                    // ts.replace(R.id.main_fragment ,indexFragment);
                }
                break;
            case 2:
                addIv.setImageResource(R.drawable.iconfont_adds);
                if (addRed.isShown()) {
                    addRed.setVisibility(View.INVISIBLE);
                }
                if (addFragment == null) {
                    addFragment = new AddFragment();
                    ts.add(R.id.main_fragment, addFragment);
                } else {
                    ts.show(addFragment);
                }
                break;
            case 3:
                messageIv.setImageResource(R.drawable.iconfont_xiaoxis);
                if (messageFragment == null) {
                    messageFragment = new MessageFragment();
                    ts.add(R.id.main_fragment, messageFragment);
                } else {
                    ts.show(messageFragment);
                }
                break;
            case 4:
                aboutMeIv.setImageResource(R.drawable.iconfont_gerenzhongxins);
                if (aboutMeFragment == null) {
                    aboutMeFragment = AboutMeFragment.newInstance(localUser
                            .getObjectId());
                    ts.add(R.id.main_fragment, aboutMeFragment);
                } else {
                    ts.show(aboutMeFragment);
                }
                break;

            default:
                break;
        }
        ts.commit();
    }

    public static void ShowRedPoint(int i) {
        switch (i) {
            case 0:
                indexRed.setVisibility(View.VISIBLE);
                break;
            case 1:
                foundRed.setVisibility(View.VISIBLE);
                break;
            case 2:
                addRed.setVisibility(View.VISIBLE);
                break;
            case 3:
                messageRed.setVisibility(View.VISIBLE);
                break;
            case 4:
                aboutMeRed.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (indexFragment != null) {
            transaction.hide(indexFragment);
        }
//        if (mainFragment!=null){
//            transaction.hide(mainFragment);
//        }
        if (foundFragment != null) {
            transaction.hide(foundFragment);
        }
        if (addFragment != null) {
            transaction.hide(addFragment);
        }
        if (messageFragment != null) {
            transaction.hide(messageFragment);
        }
        if (aboutMeFragment != null) {
            transaction.hide(aboutMeFragment);
        }
        transaction.commit();
        // 重置所有图标
        indexIv.setImageResource(R.drawable.iconfont_shouye);
        foundIv.setImageResource(R.drawable.iconfont_chaxun);
        addIv.setImageResource(R.drawable.iconfont_add);
        messageIv.setImageResource(R.drawable.iconfont_xiaoxi);
        aboutMeIv.setImageResource(R.drawable.iconfont_gerenzhongxin);
    }


    @Override
    public void setSelectedFragment(FoundFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    long curtime = 0;

    @Override
    public void onBackPressed() {
        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
            if ((System.currentTimeMillis() - curtime) < 2000) {
                super.onBackPressed();
            } else {
                // ShowToast("再按一次退出程序");
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                curtime = System.currentTimeMillis();
            }
        }

    }

    ListView lv;
    ViewFlipper vf;
    SwipeRefreshLayout sw;
    BmobRealTimeData data;// 实时数据

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    private void initRealTImeData() {
        data = new BmobRealTimeData();
        data.start(getApplicationContext(), new ValueEventListener() {

            @Override
            public void onDataChange(JSONObject arg0) {
                MyUtils.showLog("realtimedata", arg0.toString());
                if (BmobRealTimeData.ACTION_UPDATETABLE.equals(arg0
                        .optString("action"))) {
                    JSONObject data = arg0.optJSONObject("data");
                    // MyUtils.showLog("realtimedata",
                    // "price=" + data.optDouble("price"));
                    // MyUtils.showLog("realtimedata",
                    // "title=" + data.optString("title"));
                }
            }

            @Override
            public void onConnectCompleted() {
                MyUtils.showLog("realtimedata", "connect success!");
                if (data.isConnected()) {
                    // data.subRowUpdate("MyGoods", "172bdaf8e8");
//                    data.subTableUpdate("test");
                    // data.subRowUpdate("test", objectId)
                }
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /*
         * BDLocationListener接口有1个方法需要实现： 1.接收异步返回的定位结果，参数是BDLocation类型参数。
         */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // 保存地址到内存
            SharedPreferences prefernce = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = prefernce.edit();
            editor.putString("time", location.getTime());
            editor.putFloat("latitude", (float) location.getLatitude());
            editor.putFloat("longitude", (float) location.getLongitude());
            editor.commit();
            // Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                // 运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());
            mLocationClient.stop();
        }

    }

    public List<String> getSchools() {
        return schools;
    }

    public void setSchools(List<String> schools) {
        this.schools = schools;
    }
}
