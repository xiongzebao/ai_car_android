package com.github.nkzawa.socketio.androidchat;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


import androidx.core.app.NotificationCompat;

import java.util.Random;

public class NoticeManager {
    static {
        //8.0通知栏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "exception";
            String channelName = "异常上报";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);

        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) BaseAppHelper.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

      public static void startNotification(String title, String content){
        startNotification(title,content,getRandom());
    }


    public static void startNotification(String title, String content,Class goClass){
        startNotification(title,content,getRandom(),goClass);
    }

    //开启通知
    public static void startNotification(String title, String content, int id) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startNotificationV8(title, content, MainActivity.class,id, "exception", R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        } else {
            startNotification1(title, content, id,MainActivity.class);
        }
    }

    public static void startNotification(String title, String content, int id,Class goClass) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startNotificationV8(title, content, goClass,id, "exception", R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        } else {
            startNotification1(title, content, id,goClass);
        }
    }



    //8.0以上通知Api
    private static void startNotificationV8(String title, String content,Class goClass, int id, String channelId, int smallIconDrawableId, int largeIconDrawableId) {
        Intent  intent =  new Intent(BaseAppHelper.getActivity(), NotificationClickReceiver.class);
        intent.putExtra("goClass",goClass);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(BaseAppHelper.getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager = (NotificationManager) BaseAppHelper.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(BaseAppHelper.getActivity(), channelId)
                .setContentTitle(title).setContentText(content)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(smallIconDrawableId)
//                .setLargeIcon(BitmapFactory.decodeResource(BaseAppHelper.getActivity().getResources(), largeIconDrawableId))
                .setAutoCancel(true).build();

        manager.notify(id, notification);
    }


    //8.0以下通知Api
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void startNotification1(String title, String content, int id,Class goClass) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(BaseAppHelper.getActivity());
        notifyBuilder.setContentTitle(title);
        notifyBuilder.setContentText(content);
        notifyBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notifyBuilder.setAutoCancel(true);
        notifyBuilder.setPriority(Notification.PRIORITY_HIGH);
        notifyBuilder.setAutoCancel(true);
        // 将Ongoing设为true 那么notification将不能滑动删除
        //跳转到消息界面

        Intent  intent =  new Intent(BaseAppHelper.getActivity(), NotificationClickReceiver.class);
        intent.putExtra("goClass",goClass);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(BaseAppHelper.getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notifyBuilder.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) BaseAppHelper.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null && notifyBuilder != null) {
            mNotificationManager.notify(id, notifyBuilder.build());
        }
    }

    private static int getRandom(){
        Random random = new Random();
        return random.nextInt(10);
    }

}
