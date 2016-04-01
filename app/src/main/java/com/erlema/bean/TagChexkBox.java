package com.erlema.bean;

import com.erlema.util.MyUtils;
import com.example.erlema.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class TagChexkBox extends TextView {
	private String mTitleText;
	private int mtag_back_false = R.color.white;
	private int mtag_back_true = R.drawable.blue_cicle;
	private boolean selected = false;

	public TagChexkBox(Context context) {
		this(context, null);
		MyUtils.showLog("tag", "TagChexkBox1");
	}

	public TagChexkBox(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		MyUtils.showLog("tag", "TagChexkBox2");
	}

	public TagChexkBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		MyUtils.showLog("tag", "TagChexkBox3");
		/**
		 * 获得我们所定义的自定义样式属性
		 */
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.TagCheckBox, defStyle, 0);
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.TagCheckBox_tag_title:
				mTitleText = a.getString(attr);
				this.setText(mTitleText);
				break;
			case R.styleable.TagCheckBox_tag_back:
				// 默认颜色设置为白色
				mtag_back_false = a.getResourceId(attr, R.color.white);
				break;
			case R.styleable.TagCheckBox_tag_select:
				// 默认颜色设置为蓝色
				mtag_back_true = a.getResourceId(attr, R.color.main_color);
				break;
			}

		}
		setBackgroundColor(getResources().getColor(R.color.white));
		a.recycle();
		setPadding(6, 6, 6, 6);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			MyUtils.showLog("tag", "click");
			if (!this.isEnabled()) {
				return super.onTouchEvent(event);
			}
			checkState();
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	private void checkState() {
		if (selected) {
            selected = false;
            setBackgroundResource(mtag_back_false);
            setTextColor(getResources().getColor(R.color.black));
        } else {
            selected = true;
            setTextColor(getResources().getColor(R.color.white));
            setBackgroundResource(mtag_back_true);
        }
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if ( ! enabled) {
			setSelect(false);
			setBackgroundResource(mtag_back_false);
			setTextColor(getResources().getColor(R.color.gray));
		}else {
			setTextColor(getResources().getColor(R.color.black));
		}
	}

	public void setSelect(boolean bool) {
		this.selected = bool;
		checkState();
		postInvalidate();
	}

	public boolean getSelect() {
		return this.selected;
	}
}
