package com.erlema.bean;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.erlema.R;
import com.gc.materialdesign.views.LayoutRipple;

public class MyRecyclAdapter extends RecyclerView.Adapter<myViewHolder> {
	List<MyGoods> data;
	Context context;
	LayoutInflater mInfater;

	public MyRecyclAdapter(Context context, List<MyGoods> data) {
		this.context = context;
		this.data = data;
		mInfater = LayoutInflater.from(context);
	}

	@Override
	public int getItemCount() {
		return data.size() + 1;
	}

	@Override
	public void onBindViewHolder(myViewHolder viewholder, int position) {
		if (position == 0) {
			// for (int i = 0; i < 4; i++) {
			// viewholder.vf.getChildAt(i).setOnClickListener(
			// new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// Log.i("adapter", v.getId() + "");
			// }
			// });
			// }
			Glide.with(context)
					.load("http://e.hiphotos.baidu.com/image/pic/item/060828381f30e924849c74384e086e061d95f7fb.jpg")
					.into(viewholder.topic0);
			Glide.with(context)
					.load("http://h.hiphotos.baidu.com/image/pic/item/4ec2d5628535e5dd50cb5fd574c6a7efcf1b62d1.jpg")
					.into(viewholder.topic2);
			Glide.with(context)
					.load("http://e.hiphotos.baidu.com/image/pic/item/9f510fb30f2442a76536e2acd343ad4bd11302fa.jpg")
					.into(viewholder.topic1);
		} else {
			MyGoods item = data.get(position - 1);
			viewholder.title.setText(item.getTitle());
			// Log.i("adapter", "imgurl="+item.getImgUrl());
			// Picasso.with(context).load(item.getImgUrl()[0]).resize(81, 81)
			// .into(viewholder.img);
			Glide.with(context).load(item.getImgUrl()[0]).into(viewholder.img);
			// viewholder.img.setTag(item.getImgUrl());
		}
	}

	@Override
	public int getItemViewType(int position) {
		return position == 0 ? 0 : 1;
	}

	@Override
	public myViewHolder onCreateViewHolder(ViewGroup arg0, int viewTypt) {
		if (viewTypt == 0) {
			View v = mInfater.inflate(R.layout.flippy_layout, arg0, false);
			return new myViewHolder(v);
		} else {
			View v = mInfater.inflate(R.layout.my_index_list_item, arg0, false);
			myViewHolder viewholder = new myViewHolder(v);
			return viewholder;
		}
	}

}

class myViewHolder extends RecyclerView.ViewHolder {
	TextView title;
	ImageView img, topic0, topic1, topic2, topic3;
	LinearLayout lr;
	ViewFlipper vf;

	public myViewHolder(View v) {
		super(v);
		title = (TextView) v.findViewById(R.id.title);
		img = (ImageView) v.findViewById(R.id.img);
		lr = (LinearLayout) v.findViewById(R.id.layoutRipple1);
		ImageView[] topics = { topic0, topic1, topic2, topic3 };
		vf = (ViewFlipper) v.findViewById(R.id.viewFlipper1);
		topic0 = (ImageView) v.findViewById(R.id.img0);
		topic1 = (ImageView) v.findViewById(R.id.img1);
		topic2 = (ImageView) v.findViewById(R.id.img2);
		topic3 = (ImageView) v.findViewById(R.id.img3);
		for (int i = 0; i < 4; i++) {
			if (topics[i] != null) {
				topics[i].setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						switch (v.getId()) {
						case R.id.img0:
							Log.i("adapter", "topic0");
							break;
						case R.id.img1:
							Log.i("adapter", "topic1");
							break;
						case R.id.img2:
							Log.i("adapter", "topic2");
							break;
						case R.id.img3:
							Log.i("adapter", "topic3");
							break;

						default:
							break;
						}
					}
				});
			}
		}
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("adapter", getPosition() + "clicked");
			}
		});
		if (lr != null) {
			// lr.setRippleColor(Color.parseColor("#ff9800"));
		}
	}

}