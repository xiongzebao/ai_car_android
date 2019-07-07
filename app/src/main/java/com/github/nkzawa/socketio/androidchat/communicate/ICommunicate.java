package com.github.nkzawa.socketio.androidchat.communicate;

public interface ICommunicate {

    interface onMessageLisenter{

        String BT_ACCEPTED = "BT_ACCEPTED";
        String BT_CONNECTED = "BT_CONNECTED";
        String SERVER_CONNECTED = "SERVER_CONNECTED";

        void onMessage(String name,String msg);
        void onConnect(String name,String type);
        void onDisConnect(String o);
        void onError(String err_msg);

    }
    void connect();
    void disConnect();
    void destroy();
    boolean isConnected();
    void sendMessage(String msg);
    void setOnMessageLisenter(onMessageLisenter onMessageLisenter);

}
