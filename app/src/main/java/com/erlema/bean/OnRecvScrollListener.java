package com.erlema.bean;

import android.support.v4.view.ScrollingView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * 类名 onRecvScrollListener
 * 
 * @author 陈杰雄 <br/>
 *         (recycleView滑动监听器，适配各种layoutManager) 创建日期 2015/12/2 11:35
 */
public class OnRecvScrollListener extends RecyclerView.OnScrollListener
		implements ScrollHelper {
	private int LAST_COUNT = 4;// 距离底部几个item时加载更多

	/**
	 * layoutManager的类型（枚举）
	 */
	public static enum LAYOUT_MANAGER_TYPE {
		LINEAR, GRID, STAGGERED_GRID
	}

	private String TAG = getClass().getSimpleName();
	protected LAYOUT_MANAGER_TYPE layoutManagerType;
	/**
	 * 第一个可见的item
	 */
	private int firstVisibleItem;
	/**
	 * 最后一个的位置
	 */
	private int[] lastPositions;

	/**
	 * 最后一个可见的item的位置
	 */
	private int lastVisibleItemPosition;
	/*    *//**
	 * 是否正在加载
	 */
	/*
	 * private boolean isLoadingMore = false;
	 */

	/**
	 * 当前滑动的状态
	 */
	private int currentScrollState = 0;

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		super.onScrolled(recyclerView, dx, dy);

		RecyclerView.LayoutManager layoutManager = recyclerView
				.getLayoutManager();
		// int lastVisibleItemPosition = -1;
		if (layoutManagerType == null) {
			if (layoutManager instanceof LinearLayoutManager) {
				layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
			} else if (layoutManager instanceof GridLayoutManager) {
				layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
			} else if (layoutManager instanceof StaggeredGridLayoutManager) {
				layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
			} else {
				throw new RuntimeException(
						"Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
			}
		}

		switch (layoutManagerType) {
		case LINEAR:
			lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
					.findLastVisibleItemPosition();
			break;
		case GRID:
			lastVisibleItemPosition = ((GridLayoutManager) layoutManager)
					.findLastVisibleItemPosition();
			break;
		case STAGGERED_GRID:
			StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
			if (lastPositions == null) {
				lastPositions = new int[staggeredGridLayoutManager
						.getSpanCount()];
			}
			staggeredGridLayoutManager
					.findLastVisibleItemPositions(lastPositions);
			lastVisibleItemPosition = findMax(lastPositions);
			int[] into = new int[staggeredGridLayoutManager.getSpanCount()];
			staggeredGridLayoutManager.findFirstVisibleItemPositions(into);
			firstVisibleItem = findMin(into);
			break;
		}

	}

	@Override
	public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
		super.onScrollStateChanged(recyclerView, newState);
		currentScrollState = newState;
		RecyclerView.LayoutManager layoutManager = recyclerView
				.getLayoutManager();
		int visibleItemCount = layoutManager.getChildCount();
		int totalItemCount = layoutManager.getItemCount();
		if ((visibleItemCount > 0
				&& currentScrollState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItemPosition) >= totalItemCount
				- LAST_COUNT)) {
			// Log.d(TAG, "is loading more");
				onBottom();
		}
	}

	/**
	 * 查找最大值
	 */
	private int findMax(int[] lastPositions) {
		int max = lastPositions[0];
		for (int value : lastPositions) {
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	/**
	 * 查找最小值
	 */
	private int findMin(int[] firstPositions) {
		int min = firstPositions[0];
		for (int value : firstPositions) {
			if (value < min) {
				min = value;
			}
		}
		return min;
	}


	@Override
	public void onBottom() {
		// TODO Auto-generated method stub

	}

}
