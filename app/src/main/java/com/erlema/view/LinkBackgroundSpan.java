package com.erlema.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.style.ReplacementSpan;

import com.example.erlema.R;

/**
 * Created by Bxtyfufff on 2016/2/26 0026.
 * 继承replacementSpan
 * 返回想要的背景span
 */
public class LinkBackgroundSpan extends ReplacementSpan {
    private int drawableId; 

    private int bgColor = R.color.purple;
    private  String TEXT = "网页链接";
    private  Context context;
    private final int picSize;
    private final int textSize;
    private final int bgHeight;
    private final int picMarginText;
    private final int picMarginLeft;
    private final int textMarginRight;
    private final int marginLeft;
    private final int marginRight;

    public LinkBackgroundSpan(int drawableId,Context context,String text) {
        this.drawableId = drawableId;
        this.context=context;
        this.TEXT=text;
        picSize = dip2px(context, 15f);
        textSize = dip2px(context, 15f);
        bgHeight = dip2px(context, 18f);
        picMarginText = dip2px(context, 2f);
        picMarginLeft = dip2px(context, 4f);
        textMarginRight = dip2px(context, 4f);
        marginLeft = dip2px(context, 4f);
        marginRight = dip2px(context, 4f);
    }


    //主要方法，绘制想要的图案
    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
                     Paint paint) {
        x = x + marginLeft;// 向右移 整体左边距=8
        // 绘制背景和图标
        canvas.save();// 保存画布
        // 绘制圆角背景
        int roundRectWidth =
                Math.round(measureText(paint, TEXT, 0, TEXT.length()) + picSize + picMarginLeft + picMarginText
                        + textMarginRight); // 圆角矩阵背景长度
        RectF rect = new RectF(x, top, x + roundRectWidth, top + bgHeight);
        paint.setColor(context.getResources().getColor(bgColor));// 设置画笔颜色
        canvas.translate(0, y - top - textSize);//移动canvas实现让文字（网页链接）居中显示
        canvas.drawRoundRect(rect, 6f, 6f, paint);
        // 绘制ico
        int icoCenterTranslate = bgHeight / 2 - picSize / 2;
        canvas.translate(0, icoCenterTranslate);// 移动canvas实现在背景中居中显示
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        Rect src = new Rect();// 图片
        Rect dst = new Rect();// 屏幕位置及尺寸
        // src 这个是表示绘画图片的大小
        src.left = 0; // 0,0
        src.top = 0;
        src.right = bitmap.getWidth();
        src.bottom = bitmap.getHeight();
        // 下面的 dst 是表示 绘画这个图片的位置
        dst.left = Math.round(x + picMarginLeft);
        dst.top = top;
        dst.right = Math.round(x + picSize + picMarginLeft);
        dst.bottom = top + picSize;
        canvas.drawBitmap(bitmap, src, dst, null);
        canvas.restore();
        // 绘制文字
        paint.setColor(Color.WHITE);// 白色文字颜色
        paint.setTextSize(textSize);// 文字大小
        paint.setTypeface(Typeface.DEFAULT_BOLD);// 粗体字
        canvas.drawText(TEXT, 0, TEXT.length(), x + picMarginLeft + picMarginText + picSize, y,
                paint);
    }

    //返回背景占用textVIew的宽度
    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return Math.round(measureText(paint, TEXT, 0, TEXT.length()) + picSize + picMarginLeft + picMarginText
                + textMarginRight + marginLeft + marginRight);
    }

    //私有方法，计算文字占用大小，其实就是“网页链接”这几个字
    private float measureText(Paint paint, CharSequence text, int start, int end) {
        // 因为文字大小取用定制的而不是来自textView的设置，所以paint要设置
        paint.setTextSize(textSize);// 文字大小
        paint.setTypeface(Typeface.DEFAULT_BOLD);// 粗体字
        return paint.measureText(text, start, end);
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
