package com.erlema.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.erlema.activity.GoodsDetailsActivity;
import com.erlema.activity.MainActivity;
import com.erlema.bean.MyUser;
import com.erlema.util.Utils;
import com.erlema.view.LinkBackgroundSpan;
import com.example.erlema.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobUser;

/**
 * 发送的文本类型
 */
public class SendTextHolder extends BaseViewHolder {
    LinkClickListener listener;
    protected ImageView iv_avatar;

    protected ImageView iv_fail_resend;

    protected TextView tv_time;
    private Context context;
    protected TextView tv_message;
    protected TextView tv_send_status;

    protected ProgressBar progress_load;

    private BmobIMMessage message;

    public SendTextHolder(Context context, ViewGroup root) {
        super(context, root, R.layout.item_chat_sent_message);
        this.context = context;
        iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        tv_message = (TextView) itemView.findViewById(R.id.tv_message);
        tv_send_status = (TextView) itemView.findViewById(R.id.tv_send_status);
        tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        iv_fail_resend = (ImageView) itemView.findViewById(R.id.iv_fail_resend);
        progress_load = (ProgressBar) itemView.findViewById(R.id.progress_load);
    }

    public void onResendClick(View view) {

    }

    @Override
    public void bindData(Object o) {
        message = (BmobIMMessage) o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        BmobIMUserInfo info = message.getBmobIMUserInfo();
//    ViewUtil.setAvatar(info!=null?info.getAvatar():null, R.mipmap.head, iv_avatar);
        Glide.with(context).load(BmobUser.getCurrentUser(getContext(),MyUser.class).getAvator())
                .placeholder(R.drawable.ic_default_avatar_big_normal).into(iv_avatar);
        String time = dateFormat.format(message.getCreateTime());
        String content = message.getContent();
        tv_message.setText(content);
        try {
            final JSONObject jsonObject = new JSONObject(message.getExtra());
            if ("0".equals(jsonObject.getString("level")) || "2".equals(jsonObject.getString("level"))) {
                tv_message.setMovementMethod(LinkMovementMethod.getInstance());// 加了这句才能响应点击
                tv_message.setLongClickable(false);// 屏蔽掉长按效果 否则长按会崩溃！
                tv_message.setText(change2LInkSpan(message.getContent()));
                if (listener == null) {
                    listener = new LinkClickListener() {
                        @Override
                        public void onLinkClick() {
                            Intent todetails = new Intent(context,
                                    GoodsDetailsActivity.class);
                            try {
                                todetails.putExtra("goodsId", jsonObject.getString("goodsId"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            context.startActivity(todetails);
                        }
                    };
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv_time.setText(time);
        int status = message.getSendStatus();
        if (status == BmobIMSendStatus.SENDFAILED.getStatus()) {
            iv_fail_resend.setVisibility(View.VISIBLE);
            progress_load.setVisibility(View.GONE);
        } else if (status == BmobIMSendStatus.SENDING.getStatus()) {
            iv_fail_resend.setVisibility(View.GONE);
            progress_load.setVisibility(View.VISIBLE);
        } else {
            iv_fail_resend.setVisibility(View.GONE);
            progress_load.setVisibility(View.GONE);
        }
    }

    /**
     * 将指定文字转换成特定网页链接效果
     *
     * @param testText 要转换的String
     * @return SpannableStringBuilder, 可直接应用的textView上
     */
    @NonNull
    private SpannableStringBuilder change2LInkSpan(String testText) {
        //通过正则表达式提取链接字符串
        int startX = 0;
        if (testText.startsWith("想买")) {
            startX = 2;
        } else {
            startX = 6;
        }
        SpannableStringBuilder buffer = new SpannableStringBuilder();
        int temp = 0;
        //普通文字加载进来
        buffer.append(testText.substring(temp, startX));
        //加入链接效果
        buffer.append(getClickableSpan(testText.substring(startX, testText.length())));
        return buffer;
    }

    /**
     * 返回一个可点击的span
     * （没有做成接口，请在onClick中写下具体点击事件）
     *
     * @param link 传进来的链接统URL
     * @return SpannableString
     */
    private SpannableString getClickableSpan(final String link) {

        SpannableString spannableInfo = new SpannableString(link);
        int start = 0;
        int end = spannableInfo.length();
//        Log.i("length", "" + end);
//    textView.setMovementMethod(LinkMovementMethod.getInstance());// 加了这句才能响应点击
//    textView.setLongClickable(false);// 屏蔽掉长按效果 否则长按会崩溃！
        //设置背景图片span
        spannableInfo.setSpan(new LinkBackgroundSpan(R.drawable.link_ico, getContext(), link), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置点击事件
        spannableInfo.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // 方法重新设置文字背景为透明色。
                ((TextView) view).setHighlightColor(getContext().getResources().getColor(android.R.color.transparent));
                //请在下面写下具体要实现的功能
                if (listener != null) {
                    listener.onLinkClick();
                }
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableInfo;
    }


    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
