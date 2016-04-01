package com.erlema.view;

import com.erlema.util.MyUtils;
import com.example.erlema.R;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.view.View.OnTouchListener;

public class ScaleImageView extends ImageView implements
		OnGlobalLayoutListener, OnScaleGestureListener, OnTouchListener {
	/** 模板Matrix，用以初始化 */
	private Matrix mMatrix = new Matrix();
	/** 图片长度 */
	private float mImageWidth;
	/** 图片高度 */
	private float mImageHeight;
	private  float maxScale = 4.0f;
	private  float minScale = 0.5f;
	private ScaleGestureDetector mScaleGestureDetector;
	private GestureDetector mGestureDetector;
	private boolean closeWhenClick = true;

	public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// 设置ScaleType为ScaleType.MATRIX，这一步很重要
		setScaleType(ScaleType.MATRIX);
		setBackgroundColor(getResources().getColor(R.color.black));
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		mGestureDetector = new GestureDetector(getContext(),
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						// TODO Auto-generated method stub
						MyUtils.showLog("MyImageView", "单击");
						isDoubleTap = false;
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								// 不是双击且设置了单击返回
								if (!isDoubleTap && closeWhenClick) {
									if (monMyImageViewClickListener != null) {
										monMyImageViewClickListener.click();
									}
								}

							}
						}, 150);
						return super.onSingleTapUp(e);
					}

					boolean isDoubleTap = false;

					@Override
					public boolean onDoubleTap(MotionEvent e) {
						isDoubleTap = true;
						if (!isAutoScale) {
							float x = e.getX();
							float y = e.getY();
							MyUtils.showLog("MyImageView", "双击" + getScale());
							// float误差
							if (MyUtils.format(minScale, 4) <= MyUtils.format(
									getScale(), 4) && getScale() < 2.9999) {// 放大
								// mMatrix.postScale(3 / getScale(), 3 /
								// getScale(),
								// x, y);
								// center(true, true);
								// setImageMatrix(mMatrix);
								postDelayed(new AutoScaleRunnable(x, y, 3), 16);
								isAutoScale = true;
							} else {// 缩小
								// mMatrix.postScale(minScale * 2 / getScale(),
								// minScale * 2 / getScale(), x, y);
								// center(true, true);
								// setImageMatrix(mMatrix);
								postDelayed(new AutoScaleRunnable(x, y,
										minScale * 2.0f), 16);
								isAutoScale = true;
							}
						}
						return super.onDoubleTap(e);
					}

				});
		this.setOnTouchListener(this);
	}

	public ScaleImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScaleImageView(Context context) {
		this(context, null);
	}

	void setCloseOnclick(boolean bool) {
		this.closeWhenClick = bool;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	DisplayMetrics dm;
	boolean once = false;

	@Override
	public void onGlobalLayout() {
		if (!once) {
			// 得到图片，以及宽和高
			Drawable d = getDrawable();
			if (d == null)
				return;
			MyUtils.showLog("MyImageView", "once");
			once = true;
			// 得到控件的宽高
			int width = getWidth();
			int height = getHeight();

			// 拉伸后的宽度.而不是真正图片的宽度
			int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();
			MyUtils.showLog("MyImageView", width + " width,height" + height);
			MyUtils.showLog("MyImageView", dw + " dw,dh" + dh);
			float scale = 1.0f;// 默认缩放的值

			// 将图片的宽高和控件的宽高作对比，如果图片比较小，则将图片放大，反之亦然。
			// 如果图片的宽度大于控件的宽度,并且图片高度小于控件高度
			if (dw > width && dh < height) {
				scale = width * 1.0f / dw;// 图片太宽，宽度缩放
				MyUtils.showLog("MyImageView", " 图片太宽，宽度缩放");
			}

			if (dh > height && dw < width) {
				scale = height * 1.0f / dh;// 图片太高，高度缩放
				MyUtils.showLog("MyImageView", " 图片太高，高度缩放");
			}

			if (dw < width && dh < height || dw > width && dh > height) {
				scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
				MyUtils.showLog("MyImageView", " 图片宽高大于屏幕或小于屏幕");
			}

			// 得到初始化缩放的比例
			minScale = scale / 2;// 原大小
			MyUtils.showLog("MyImageView", "minScale=" + minScale);
			MyUtils.showLog("MyImageView", "maxScale=" + maxScale);
			// 将图片移动到控件的中心
			int dx = getWidth() / 2 - dw / 2;// 向x轴移动dx距离
			int dy = getHeight() / 2 - dh / 2;// 向y轴移动dx距离

			/**
			 * matrix: xScale xSkew xTrans 需要9个 ySkew yScale yTrans 0 0 0
			 */
			mMatrix.postTranslate(dx, dy);// 平移
			mMatrix.postScale(scale, scale, width / 2, height / 2);// 缩放,正常显示width/2,height/2中心
			setImageMatrix(mMatrix);
		}
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float mscale = getScale();
		float scaleFactor = detector.getScaleFactor();
		/*
		 * 缩放范围控制
		 */
		if (mscale < maxScale && scaleFactor > 1.0
				|| (mscale > minScale && scaleFactor < 1.0)) {
			MyUtils.showLog("MyImageView", mscale + "");
			// // 最小
			// if (mscale * scaleFactor < minScale) {
			// scaleFactor = minScale / mscale;
			// }
			// // 最大
			// if (mscale * scaleFactor > maxScale) {
			// scaleFactor = maxScale / mscale;
			// }
			mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(),
					detector.getFocusY());
			center(true, true);
			setImageMatrix(mMatrix);
		}
		return true;
	}

	/**
	 * 横向、纵向居中
	 */
	protected void center(boolean horizontal, boolean vertical) {
		if (getDrawable() == null) {
			return;
		}
		RectF rect = getMatrixRectF();
		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;
		// MyUtils.showLog("MyImageView", rect.left+"l    r "+rect.right);
		// MyUtils.showLog("MyImageView", rect.top+"t     b"+rect.bottom);

		if (vertical) {
			// 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
			int screenHeight = getHeight();
			if (height < screenHeight) {
				MyUtils.showLog("MyImageView", "height < screenHeight");
				deltaY = (screenHeight - height) * 1.0f / 2 - rect.top;
			} else if (rect.top > 0) {
				MyUtils.showLog("MyImageView", "rect.top > 0");
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				MyUtils.showLog("MyImageView", "rect.bottom < screenHeight");
				deltaY = this.getHeight() - rect.bottom;
			}

		}

		if (horizontal) {
			int screenWidth = getWidth();
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < screenWidth) {
				deltaX = screenWidth - rect.right;
			}
		}
		mMatrix.postTranslate(deltaX, deltaY);
	}

	/*
	 * 获取当前缩放级别
	 */
	private float getScale() {
		float[] values = new float[9];
		mMatrix.getValues(values);
		return values[Matrix.MSCALE_X];
	}

	// RectF表示一个矩形，类型为float，获取图片放大缩小后的宽和高，以及左右上下4条边的坐标
	private RectF getMatrixRectF() {
		Matrix matrix = mMatrix;
		RectF rect = new RectF();
		Drawable d = getDrawable();
		if (null != d) {
			rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rect);
		}
		return rect;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		// MyUtils.showLog("MyImageView", "begin");
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {

	}

	private float startX, startY, dx, dy;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mScaleGestureDetector.onTouchEvent(event);
		mGestureDetector.onTouchEvent(event);
		RectF f = getMatrixRectF();
		if (event.getPointerCount() == 1 && getScale() > minScale * 2) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				MyUtils.showLog("MyImageView", "down");
				if (f.width() > getWidth() + 0.01
						|| f.height() > getHeight() + 0.01) {
					getParent().requestDisallowInterceptTouchEvent(true);
				}
				startX = event.getX();
				startY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				MyUtils.showLog("MyImageView", "move");
				if (f.width() > getWidth() + 0.01
						|| f.height() > getHeight() + 0.01) {
					getParent().requestDisallowInterceptTouchEvent(true);
				}
				dx = event.getX() - startX;
				dy = event.getY() - startY;
				if (getMatrixRectF().height() < getHeight()) {
					dy = 0;
				}
				// 避免和双击冲突,大于10f才算是拖动
				if (Math.sqrt(dx * dx + dy * dy) > 10f) {
					mMatrix.postTranslate(dx, dy);
					this.setImageMatrix(mMatrix);
				}
				checkBound();

				startX = event.getX();
				startY = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				MyUtils.showLog("MyImageView", "up");

				break;

			default:
				break;
			}
		}

		return true;
	}

	/*
	 * 检查边界
	 */
	private void checkBound() {
		RectF r = getMatrixRectF();
		dx = 0;
		dy = 0;
		if (r.left > 0) {
			dx = -r.left;
		}

		if (r.right < getWidth()) {
			dx = getWidth() - r.right;
		}
		if (r.height() < getHeight()) {
			if (r.top < 0) {
				dy = -r.top;
			}
			if (r.bottom > getHeight()) {
				dy = -r.bottom + getHeight();
			}
		} else {
			if (r.bottom < getHeight()) {
				dy = -r.bottom + getHeight();
			}
			if (r.top > 0) {
				dy = -r.top;
			}
		}
		mMatrix.postTranslate(dx, dy);
		this.setImageMatrix(mMatrix);
	}

	/*
	 * 缓慢缩小放大
	 */
	boolean isAutoScale;

	public class AutoScaleRunnable implements Runnable {

		float x, y, targetScale;// 缩放中心点 目标值
		private final float BIGGER = 1.05f;
		private final float SMALLER = 0.95f;
		private float tempScale;

		public AutoScaleRunnable(float x, float y, float targetScale) {
			super();
			this.x = x;
			this.y = y;
			this.targetScale = targetScale;
			if (targetScale > getScale()) {
				this.tempScale = BIGGER;
			} else {
				this.tempScale = SMALLER;
			}
		}

		@Override
		public void run() {
			mMatrix.postScale(tempScale, tempScale, x, y);
			center(true, true);
			setImageMatrix(mMatrix);
			float currentScale = getScale();
			if (tempScale > 1.0f && currentScale < targetScale
					|| tempScale < 1.0f && currentScale > targetScale) {
				postDelayed(this, 16);
			} else {
				// 设置目标值
				float scale = targetScale / currentScale;
				mMatrix.postScale(scale, scale, x, y);
				center(true, true);
				setImageMatrix(mMatrix);
				isAutoScale = false;
			}
		}
	}

	public interface onScaleImageViewClickListener {
		void click();
	}

	onScaleImageViewClickListener monMyImageViewClickListener;

	public void setOnMyImageViewClickListener(onScaleImageViewClickListener l) {
		this.monMyImageViewClickListener = l;
	}
}
