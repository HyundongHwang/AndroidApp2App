package com.hhd2002.myappa;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by hhd20 on 2/9/2018.
 */

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (this.getIntent() != null) {
            boolean skipUi = _processIntent(this.getIntent());

            if (skipUi)
                return;
        }

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
    }

    private boolean _processIntent(Intent intent) {
        String msg = intent.getStringExtra("msg");

        if (msg != null) {
            String newMsg = String.format("%s world", msg);
            Intent resIntent = new Intent();
            resIntent.putExtra("msg", newMsg);
            this.setResult(2000, resIntent);
            finish();
        }

        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        _processIntent(this.getIntent());
    }
}
