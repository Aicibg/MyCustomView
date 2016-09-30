package com.app.mycustomview;

import android.content.Context;

/**
 * Created by DongHao on 2016/9/30.
 * Description:
 */

public class Utils {
    public static float dp2px(Context context,int dp){
        float density=context.getResources().getDisplayMetrics().density;
        return dp*density;
    }

    public static int px2dp(Context context,float px){
        float density=context.getResources().getDisplayMetrics().density;
        return (int) (px/density);
    }
}
