package com.example.messengerdemo.messenger2;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

public class MyService2 extends Service {
    Messenger serviceMessenger;

    static class FromClientHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MsgWhatConstant2.CLIENT_TO_SERVICE:
                    Bundle data = msg.getData();
                    String name = data.getString("name");
                    Log.v("ljh","这里是service，从client收到的name为："+ name);

                    //注意，下面是向客户端返回消息
                    Messenger messenger = msg.replyTo;//得到客户端传来的messenger

                    Bundle replyData = new Bundle();
                    replyData.putString("reply","6666666");

                    Message message = Message.obtain(null,MsgWhatConstant2.SERVICE_BACK_CLIENT);

                    message.setData(replyData);
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        serviceMessenger = new Messenger(new FromClientHandler());
        return serviceMessenger.getBinder();
    }
}