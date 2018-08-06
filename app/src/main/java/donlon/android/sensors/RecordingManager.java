package donlon.android.sensors;

import android.content.Context;
import android.hardware.SensorEvent;
import android.support.v7.app.AlertDialog;

import donlon.android.sensors.utils.SensorUtils;

public class RecordingManager implements SensorEventCallback {

  private Context mContext;
  private SensorsManager mSensorsManager;
  private String[] sensorNameList;

  public RecordingManager(Context context, SensorsManager sensorsManager){
    mContext = context;
    mSensorsManager = sensorsManager;

    sensorNameList = new String[sensorsManager.getSensorList().size()];

    for(CustomSensor sensor : sensorsManager.getSensorList()){
      sensorNameList[sensor.id] = SensorUtils.getSensorNameByType(sensor.getSensorObject().getType());
    }
  }

  public void showStarterDialog(){
    AlertDialog alertDialog = new AlertDialog.Builder(mContext)
            .setTitle(R.string.recording_starter_title)
            .setMultiChoiceItems(sensorNameList,
                    null,
                    null)
            .setPositiveButton(R.string.btn_positive,null)
            .setNegativeButton(R.string.btn_cancel,null)
            .show();
  }

  public void startRecording(){
    mSensorsManager.clearCallbacksForAllSensors();

    //for each...
    mSensorsManager.registerCallbackForSensor(null, this);
  }

  public void stopRecording(){
    mSensorsManager.clearCallbacksForAllSensors();
  }

  //TODO: run on new thread
  @Override
  public void onSensorChanged(CustomSensor sensor, SensorEvent event) {
    
  }


}
