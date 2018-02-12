package com.hhd2002.myappa;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by hhd20 on 2/9/2018.
 */

public class MyMessengerService extends Service {

    private Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String msgStr = msg.getData().getString("msg");
            String pkg = msg.getData().getString("pkg");
            String cls = msg.getData().getString("cls");

            Intent newIntent = new Intent();
            newIntent.setComponent(new ComponentName(pkg, cls));

            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP );

            String newMsg = String.format("%s world", msgStr);
            newIntent.putExtra("msg", newMsg);
            startActivity(newIntent);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        IBinder binder = new Messenger(_handler).getBinder();
        return binder;
    }
}
