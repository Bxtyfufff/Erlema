package com.erlema.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.erlema.activity.WantedGoodsDetailsActivity;
import com.erlema.bean.GoodsWanted;
import com.erlema.bean.MyUser;
import com.erlema.util.MyUtils;
import com.example.erlema.R;
import com.gc.materialdesign.widgets.Dialog;

public class myQiuFragment extends BaseFragment {
    View RootView = null;
    private List<GoodsWanted> datawgd = new ArrayList<GoodsWanted>();
    private RecyclerView myRecycle;
    private SwipeRefreshLayout sw;
    private int tapTag;// 标签类型
    MyUser login_user;
    recWgdAdapter adapter;
    int gotcount;
    private boolean isme;
    private String userId;

    public static myQiuFragment newInstance(boolean arg, String userId) {
        myQiuFragment fragment = new myQiuFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isme", arg);
        bundle.putString("userId", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MyUtils.showLog("fragment", tapTag + "onCreateView");
        RootView = inflater.inflate(R.layout.goods_about_me1, container, false);
        myRecycle = (RecyclerView) RootView.findViewById(R.id.goodsrv);
        return RootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isme = getArguments().getBoolean("isme");
            userId = getArguments().getString("userId");
        }
    }

    @Override
    public void initViews() {
        myRecycle.setItemAnimator(new DefaultItemAnimator());
        myRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        sw = (SwipeRefreshLayout) getActivity().findViewById(R.id.about_swp1);
        sw.setColorScheme(R.color.main_color, R.color.red, R.color.green);
        sw.post(new Runnable() {
            @Override
            public void run() {
                if (gotcount == 0) {
                    sw.setRefreshing(true);
                    ReflashData();
                }
            }
        });
    }

