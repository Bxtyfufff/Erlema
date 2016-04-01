package com.erlema.bean;

import com.example.erlema.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class myActionBar extends FrameLayout {
	private ImageButton leftButton, rightButton;
	private TextView titleText;
	private mClickListener mclickListener;

	public myActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.mybar, this);
		titleText = (TextView) findViewById(R.id.title_text);
		leftButton = (ImageButton) findViewById(R.id.button_left);
		rightButton = (ImageButton) findViewById(R.id.button_right);
		leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mclickListener != null) {
					mclickListener.onLeftClick();
				}
				if (getContext() instanceof Activity) {
					((Activity) getContext()).finish();
				}
			}
		});
		rightButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mclickListener != null) {
					mclickListener.onRightClick();
				}
			}
		});
	}

	public void setTitleText(String text) {
		titleText.setText(text);
	}

	public void setButtonVisible(boolean left,boolean right) {
		if (right) {
			rightButton.setVisibility(View.VISIBLE);
		} else {
			rightButton.setVisibility(View.GONE);
		}
		if (left) {
			leftButton.setVisibility(View.VISIBLE);
		} else {
			leftButton.setVisibility(View.GONE);
		}
	}

	public void setLeftButtonListener(OnClickListener l) {
		leftButton.setOnClickListener(l);
	}

	public void setmListener(mClickListener l) {
		this.mclickListener = l;
	}
	public void setRightButtonDrawable(Drawable d){
		rightButton.setImageDrawable(d);
	}

	public interface mClickListener {
		public void onLeftClick();

		public void onRightClick();
	}
}
