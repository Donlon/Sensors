package donlon.android.sensors;

import android.app.*;
import android.content.pm.ActivityInfo;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.hardware.*;

public class SensorDetailsActivity extends Activity {
  private SensorManager sensorManager;
  private CustomSensor mySensor;
  private Sensor sensorInternal;

  private Button btnBack;
  private Button btnRecode;
  private Button btnPause;
  private TextView textHead;
  private TextView textHead2;
  private TextView textValues;

  private boolean previewing;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sensor_details_activity);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    textHead   = findViewById(R.id.tvHead);
    textHead2  = findViewById(R.id.tvHead2);
    textValues = findViewById(R.id.tvValues);

    btnBack   = findViewById(R.id.btnBack);
    btnRecode = findViewById(R.id.bthRecode);
    btnPause  = findViewById(R.id.butnPause);

    int sensorPos = getIntent().getIntExtra("SensorPos", -1);
    if(sensorPos >= MainActivity.sensorsManager.getSensorList().size()){
      MainActivity.log("sensor pos error.");
      finish();
      return;
    }
    mySensor = MainActivity.sensorsManager.getSensorList().get(sensorPos);// TODO: fashion singleton
    sensorInternal = mySensor.getSensorObject();

    textHead.setText(SensorUtils.getSensorNameByType(sensorInternal.getType()));
    textHead2.setText(sensorInternal.getName()
            +" By "+sensorInternal.getVendor()+", "+sensorInternal.getVersion());

//    TextView sensorName = (TextView) findViewById(R.id.textViewSensorName);
//    sensorName.setText(position + "  "+nameList[position]);
//
//    TextView sensorData = (TextView) findViewById(R.id.sensorData);
//    sensorData.setText(sensorList.get(position).str+"\n"+sensorList.get(position).lastEvent.timestamp);


    btnBack.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });

    btnRecode.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getApplicationContext(), "TODO.", Toast.LENGTH_SHORT).show();
      }
    });

    btnPause.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getApplicationContext(), "TODO.", Toast.LENGTH_SHORT).show();
        if(previewing){
          btnPause.setText("Continue");
          previewing = false;
        }else{
          btnPause.setText("Pause");
          previewing = true;
        }
      }
    });
  }
   //   textValues.setText(SensorUtils.parseData(mySensor.getType(), event.values));

}