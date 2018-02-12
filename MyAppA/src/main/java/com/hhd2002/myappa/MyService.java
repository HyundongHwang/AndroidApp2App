package com.hhd2002.myappa;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by hhd20 on 2/12/2018.
 */

public class MyService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
            return Service.START_STICKY;
        else
            _processIntent(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    private void _processIntent(Intent intent) {
        String msg = intent.getStringExtra("msg");
        String pkg = intent.getStringExtra("pkg");
        String cls = intent.getStringExtra("cls");

        Intent newIntent = new Intent();
        newIntent.setComponent(new ComponentName(pkg, cls));

        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP );

        String newMsg = String.format("%s world", msg);
        newIntent.putExtra("msg", newMsg);
        startActivity(newIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
