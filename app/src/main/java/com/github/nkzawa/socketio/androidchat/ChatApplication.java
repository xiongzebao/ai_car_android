package com.github.nkzawa.socketio.androidchat;

import android.app.Application;
import android.util.Log;

import com.blankj.utilcode.util.Utils;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class ChatApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
