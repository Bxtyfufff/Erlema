package com.erlema.bean;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.erlema.bean.MyViewFlipper.OnMyflipperLisrener;
import com.bumptech.glide.Glide;
import com.erlema.util.MyUtils;
import com.example.erlema.R;

public class MyRecyclViewAdapter extends RecyclerView.Adapter<ViewHolder> {
	List<MyGoods> data;
	Context context;
	LayoutInflater mInfater;
	onRecClickListener onRecClickListener;
	onRecLongClickListener onRecLongClickListener;
	private int headCount = 0;

	public MyRecyclViewAdapter(List<MyGoods> data, Context context) {
		super();
		this.data = data;
		this.context = context;
		this.mInfater = LayoutInflater.from(context);
	}

	@Override
	public int getItemCount() {
		return data == null ? 0 : data.size() + headCount;
	}

	@Override
	public void onBindViewHolder(ViewHolder viewholder, int position) {
		// ViewGroup.LayoutParams params =
		// viewholder.itemView.getLayoutParams();//得到item的LayoutParams布局参数
		// params.height =(int) (200+Math.random()*100);//把随机的高度赋予item布局
		// // MyUtils.showLog("rec", params.height+"");
		// viewholder.itemView.setLayoutParams(params);//把params设置给item布局
		if (viewholder instanceof NormalViewHolder) {
			ViewGroup.LayoutParams params = viewholder.itemView
					.getLayoutParams();// 得到item的LayoutParams布局参数
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			int width = wm.getDefaultDisplay().getWidth();
			MyUtils.showLog("revycleAdapter", width + "");
			params.width = width;// headview 宽度为屏幕宽度
			viewholder.itemView.setLayoutParams(params);// 把params设置给item布局
			NormalViewHolder holder = (NormalViewHolder) viewholder;
			MyGoods g = data.get(position - headCount);
			Glide.with(context).load(g.getImgUrl()[0]).override(200, 200)
					.into(holder.img);
			holder.title.setText(g.getTitle());
			holder.price.setText("￥ " + g.getPirce());
			// viewholder.star.setText("❤  "+g.getStarCount());
			SpannableString sp1 = new SpannableString("/热度 "
					+ g.getClickCount());
			Drawable d1 = context.getResources().getDrawable(R.drawable.hot);
			d1.setBounds(0, -8, d1.getIntrinsicHeight(), d1.getIntrinsicWidth());
			sp1.setSpan(new ImageSpan(d1), 0, 3,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.star.setText(sp1);
			holder.time.setText(MyUtils.getTimeElapse(g.getCreatedAt()));
		}
	}

	@Override
	public int getItemViewType(int position) {
//		return position == 0 ? 0 : 1;
		return 1;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int viewType) {
		if (viewType == 1) {
			View v = mInfater.inflate(R.layout.fund_list_item, arg0, false);
			NormalViewHolder viewHolder = new NormalViewHolder(v);
			return viewHolder;
		} else {
			View v = mInfater.inflate(R.layout.flippy_layout, arg0, false);
			return new HeadViewHolder(v);
		}
	}

	public void AddItem(int positon, MyGoods g) {
		this.data.add(g);
		this.notifyItemInserted(positon + headCount);
	}

	public void DeleteAll() {
		this.data.clear();
		notifyDataSetChanged();
	}

	public void SetOnRecClickListener(onRecClickListener l) {
		this.onRecClickListener = l;
	}

	public void SetOnRecLongClickListener(onRecLongClickListener l) {
		this.onRecLongClickListener = l;
	}

	class NormalViewHolder extends ViewHolder {
		public ImageView img;
		public TextView title, price, star, time;

		public NormalViewHolder(View arg0) {
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

	}

	/*
	 * headview viewHolder
	 */
	class HeadViewHolder extends ViewHolder {
		public MyViewFlipper viewFlipper;
		public ImageView mark0, mark1, mark2, mark3;

		public HeadViewHolder(View arg0) {
			super(arg0);
			viewFlipper = (MyViewFlipper) arg0.findViewById(R.id.viewFlipper1);
			OnMyflipperLisrener mMyflipperItemClickLisrener = new OnMyflipperLisrener() {

				@Override
				public void onItemClick(int position) {
					MyUtils.showLog("revycleAdapter", position + "");
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
			viewFlipper
					.SetOnMyflipperItemClickLisrener(mMyflipperItemClickLisrener);
			mark0 = (ImageView) arg0.findViewById(R.id.index1);
			mark1 = (ImageView) arg0.findViewById(R.id.index2);
			mark2 = (ImageView) arg0.findViewById(R.id.index3);
			mark3 = (ImageView) arg0.findViewById(R.id.index4);
			InitViewFlipper();
		}

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

		private void InitViewFlipper() {
			ImageView img0 = null, img1 = null, img2 = null, img3 = null;
			ImageView[] imgs = { img0, img1, img2, img3 };
			int[] imgID = { R.id.img0, R.id.img1, R.id.img2, R.id.img3 };
			String[] url = new String[] {
					"http://h.hiphotos.baidu.com/image/h%3D200/sign=b3d52605a0c27d1eba263cc42bd4adaf/b21bb051f819861851907b254eed2e738ad4e6b8.jpg",
					"http://pic2.nipic.com/20090413/172345_123346059_2.jpg",
					"http://img3.imgtn.bdimg.com/it/u=699744761,252724216&fm=21&gp=0.jpg",
					"http://img4.imgtn.bdimg.com/it/u=3420430574,3398735358&fm=21&gp=0.jpg" };
			for (int i = 0; i < 4; i++) {
				imgs[i] = (ImageView) viewFlipper.findViewById(imgID[i]);
				Glide.with(context).load(url[i]).into(imgs[i]);
			}

		}

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
