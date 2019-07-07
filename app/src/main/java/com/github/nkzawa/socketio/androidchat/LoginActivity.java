package com.github.nkzawa.socketio.androidchat;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;


import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.github.nkzawa.socketio.androidchat.communicate.ICommunicate;
import com.github.nkzawa.socketio.androidchat.communicate.MyBluetoothManager;
import com.github.nkzawa.socketio.androidchat.communicate.MySocketManager;
import com.github.nkzawa.socketio.androidchat.databinding.ActivityLoginBinding;


/**
 * A login screen that offers login via username.
 */
public class LoginActivity extends BaseActivity implements ICommunicate.onMessageLisenter{
    private User user = new User("xiongbin","123456");
    private ICommunicate communicateDevice;
    private String communicateMode = SPUtils.getInstance().getString(Constants.COMMUNICATE_MODE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        binding.setUser(user);

        if(TextUtils.isEmpty(communicateMode)){
            ToastUtils.showLong("请在右上角选择菜单选择无线连接方式");
            return;
        }
        initCommunicateDevice(communicateMode);
    }

    private void initCommunicateDevice(String communicateMode){
        if(communicateMode.equals(Constants.BLUETOOTH_MODE)){
            communicateDevice = new MyBluetoothManager(this);
            ((MyBluetoothManager) communicateDevice).requestDiscoverable();
            ((MyBluetoothManager) communicateDevice).startBluetoothServer();
        }else{
            communicateDevice = new MySocketManager(this);
        }
        communicateDevice.setOnMessageLisenter(this);
    }


    @Override
    public void onMessage(String name,String msg) {
        LogUtils.eTag("xiong",msg);
        ToastUtils.showShort(name+":"+msg);
    }

    @Override
    public void onConnect(String name,String type) {
        ToastUtils.showShort(name+":"+type);
    }

    @Override
    public void onDisConnect(String msg) {
        ToastUtils.showShort(msg);
    }

    @Override
    public void onError(String err_msg) {
        ToastUtils.showShort(err_msg);
    }


    public void onClickLogin(View view) {
        if(communicateDevice==null||!communicateDevice.isConnected()){
            ToastUtils.showShort("通信未连接");
            return;
        }
        communicateDevice.sendMessage(user.username);
    }


    //菜单触发事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bluetooth:
                communicateMode = Constants.BLUETOOTH_MODE;
                break;
            case R.id.action_wire_less:
                communicateMode = Constants.WIFI_MODE;
                break;
        }
        SPUtils.getInstance().put(Constants.COMMUNICATE_MODE,communicateMode);
        initCommunicateDevice(communicateMode);
        communicateDevice.connect();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("xiong","onDestroy");
        communicateDevice.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}



