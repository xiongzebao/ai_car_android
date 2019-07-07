package com.github.nkzawa.socketio.androidchat;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.fragment.app.Fragment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Stack;


/*
*
* 框架层的帮助类，应用层无法访问，BaseAppUtils暴露应用层的访问接口
*
* */
public final class BaseAppHelper {

    private Fragment fragment;
    private Stack actStack = new Stack();

    private static BaseAppHelper baseAppHelper=null;

    private BaseAppHelper(){

    }

    public  static   Activity getActivity(){
      return   BaseAppHelper.Instance().getBaseActivity();
    }

    public static BaseAppHelper  Instance(){
        if(baseAppHelper==null){
            synchronized (BaseAppHelper.class){
                if(baseAppHelper==null){
                    baseAppHelper= new BaseAppHelper();
                    return baseAppHelper;
                }
            }
        }
        return baseAppHelper;
    }


    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Activity getBaseActivity() {
        if(actStack.isEmpty()){
            return null;
        }
       Activity act= (Activity) actStack.peek();
        return act;
    }


    public Activity pushActivity(Activity act){
        return (Activity) actStack.push(act);
    }

    public  void popActivity(Activity act ){
            actStack.remove(act);
    }

    public   void closeApp(){
        for (int i=actStack.size();i>=0;i--){
            Activity act = (Activity) actStack.pop();
           if(act!=null){
               act.finish();
           }
        }
    }


    public   void forceStopAPK(){
        String pkgName = getAppProcessName();
        Process sh = null;
        DataOutputStream os = null;
        try {
            sh = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(sh.getOutputStream());
            final String Command = "am force-stop "+pkgName+ "\n";
            os.writeBytes(Command);
            os.flush();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            sh.waitFor();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    public String getAppVersion(String packname){
        //包管理操作管理类
        Context context = getActivity();
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(packname, 0);

            return packinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return packname;
    }
    public String getAppProcessName() {
        Context context = getActivity();
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }


}
