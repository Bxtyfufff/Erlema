package com.erlema.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;

import java.util.List;

/**
 * Created by Bxtyfufff on 2016/3/5 0005.
 */
public class TopNewsAdapter extends LoopPagerAdapter {
    private List<String> url;
    private Context context;
    private onTopnewsClicklistener mlistener;

    public TopNewsAdapter(Context context, RollPagerView viewPager, List<String> imgs) {
        super(viewPager);
        this.url = imgs;
        this.context = context;
    }

    @Override
    public View getView(ViewGroup container, final int position) {
        ImageView view = new ImageView(container.getContext());
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(context).load(url.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
        if (mlistener != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mlistener.onClick(position);
                }
            });
        }
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public int getRealCount() {
        return url == null ? 0 : url.size();
    }

    public void setOnTopNEwsClicklistener(onTopnewsClicklistener mlistener) {
        this.mlistener = mlistener;
    }

    public interface onTopnewsClicklistener {
        void onClick(int position);
    }
}
