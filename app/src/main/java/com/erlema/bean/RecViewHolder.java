package com.erlema.bean;

import com.example.erlema.R;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class RecViewHolder extends RecyclerView.ViewHolder {

	public ImageView img;
	public TextView title, price, star, time;
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
		img = (ImageView) arg0.findViewById(R.id.fund_item_img);
		title = (TextView) arg0.findViewById(R.id.fund_item_title);
		price = (TextView) arg0.findViewById(R.id.fund_item_price);
		star = (TextView) arg0.findViewById(R.id.fund_item_start);
		time = (TextView) arg0.findViewById(R.id.fund_item_time);
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
