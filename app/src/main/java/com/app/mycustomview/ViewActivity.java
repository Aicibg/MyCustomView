package com.app.mycustomview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.mycustomview.bean.PieData;
import com.app.mycustomview.customview.PieView;

import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity {
    private PieView mPieView;
    private ArrayList<PieData> mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        mPieView= (PieView) findViewById(R.id.pieview);

        mData=new ArrayList<>();
        for(int i=0;i<5;i++){
           PieData pieData=new PieData(i*15,"item"+i);
            mData.add(pieData);
        }

        mPieView.setmDatas(mData);
    }
}
