package donlon.android.sensors;

import java.util.ArrayList;
import java.util.List;

import android.app.*;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.os.*;
import android.util.Log;
import android.widget.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.graphics.*;
import android.hardware.*;

public class SensorDetailsActivity extends Activity {
  private SensorManager sensorManager;
  private Sensor mySensor;

  private Button buttonBack;
  private Button buttonRecode;
  private Button buttonPause;
  private TextView textHead;
  private TextView textHead2;
  private TextView textValues;

  private boolean status=true;//true:reflushing, false:pause
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sensor_details_activity);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    textHead   = (TextView) findViewById(R.id.SensorDetails_TextHead);
    textHead2  = (TextView) findViewById(R.id.SensorDetails_TextHead2);
    textValues = (TextView) findViewById(R.id.SensorDetails_TextValues);

    /*mySensor=MainActivity.sensorList.get(
            Integer.parseInt(getIntent().getStringExtra("SensorID"))
    ).sensor;*/

    textHead.setText(SensorUtils.getSensorNameByType(mySensor.getType()));
    textHead2.setText(mySensor.getName()+"By"+mySensor.getVendor()+", "+mySensor.getVersion());



    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


    sensorManager.registerListener(new SensorEventListener(){
      @Override
      public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
      }
      @Override
      public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        textValues.setText(SensorUtils.parseData(mySensor.getType(), event.values));
      }
    }, mySensor, SensorManager.SENSOR_DELAY_NORMAL);




	/*	TextView sensorName = (TextView) findViewById(R.id.textViewSensorName);
		sensorName.setText(position + "  "+nameList[position]);

		TextView sensorData = (TextView) findViewById(R.id.sensorData);
		sensorData.setText(sensorList.get(position).str+"\n"+sensorList.get(position).lastEvent.timestamp);
		*/



    buttonBack   = (Button) findViewById(R.id.SensorDetails_ButtonBack);
    buttonRecode = (Button) findViewById(R.id.SensorDetails_ButtonRecode);
    buttonPause  = (Button) findViewById(R.id.SensorDetails_ButtonPause);

    buttonBack.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
    buttonRecode.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getApplicationContext(), "TODO.", Toast.LENGTH_SHORT).show();
      }
    });
    buttonPause.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getApplicationContext(), "TODO.", Toast.LENGTH_SHORT).show();
        if(status){
          buttonPause.setText("Countinue");
          status=false;
        }else{
          buttonPause.setText("Pause");
          status=true;
        }
      }
    });
  }


}
/*
package com.android.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SecondActivity extends Activity {
    private Button secondBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        secondBtn=(Button)findViewById(R.id.secondBtn);
        //响应按钮secondBtn事件
        secondBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示方式声明Intent，直接启动MainActivity
                Intent intent = new Intent(SecondActivity.this,MainActivity.class);
                //启动Activity
                startActivity(intent);
            }
        });
    }
}
*/






