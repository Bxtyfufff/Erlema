package com.erlema.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.erlema.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;

public class ResetPasswordActivity extends BaseActivity {
    EditText et_email;
    Button bt_resetps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("重置密码");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initViews() {
        et_email = (EditText) findViewById(R.id.et_email);
        bt_resetps = (Button) findViewById(R.id.bt_resetpassw);
    }

    @Override
    public void initListeners() {
        bt_resetps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = et_email.getText().toString().trim();
                if ("".equals(email)) {
                    et_email.setError("请输入注册的邮箱");
                } else {
                    BmobUser.resetPasswordByEmail(getApplicationContext(), email, new ResetPasswordByEmailListener() {
                        @Override
                        public void onSuccess() {
                            Snackbar.make(et_email, "重置密码请求成功，请到" + email + "邮箱进行密码重置操作", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }


                        @Override
                        public void onFailure(int i, String s) {
                            Snackbar.make(et_email, "重置密码失败:" + s, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ResetPasswordActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

