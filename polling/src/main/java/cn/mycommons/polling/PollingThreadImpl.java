package cn.mycommons.polling;

import android.text.TextUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * PollingThreadImpl <br/>
 */
class PollingThreadImpl implements IPolling {

    private static final String TYPE = "PollingThreadImpl";

    private ScheduledExecutorService scheduledExecutorService;
    private Map<String, ScheduledFuture> scheduledFutureMap;
    private Map<String, PollingTask> taskMap;

    PollingThreadImpl() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledFutureMap = new ConcurrentHashMap<>();
        taskMap = new ConcurrentHashMap<>();
    }

    @Override
    public void addTask(String name, PollingTask task) {
        ScheduledFuture scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(
                task.getTaskRunnable(PollingThreadImpl.TYPE),
                task.getTrigger(),
                task.getInterval(),
                TimeUnit.SECONDS
        );

        scheduledFutureMap.put(name, scheduledFuture);
        taskMap.put(name, task);
    }

    @Override
    public PollingTask getTask(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        return taskMap.get(name);
    }

    @Override
    public boolean removeTask(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }

        taskMap.remove(name);

        ScheduledFuture future = scheduledFutureMap.remove(name);
        if (future != null) {
            future.cancel(true);
        }

        return true;
    }

    @Override
    public void removeAllTask() {
        if (taskMap != null && !taskMap.isEmpty()) {
            for (Map.Entry<String, PollingTask> entry : taskMap.entrySet()) {
                removeTask(entry.getKey());
            }
        }
    }
}