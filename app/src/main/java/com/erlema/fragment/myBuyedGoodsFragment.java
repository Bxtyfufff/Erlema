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
import cn.bmob.v3.listener.GetListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.erlema.activity.GoodsDetailsActivity;
import com.erlema.bean.MyGoods;
import com.erlema.bean.MyUser;
import com.erlema.util.MyUtils;
import com.example.erlema.R;
import com.gc.materialdesign.widgets.Dialog;

public class myBuyedGoodsFragment extends BaseFragment {
	View RootView = null;
	private List<MyGoods> datagd = new ArrayList<MyGoods>();
	private RecyclerView myRecycle;
	private SwipeRefreshLayout sw;
	MyUser loginUser;
	String userId;
	boolean isme;
	recGdAdapter gadapter;
	int gotcount;


	public static myBuyedGoodsFragment newInstance(String arg) {
		myBuyedGoodsFragment fragment = new myBuyedGoodsFragment();
		Bundle bundle = new Bundle();
		bundle.putString("userId", arg);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		RootView = inflater.inflate(R.layout.goods_about_me2, container, false);
		myRecycle = (RecyclerView) RootView.findViewById(R.id.goodsrv);
		return RootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			userId = getArguments().getString("userId");
		}
	}

	@Override
	public void initViews() {
		loginUser = BmobUser.getCurrentUser(getActivity(), MyUser.class);
		if (loginUser.getObjectId().equals(userId)) {
			isme=true;
		}
		myRecycle.setItemAnimator(new DefaultItemAnimator());
		myRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
		sw = (SwipeRefreshLayout) getActivity().findViewById(R.id.about_swp2);
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
				ReflashData();
			}
		});
	}

	@Override
	public void initData() {
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
					Dialog d = new Dialog(getActivity(), "警告", "确认删除这条记录？");
					d.addCancelButton("取消");
					d.setOnAcceptButtonClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							loginUser.removeBuys(datagd.get(position)
									.getObjectId());
							loginUser.update(getActivity());
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
		datagd.clear();
		gotcount = 0;
		gadapter.DeleteAll();
		GetData();
	}

	private void GetData() {
		searchBuyedgoods(userId);
	}

	private void searchBuyedgoods(String userID) {
		BmobQuery<MyUser> query = new BmobQuery<MyUser>();
		query.getObject(getActivity(), userID, new GetListener<MyUser>() {

			@Override
			public void onFailure(int arg0, String arg1) {
				MyUtils.showTost(getActivity(), "网络出错");
				sw.setRefreshing(false);
			}

			@Override
			public void onSuccess(MyUser arg0) {
				List<String> myBuystId = arg0.getBuys();
				if (myBuystId != null) {
					gotcount += myBuystId.size();
					for (String string : myBuystId) {
						BmobQuery<MyGoods> bq = new BmobQuery<MyGoods>();
						bq.getObject(getActivity(), string,
								new GetListener<MyGoods>() {

									@Override
									public void onFailure(int arg0, String arg1) {
										sw.setRefreshing(false);
									}

									@Override
									public void onSuccess(MyGoods arg0) {

										gadapter.AddItem(gotcount, arg0);
										sw.setRefreshing(false);
									}
								});
					}
					sw.setRefreshing(false);
				} else {
					sw.setRefreshing(false);
				}
			}
		});
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
			if (g.getStatus() == 0) {
				holder.status.setText("出售中");
			}
			if (g.getStatus() == 1) {
				holder.status.setText("审核中");
			}
			if (g.getStatus() == 3) {
				holder.status.setText("交易完成");
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

	private class RecViewHolder extends RecyclerView.ViewHolder {

		ImageView img;
		TextView title, content, price, time, status;

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
			status = (TextView) arg0.findViewById(R.id.qiu_schoo);
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
