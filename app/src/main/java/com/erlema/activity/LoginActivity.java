/**
 * LoginActivity.java
 *ErLeMa
 *@author 作者：bxtyfufff
 *@version 创建时间：2015-7-27 下午11:10:43 
 * 类说明 
 */

package com.erlema.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.BmobUser.BmobThirdUserAuth;
import cn.bmob.v3.listener.OtherLoginListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.baidu.location.LocationClient;
import com.erlema.bean.MyUser;
import com.erlema.config.Constants;
import com.example.erlema.R;
import com.example.erlema.R.color;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * LoginActivity.java
 * 
 * @author bxtyfufff
 * @version 2015-7-27下午11:10:43
 */
public class LoginActivity extends BaseActivity implements OnClickListener {


	SharedPreferences preference;
	SharedPreferences.Editor editor;
	public EditText username_et, userps_et;
    private TextInputLayout ti_username,ti_passw;
	public CheckBox remeberps;
	public ButtonRectangle login, regerst;
	public ButtonFlat login_weibo, login_qq,
			fergetpw;
	public ProgressBarCircularIndeterminate loadin_bar;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_login);
	}

	@Override
	public void initViews() {
//		 mLocationClient = new LocationClient(getApplicationContext()); //
		// 声明LocationClient类
		// mLocationClient.registerLocationListener(myListener); // 注册监听函数
		username_et = (EditText) findViewById(R.id.lg_ex_username);
        ti_username= (TextInputLayout) findViewById(R.id.ti_username);
        ti_passw= (TextInputLayout) findViewById(R.id.ti_passw);
        userps_et = (EditText) findViewById(R.id.lg_ex_userpw);
		remeberps = (CheckBox) findViewById(R.id.checkBox);
//		remeberps.setChecked(true);
		login = (ButtonRectangle) findViewById(R.id.btr_login);
		regerst = (ButtonRectangle) findViewById(R.id.btr_regest);
		login_weibo = (ButtonFlat) findViewById(R.id.login_weibo);
		login_qq = (ButtonFlat) findViewById(R.id.login_qq);
		fergetpw = (ButtonFlat) findViewById(R.id.forgetpw);
		loadin_bar = (ProgressBarCircularIndeterminate) findViewById(R.id.loadingbar);
		preference=getPreferences(MODE_PRIVATE);
		editor=preference.edit();
	}

	@Override
	public void initListeners() {
		regerst.setOnClickListener(this);
		fergetpw.setOnClickListener(this);
		login.setOnClickListener(this);
		login_qq.setOnClickListener(this);
		login_weibo.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// qqAuthorize();
		// weiboAuthorize();
	
		username_et.setText(preference.getString("username", ""));
		if(! "".equalsIgnoreCase(username_et.getText().toString().trim())){
			userps_et.setText(preference.getString("userpw", ""));
		}
	}

	private AuthInfo mAuthInfo;
	private SsoHandler mSsoHandler;

	// private AuthListener mAuthListener = new AuthListener();
	private void weiboAuthorize() {// 微博授权

		mAuthInfo = new AuthInfo(getApplicationContext(), Constants.APP_KEY,
				Constants.REDIRECT_URL, Constants.SCOPE);
		mSsoHandler = new SsoHandler(LoginActivity.this, mAuthInfo);
		mSsoHandler.authorize(new AuthListener());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	public static Tencent mTencent;

	private void qqAuthorize() {// qq授权
		if (mTencent == null) {
			mTencent = Tencent.createInstance(
					Constants.QQ_APPID,
					getApplicationContext());
		}
		mTencent.login(this, "all", new IUiListener() {

			@Override
			public void onComplete(Object arg0) {
				// TODO Auto-generated method stub
				if (arg0 != null) {
					JSONObject jsonObject = (JSONObject) arg0;
					try {
						String token = jsonObject
								.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
						String expires = jsonObject
								.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
						String openId = jsonObject
								.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
						BmobThirdUserAuth authInfo = new BmobThirdUserAuth(
								BmobThirdUserAuth.SNS_TYPE_QQ, token, expires,
								openId);
						loginWithAuth(authInfo);

					} catch (JSONException e) {
					}
				}
			}

			@Override
			public void onError(UiError arg0) {
				// TODO Auto-generated method stub
				ShowToast("QQ授权出错：" + arg0.errorCode + "--" + arg0.errorDetail);
			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				ShowToast("取消qq授权");
			}
		});
	}

	private void loginWithAuth(final BmobThirdUserAuth authInfo) {// bmob认证登陆
		BmobUser.loginWithAuthData(LoginActivity.this, authInfo,
				new OtherLoginListener() {

					@Override
					public void onSuccess(JSONObject userAuth) {
						// TODO Auto-generated method stub
						Log.i("login", authInfo.getSnsType() + "登陆成功返回:"
								+ userAuth);
						if (authInfo.getSnsType() == "qq") {
							getQqIfo();
						} else {
							getWeiboIfo();
						}
						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);
						intent.putExtra("json", userAuth.toString());
						intent.putExtra("from", authInfo.getSnsType());
						startActivity(intent);
						LoginActivity.this.finish();
					}

					@Override
					public void onFailure(int code, String msg) {
						// TODO Auto-generated method stub
						ShowToast("第三方登陆失败：" + msg);
					}

				});
	}

	protected void getWeiboIfo() {// WeiBo资料获取及更新信息
		MyUser mu = BmobUser.getCurrentUser(getApplicationContext(),
				MyUser.class);
		mu.setUsername("wb" + mu.getUsername());
		mu.update(getApplicationContext(), new UpdateListener() {

			@Override
			public void onSuccess() {
				Log.i("login", "success");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void getQqIfo() {// qq资料获取及更新信息
		QQToken qqt = mTencent.getQQToken();
		UserInfo qqif = new UserInfo(getApplicationContext(), qqt);
		qqif.getUserInfo(new IUiListener() {// 获取信息

			@Override
			public void onError(UiError arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete(Object arg0) {
				// TODO Auto-generated method stub
				JSONObject js = (JSONObject) arg0;
				Log.i("login", arg0.toString());
				try {
					String name = js.getString("nickname");
					String gender = js.getString("gender");
					String img_url = js.getString("figureurl");
					// ShowToast(BmobUser.getCurrentUser(getApplicationContext()).getUsername());
					Log.i("login", name + gender + img_url);
					Log.i("login",
							BmobUser.getCurrentUser(getApplicationContext())
									.getUsername());
					MyUser bmobUser = BmobUser.getCurrentUser(
							LoginActivity.this, MyUser.class);
					bmobUser.setGender(gender);
					bmobUser.update(getApplicationContext(),
							bmobUser.getObjectId(), new UpdateListener() {

								@Override
								public void onSuccess() {
									// TODO
									// Auto-generated
									// method stub
									Log.i("login", "success");
								}

								@Override
								public void onFailure(int arg0, String arg1) {
									// TODO
									// Auto-generated
									// method stub
									// ShowToast(arg0+"  "+arg1);
									Log.i("login", arg0 + "  " + arg1);
								}
							});
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub

			}
		});
	}

	private class AuthListener implements WeiboAuthListener {// 登入按钮的监听器，接收授权结果
		@Override
		public void onComplete(Bundle values) {
			Oauth2AccessToken accessToken = Oauth2AccessToken
					.parseAccessToken(values);
			if (accessToken != null && accessToken.isSessionValid()) {
				// 调用Bmob提供的授权登录方法进行微博登陆，登录成功后，你就可以在后台管理界面的User表中看到微博登陆后的用户啦
				String token = accessToken.getToken();
				String expires = String.valueOf(accessToken.getExpiresTime());
				String uid = accessToken.getUid();
				Log.i("login", accessToken.toString());
				Log.i("login", "微博授权成功后返回的信息:token = " + token + ",expires ="
						+ expires + ",uid = " + uid);
				BmobThirdUserAuth authInfo = new BmobThirdUserAuth(
						BmobThirdUserAuth.SNS_TYPE_WEIBO, token, expires, uid);
				loginWithAuth(authInfo);

			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			ShowToast(e.getMessage());
		}

		@Override
		public void onCancel() {
			ShowToast("取消weibo授权");
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btr_login:
			remeberps.setChecked(true);
			loadin_bar.setVisibility(View.VISIBLE);
			String username = username_et.getText().toString().trim();
			String userpw = userps_et.getText().toString().trim();
			editor.putString("username", username);
//			ShowToast(remeberps.isCheck()+"");
			if (remeberps.isChecked()) {
				editor.putString("userpw", userpw);
			}else{
				editor.clear();
			}
			editor.commit();
			if ("".equalsIgnoreCase(username)) {
				username_et.setError("用戶名不能為空");
				loadin_bar.setVisibility(View.INVISIBLE);
			} else if ("".equalsIgnoreCase(userpw)) {
				
				userps_et.setError("密碼不能為空");
				loadin_bar.setVisibility(View.INVISIBLE);
			}else{
				MyUser user=new MyUser();
				user.setUsername(username);
				user.setPassword(userpw);
				user.login(getApplicationContext(), new SaveListener() {
					
					@Override
					public void onSuccess() {
						ToActivity(MainActivity.class);
						finish();
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						Log.i("login",arg0+arg1);
						if (arg0==101) {
							userps_et.setText("");
							userps_et.setError("用戶名或密碼錯誤");
							loadin_bar.setVisibility(View.INVISIBLE);
						}else{
							ShowToast("无法联网!");
							loadin_bar.setVisibility(View.INVISIBLE);
						}
					}
				});
			}
			
			break;
		case R.id.btr_regest:
			Intent toreg=new Intent(LoginActivity.this,RegisterActivity.class);
			startActivity(toreg);
			break;
		case R.id.login_weibo:
			weiboAuthorize();
			break;
		case R.id.login_qq:
			qqAuthorize();
			break;
			case R.id.forgetpw:
				startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
				break;

		default:
			break;
		}

	}
}
