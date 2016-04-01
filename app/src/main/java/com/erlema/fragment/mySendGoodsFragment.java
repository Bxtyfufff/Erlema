package com.erlema.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.erlema.activity.GoodsDetailsActivity;
import com.erlema.bean.MyGoods;
import com.erlema.bean.MyUser;
import com.erlema.util.MyUtils;
import com.example.erlema.R;
import com.gc.materialdesign.widgets.Dialog;

public class mySendGoodsFragment extends BaseFragment {
	View RootView = null;
	private List<MyGoods> datagd = new ArrayList<MyGoods>();
	private RecyclerView myRecycle;
	private SwipeRefreshLayout sw;
	private int tapTag;// 标签类型
	MyUser login_user;
	recGdAdapter gadapter;
	int gotcount;
	private boolean isme;
	private String userId;
	public static mySendGoodsFragment newInstance(boolean isme,String userId) {
		MyUtils.showLog("mySendGoodsFragment","newInstance");
		mySendGoodsFragment fragment = new mySendGoodsFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isme", isme);
		bundle.putString("userId", userId);
		fragment.setArguments(bundle);
		return fragment;
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
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		MyUtils.showLog("fragment", tapTag + "onCreateView");
		RootView = inflater.inflate(R.layout.goods_about_me, container, false);
		myRecycle = (RecyclerView) RootView.findViewById(R.id.goodsrv);
		return RootView;
	}

	@Override
	public void initViews() {
		myRecycle.setItemAnimator(new DefaultItemAnimator());
		myRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
		sw = (SwipeRefreshLayout) getActivity().findViewById(R.id.about_swp);
		sw.setColorScheme(R.color.main_color, R.color.red, R.color.green);
		sw.post(new Runnable() {
			@Override
			public void run() {
				if (gotcount == 0) {
					sw.setRefreshing(true);
					MyUtils.showLog("mySendGoodsFragment", "ReflashData");
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
		gadapter = new recGdAdapter(getActivity(), datagd);
		gadapter.SetOnRecClickListener(new onRecClickListener() {

			@Override
			public void onClick(int position) {
				MyUtils.showLog("send", "click");
				MyGoods g = datagd.get(position);
				// 点击计数
				g.increment("clickCount");
				g.update(getActivity());
				Intent todetails = new Intent(getActivity(),
						GoodsDetailsActivity.class);
				todetails.putExtra("goodsId", g.getObjectId());
				todetails.putExtra("GoodsTitle", g.getTitle());
				todetails.putExtra("GoodsImgs", g.getImgUrl());
				startActivity(todetails);
			}
		});
		if (isme) {
			gadapter.SetOnRecLongClickListener(new onRecLongClickListener() {

				@Override
				public void onLongClick(final int position) {
					Dialog d = new Dialog(getActivity(), "警告", "确认删除？");
					d.addCancelButton("取消");
					d.setOnAcceptButtonClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							MyGoods g = new MyGoods();
							g.setObjectId(datagd.get(position).getObjectId());
							g.delete(getActivity());
							gadapter.DeleteItem(position);
						}
					});
					d.show();
				}
			});
		}
		myRecycle.setAdapter(gadapter);
	}

	private void ReflashData() {
		datagd.clear();
		gotcount = 0;
		gadapter.DeleteAll();
		GetData();
	}

	private void GetData() {
		MyUtils.showLog("fragment", tapTag + "GetData");
		searchMysend();
	}

	private onRecClickListener onRecClickListener;
	private onRecLongClickListener onRecLongClickListener;

	private class recGdAdapter extends RecyclerView.Adapter<RecViewHolder> {
		private final LayoutInflater mLayoutInflater;
		private final Context mContext;
		List<MyGoods> mDatas;

		public recGdAdapter(Context mContext, List<MyGoods> mDatas) {
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
			MyGoods g = mDatas.get(position);
			Glide.with(mContext).load(g.getImgUrl()[0])
					.error(R.drawable.img_error).override(240, 180)
					.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
					.into(iv);// 缓存所有尺寸
			holder.title.setText(g.getTitle());
			holder.content.setText(g.getDescrib());
			holder.time.setText(MyUtils.getTimeElapse(g.getCreatedAt()));
			if (g.getStatus() == 0||g.getStatus()==2) {
				holder.statusl.setText("出售中");
			}
			if (g.getStatus() == 1) {
				holder.statusl.setText("审核中");
			}
			if (g.getStatus() == 3) {
				holder.statusl.setText("交易完成");
			}
			holder.price.setText("￥" + g.getPirce());
		}

		@Override
		public RecViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
			View v = mLayoutInflater.inflate(R.layout.my_send_list_item, arg0,
					false);
			return new RecViewHolder(v);
		}

		public void AddItem(int positon, MyGoods g) {
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

	private void searchMysend() {
		MyUtils.showLog("fragment", tapTag + "searchMysend");
		BmobQuery<MyGoods> query = new BmobQuery<MyGoods>();
		query.order("status,-createdAt");
		// query.addWhereEqualTo("school", login_user.getSchool());// 返回同一个学校/
		query.addWhereEqualTo("owerID", userId);
		query.setSkip(gotcount);
		// query.setLimit(10);// ///////////测试使用的实际为10
		query.findObjects(getActivity(), new FindListener<MyGoods>() {

			@Override
			public void onSuccess(List<MyGoods> arg0) {
				MyUtils.showLog(tapTag + "index", arg0.size() + "");
				if (arg0.size() == 0) {
					Snackbar.make(myRecycle,"没有数据。",Snackbar.LENGTH_LONG).show();
//					ShowToast("没有数据...");
				} else {
					gotcount += arg0.size();
					for (int i = 0; i < arg0.size(); i++) {
						gadapter.AddItem(gotcount + i, arg0.get(i));
						gadapter.notifyItemInserted(gotcount+i);
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
//					ShowToast("网络错误");
					Snackbar.make(myRecycle,"网络出错。",Snackbar.LENGTH_LONG).show();
				}
			}
		});
	}

	private class RecViewHolder extends RecyclerView.ViewHolder {

		ImageView img;
		TextView title, content, price, time, statusl;

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
			statusl = (TextView) arg0.findViewById(R.id.qiu_schoo);
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
}