    @Override
    public void initListeners() {
        sw.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                MyUtils.showLog("fragment", tapTag + "onRefresh");
                ReflashData();
            }
        });
    }

    @Override
    public void initData() {
        login_user = BmobUser.getCurrentUser(getActivity(), MyUser.class);
        adapter = new recWgdAdapter(getActivity(), datawgd);
        adapter.SetOnRecClickListener(new onRecClickListener() {

            @Override
            public void onClick(int position) {
                // 点击计数
                GoodsWanted g = datawgd.get(position);
                // g.increment("clickCount");
                // g.update(getActivity());
                MyUtils.showLog("ListView", datawgd.size() + "you clicked on "
                        + position);
                Intent viewIma = new Intent(getActivity(),
                        WantedGoodsDetailsActivity.class);
                viewIma.putExtra("goodsId", g.getObjectId());
                startActivity(viewIma);
            }
        });
        if (isme) {
            adapter.SetOnRecLongClickListener(new onRecLongClickListener() {

                @Override
                public void onLongClick(final int position) {
                    Dialog d = new Dialog(getActivity(), "警告", "确认删除？");
                    d.addCancelButton("取消");
                    d.setOnAcceptButtonClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            GoodsWanted g = new GoodsWanted();
                            g.setObjectId(datawgd.get(position).getObjectId());
                            g.delete(getActivity());
                            adapter.DeleteItem(position);
                        }
                    });
                    d.show();
                }
            });
        }
        myRecycle.setAdapter(adapter);
    }

    private void ReflashData() {
        datawgd.clear();
        datawgd.clear();
        gotcount = 0;
        adapter.DeleteAll();
        GetData();
    }

    private void GetData() {
        MyUtils.showLog("fragment", tapTag + "GetData");
        searchMyqiu();
    }

    private onRecClickListener onRecClickListener;
    private onRecLongClickListener onRecLongClickListener;

    private class recWgdAdapter extends RecyclerView.Adapter<RecViewHolder> {
        private final LayoutInflater mLayoutInflater;
        private final Context mContext;
        List<GoodsWanted> mDatas;

        public recWgdAdapter(Context mContext, List<GoodsWanted> mDatas) {
            super();
            this.mContext = mContext;
            this.mLayoutInflater = LayoutInflater.from(mContext);
            this.mDatas = mDatas;
        }

        @Override
        public int getItemCount() {
            return mDatas == null ? 0 : mDatas.size();
        }

        @Override
        public void onBindViewHolder(RecViewHolder holder, int position) {
            final ImageView iv = holder.img;
            final GoodsWanted g = (GoodsWanted) mDatas.get(position);
            BmobQuery<MyUser> bq = new BmobQuery<MyUser>();
            bq.getObject(mContext, g.getOwerID(), new GetListener<MyUser>() {

                @Override
                public void onFailure(int arg0, String arg1) {

                }

                @Override
                public void onSuccess(MyUser u) {
                    GoodsWanted wg = new GoodsWanted();
                    wg.setAvator(u.getAvator());
                    wg.update(mContext, g.getObjectId(), null);
                }
            });
            Glide.with(getActivity()).load(g.getAvator())
                    .error(R.drawable.img_error).override(240, 180)
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(iv);// 缓存所有尺寸
            holder.title.setText(g.getTitle());
            holder.content.setText(g.getDescrib());
            holder.time.setText(MyUtils.getTimeElapse(g.getCreatedAt()));
            holder.school.setText(g.getSchool());
            holder.price.setText("￥" + g.getPirce_low() + "~"
                    + g.getPirce_high());
        }

        @Override
        public RecViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
            View v = mLayoutInflater.inflate(R.layout.my_index_list_item, arg0,
                    false);
            return new RecViewHolder(v);
        }

        public void AddItem(int positon, GoodsWanted g) {
            this.mDatas.add(g);
            this.notifyItemInserted(positon);
        }

        public void DeleteAll() {
            this.mDatas.clear();
            notifyDataSetChanged();
        }

        public void DeleteItem(int location) {
            this.mDatas.remove(location);
            notifyItemRemoved(location);
        }

        public void SetOnRecClickListener(onRecClickListener l) {
            onRecClickListener = l;
        }

        public void SetOnRecLongClickListener(onRecLongClickListener l) {
            onRecLongClickListener = l;
        }
    }

    private class RecViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView title, content, price, time, school;

        public RecViewHolder(View arg0) {
            super(arg0);
            arg0.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getPosition();
                    if (onRecClickListener != null) {
                        onRecClickListener.onClick(position);
                    }
                }
            });
            arg0.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    int position = getPosition();
                    if (onRecLongClickListener != null) {
                        onRecLongClickListener.onLongClick(position);
                    }
                    return false;
                }
            });
            title = (TextView) arg0.findViewById(R.id.title);
            img = (ImageView) arg0.findViewById(R.id.img);
            content = (TextView) arg0.findViewById(R.id.qiu_content);
            price = (TextView) arg0.findViewById(R.id.qiu_pricerange);
            time = (TextView) arg0.findViewById(R.id.qiu_time);
            school = (TextView) arg0.findViewById(R.id.qiu_schoo);
        }

    }

    // 点击接口回调
    public interface onRecClickListener {
        public void onClick(int position);
    }

    // 长按接口回调
    public interface onRecLongClickListener {
        public void onLongClick(int position);
    }

    private void searchMyqiu() {
        MyUtils.showLog("fragment", tapTag + "searchMyqiu");
        BmobQuery<GoodsWanted> query = new BmobQuery<GoodsWanted>();
        query.order("status");
        // query.addWhereEqualTo("school", login_user.getSchool());// 返回同一个学校/
        query.addWhereEqualTo("owerID", userId);
        query.setSkip(gotcount);
        // query.setLimit(10);// ///////////测试使用的实际为10
        query.findObjects(getActivity(), new FindListener<GoodsWanted>() {

            @Override
            public void onSuccess(List<GoodsWanted> arg0) {
                MyUtils.showLog("index", arg0.size() + "");
                if (arg0.size() == 0) {
                    // ShowToast("没有数据...");
                } else {
                    gotcount += arg0.size();
                    for (int i = 0; i < arg0.size(); i++) {
                        adapter.AddItem(gotcount + i, arg0.get(i));
                        adapter.notifyItemInserted(gotcount + i);
                    }
                }
                sw.setRefreshing(false);
            }

            @Override
            public void onError(int arg0, String arg1) {
                MyUtils.showLog("indext", arg0 + "");
                sw.setRefreshing(false);
                sw.setRefreshing(false);
                if (9009 != arg0) {
                    // ShowToast("网络错误");
                }
            }
        });
    }
}
