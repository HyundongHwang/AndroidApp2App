package com.hhd2002.myappb;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Random;
import java.util.UUID;

/**
 * Created by hhd20 on 2/9/2018.
 */

public class MainActivity extends AppCompatActivity {
    private TextView _tvLog;
    private Messenger _messenger;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        _tvLog = this.findViewById(R.id.tv_log);

        Intent intent = this.getIntent();

        if (intent != null)
            _processIntent(intent);

        this.findViewById(R.id.btn_send_msg_by_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _send_msg_by_activity();
            }
        });

        this.findViewById(R.id.btn_send_msg_by_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _send_msg_by_service();
            }
        });

        this.findViewById(R.id.btn_send_msg_by_messenger_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _send_msg_by_messenger_service();
            }
        });

        this.findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _clear();
            }
        });
    }

    private void _clear() {
        _tvLog.setText("");
    }

    private void _send_msg_by_messenger_service() {
        if (_messenger == null) {
            Intent msgIntent = new Intent();
            msgIntent.setComponent(new ComponentName("com.hhd2002.myappa", "com.hhd2002.myappa.MyMessengerService"));

            ServiceConnection serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    _writeLog("onServiceConnected !!! ");
                    _messenger = new Messenger(service);
                    _send_msg_by_messenger();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    _writeLog("onServiceDisconnected !!! ");
                }
            };

            bindService(msgIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            _send_msg_by_messenger();
        }
    }

    private void _send_msg_by_messenger() {
        Message newMsg = new Message();
        String msg = _newMsg();
        newMsg.getData().putString("msg", msg);
        newMsg.getData().putString("pkg", getPackageName());
        newMsg.getData().putString("cls", MainActivity.class.getName());

        try {
            _messenger.send(newMsg);
            _writeLog(String.format("B->A : %s", msg));
        } catch (Exception e) {
            e.printStackTrace();
            _messenger = null;
            _send_msg_by_messenger_service();
        }
    }

    private void _send_msg_by_service() {
        Random random = new Random();
        String msg = String.format("hello %d", random.nextInt(100));

        Intent msgIntent = new Intent();
        msgIntent.setComponent(new ComponentName("com.hhd2002.myappa", "com.hhd2002.myappa.MyService"));
        msgIntent.putExtra("msg", msg);
        msgIntent.putExtra("pkg", getPackageName());
        msgIntent.putExtra("cls", MainActivity.class.getName());
        startService(msgIntent);

        _writeLog(String.format("B->A : %s", msg));
    }

    private void _send_msg_by_activity() {
        String msg = _newMsg();
        Intent msgIntent = new Intent();
        msgIntent.setComponent(new ComponentName("com.hhd2002.myappa", "com.hhd2002.myappa.MainActivity"));
        msgIntent.putExtra("msg", msg);
        msgIntent.putExtra("pkg", getPackageName());
        msgIntent.putExtra("cls", MainActivity.class.getName());
        _writeLog(String.format("B->A : %s", msg));
        startActivityForResult(msgIntent, 1000);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        _processIntent(intent);
    }

    private void _processIntent(Intent intent) {
        String msg = intent.getStringExtra("msg");

        if (msg != null)
            _writeLog(String.format("A->B : %s", msg));
    }

    private void _writeLog(String msg) {
        _tvLog.setText(_tvLog.getText().toString() + "\n" + msg);
    }

    private String _newMsg() {
        Random random = new Random();
        String msg = String.format("hello %d", random.nextInt(100));
        return msg;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == 2000) {
            String msg = data.getStringExtra("msg");
            _writeLog(String.format("A->B : %s", msg));
        }
    }
}
