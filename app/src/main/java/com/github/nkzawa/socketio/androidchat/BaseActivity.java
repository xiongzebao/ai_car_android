package com.github.nkzawa.socketio.androidchat;

import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        BaseAppHelper.Instance().pushActivity(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        BaseAppHelper.Instance().popActivity(this);
        super.onDestroy();
    }
}
