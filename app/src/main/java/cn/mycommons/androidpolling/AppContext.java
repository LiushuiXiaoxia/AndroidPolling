package cn.mycommons.androidpolling;

import android.app.Application;

import cn.mycommons.polling.PollingLog;
import cn.mycommons.polling.PollingManager;


/**
 * AppContext <br/>
 * Created by xiaqiulei on 2016-06-30.
 */

public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PollingLog.log(BuildConfig.DEBUG);
        PollingManager.setup(this);
    }
}