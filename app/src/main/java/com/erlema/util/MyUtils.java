package com.erlema.util;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

import com.erlema.bean.MyUser;
import com.erlema.config.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler.Callback;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MyUtils {
    final static private boolean DEBUG = true;
    private static final int seconds_of_1minute = 60;
    private static final int seconds_of_30minutes = 30 * 60;
    private static final int seconds_of_1hour = 60 * 60;
    private static final int seconds_of_1day = 24 * 60 * 60;
    private static final int seconds_of_15days = seconds_of_1day * 15;
    private static final int seconds_of_30days = seconds_of_1day * 30;
    private static final int seconds_of_6months = seconds_of_30days * 6;
    private static final int seconds_of_1year = seconds_of_30days * 12;

    /*
     * 保留i位小数
     */
    public static double format(double d, int i) {
        BigDecimal bd = new BigDecimal(d);
        return (bd.setScale(i, BigDecimal.ROUND_HALF_UP).doubleValue());
    }

    /*
     * 打印toast
     */
    public static void showTost(final Activity act, final String msg) {
        if ("main".equals(Thread.currentThread().getName())) {// 主线程
            Toast.makeText(act, msg, 0).show();
        } else {// 子线程
            act.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(act, msg, 0).show();
                }
            });
        }

    }

    /*
     * 打印日志
     */
    public static void showLog(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static String getTimeElapse(String date) {
        long nowTime = new Date().getTime() / 1000;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long oldTime = 0;
        try {
            oldTime = sf.parse(date).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // elapsedTime是发表和现在的间隔时间
        long elapsedTime = nowTime - oldTime;
        if (elapsedTime < seconds_of_1minute) {
            return "刚刚";
        }
        if (elapsedTime < seconds_of_30minutes) {
            return elapsedTime / seconds_of_1minute + "分钟前";
        }
        if (elapsedTime < seconds_of_1hour) {
            return "半小时前";
        }
        if (elapsedTime < seconds_of_1day) {
            return elapsedTime / seconds_of_1hour + "小时前";
        }
        if (elapsedTime < seconds_of_15days) {
            return elapsedTime / seconds_of_1day + "天前";
        }
        if (elapsedTime < seconds_of_30days) {
            return "半个月前";
        }
        if (elapsedTime < seconds_of_6months) {
            return elapsedTime / seconds_of_30days + "月前";
        }
        if (elapsedTime < seconds_of_1year) {
            return "半年前";
        }
        if (elapsedTime >= seconds_of_1year) {
            return elapsedTime / seconds_of_1year + "年前";
        }
        return "未知时间";
    }

    private static final int MEDIA_TYPE_IMAGE = 1;

    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = null;
        try {
            // This location works best if you want the created images to be
            // shared
            // between applications and persist after your app has been
            // uninstalled.
            mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "erlema");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                // 在SD卡上创建文件夹需要权限：
                // <uses-permission
                // android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                return null;
            }

        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }
}
