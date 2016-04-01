package com.erlema.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.erlema.bean.MyGoods;
import com.erlema.bean.MyUser;
import com.example.erlema.R;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.update.AppVersion;

public class AboutActivity extends BaseActivity {
    TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("历史版本");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AboutActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initViews() {
        tv_version = (TextView) findViewById(R.id.tv_version);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {
        BmobQuery<AppVersion> bq = new BmobQuery<>("appVersion");
        bq.order("-createdAt");
        bq.findObjects(getApplicationContext(), new FindListener<AppVersion>() {
            @Override
            public void onSuccess(List<AppVersion> list) {
                SpannableStringBuilder ss = new SpannableStringBuilder();
                for (AppVersion version : list) {
                    String firsL = version.getVersion();
                    ss.append(firsL);
                    ss.setSpan(new ForegroundColorSpan(Color.BLUE),
                            ss.length() - firsL.length(), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.append("(" + version.getCreatedAt() + ")\n" );
                    if (version.getVersion().equals(getVersion())){
                        ss.append("当前版本\n");
                    }
                    ss.append("------------------------------------------------------------\n");
                    ss.append(version.getUpdate_log() + "\n\n");
                }

                tv_version.append(ss);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "未知";
        }
    }
}
