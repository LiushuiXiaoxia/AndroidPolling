package cn.mycommons.polling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * PollingAlarmImpl <br/>
 * Created by xiaqiulei on 2016-06-30.
 */
class PollingAlarmImpl implements IPolling {

    static final String TYPE = "PollingAlarmImpl";

    private static final long ONE_SECOND = 1000L;

    private Context context;
    private AlarmManager alarmManager;
    private Map<String, PendingIntent> intentMap;
    private Map<String, PollingTask> taskMap;

    PollingAlarmImpl(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);

        intentMap = new ConcurrentHashMap<>();
        taskMap = new ConcurrentHashMap<>();
    }


    @Override
    public void addTask(String name, PollingTask task) {
        PendingIntent pendingIntent = getPendingIntent(name);

        alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + task.getTrigger() * ONE_SECOND,
                task.getInterval() * ONE_SECOND,
                pendingIntent
        );

        taskMap.put(name, task);
    }

    @Override
    public PollingTask getTask(String name) {
        if (!TextUtils.isEmpty(name)) {
            return taskMap.get(name);
        }
        return null;
    }

    @Override
    public boolean removeTask(String name) {
        if (cancelTask(name)) {
            removePendingIntent(name);
            return true;
        }
        return false;
    }

    @Override
    public void removeAllTask() {
        if (!taskMap.isEmpty()) {
            for (Map.Entry<String, PollingTask> entry : taskMap.entrySet()) {
                removeTask(entry.getKey());
            }
        }
    }

    private PendingIntent getPendingIntent(String key) {
        if (intentMap.get(key) != null) {
            return intentMap.get(key);
        }

        Intent intent = new Intent(context, PollingServer.class);
        intent.setAction(key);

        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        intentMap.put(key, pendingIntent);

        return pendingIntent;
    }

    private boolean cancelTask(String key) {
        PendingIntent pendingIntent = getPendingIntent(key);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            return true;
        }
        return false;
    }

    private boolean removePendingIntent(String name) {
        PendingIntent intent = intentMap.remove(name);
        return intent != null;
    }
}