package cn.mycommons.polling;

import android.util.Log;

/**
 * PollingLog <br/>
 * Created by xiaqiulei on 2016-06-30.
 */
public class PollingLog {

    private static final String TAG = "Polling";
    private static boolean ENABLE = false;

    static void i(String msg, Object... args) {
        if (ENABLE) {
            if (args != null && args.length != 0) {
                msg = String.format(msg, args);
            }
            Log.i(TAG, msg);
        }
    }

    static void e(String msg, Object... args) {
        if (ENABLE) {
            if (args != null && args.length != 0) {
                msg = String.format(msg, args);
            }
            Log.e(TAG, msg);
        }
    }

    /**
     * 是否开启日志
     *
     * @param enable 是否开启日志
     */
    public static void log(boolean enable) {
        PollingLog.ENABLE = enable;
    }
}