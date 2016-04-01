package com.erlema.activity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.erlema.util.MyUtils;
import com.erlema.view.ScaleImageView;
import com.example.erlema.R;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ImageActivity extends BaseActivity {

	private ViewPager mViewPager;
	private String[] imageUrls = null;
	private int index;
	private ScaleImageView[] imageViews;
	private TextView tv;
	private int imaCount = 0;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_image);
		MyUtils.showLog("ViewPager", "start");
	}

	@Override
	public void initViews() {
		mViewPager = (ViewPager) findViewById(R.id.img_viewpager);
		tv = (TextView) findViewById(R.id.image_tv_count);
		imageUrls = getIntent().getStringArrayExtra("ImageUrls");
		index=getIntent().getIntExtra("index", 0);
		if (imageUrls != null) {
			imaCount = imageUrls.length;
			tv.setText(1 + "/" + imaCount);
			imageViews = new ScaleImageView[imaCount];
			for (int i = 0; i < imaCount; i++) {
				imageViews[i] = new ScaleImageView(getApplicationContext());
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void initListeners() {
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				tv.setText(arg0 + 1 + "/" + imaCount);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@Override
	public void initData() {
		mViewPager.setAdapter(new myPagerAdapter());
		mViewPager.setCurrentItem(index, true);
	}

	/*
	 * ViewPagerAdapter
	 */
	private class myPagerAdapter extends PagerAdapter {

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// ScaleImageView iv = new ScaleImageView(getApplicationContext());
			Glide.with(ImageActivity.this).load(imageUrls[position]).diskCacheStrategy(DiskCacheStrategy.ALL)
					.into(imageViews[position]);
			// iv.setImageResource(R.drawable.login_back);
			container.addView(imageViews[position]);
			// imageViews[position] = iv;
			return imageViews[position];
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(imageViews[position]);
		}

		@Override
		public int getCount() {
			return imageUrls == null ? 0 : imageUrls.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

}
