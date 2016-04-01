package com.erlema.adapter;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.erlema.bean.GoodsWanted;
import com.erlema.bean.MyUser;
import com.erlema.util.MyUtils;
import com.example.erlema.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IndexListviewAdapter extends BaseAdapter {

	List<GoodsWanted> data;
	Context context;
	LayoutInflater mInflater;

	public IndexListviewAdapter(Context context, List<GoodsWanted> data) {
		this.data = data;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.my_index_list_item,
					parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.content = (TextView) convertView
					.findViewById(R.id.qiu_content);
			holder.price = (TextView) convertView
					.findViewById(R.id.qiu_pricerange);
			holder.time = (TextView) convertView.findViewById(R.id.qiu_time);
			holder.school = (TextView) convertView.findViewById(R.id.qiu_schoo);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ImageView iv = holder.img;
		final GoodsWanted g = data.get(position);
		BmobQuery<MyUser> bq = new BmobQuery<MyUser>();
		bq.getObject(context, g.getOwerID(), new GetListener<MyUser>() {

			@Override
			public void onFailure(int arg0, String arg1) {

			}

			@Override
			public void onSuccess(MyUser u) {
				GoodsWanted wg=new GoodsWanted();
				wg.setAvator(u.getAvator());
				wg.update( context,g.getObjectId(), null);
			}
		});
		Glide.with(context).load(g.getAvator()).error(R.drawable.img_error)
				.override(240, 180).centerCrop()
				.diskCacheStrategy(DiskCacheStrategy.ALL).into(iv);// 缓存所有尺寸
		holder.title.setText(g.getTitle());
		holder.content.setText(g.getDescrib());
		holder.time.setText(MyUtils.getTimeElapse(g.getCreatedAt()));
		holder.school.setText(g.getSchool());
		holder.price.setText("￥" + g.getPirce_low() + "~" + g.getPirce_high());
		return convertView;
	}

	class ViewHolder {
		ImageView img;
		TextView title, content, price, time, school;
	}

}
