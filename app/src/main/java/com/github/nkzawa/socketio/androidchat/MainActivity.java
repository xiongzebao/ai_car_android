package com.github.nkzawa.socketio.androidchat;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;


public class MainActivity extends BaseActivity {

    MainFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
       fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    public void onClick(View v){
        fragment.onClick(v);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("xiong","onDestroyed main");
       // MySocketManager.getInstance().disConnect();
    }
}
