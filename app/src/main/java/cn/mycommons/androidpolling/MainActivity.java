package cn.mycommons.androidpolling;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import cn.mycommons.polling.PollingManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String TASK_NAME = "poll_order_count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.start);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Start Polling");
                PollingManager.getInstance().addTask(TASK_NAME, 1, 5, new OrderPollTask());
            }
        });

        view = findViewById(R.id.stop);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Stop Polling");
                PollingManager.getInstance().removeTask(TASK_NAME);
            }
        });
    }

    private static class OrderPollTask implements Runnable {

        @Override
        public void run() {
            Log.i(TAG, "OrderPollTask run.");
        }
    }
}