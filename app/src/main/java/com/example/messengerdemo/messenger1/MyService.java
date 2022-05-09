package com.example.messengerdemo.messenger1;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.messengerdemo.messenger1.MsgWhatConstant;

/**
 * Created by didiwei on 2022/5/9
 * desc: 服务端
 */
public class MyService extends Service {
    Messenger messenger;

    static class FromClientHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MsgWhatConstant.CLIENT_TO_SERVER:
                    //显示客户端传来的字符串
                    Bundle data = msg.getData();
                    String name = data.getString("name");
                    Log.v("ljh","这是在Service进程，收到的来自Client的name为:" + name);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        messenger = new Messenger(new FromClientHandler());
        return messenger.getBinder();
    }
}