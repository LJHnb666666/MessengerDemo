package com.example.messengerdemo.messenger1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
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
 * desc: 一个简单的Messenger使用实例，在这个例子中，服务端无法返回给客户端数据
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button bindService;
    Button sendToService;
    ServiceConnection serviceConnection;//连接Service的回调接口
    Messenger messenger;//根据Service返回的IBinder创建的Messenger
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService = findViewById(R.id.btn_bindService);
        sendToService = findViewById(R.id.btn_sendToService);
        bindService.setOnClickListener(this);
        sendToService.setOnClickListener(this);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //连接成功
                Log.v("ljh","这是在Client进程，Service连接成功");

                messenger = new Messenger(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //断开连接
                Log.v("ljh","这是在Client进程，Service断开连接");
            }
        };
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_bindService:
                //连接服务
                bindService(new Intent(MainActivity.this, MyService.class),serviceConnection,Context.BIND_AUTO_CREATE);
                break;
            case R.id.btn_sendToService:
                Bundle bundle = new Bundle();
                bundle.putString("name","hahahaha");
                Message message = Message.obtain(null, MsgWhatConstant.CLIENT_TO_SERVER);
                message.setData(bundle);

                try {
                    messenger.send(message);
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