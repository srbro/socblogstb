package com.ug.eon.android.tv;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class EONWatchdogService extends Service {

    private static final int INTERVAL = 3000;
    private static final String packageName = "com.ug.eon.android.tv";
    private static boolean stopTask = false;
    private static Timer timer = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(timer != null && !stopTask)
            return START_STICKY;

        stopTask = false;

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (stopTask) {
                    this.cancel();
                }

                InputStream in = null;
                try {
                    Process child = Runtime.getRuntime().exec("ps");
                    in = child.getInputStream();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    boolean exists = false;
                    while ((line = bufferedReader.readLine()) != null) {
                        if(line.contains(packageName) && !line.contains(packageName + ".")) {
                            exists = true;
                        }
                    }
                    if(!exists) {
                        Intent dialogIntent = new Intent(getApplicationContext(), com.ug.eon.android.tv.TvActivity.class);
                        dialogIntent.setAction(Intent.ACTION_MAIN);
                        startActivity(dialogIntent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 3000, INTERVAL);
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        stopTask = true;
        timer = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
