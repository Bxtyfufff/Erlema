package com.erlema.fragment;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.erlema.bean.GoodsWanted;
import com.erlema.bean.MyGoods;
import com.erlema.bean.MyUser;
import com.erlema.util.MyUtils;
import com.example.erlema.R;
import android.content.Context;
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

public class GoodsAboutMeFragment extends BaseFragment {
	View RootView = null;
	private List<GoodsWanted> datawgd = new ArrayList<GoodsWanted>();
	private List<MyGoods> datagd = new ArrayList<MyGoods>();
	private RecyclerView myRecycle;
	private SwipeRefreshLayout sw;
	private int tapTag;// 标签类型
	MyUser login_user;
	recGdAdapter gadapter;
	recWgdAdapter wadapter;
	int gotcount;

	public static GoodsAboutMeFragment newInstance(int num) {
		GoodsAboutMeFragment fragment = new GoodsAboutMeFragment();
		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		fragment.setArguments(args);
		return fragment;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tapTag = getArguments() != null ? getArguments().getInt("num") : 0;
		MyUtils.showLog("fragment", tapTag + "onCreate");
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
				if (datagd.size() == 0 && datawgd.size() == 0) {
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
		switch (tapTag) {
		case 0:
			gadapter = new recGdAdapter(getActivity(), datagd);
			myRecycle.setAdapter(gadapter);
			break;
		case 1:
			wadapter = new recWgdAdapter(getActivity(), datawgd);
			myRecycle.setAdapter(wadapter);
			break;
		case 2:
			gadapter = new recGdAdapter(getActivity(), datagd);
			myRecycle.setAdapter(gadapter);
			break;
		case 3:
			gadapter = new recGdAdapter(getActivity(), datagd);
			myRecycle.setAdapter(gadapter);
			break;
		}

	}

	boolean once = false;

	private void ReflashData() {
		datagd.clear();
		datawgd.clear();
		gotcount = 0;
		if (tapTag == 1) {
			wadapter.DeleteAll();
		} else {
			gadapter.DeleteAll();
		}
		GetData();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		MyUtils.showLog("fragment", tapTag + "onDestroy");
	}

	private void GetData() {
		MyUtils.showLog("fragment", tapTag + "GetData");
		switch (tapTag) {
		case 0:
			searchMysend();
			break;
		case 1:
			// searchMyqiu();
			break;
		case 2:
			// searchMyCollection();
			break;
		case 3:
			// searchMyCollection();
			break;

		}
	}

	private void searchMysend() {
		MyUtils.showLog("fragment", tapTag + "searchMysend");
		BmobQuery<MyGoods> query = new BmobQuery<MyGoods>();
		query.order("status");
		// query.addWhereEqualTo("school", login_user.getSchool());// 返回同一个学校/
		query.addWhereEqualTo("owerID", login_user.getObjectId());
		query.setSkip(gotcount);
		// query.setLimit(10);// ///////////测试使用的实际为10
		query.findObjects(getActivity(), new FindListener<MyGoods>() {

			@Override
			public void onSuccess(List<MyGoods> arg0) {
				MyUtils.showLog(tapTag + "index", arg0.size() + "");
				if (arg0.size() == 0) {
					ShowToast("没有更多了...");
				} else {
					gotcount += arg0.size();
					for (int i = 0; i < arg0.size(); i++) {
						datagd.add(arg0.get(i));
						gadapter.AddItem(gotcount + i, arg0.get(i));
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
					ShowToast("网络错误");
				}
			}
		});
	}

	private void searchMyCollection() {
		MyUtils.showLog("fragment", tapTag + "searchMyCollection");
		BmobQuery<MyUser> query = new BmobQuery<MyUser>();
		query.getObject(getActivity(), login_user.getObjectId(),
				new GetListener<MyUser>() {

					@Override
					public void onFailure(int arg0, String arg1) {
						sw.setRefreshing(false);
						MyUtils.showTost(getActivity(), "网络出错");
					}

					@Override
					public void onSuccess(MyUser arg0) {
						List<String> myCollectId = arg0.getCollection();
						gotcount += myCollectId.size();
						for (String string : myCollectId) {
							BmobQuery<MyGoods> bq = new BmobQuery<MyGoods>();
							bq.getObject(getActivity(), string,
									new GetListener<MyGoods>() {

										@Override
										public void onFailure(int arg0,
												String arg1) {
											sw.setRefreshing(false);
										}

										@Override
										public void onSuccess(MyGoods arg0) {

											datagd.add(arg0);
											gadapter.AddItem(gotcount, arg0);
											sw.setRefreshing(false);
										}
									});
						}
					}
				});

	}

	private void searchMyqiu() {
		MyUtils.showLog("fragment", tapTag + "searchMyqiu");
		BmobQuery<GoodsWanted> query = new BmobQuery<GoodsWanted>();
		query.order("status");
		// query.addWhereEqualTo("school", login_user.getSchool());// 返回同一个学校/
		query.addWhereEqualTo("owerID", login_user.getObjectId());
		query.setSkip(gotcount);
		// query.setLimit(10);// ///////////测试使用的实际为10
		query.findObjects(getActivity(), new FindListener<GoodsWanted>() {

			@Override
			public void onSuccess(List<GoodsWanted> arg0) {
				MyUtils.showLog("index", arg0.size() + "");
				if (arg0.size() == 0) {
					ShowToast("没有更多了...");
				} else {
					gotcount += arg0.size();
					for (int i = 0; i < arg0.size(); i++) {
						datawgd.add(arg0.get(i));
						wadapter.AddItem(gotcount + i, arg0.get(i));
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

	private class recWgdAdapter extends RecyclerView.Adapter<RecViewHolder> {
		private final LayoutInflater mLayoutInflater;
		private final Context mContext;
		List<GoodsWanted> mDatas;
		private onRecClickListener onRecClickListener;
		private onRecLongClickListener onRecLongClickListener;

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
			if (tapTag == 1) {

				final ImageView iv = holder.img;
				GoodsWanted g = (GoodsWanted) mDatas.get(position);
				BmobQuery<MyUser> bq = new BmobQuery<MyUser>();
				bq.getObject(mContext, g.getOwerID(),
						new GetListener<MyUser>() {

							@Override
							public void onFailure(int arg0, String arg1) {

							}

							@Override
							public void onSuccess(MyUser u) {
								Glide.with(mContext)
										.load(u.getAvator())
										.error(R.drawable.img_error)
										.override(240, 180)
										.centerCrop()
										.diskCacheStrategy(
												DiskCacheStrategy.ALL).into(iv);// 缓存所有尺寸
							}
						});
				holder.title.setText(g.getTitle());
				holder.content.setText(g.getDescrib());
				holder.time.setText(MyUtils.getTimeElapse(g.getCreatedAt()));
				holder.school.setText(g.getSchool());
				holder.price.setText("￥" + g.getPirce_low() + "~"
						+ g.getPirce_high());
			}
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

		public void SetOnRecClickListener(onRecClickListener l) {
			this.onRecClickListener = l;
		}

		public void SetOnRecLongClickListener(onRecLongClickListener l) {
			this.onRecLongClickListener = l;
		}
	}

	private class recGdAdapter extends RecyclerView.Adapter<RecViewHolder> {
		private final LayoutInflater mLayoutInflater;
		private final Context mContext;
		List<MyGoods> mDatas;
		private onRecClickListener onRecClickListener;
		private onRecLongClickListener onRecLongClickListener;

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
			if (tapTag == 1) {

				final ImageView iv = holder.img;
				MyGoods g = mDatas.get(position);
				BmobQuery<MyUser> bq = new BmobQuery<MyUser>();
				bq.getObject(mContext, g.getOwerID(),
						new GetListener<MyUser>() {

							@Override
							public void onFailure(int arg0, String arg1) {

							}

							@Override
							public void onSuccess(MyUser u) {
								Glide.with(mContext)
										.load(u.getAvator())
										.error(R.drawable.img_error)
										.override(240, 180)
										.centerCrop()
										.diskCacheStrategy(
												DiskCacheStrategy.ALL).into(iv);// 缓存所有尺寸
							}
						});
				holder.title.setText(g.getTitle());
				holder.content.setText(g.getDescrib());
				holder.time.setText(MyUtils.getTimeElapse(g.getCreatedAt()));
				holder.school.setText(g.getSchool());
				holder.price.setText("￥" + g.getPirce());
			}
		}

		@Override
		public RecViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
			View v = mLayoutInflater.inflate(R.layout.my_index_list_item, arg0,
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

		public void SetOnRecClickListener(onRecClickListener l) {
			this.onRecClickListener = l;
		}

		public void SetOnRecLongClickListener(onRecLongClickListener l) {
			this.onRecLongClickListener = l;
		}
	}

	private class RecViewHolder extends RecyclerView.ViewHolder {

		ImageView img;
		TextView title, content, price, time, school;
		onRecClickListener onRecClickListener;
		onRecLongClickListener onRecLongClickListener;

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
}
