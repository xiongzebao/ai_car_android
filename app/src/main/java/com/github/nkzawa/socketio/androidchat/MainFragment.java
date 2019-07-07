package com.github.nkzawa.socketio.androidchat;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;

import android.view.View;

import butterknife.BindView;
import io.socket.emitter.Emitter;


/**
 * A chat fragment containing messages view and input form.
 */
public class MainFragment extends BaseFragment {

    private static final int REQUEST_LOGIN = 0;

    @BindView(R.id.turn_left)
    public View turnLeft;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("xiong","onCreate fragment");
        if(!TextUtils.isEmpty(Constants.getUserName())){
    /*        if(!MySocketManager.getInstance().isConnected()){
                Log.e("xiong","connect");
                MySocketManager.getInstance().connect();
                MySocketManager.getInstance().on(Constants.EVENT_MSG,onNewMessage);
            }
*/
        }else {
            startSignIn();
            getActivity().finish();
        }

    }

    private void initTouchEvent(){

      /*  turnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.e("xiong","action down");
                    Controller.turnLeft();
                    return false;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    Log.e("xiong","action up");
                    Controller.stop();
                }

                return false;
            }
        });*/
    }



    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("xiong","onDestroyed");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTouchEvent();
    }



    public void onClick(View view){

       /* switch (view.getId()){
            case R.id.turn_left: Controller.turnLeft();break;
            case R.id.turn_right:
                Controller.turnRight();break;
            case R.id.forward:
                Controller.forward();break;
            case R.id.back:
                Controller.back();break;
            case R.id.stop:
                Controller.stop();break;
            case R.id.speed_up:
                Controller.speedUp();break;
            case R.id.speed_down:
                Controller.speedDown();break;
        }*/
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                //    MySocketManager.getInstance().attemptSend(new MessageEvent(""));
                    /*JSONObject data = (JSONObject) args[0];
                   String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }*/
                }
            });
        }
    };


    private void startSignIn() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }


}

