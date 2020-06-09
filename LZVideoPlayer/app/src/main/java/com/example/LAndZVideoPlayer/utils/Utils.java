package com.example.LAndZVideoPlayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

//视频市场和屏幕宽高
//Liu
public class Utils {
    private static final String UNKNOWN_SIZE = "00:00";

    //转换视频时长格式
    public static String formatVideoTimeLength(long miliseconds) {
        int seconds = (int) (miliseconds / 1000);

        String formatLength;
        if(seconds == 0) {
            formatLength = UNKNOWN_SIZE;
        } else if(seconds < 60) {//小于1分钟
            formatLength = "00:" + (seconds < 10 ? "0" + seconds : seconds);
        } else if(seconds < 60 * 60) {  //小于1小时
            long sec = seconds % 60;
            long min = seconds / 60;
            formatLength = (min < 10 ? "0" + min : String.valueOf(min)) + ":" +
                    (sec < 10 ? "0" + sec : String.valueOf(sec));
        } else {
            long hour = seconds / 3600;
            long min = seconds % 3600 / 60;
            long sec = seconds % 3600 % 60;
            formatLength = (hour < 10 ? "0" + hour : String.valueOf(hour)) + ":" +
                    (min < 10 ? "0" + min : String.valueOf(min)) + ":" +
                    (sec < 10 ? "0" + sec : String.valueOf(sec));
        }
        return formatLength;
    }

    public static void showViewIfNeed(View view) {
        if(view.getVisibility() == View.GONE || view.getVisibility() == View.INVISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hideViewIfNeed(View view) {
        if(view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    public static boolean isViewShown(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    // Get activity from context object
    public static Activity getActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return getActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }
}
