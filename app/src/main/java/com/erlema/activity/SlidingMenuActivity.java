/**
 * SlidingMenuActivity.java
 *ErLeMa
 *@author 作者：bxtyfufff
 *@version 创建时间：2015-8-5 上午11:28:18 
 * 类说明 
 */

package com.erlema.activity;

import java.util.zip.Inflater;

import android.R.anim;
import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.erlema.R;

/**
 * SlidingMenuActivity.java
 * 
 * @author bxtyfufff
 * @version 2015-8-5上午11:28:18
 */
public class SlidingMenuActivity extends BaseActivity {
	private ListView mlistView;
	private DrawerLayout mdrawlayout;

	@Override
	public void setContentView() {
		setContentView(R.layout.slidin_layout);
	}

	@Override
	public void initViews() {
		mlistView = (ListView) findViewById(R.id.left_drawer);
	}

	@Override
	public void initListeners() {
		String[] drawlist = { "androuid", "java", "cmcc", "Chinanet" , "androuid", "java", "cmcc", "Chinanet" , "androuid", "java", "cmcc", "Chinanet" , "androuid", "java", "cmcc", "Chinanet" , "androuid", "java", "cmcc", "Chinanet" ,};
		mlistView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, drawlist));
	}

	@Override
	public void initData() {
		View content=findViewById(R.id.content_frame);
//		FragmentManager fragm=getFragmentManager().beginTransaction().add(fragment, tag)
	}

}
