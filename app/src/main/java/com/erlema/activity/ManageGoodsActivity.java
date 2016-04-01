package com.erlema.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.erlema.bean.MyGoods;
import com.erlema.bean.MyUser;
import com.erlema.config.Constants;
import com.erlema.util.MyUtils;
import com.example.erlema.R;
import com.gc.materialdesign.widgets.Dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

public class ManageGoodsActivity extends BaseActivity implements View.OnClickListener {
    private ListView mListView;//listview
    private Button bt_deleta, bt_complete;
    private ArrayAdapter adapter;//listView 适配器
    private String[] buyers_id;//传进来的数据
    private String[] buyers_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_manage_goods);
        buyers_id = getIntent().getBundleExtra("buyers").getStringArray("buyers");
    }

    @Override
    public void initViews() {
        mListView = (ListView) findViewById(R.id.lv_buyer);
        bt_complete = (Button) findViewById(R.id.bt_complete);
        bt_deleta = (Button) findViewById(R.id.bt_delete);
    }

    @Override
    public void initListeners() {
        bt_complete.setOnClickListener(this);
        bt_deleta.setOnClickListener(this);
    }

    @Override
    public void initData() {
        getBuyerName();
//        if (buyers_id.length!=0){
        buyers_name = Arrays.copyOf(buyers_id, buyers_id.length);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, buyers_name);
        mListView.setAdapter(adapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        View emptyView = findViewById(R.id.emptyview);
        mListView.setEmptyView(emptyView);
//        }
    }

    /**
     * 获取想要购买的用户
     */
    private void getBuyerName() {
        if (buyers_id != null) {
            for (int i = 0; i < buyers_id.length; i++) {
                BmobQuery<MyUser> bq = new BmobQuery<MyUser>();
                bq.addQueryKeys("username");
                final int finalI = i;
                bq.getObject(getApplicationContext(), buyers_id[i], new GetListener<MyUser>() {
                    @Override
                    public void onSuccess(MyUser u) {
                        buyers_name[finalI] = u.getUsername();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_delete:
                Dialog d = new Dialog(this, "警告", "将删除这个物品及其评论，该操作不可逆！");
                d.addCancelButton("取消");
                d.setOnAcceptButtonClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //数据是使用Intent返回
                        Intent intent = new Intent();
                        //把返回数据存入Intent
                        //设置返回数据
                        ManageGoodsActivity.this.setResult(Constants.RESULT_DELETE, intent);
                        //关闭Activity
                        ManageGoodsActivity.this.finish();
                    }
                });
                d.show();
                break;
            case R.id.bt_complete:
                int position=mListView.getCheckedItemPosition();
                MyUtils.showLog("position=",position+"");
                if (position>=0){
                    //数据是使用Intent返回
                    Intent in = new Intent();
                    //把返回数据存入Intent
                    in.putExtra("userID",buyers_id[position]);
                    in.putExtra("userName",buyers_name[position]);
                    //设置返回数据
                    ManageGoodsActivity.this.setResult(Constants.RESULT_COMPLETE, in);
                    //关闭Activity
                    ManageGoodsActivity.this.finish();
                }else {
                    Snackbar.make(bt_complete,"请选择买家",Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
