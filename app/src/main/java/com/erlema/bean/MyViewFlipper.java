package com.erlema.bean;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.erlema.util.MyUtils;
import com.example.erlema.R;

public class MyViewFlipper extends ViewFlipper implements OnGestureListener {
	GestureDetector mGestureDetector;

	public MyViewFlipper(Context context) {
		super(context, null);
		// MyUtils.showLog("viewflipper", "MyViewFlipper1");
		mGestureDetector = new GestureDetector(getContext(), this);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	public MyViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
		// MyUtils.showLog("viewflipper", "MyViewFlipper2");
		mGestureDetector = new GestureDetector(getContext(), this);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		// View v = LayoutInflater.from(context).inflate(
		// R.layout.flippy_layout, null, false);
	}

	boolean isOnce = true;

	@Override
	public void startFlipping() {
		if (!isOnce) {
			showNext();
		}
		super.startFlipping();
		isOnce = false;
	}

	AnimationListener mAnimationListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			isChanging = false;
		}
	};

	@Override
	public void showNext() {
		this.setInAnimation(AnimationUtils.loadAnimation(getContext(),
				R.anim.push_right_in));
		this.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
				R.anim.push_left_out));
		super.showNext();
		if (mLisrener != null) {
			// MyUtils.showLog("viewflipper", "next--->" +
			// this.getDisplayedChild());
			mLisrener.onShowNext(getDisplayedChild());
		}
		getInAnimation().setAnimationListener(mAnimationListener);
	}

	@Override
	public void showPrevious() {
		this.setInAnimation(AnimationUtils.loadAnimation(getContext(),
				R.anim.push_left_in));
		this.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
				R.anim.push_right_out));
		super.showPrevious();
		if (mLisrener != null) {
			mLisrener.onShowPrevious(getDisplayedChild());
			// MyUtils.showLog("viewflipper", "previous--->" +
			// this.getDisplayedChild());
			// SetMark(getDisplayedChild());
		}
		getInAnimation().setAnimationListener(mAnimationListener);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// MyUtils.showLog("viewflipper", "dispatchTouchEvent");
		getParent().requestDisallowInterceptTouchEvent(true); // 让父类不拦截触摸事件
		// this.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
		// return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// MyUtils.showLog("viewflipper", "onInterceptTouchEvent");
		return super.onInterceptTouchEvent(ev);
	}

	float startX, startY;
	View currentView;
	private int mTouchSlop;
	private float mPrevX;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// MyUtils.showLog("viewflipper", "onTouchEvent");
		mGestureDetector.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		MyUtils.showLog("viewflipper", "down");
		getParent().requestDisallowInterceptTouchEvent(true);
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// MyUtils.showLog("viewflipper", "onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// MyUtils.showLog("viewflipper", "onSingleTapUp"+getDisplayedChild());
		if (mLisrener != null) {
			mLisrener.onItemClick(getDisplayedChild());
		}
		return false;
	}

	boolean isChanging = false;

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// MyUtils.showLog("viewflipper", "onScroll");
		getParent().requestDisallowInterceptTouchEvent(true);
		if (!isChanging) {
			if (e1.getX() - e2.getX() > 100) {// 右滑
				this.showNext();
				reStart();
				isPostting = true;
				isChanging = true;
				// return true;
			} else if (e2.getX() - e1.getX() > 100) {
				this.showPrevious();
				reStart();
				isPostting = true;
				isChanging = true;
				// return true;
			}
		}
		return false;
	}

	boolean isPostting = false;
	int sleepTime = 4000;

	private void reStart() {
		stopFlipping();
		if (!isPostting && !isChanging) {
			MyUtils.showLog("viewflipper", "restarting");
			postDelayed(new Runnable() {
				@Override
				public void run() {
					startFlipping();
					MyUtils.showLog("viewflipper", "start_run");
					isPostting = false;
				}
			}, sleepTime);
		}
	}

	Runnable reSet = new Runnable() {

		@Override
		public void run() {

		}
	};

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// MyUtils.showLog("viewflipper", "fling");
		return false;
	}

	OnMyflipperLisrener mLisrener;

	public interface OnMyflipperLisrener {
		void onItemClick(int position);

		void onShowNext(int position);

		void onShowPrevious(int position);
	};

	public void SetOnMyflipperItemClickLisrener(OnMyflipperLisrener l) {
		this.mLisrener = l;
	}

}
