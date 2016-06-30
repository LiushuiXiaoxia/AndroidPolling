package cn.mycommons.polling;


import android.content.Context;
import android.text.TextUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 轮询管理器。负责添加、获取、移除轮询任务。<br/>
 * 定义2种IPolling的实现方式，一种是AlarmManager方式，另外一种是Thread的方式，
 * 为什么采用2种方式呢，因为某些机型使用AlarmManager方式
 * 不能正常的运行，故采用2中方式结合
 */
public class PollingManager {

    private static PollingManager instance;

    public synchronized static void setup(Context context) {
        if (instance != null) {
            throw new RuntimeException("PollingManager is already setup.");
        }
        if (context == null) {
            throw new IllegalArgumentException("context is null.");
        }
        instance = new PollingManager(context.getApplicationContext());
    }

    public static PollingManager getInstance() {
        return instance;
    }

    private IPolling alarmPolling;
    private IPolling threadPolling;

    // 记录polling 任务运行的时间
    private Map<String, Long> execTimeMap;

    private PollingManager(Context context) {
        alarmPolling = new PollingAlarmImpl(context);
        threadPolling = new PollingThreadImpl();
        execTimeMap = new ConcurrentHashMap<>();
    }

    /**
     * 创建一个轮训任务
     *
     * @param name     名称
     * @param trigger  触发时间，过多少秒后执行
     * @param interval 间隔时间，单位秒
     * @param runnable 执行任务
     */
    public void addTask(String name, long trigger, long interval, Runnable runnable) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("name is null");
        }

        if (runnable == null) {
            throw new IllegalArgumentException("runnable is null");
        }

        // interval = 10; // 周期，用于测试
        PollingTask task = new PollingTask(name, trigger, interval, runnable);

        if (!task.validate()) {
            throw new IllegalArgumentException("task is invalidate");
        }

        removeTask(name);

        threadPolling.addTask(name, task);
        alarmPolling.addTask(name, task);
    }

    public void removeTask(String name) {
        if (!TextUtils.isEmpty(name)) {
            threadPolling.removeTask(name);
            alarmPolling.removeTask(name);

            execTimeMap.remove(name);
        }
    }

    /**
     * 删除所有的任务
     */
    public void removeAllTask() {
        threadPolling.removeAllTask();
        alarmPolling.removeAllTask();

        execTimeMap.clear();
    }

    PollingTask getPollingTask(String name) {
        return alarmPolling.getTask(name);
    }

    // 添加一个任务运行的时间
    synchronized void addExecTask(String name) {
        execTimeMap.put(name, System.currentTimeMillis());
    }

    // 获取一个任务上一次运行的时间
    synchronized long getLastExecTime(String name) {
        if (execTimeMap.containsKey(name)) {
            return execTimeMap.get(name);
        }
        return 0;
    }
}