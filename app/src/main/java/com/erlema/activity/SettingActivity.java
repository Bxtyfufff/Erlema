package com.erlema.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.LocalThumbnailListener;
import com.bmob.btp.callback.UploadListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.erlema.bean.CircleImageView;
import com.erlema.bean.MyUser;
import com.erlema.util.MyUtils;
import com.example.erlema.R;
import com.gc.materialdesign.widgets.Dialog;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private EditText ex_tel, et_nickname, et_realname, et_gender, et_school, et_city, et_email, et_statement;
    private Button loginout, bt_clear, bt_about;
    private MyUser loginUer;
    private CircleImageView iv_avactor;
    private String pathImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 打开图片
        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                // 查询选择图片
                Cursor cursor = getContentResolver().query(uri,
                        new String[]{MediaStore.Images.Media.DATA}, null,
                        null, null);
                // 返回 没找到选择图片
                if (null == cursor) {
                    return;
                }
                // 光标移动至开头 获取图片路径
                cursor.moveToFirst();
                pathImage = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
                if (pathImage == null) {
                    ShowToast("图片地址异常");
                    return;
                }
            } else {
                pathImage = data.getData().getEncodedPath();
                MyUtils.showLog("imageop", "  else" + pathImage);
            }
            if (pathImage != null) {
                Glide.with(SettingActivity.this).load(pathImage).override(300, 300).into(iv_avactor);
                BmobProFile.getInstance(getApplicationContext()).getLocalThumbnail(pathImage
                        , 5, 300, 300, new LocalThumbnailListener() {
                    @Override
                    public void onSuccess(String s) {
                        uploadThubnail(s);
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        } // end if 打开图片
    }


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(AuthActivity.class, null, true);
            }
        });
    }


    @Override
    public void initViews() {
        loginout = (Button) findViewById(R.id.loginout);
        bt_clear = (Button) findViewById(R.id.bt_clear);
        bt_about = (Button) findViewById(R.id.bt_about);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        et_nickname.setKeyListener(null);
        et_realname = (EditText) findViewById(R.id.et_realname);
        et_gender = (EditText) findViewById(R.id.et_gender);
        et_school = (EditText) findViewById(R.id.et_school);
        et_school.setKeyListener(null);
        et_city = (EditText) findViewById(R.id.et_city);
        et_email = (EditText) findViewById(R.id.et_email);
        et_statement = (EditText) findViewById(R.id.et_introduce);
        ex_tel = (EditText) findViewById(R.id.et_tel);
        iv_avactor = (CircleImageView) findViewById(R.id.iv_avatar);
    }

    @Override
    public void initListeners() {
        loginout.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        iv_avactor.setOnClickListener(this);
        bt_about.setOnClickListener(this);
    }

    @Override
    public void initData() {
        loginUer = BmobUser.getCurrentUser(getApplicationContext(), MyUser.class);
        et_nickname.setText(loginUer.getUsername());
        et_realname.setText(loginUer.getrelName());
        et_gender.setText(loginUer.getGender());
        et_school.setText(loginUer.getSchool());
        et_city.setText(loginUer.getCity());
        et_email.setText(loginUer.getEmail());
        et_statement.setText(loginUer.getStatement());
        ex_tel.setText(loginUer.getMobilePhoneNumber());
        Glide.with(SettingActivity.this).load(loginUer.getAvator()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_round_head).into(iv_avactor);
        updateLoginUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                SettingActivity.this.finish();
                break;
            case R.id.action_update:
                saveUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateLoginUser() {
        BmobQuery<MyUser> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(getApplicationContext(), loginUer.getObjectId(), new GetListener<MyUser>() {
            @Override
            public void onSuccess(MyUser myUser) {
                loginUer.setSchool(myUser.getSchool());
                loginUer.setAvator(myUser.getAvator());
                loginUer.setCity(myUser.getCity());
                loginUer.setCredibility(myUser.getCredibility());
                loginUer.setGender(myUser.getGender());
                loginUer.setrelName(myUser.getrelName());
                loginUer.setStatement(myUser.getStatement());
                loginUer.setAuthed(myUser.getAuthed());
                loginUer.update(getApplicationContext());
                et_nickname.setText(loginUer.getUsername());
                et_realname.setText(loginUer.getrelName());
                et_gender.setText(loginUer.getGender());
                et_school.setText(loginUer.getSchool());
                et_city.setText(loginUer.getCity());
                et_email.setText(loginUer.getEmail());
                et_statement.setText(loginUer.getStatement());
                ex_tel.setText(loginUer.getMobilePhoneNumber());
                Glide.with(SettingActivity.this).load(loginUer.getAvator()).placeholder(R.drawable.default_round_head).into(iv_avactor);
            }

            @Override
            public void onFailure(int i, String s) {
                Snackbar.make(et_nickname, "获取最新信息失败" + s, Snackbar.LENGTH_LONG)
                        .setAction("ok", null).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginout:
                LoginOut();
                break;
            case R.id.iv_avatar:
                Intent intent1 = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent1, 1);
                break;
            case R.id.bt_clear:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(getApplicationContext()).clearDiskCache();
                    }
                }).start();
                Glide.get(getApplicationContext()).clearMemory();
                ShowToast("清理完成");
                break;
            case R.id.bt_about:
                startActivity(AboutActivity.class,null,false);
                break;
        }
    }

    private void saveUser() {
        loginUer.setrelName(et_realname.getText().toString().trim());
        loginUer.setMobilePhoneNumber(ex_tel.getText().toString().trim());
        loginUer.setEmail(et_email.getText().toString().trim());
        loginUer.setStatement(et_statement.getText().toString().trim());
        loginUer.setCity(et_city.getText().toString().trim());
        loginUer.update(getApplicationContext(), loginUer.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                ShowToast("更新成功");
                SettingActivity.this.finish();
            }

            @Override
            public void onFailure(int i, String s) {
                Snackbar.make(et_nickname, "更新失败：" + s, Snackbar.LENGTH_LONG)
                        .setAction("ok", null).show();
            }
        });
    }

    private void LoginOut() {
        Dialog d = new Dialog(SettingActivity.this, null, "是否注销?");
        d.addCancelButton("取消");
        d.setOnAcceptButtonClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BmobUser.logOut(getApplicationContext());
                Intent login = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(login);
                SettingActivity.this.finish();
            }
        });
        d.show();
    }

    /**
     * 上传缩略图
     *
     * @param s
     */
    private void uploadThubnail(String s) {
        BTPFileResponse response = BmobProFile.getInstance(getApplicationContext()).upload(s, new UploadListener() {

            @Override
            public void onSuccess(String fileName, String url, BmobFile file) {
                Log.i("bmob", "文件上传成功：" + fileName + ",可访问的文件地址：" + file.getUrl());
                Glide.with(getApplicationContext()).load(file.getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_avactor);
                loginUer.setAvator(file.getUrl());
                loginUer.update(getApplicationContext());
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgress(int progress) {
                // TODO Auto-generated method stub
                Log.i("bmob", "onProgress :" + progress);
                ShowToast("头像上传中，进度：" + progress + "%");
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                // TODO Auto-generated method stub
                Log.i("bmob", "文件上传失败：" + errormsg);
            }
        });
    }

}
