package com.example.messengerdemo.messenger2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.messengerdemo.R;
/**
 * Created by didiwei on 2022/5/9
 * desc: 一个简单的Messenger使用实例，在这个例子中，服务端可以返回给客户端数据
 */
public class MainActivity2 extends AppCompatActivity implements View.OnClickListener{
    Button bindService;
    Button sendToService;
    ServiceConnection serviceConnection;
    Messenger clientMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bindService = findViewById(R.id.btn_bindService);
        sendToService = findViewById(R.id.btn_sendToService);
        bindService.setOnClickListener(this::onClick);
        sendToService.setOnClickListener(this::onClick);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //连接成功
                Log.v("ljh","这是在Client进程，Service连接成功");

                clientMessenger = new Messenger(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //断开连接
                Log.v("ljh","这是在Client进程，Service断开连接");
            }
        };
    }

    //注意，这里是客户端新增的handler，这个handler用来处理从service返回的数据
    static class FromServiceHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MsgWhatConstant2.SERVICE_BACK_CLIENT:
                    //msg为从service返回的msg
                    Bundle data = msg.getData();
                    String reply = data.getString("reply");
                    Log.v("ljh","这里是客户端，从service返回的reply为："+ reply);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_bindService:
                bindService(new Intent(MainActivity2.this,MyService2.class),serviceConnection, Context.BIND_AUTO_CREATE);
                break;
            case R.id.btn_sendToService:
                Bundle bundle = new Bundle();
                bundle.putString("name","hahahaha");

                //注意，这里要创建messenger，赋值给replyTo字段，用于service返回数据
                Messenger messenger = new Messenger(new FromServiceHandler());

                Message message = Message.obtain(null,MsgWhatConstant2.CLIENT_TO_SERVICE);

                message.setData(bundle);
                message.replyTo = messenger;

                try {
                    clientMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }
}