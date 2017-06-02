package com.sober.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Sober on 2016/1/22.
 */

public class LogUtil {

    private static final boolean SHOW_MSG = true;

    public static void showMsg(Context context, String msg){
        if (SHOW_MSG){
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            Log.d(context.getClass().getSimpleName(), msg);
        }
    }

    public static void showMsgLog(Context context, String msg){
        if (SHOW_MSG){
            Log.d(context.getClass().getSimpleName(), msg);
        }
    }
}
