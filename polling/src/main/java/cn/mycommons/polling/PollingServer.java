package cn.mycommons.polling;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * PollingServer <br/>
 * Created by xiaqiulei on 2016-06-30.
 */

public class PollingServer extends IntentService {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE_TIME = 1L;

    private ThreadPoolExecutor executor;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public PollingServer() {
        super("PollingServer");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            executor.allowCoreThreadTimeOut(true);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            PollingLog.i("PollingServer.onHandleIntent action = " + action);
            PollingTask task = PollingManager.getInstance().getPollingTask(action);
            PollingLog.i("PollingServer.onHandleIntent task = " + task);

            if (task != null) {
                executor.execute(task.getTaskRunnable(PollingAlarmImpl.TYPE));
            }
        }
    }
}