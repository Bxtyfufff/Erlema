package com.erlema.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.erlema.bean.MySchools;
import com.example.erlema.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

public class SchoolsActivity extends BaseActivity {
    private List<String> schools;
    private ListView lv_school;
    private ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_schools);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void initViews() {
        lv_school= (ListView) findViewById(R.id.lv_school);
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, schools);
        lv_school.setAdapter(adapter);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {

    }


}
