package com.app.mycustomview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.app.mycustomview.customview.DownloadProgressBar;

public class MainActivity extends AppCompatActivity {
    private DownloadProgressBar downloadProgressBar;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            downloadProgressBar.setCurrentProgress(msg.arg1);
            if (msg.arg1==100){
                downloadProgressBar.finishLoad();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadProgressBar= (DownloadProgressBar) findViewById(R.id.pb_download);

        downloadProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!downloadProgressBar.isFinish()){
                    downloadProgressBar.toggle();
                }
            }
        });

        download();
    }

    private void download() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<100;i++){
                    try {
                        Thread.sleep(100);
                        Message message=Message.obtain();
                        message.arg1=i+1;
                        handler.sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
