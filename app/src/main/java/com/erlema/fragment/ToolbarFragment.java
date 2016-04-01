package com.erlema.fragment;

import com.example.erlema.R;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ToolbarFragment extends Fragment {

	private int resource = 0;


	public ToolbarFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootview;
		if (resource == 0) {
			rootview = inflater.inflate(R.layout.bottom_tool_bar_normal,
					container, false);
		} else {
			rootview = inflater.inflate(resource, container, false);
		}

		return rootview;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
