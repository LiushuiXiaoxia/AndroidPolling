package cn.mycommons.polling;

class PollingTask {

    private static final float ONE_SECOND = 1000F;

    /**
     * 任务名字
     */
    private String name;

    /**
     * 触发时间，单位为秒
     */
    private long trigger;

    /**
     * 轮训周期，单位为秒
     */
    private long interval;

    /**
     * 轮训任务
     */
    private Runnable runnable;

    public PollingTask(String name, long interval, Runnable runnable) {
        this(name, 0, interval, runnable);
    }

    PollingTask(String name, long trigger, long interval, Runnable runnable) {
        this.name = name;
        this.trigger = trigger;
        this.interval = interval;
        this.runnable = runnable;
    }

    long getTrigger() {
        return trigger;
    }

    long getInterval() {
        return interval;
    }

    /**
     * 获取任务
     */
    Runnable getTaskRunnable(final String type) {
        return new Runnable() {
            @Override
            public void run() {
                PollingManager manager = PollingManager.getInstance();
                long last = manager.getLastExecTime(name);

                float time = (System.currentTimeMillis() - last) / ONE_SECOND;
                double ceilTime = Math.ceil(time);

                String format = "PollingTask.check type = %s ,name = %s ,ceilTime = %s ,last = %s ,interval = %s";
                PollingLog.i(format, type, name, ceilTime, last, interval);

                // 因为采用2种方式结合，所以只记录运行时候的时间，当某一种方式提前运行
                // ，后一种运行方式定时又到了，则认为在特定的运行周期内，只运行一次
                if (ceilTime >= interval && runnable != null) {
                    PollingLog.i("PollingTask exec");

                    manager.addExecTask(name);
                    runnable.run();
                } else {
                    PollingLog.i("PollingTask not exec");
                }
            }
        };
    }

    /**
     * 检验是否合法
     */
    boolean validate() {
        return runnable != null && interval > 0;
    }

    @Override
    public String toString() {
        return "PollingTask{" +
                "trigger=" + trigger +
                ", interval=" + interval +
                ", name='" + name + '\'' +
                ", runnable=" + runnable +
                '}';
    }
}