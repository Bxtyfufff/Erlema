package com.erlema.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

import com.erlema.activity.AddQiuActivity;
import com.erlema.activity.MainActivity;
import com.erlema.activity.WantedGoodsDetailsActivity;
import com.erlema.bean.GoodsWanted;
import com.erlema.adapter.IndexListviewAdapter;
import com.erlema.bean.MyUser;
import com.erlema.util.MyUtils;
import com.erlema.bean.MyViewFlipper;
import com.erlema.bean.MyViewFlipper.OnMyflipperLisrener;
import com.erlema.bean.myActionBar;
import com.erlema.bean.myActionBar.mClickListener;
import com.example.erlema.R;
import com.gc.materialdesign.widgets.Dialog;

public class IndexFragment extends BaseFragment {
	private ListView myListView;
	private myActionBar myactionbar;
	private IndexListviewAdapter adapter;
	private SwipeRefreshLayout sw;
	private MyViewFlipper viewFlipper;
	private List<GoodsWanted> data;
	private static int gotcount = 0;
	int headCount = 0;

	View RootView = null;

	public static IndexFragment newInstance(int num) {
		IndexFragment fragment = new IndexFragment();
		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (null == RootView) {
			RootView = inflater.inflate(R.layout.idex_layout, container, false);
		} else {
			ViewGroup mViewGroup = (ViewGroup) RootView.getParent();
			mViewGroup.removeView(RootView);
		}
		return RootView;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void initViews() {
		// loading();
		myactionbar = (myActionBar) getActivity().findViewById(
				R.id.myActionBar1);
		myactionbar.setButtonVisible(false, true);
		myactionbar.setRightButtonDrawable(getResources().getDrawable(
				R.drawable.add_bg));
		myactionbar.setTitleText("求购");
		myListView = (ListView) getActivity().findViewById(R.id.mytopic);
		sw = (SwipeRefreshLayout) getActivity().findViewById(R.id.indext_swp);
		sw.setColorScheme(R.color.main_color, R.color.red, R.color.green);
		sw.post(new Runnable() {
			@Override
			public void run() {
				sw.setRefreshing(true);
			}
		});
	}

	@Override
	public void initListeners() {
		sw.setOnRefreshListener(mOnRefreshListener);
		myListView.setOnItemClickListener(mOnItemClickListener);
		myListView.setOnScrollListener(mOnScrollListener);
		myactionbar.setmListener(new mClickListener() {

			@Override
			public void onRightClick() {
				BmobUser localUser = BmobUser.getCurrentUser(getActivity(),
						MyUser.class);
				BmobQuery<MyUser> bq = new BmobQuery<MyUser>();
				bq.getObject(getActivity(), localUser.getObjectId(),
						new GetListener<MyUser>() {

							@Override
							public void onFailure(int arg0, String arg1) {

							}

							@Override
							public void onSuccess(MyUser arg0) {
								MyUser u = new MyUser();
								u.setEmailVerified(arg0.getEmailVerified());
								u.setAuthed(arg0.getAuthed());
								u.setCollection(arg0.getCollection());
								u.setBuys(arg0.getBuys());
								u.update(getActivity(), arg0.getObjectId(),
										null);
							}
						});
				// 判断是否认证
				Boolean authed = BmobUser.getCurrentUser(getActivity(),
						MyUser.class).getEmailVerified();
				if (authed) {
					startActivity(new Intent(getActivity(),
							AddQiuActivity.class));
				} else {
					Dialog d = new Dialog(getActivity(), "邮箱还未认证！", "请去邮箱查收邮件~");
					d.show();
				}
			}

			@Override
			public void onLeftClick() {

			}
		});
	}

	@Override
	public void initData() {
		gotcount = 0;
		data = new ArrayList<GoodsWanted>();
		adapter = new IndexListviewAdapter(getActivity(), data);
		myListView.setAdapter(adapter);
		GetData();
	}

	private void ReflashData() {
		data.clear();
		gotcount = 0;
		GetData();
	}

	boolean once = false;
	private List<String> scholls;
	private void GetData() {
		BmobQuery<GoodsWanted> query = new BmobQuery<GoodsWanted>();
		MyUser login_user = BmobUser
				.getCurrentUser(getActivity(), MyUser.class);
		query.order("-createdAt");
		if (!once) {

			// query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);//
			// 先从缓存读取,否则网络
			once = true;
			if (query.hasCachedResult(getActivity(), GoodsWanted.class)) {
				sw.setRefreshing(false);
			}
		}
		// query.addWhereEqualTo("school", login_user.getSchool());// 返回同一个学校/
		scholls=((MainActivity)getActivity()).getSchools();
		if (scholls!=null &&scholls.size()>1){
			query.addWhereContainedIn("school",scholls);
		}
		query.setSkip(gotcount);
		query.setLimit(10);// ///////////测试使用的实际为10
		query.findObjects(getActivity(), new FindListener<GoodsWanted>() {

			@Override
			public void onSuccess(List<GoodsWanted> arg0) {
				MyUtils.showLog("index", arg0.size() + "");
				if (arg0.size() == 0) {
//					 ShowToast("没有更多了...");
					Snackbar.make(myactionbar,"没有数据了。",Snackbar.LENGTH_LONG).show();
				} else {
					gotcount += arg0.size();
					for (GoodsWanted myGoods : arg0) {
						data.add(myGoods);
					}
					adapter.notifyDataSetChanged();
					// loaded();
				}
				sw.setRefreshing(false);
				myListView.setEnabled(true);
				myListView.removeFooterView(footer);
			}

			@Override
			public void onError(int arg0, String arg1) {
				MyUtils.showLog("indext", arg0 + "");
				sw.setRefreshing(false);
				sw.setRefreshing(false);
				if (9009 != arg0) {
					Snackbar.make(myactionbar,"网络错误。",Snackbar.LENGTH_LONG).show();
					// ShowToast("网络错误");
					// SnackBar snb = new SnackBar(getActivity(), arg1, "ok",
					// new View.OnClickListener() {
					//
					// @Override
					// public void onClick(View v) {
					// sw.setRefreshing(false);
					// }
					// });
					// snb.setDismissTimer(60000);
					// snb.setCanceledOnTouchOutside(false);
					// snb.show();
				}
				if (footer != null) {
					myListView.removeFooterView(footer);
				}
			}
		});
	}

	int lastVisibleItem = 0;
	int lastItem = 0;
	View footer;
	OnScrollListener mOnScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// ShowToast(lastVisibleItem + "" + lastItem);
			if (lastVisibleItem == lastItem
					&& scrollState == SCROLL_STATE_TOUCH_SCROLL) {
//				if (footer == null) {
//					footer = LayoutInflater.from(getActivity()).inflate(
//							R.layout.loading, myListView, false);
//				}
//				if (lastItem == headCount + gotcount) {
//					myListView.addFooterView(footer);
//				}
				GetData();
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			lastVisibleItem = firstVisibleItem + visibleItemCount;
			lastItem = totalItemCount;
		}
	};
	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position <= gotcount) {
                // 点击计数
				GoodsWanted g = data.get(position - headCount);
				// g.increment("clickCount");
				// g.update(getActivity());
				MyUtils.showLog("ListView", data.size() + "you clicked on "
						+ position);
				Intent viewIma = new Intent(getActivity(),
						WantedGoodsDetailsActivity.class);
				viewIma.putExtra("goodsId", g.getObjectId());
				startActivity(viewIma);
			}
		}
	};
	OnRefreshListener mOnRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			myListView.setEnabled(false);
			ReflashData();
		}
	};
	private View loadview;

	public void loading() {
		if (loadview != null) {
			loadview.setVisibility(View.VISIBLE);
			return;
		}

		ViewStub stub = (ViewStub) getActivity().findViewById(
				R.id.loadinglayout);
		loadview = stub.inflate();
	}

	public void loaded() {
		if (loadview != null) {
			loadview.setVisibility(View.GONE);
		}
	}

	OnMyflipperLisrener mMyflipperItemClickLisrener = new OnMyflipperLisrener() {

		@Override
		public void onItemClick(int position) {
			ShowToast(position + 1 + "");
		}

		@Override
		public void onShowNext(int position) {
			SetMark(position);
		}

		@Override
		public void onShowPrevious(int position) {
			SetMark(position);
		}
	};

	ImageView mark0, mark1, mark2, mark3;

	public void SetMark(int displayedChild) {
		switch (displayedChild) {
		case 0:
			mark3.setImageResource(R.drawable.yuandian);
			mark0.setImageResource(R.drawable.star);
			mark1.setImageResource(R.drawable.yuandian);
			break;
		case 1:
			mark0.setImageResource(R.drawable.yuandian);
			mark1.setImageResource(R.drawable.star);
			mark2.setImageResource(R.drawable.yuandian);
			break;
		case 2:
			mark1.setImageResource(R.drawable.yuandian);
			mark2.setImageResource(R.drawable.star);
			mark3.setImageResource(R.drawable.yuandian);
			break;
		case 3:
			mark2.setImageResource(R.drawable.yuandian);
			mark3.setImageResource(R.drawable.star);
			mark0.setImageResource(R.drawable.yuandian);
			break;

		default:
			break;
		}
	}

}
