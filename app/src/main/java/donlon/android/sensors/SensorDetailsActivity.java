package donlon.android.sensors;

import android.content.pm.ActivityInfo;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.widget.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.hardware.*;

import donlon.android.sensors.utils.LOG;

public class SensorDetailsActivity extends AppCompatActivity   {
  private SensorManager sensorManager;
  private CustomSensor mSensor;
  private Sensor sensorInternal;

  private Button btnBack;
  private Button btnRecode;
  private Button btnPause;
  private TextView textHead;
  private TextView textHead2;
  private TextView textValues;

  private void initializeUi(){
    setContentView(R.layout.sensor_details_activity);
//    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

    textHead   = findViewById(R.id.tvHead);
    textHead2  = findViewById(R.id.tvHead2);
    textValues = findViewById(R.id.tvValues);

    btnBack   = findViewById(R.id.btnBack);
    btnRecode = findViewById(R.id.bthRecode);
    btnPause  = findViewById(R.id.butnPause);

    btnBack.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
        mSensor.state = SensorStates.Previewing;
        Toast.makeText(getApplicationContext(), "TODO.", Toast.LENGTH_SHORT).show();
      }
    });

    btnRecode.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getApplicationContext(), "TODO.", Toast.LENGTH_SHORT).show();
      }
    });


    android.support.v7.app.ActionBar actionBar = getSupportActionBar();
    if(actionBar != null){
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /*btnPause.setOnClickListener(new OnClickListener() {
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
    });*/
  }

  private void initializeSensor(){

    int sensorPos = getIntent().getIntExtra("SensorPos", -1);

    if(sensorPos >= MainActivity.sensorsManager.getSensorList().size()){
      LOG.w("Sensor position unexpectedly wrong");
      finish();
      return;
    }

    mSensor = MainActivity.sensorsManager.getSensorList().get(sensorPos);// TODO: fashion singleton
    mSensor.state = SensorStates.Viewing;
    mSensor.correlatedDetailsWndWidgets = new SensorDetailsActivityWidgets();
    mSensor.correlatedDetailsWndWidgets.textHead = textHead;
    mSensor.correlatedDetailsWndWidgets.textHead2 = textHead2;
    mSensor.correlatedDetailsWndWidgets.textValues = textValues;


    sensorInternal = mSensor.getSensorObject();

    textHead.setText(SensorUtils.getSensorNameByType(sensorInternal.getType()));
    textHead2.setText(sensorInternal.getName() + " By " + sensorInternal.getVendor());

//    TextView sensorName = (TextView) findViewById(R.id.textViewSensorName);
//    sensorName.setText(position + "  "+nameList[position]);
//
//    TextView sensorData = (TextView) findViewById(R.id.sensorData);
//    sensorData.setText(sensorList.get(position).str+"\n"+sensorList.get(position).lastEvent.timestamp);

  }

  //TODO: recycling
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    initializeUi();
    initializeSensor();
  }

  @Override
  public void onStop() {
    super.onStop();

    mSensor.state = SensorStates.Previewing;
    mSensor.correlatedDetailsWndWidgets = null;

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }


  private boolean mViewingPaused = false;
  private boolean mRecording = false;

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menuPause:
        if(mViewingPaused){
          item.setTitle(R.string.pause);
        }else{
          item.setTitle(R.string.start);
        }
        mViewingPaused = !mViewingPaused;
        break;
      case R.id.menuRecord:
        if(mRecording){
          item.setTitle(R.string.stop_recording);
        }else{
          item.setTitle(R.string.record);
        }
        mRecording = !mRecording;
        break;
      case android.R.id.home:
        finish(); // back button
        break;
      default:
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  public class SensorDetailsActivityWidgets{
    public TextView textHead;
    public TextView textHead2;
    public TextView textValues;
  }
}