package com.erlema.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;

import com.erlema.bean.GoodsWanted;
import com.erlema.bean.MyGoods;
import com.erlema.bean.MyUser;
import com.erlema.bean.myActionBar;
import com.example.erlema.R;
import com.example.erlema.R.id;
import com.example.erlema.R.layout;
import com.gc.materialdesign.views.ButtonRectangle;

public class AddQiuActivity extends BaseActivity {
	private myActionBar myactionbar;
	private Spinner catesp;
	private EditText title, pricelow, pricehigh, describ;
	private ButtonRectangle btrsubmit;

	@Override
	public void setContentView() {
		setContentView(layout.activity_add_qiu);

	}

	@Override
	public void initViews() {
		myactionbar = (myActionBar) findViewById(id.myActionBar1);
		myactionbar.setTitleText("求购");
		title = (EditText) findViewById(id.qiu_title);
		catesp = (Spinner) findViewById(id.qiu_cate);
		pricelow = (EditText) findViewById(id.price1);
		pricehigh = (EditText) findViewById(id.price2);
		describ = (EditText) findViewById(id.qiucontent);
		btrsubmit = (ButtonRectangle) findViewById(id.btr_submit);

	}

	@Override
	public void initListeners() {
		btrsubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkData()) {
					submit();
				}
			}

		});
	}

	String titletext, describe;
	int price1, price2, cate;

	private boolean checkData() {
		titletext = title.getText().toString();
		try {
			price1 = Integer.valueOf(pricelow.getText().toString());
			price2 = Integer.valueOf(pricehigh.getText().toString());
		} catch (Exception e) {
			ShowToast("请填写价格区间");
			return false;
		}
		describe = describ.getText().toString();
		if (titletext == null || titletext.trim().equals("")) {
			ShowToast("请填写完整信息");
			return false;
		}
		if (describe == null || describe.trim().equals("")) {
			ShowToast("请填写完整信息");
			return false;
		}
		if (price1 > price2) {
			ShowToast("价格区间有误！");
			return false;
		}
		cate = catesp.getSelectedItemPosition();
		return true;
	}

	// private String title;// 标题
	// private String describ;//描述
	// private Integer status=0;//0 正常 1 交易完成 2 审核不通过
	// private String owerID;// 主人
	// private double pirce_high;// 最高价格
	// private double pirce_low;// 最低价格
	// private String school;// 学校
	// private Integer cate;// 类别
	protected void submit() {
		GoodsWanted g = new GoodsWanted();
		MyUser user = BmobUser.getCurrentUser(getApplicationContext(),
				MyUser.class);
		String oweid = user.getObjectId();
		g.setTitle(titletext.trim());
		g.setDescrib(describe);
		g.setOwerID(oweid);
		g.setPirce(price1, price2);
		g.setCate(cate);
		g.setSchool(user.getSchool());
		g.save(getApplicationContext(), new SaveListener() {

			@Override
			public void onSuccess() {
				ShowToast("发送成功");

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("发送失败");
			}
		});
		AddQiuActivity.this.finish();
	}

	@Override
	public void initData() {

	}
}
