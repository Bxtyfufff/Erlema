package com.erlema.activity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.LocationClientOption.LocationMode;
import com.erlema.bean.MySchools;
import com.erlema.bean.MyUser;
import com.example.erlema.R.id;
import com.example.erlema.R.layout;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity {

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	public BDNotifyListener mNotifyer;

	public Spinner Spschool;
	SwipeRefreshLayout sw;
	public List<String> schools;
	public EditText etNickname, etEmail, etPasswd, etRePw;
	public ButtonRectangle regist;
	public ProgressBarCircularIndeterminate loadingbar;

	@Override
	public void setContentView() {
		setContentView(layout.activity_register);
		getSchool();

	}

	private void getSchool() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		final BmobGeoPoint point = new BmobGeoPoint(preferences.getFloat(
				"longitude", (float) 112.934755), preferences.getFloat(
				"latitude", (float) 24.52065));
		BmobQuery<MySchools> bmobQuery = new BmobQuery<MySchools>();
		bmobQuery.addWhereNear("geopoint", point);
		bmobQuery.setLimit(5); // 获取最接近用户地点的5条数据
		schools = new ArrayList<String>();
		bmobQuery.findObjects(getApplicationContext(),
				new FindListener<MySchools>() {

					@Override
					public void onSuccess(List<MySchools> arg0) {
						sw.setRefreshing(false);
                        schools.clear();
						for (int i = 0; i < arg0.size(); i++) {
							schools.add(arg0.get(i).getSchoolname());
							Log.i("school",arg0.get(i).getSchoolname());

						}
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(
								RegisterActivity.this,
								android.R.layout.simple_list_item_1, schools);
						adapter.notifyDataSetChanged();
						Spschool.setAdapter(adapter);
					}

					@Override
					public void onError(int arg0, String arg1) {
						Log.i("getschools", arg1);
						sw.setRefreshing(false);
						schools.add("获取附近学校失败,请下拉刷新");
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(
								RegisterActivity.this,
								android.R.layout.simple_list_item_1, schools);
						Spschool.setAdapter(adapter);
					}
				});
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		Spschool = (Spinner) findViewById(id.school);
		etNickname = (EditText) findViewById(id.nickname_et);
		etEmail = (EditText) findViewById(id.email_et);
		etPasswd = (EditText) findViewById(id.pass_et);
		etRePw = (EditText) findViewById(id.comnfirm_et);
		regist = (ButtonRectangle) findViewById(id.btr_regest);
		loadingbar = (ProgressBarCircularIndeterminate) findViewById(id.loadingbar);
	}

	@Override
	public void initListeners() {
		regist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadingbar.setVisibility(View.VISIBLE);
				String school = "请选择学校";
//				if (Spschool.getCount() >= 1) {
//					school = Spschool.getItemAtPosition(0).toString().trim();
//				}
				if (schools.size()>0) {
					school = Spschool.getSelectedItem().toString();
				}
				String nickname = etNickname.getText().toString().trim();
				String email = etEmail.getText().toString().trim();
				String passwd = etPasswd.getText().toString().trim();
				String passwd2 = etRePw.getText().toString().trim();
				if ("获取附近学校失败,请下拉刷新".equalsIgnoreCase(school)
						|| "请选择学校".equalsIgnoreCase(school)) {
					ShowToast("无法定位到学校。下拉刷新试试？");
					loadingbar.setVisibility(View.INVISIBLE);
				} else if ("".equalsIgnoreCase(email)
						| "".equalsIgnoreCase(nickname)
						| "".equalsIgnoreCase(passwd)) {
					etEmail.setHint("邮箱不能为空");
					etNickname.setHint("昵称不能为空");
					etPasswd.setHint("密码不能为空");
					etRePw.setHint("密码不能为空");
					loadingbar.setVisibility(View.INVISIBLE);
				} else if (!(passwd.equals(passwd2))) {
					etPasswd.setHint("两次密码不一致");
					etPasswd.setText("");
					loadingbar.setVisibility(View.INVISIBLE);
				} else if ("".equalsIgnoreCase(passwd)) {
					etPasswd.setHint("密码不能为空");
					etPasswd.setText("");
					loadingbar.setVisibility(View.INVISIBLE);
				} else {
					MyUser u = new MyUser();
					u.setUsername(nickname);
					u.setSchool(school);
					u.setEmail(email);
					u.setPassword(passwd);
					u.signUp(getApplicationContext(), new SaveListener() {

						@Override
						public void onSuccess() {
							ShowToast("注册成功");
							loadingbar.setVisibility(View.INVISIBLE);
							finish();
						}

						@Override
						public void onFailure(int arg0, String arg1) {
							Log.i("reg", arg0 + arg1);
							switch (arg0) {
							case 202:
								etNickname.setHint("昵称已存在!");
								etNickname.setText("");
								break;
							case 203:
								etEmail.setHint("邮箱已注册");
								etEmail.setText("");
								break;
							case 301:
								etEmail.setHint("邮箱不合法");
								etEmail.setText("");
								break;
							default:
								break;
							}
							ShowToast("注册失败");
							loadingbar.setVisibility(View.INVISIBLE);
						}
					});
				}

			}
		});
		sw = (SwipeRefreshLayout) findViewById(id.resgister_sw);
		sw.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getSchool();
			}
		});
	}

	@Override
	public void initData() {
		initLocation();
		mLocationClient.start();
		// 位置提醒相关代码
		// mNotifyer = new NotifyLister();
		// mNotifyer.SetNotifyLocation(25.052154, 118.862084, 3000, "gps");//
		// 4个参数代表要位置提醒的点的坐标，具体含义依次为：纬度，经度，距离范围，坐标系类型(gcj02,gps,bd09,bd09ll)

		// mLocationClient.registerNotify(mNotifyer);
		// 注册位置提醒监听事件后，可以通过SetNotifyLocation 来修改位置提醒设置，修改后立刻生效。
		// mNotifyer.SetNotifyLocation(24.60891, 118.091652, 9000000, "gps");
		// //取消位置提醒
		// mLocationClient.removeNotifyEvent(mNotifyer);
		sw.post(new Runnable() {

            @Override
            public void run() {
                sw.setRefreshing(true);
                getSchool();
			}
		});
	}

	/*
	 * LocationClientOption类，该类用来设置定位SDK的定位方式
	 */
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
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

	// BDNotifyListner实现
	public class NotifyLister extends BDNotifyListener {
		public void onNotify(BDLocation mlocation, float distance) {
			// mVibrator01.vibrate(1000);//振动提醒已到设定位置附近
			Log.i("BaiduLocationApiDem", "已到设定位置附近" + distance);
			if (distance <= 5000) {
				for (int i = 0; i < 10; i++) {
					Toast.makeText(getApplicationContext(),
							"我猜你在华侨大学厦门校区附近，距离" + distance + "m",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				for (int i = 0; i < 10; i++) {
					Toast.makeText(getApplicationContext(),
							"%>_<%距离华侨大学厦门校区" + distance + "m",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
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
            schools.clear();
            getSchool();
			mLocationClient.stop();
		}

	}

}
