package com.github.nkzawa.socketio.androidchat.communicate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MyBluetoothManager implements ICommunicate {

    final public int REQUEST_ENABLE_BT = 100;
    final public int REQUEST_DISCOVERABLE = 101;
    final public int MESSAGE_READ = 1;
    final public int MESSAGE_FOUND_DEVICE = 2;
    final public int ON_CONNECTED = 3;



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case ON_CONNECTED:
                    String name = String.valueOf(msg.arg1);
                    String type = String.valueOf(msg.arg2);
                    lisenter.onConnect(name,type);

                case MESSAGE_READ:
                   Bundle bundle =  msg.getData();
                   if(bundle==null){
                       return;
                   }
                    String client_name = "";
                    String content = "";
                   if(!TextUtils.isEmpty(bundle.getString("name"))){
                         client_name = String.valueOf(bundle.getString("name"));
                   }
                    if(bundle.getByteArray("content").length!=0){
                      byte[] bytes =   bundle.getByteArray("content");

                        try {
                            content= new String(bytes,"utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }
                    lisenter.onMessage(client_name,content);
                    break;
                case MESSAGE_FOUND_DEVICE:

                    BluetoothDevice device = (BluetoothDevice) msg.obj;
                    if (alertListView == null) {
                        alertListView = new AlertListView(context, devices, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ToastUtils.showShort(devices.get(which).getName());
                            }
                        });
                        alertListView.show();
                    }
                    if (device.getName() != null) {
                        devices.add(device);
                        alertListView.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };


    public String NAME = "ai_car_android";
    public UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BroadcastReceiver mReceiver = null;
    private ConnectedThread connectedThread;//读取数据线程

    private AlertListView alertListView;
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private ICommunicate.onMessageLisenter lisenter;
    private Context context;

/*    public   static MyBluetoothManager bluetoothManager;
    public static  MyBluetoothManager Instance(Context context){
        if(bluetoothManager==null){
            bluetoothManager = new MyBluetoothManager();
        }
        return bluetoothManager;
    }*/

    public MyBluetoothManager(Context context){
        this.context = context;
    }

    @Override
    public void connect() {
       enableBluetooth();//如果蓝牙没开启，开启蓝牙
       String bluetooth =  SPUtils.getInstance().getString(Constants.CURRENT_BLUETOOTH);//最近一次连接的蓝牙名字
       BluetoothDevice bluetoothDevice = findBlueToothDeviceByName(bluetooth);
       if(bluetoothDevice==null){//如果没有连接过蓝牙
         //  requestDiscoverable();
           registerDiscoveryReceiver();//注册发现蓝牙广播接收器
           startScan();//开始发现蓝牙
           return;
       }
       connect(bluetoothDevice);//如果有蓝牙就连接
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if(requestCode == REQUEST_DISCOVERABLE){
                return;
            }
    }

    @Override
    public void disConnect() {

    }

    @Override
    public void destroy() {

        unRegisterDiscoveryReceiver(context);
    }

    @Override
    public boolean isConnected() {
        if(connectedThread!=null&&connectedThread.isConnected()){
            return true;
        }
        return false;
    }

    @Override
    public void sendMessage(String msg) {
        write(msg);
    }

    @Override
    public void setOnMessageLisenter(ICommunicate.onMessageLisenter onMessageLisenter) {
        this.lisenter = onMessageLisenter;
    }


    private BluetoothDevice findBlueToothDeviceByName(String name){
        Set<BluetoothDevice> devices = getPairedDevices();
        for (BluetoothDevice device:devices){
            if(device.getName().equals(name)){
                return device;
            }
        }
        return null;
    }



    private class AlertListView {
        private AlertDialog alertDialog1;

        private MyAdapter simpleAdapter;
        private AlertDialog.OnClickListener onClickListener;
        private List list;

        public AlertListView(Context context, List list, AlertDialog.OnClickListener onClickListener) {
            this.list = list;
            simpleAdapter = new MyAdapter(list, context);
            if (alertDialog1 == null) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setTitle("已发现的蓝牙");
                alertBuilder.setAdapter(simpleAdapter, onClickListener);
                alertDialog1 = alertBuilder.create();
                alertDialog1.getListView();
            }
        }


        public void show() {
            if (alertDialog1 == null) {
                return;
            }
            alertDialog1.show();
        }


        public void notifyDataSetChanged() {
            simpleAdapter.notifyDataSetChanged();
        }


        public class MyAdapter extends BaseAdapter {
            private List<BluetoothDevice> Datas;
            private Context mContext;

            public MyAdapter(List<BluetoothDevice> datas, Context mContext) {
                Datas = datas;
                this.mContext = mContext;
            }

            @Override
            public int getCount() {
                return Datas.size();
            }

            @Override
            public Object getItem(int i) {
                return Datas.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(final int i, View view, ViewGroup viewGroup) {
                view = LayoutInflater.from(mContext).inflate(R.layout.list_item, viewGroup, false);
                TextView nameView = view.findViewById(R.id.text);
                TextView pairView = view.findViewById(R.id.pair);

                if (Datas.get(i).getBondState() == BluetoothDevice.BOND_BONDED) {
                    nameView.setText(Datas.get(i).getName() + "(已配对)");
                    pairView.setText("");
                } else {
                    nameView.setText(Datas.get(i).getName() + "(未配对)");
                    pairView.setText("配对");
                    pairView.setTextColor(mContext.getResources().getColor(android.R.color.holo_blue_bright));
                    pairView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ToastUtils.showShort("配对");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                if (Datas.get(i).createBond()) {
                                    ToastUtils.showLong("正在配对，请稍等...");
                                } else {
                                    ToastUtils.showLong("配对失败");
                                }

                            }
                        }
                    });
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showShort("正在连接，请稍候...");
                        connect(Datas.get(i));
                    }
                });

                // 此处需要返回view 不能是view中某一个
                return view;
            }
        }
    }

    //检查蓝牙是否可用
    public boolean isAvailable() {
        if (mBluetoothAdapter == null) {
            return false;
        }
        return true;
    }

    /*   系统将显示对话框，请求用户允许启用蓝牙。如果用户响应“Yes”，系统将开始启用蓝牙，
        并在该过程完成（或失败）后将焦点返回到您的应用。
        传递给 startActivityForResult() 的 REQUEST_ENABLE_BT必须大于 0，
        系统会将其作为 requestCode 参数传递回您的 onActivityResult() 实现。
        如果成功启用蓝牙，您的 Activity 将会在 onActivityResult() 回调中收到 RESULT_OK 结果代码。
        如果由于某个错误（或用户响应“No”）而没有启用蓝牙，则结果代码为 RESULT_CANCELED。*/
    public void enableBluetooth( ) {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
             if(context instanceof  Activity){
                 Activity act = (Activity) context;
                 act.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

             }

        }
      /*  if (!mBluetoothAdapter.isEnabled()) {
            //若没打开则打开蓝牙
            mBluetoothAdapter.enable();

        }*/
    }

    public void connect(BluetoothDevice device) {
        new ConnectThread(device).start();
    }

    //查询配对的设备
    public Set<BluetoothDevice> getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        return pairedDevices;
    }

    public void registerDiscoveryReceiver( ) {
        // Create a BroadcastReceiver for ACTION_FOUND

        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    Message message = handler.obtainMessage();
                    message.obj = device;
                    message.what = MESSAGE_FOUND_DEVICE;
                    handler.sendMessage(message);
                }
                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    ToastUtils.showShort("ACTION_BOND_STATE_CHANGED");
                    Log.e("xiong", "ACTION_BOND_STATE_CHANGED");
                    ToastUtils.showLong("配对成功");
                    for (int i = 0; i < devices.size(); i++) {
                        if (devices.get(i).getAddress().equals(device.getAddress())) {
                            devices.set(i, device);
                            alertListView.notifyDataSetChanged();
                        }
                    }
                }
                if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                    ToastUtils.showLong("ACTION_PAIRING_REQUEST");
                }

            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }

    public void startScan() {
        mBluetoothAdapter.startDiscovery();
    }

    public void unRegisterDiscoveryReceiver(Context context) {
        context.unregisterReceiver(mReceiver);
    }

    /*   如果您希望将本地设备设为可被其他设备检测到，请使用 ACTION_REQUEST_DISCOVERABLE 操作 Intent
       调用 startActivityForResult(Intent, int)。 这将通过系统设置发出启用可检测到模式的请求（无需停止您的应用）
       默认情况下，设备将变为可检测到并持续 120 秒钟。
       您可以通过添加 EXTRA_DISCOVERABLE_DURATION Intent Extra 来定义不同的持续时间。
       应用可以设置的最大持续时间为 3600 秒，值为 0 则表示设备始终可检测到。
       任何小于 0 或大于 3600 的值都会自动设为 120 秒*/
    public void requestDiscoverable( ) {

        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        if(context instanceof Activity){
            Activity act  = (Activity) context;
            act.startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);
        }

    }

    public void startBluetoothServer() {
        new AcceptThread().start();
    }

    public void write(byte[] bytes) {
        connectedThread.write(bytes);
    }

    public void write(String str) {
        connectedThread.write(str.getBytes());
    }


    public void manageConnectedSocket(BluetoothSocket mmSocket) {
        connectedThread = new ConnectedThread(mmSocket);
        connectedThread.start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    Log.e("xiong", "accept   bluetooth socket:" + socket.getRemoteDevice().getName());
                    lisenter.onConnect(socket.getRemoteDevice().getName(), onMessageLisenter.BT_ACCEPTED);
                    manageConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);

            } catch (IOException e) {

            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                if (!mmSocket.isConnected()) {
                    mmSocket.connect();
                }else{
                    lisenter.onConnect(mmDevice.getName(), onMessageLisenter.BT_CONNECTED);
                }

                String tip = mmDevice.getName() + " connect success";
             //   ToastUtils.showLong(tip);
                Log.e("xiong", mmDevice.getName() + " connect success");
                lisenter.onConnect(mmDevice.getName(), onMessageLisenter.BT_CONNECTED);

            } catch (IOException connectException) {

                lisenter.onDisConnect(connectException.getMessage());
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }
            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;


        public boolean isConnected(){
            if(mmSocket!=null&&mmSocket.isConnected()){
                return true;
            }
            return false;
        }

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;

            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    Message msg = handler.obtainMessage();
                    msg.what = MESSAGE_READ;
                    Bundle bundle = new Bundle();
                    bundle.putString("name",mmSocket.getRemoteDevice().getName());
                    bundle.putByteArray("content",buffer);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }



}
