package com.erlema.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.erlema.bean.CircleImageView;
import com.erlema.bean.Comments;
import com.erlema.util.MyUtils;
import com.example.erlema.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class fundcommentsAdapter extends BaseAdapter {
	public Context context;
	public List<Comments> data;
	public LayoutInflater mInflater;

	public fundcommentsAdapter(Context context, List<Comments> data) {
		super();
		this.context = context;
		this.data = data;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if (data == null || data.size() == 0) {
			return 1;
		} else {
			return data.size();
		}
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Log.i("ffffff", data+"getView+"+data.size());
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.comments_layout,
					parent, false);
			holder = new ViewHolder();
			holder.avator = (CircleImageView) convertView
					.findViewById(R.id.comments_avatar);
			holder.author = (TextView) convertView
					.findViewById(R.id.comments_author);
			holder.comment = (TextView) convertView
					.findViewById(R.id.comments_commet);
			holder.time = (TextView) convertView
					.findViewById(R.id.comments_commet_time);
			holder.none=(LinearLayout) convertView.findViewById(R.id.none);
			holder.commentsLayout=(LinearLayout) convertView.findViewById(R.id.comments);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (data.size()==0) {
			holder.none.setVisibility(View.VISIBLE);
			holder.commentsLayout.setVisibility(View.GONE);
		} else {
			holder.none.setVisibility(View.GONE);
			holder.commentsLayout.setVisibility(View.VISIBLE);
			Comments com = data.get(position);
			if (com.getUserAvator() != null && data.size() != 0) {
				String url=com.getUserAvator();
				Glide.with(context).load(url).override(81, 81)
						.centerCrop().into(holder.avator);
			} else {
				holder.avator.setImageResource(R.drawable.default_round_head);
			}
			holder.author.setText(com.getUsername());
			String content = com.getAnswerto() == null ? com.getCommets()
					: "回复" + com.getAnswerto() + "：" + com.getCommets();
			holder.comment.setText(content);
			holder.time.setText(MyUtils.getTimeElapse(com.getCreatedAt()));
			final String name = com.getUsername();
			holder.avator.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (monAvaorclicListener != null) {
						monAvaorclicListener.onClick(position);
					}
				}
			});
		}
		return convertView;
	}

	class ViewHolder {
		CircleImageView avator;
		TextView author, comment, time;
		LinearLayout none,commentsLayout;
	}

	onAvatorClickListener monAvaorclicListener;

	public void SetOnAvatorClickListener(onAvatorClickListener l) {
		this.monAvaorclicListener = l;
	}

	public interface onAvatorClickListener {
		public void onClick(int position);
	}
}