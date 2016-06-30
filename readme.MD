# AndroidPolling

---

AndroidPolling用于后台轮训,技术方案为Thread和AlarmManager结合,互补互备。


## 使用

初始化

```
public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PollingLog.log(BuildConfig.DEBUG);
        PollingManager.setup(this);
    }
}
```

开始任务

```
class PollingTaskRunnable implements Runnable {

    @Override
    public void run() {
        Log.i(TAG, "PollingTaskRunnable run.");
    }
}
    
PollingManager.getInstance().addTask(TASK_NAME, 1, 5, new PollingTaskRunnable());
```

```
    /**
     * 创建一个轮训任务
     *
     * @param name     名称
     * @param trigger  触发时间，过多少秒后执行
     * @param interval 间隔时间，单位秒
     * @param runnable 执行任务
     */
    public void addTask(String name, long trigger, long interval, Runnable runnable);
```

结束任务

```
PollingManager.getInstance().removeTask(TASK_NAME);
```

## 后续

* 添加JobService支持
* 可以自定义轮训策略