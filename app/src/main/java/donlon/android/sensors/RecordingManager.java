package donlon.android.sensors;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.support.v7.app.AlertDialog;

import donlon.android.sensors.activities.RecordingActivity;
import donlon.android.sensors.activities.SensorDetailsActivity;
import donlon.android.sensors.utils.LOG;
import donlon.android.sensors.utils.SensorUtils;

public class RecordingManager implements SensorEventCallback {

//  private Context mContext;
  private SensorsManager mSensorsManager;
  private String[] sensorNameList;

  private RecordingManager(Context context, SensorsManager sensorsManager){
//    mContext = context.getApplicationContext();
    mSensorsManager = sensorsManager;

    sensorNameList = new String[sensorsManager.getSensorList().size()];

    for(CustomSensor sensor : sensorsManager.getSensorList()){
      sensorNameList[sensor.id] = SensorUtils.getSensorEnglishNameByType(sensor.getSensorObject().getType());
    }
  }

  /**
   * SingleTon creator
   * @param context context
   * @return instance created
   */
  public static RecordingManager create(Context context, SensorsManager sensorsManager){
    return singleTonInstance = new RecordingManager(context, sensorsManager);
  }

  /**
   * SingleTon instance
   */
  private static RecordingManager singleTonInstance;

  public static /*synchronized */RecordingManager getInstance(){
    if (null == singleTonInstance){
      LOG.w("");
    }
    return singleTonInstance;
  }


  public void showStarterDialog(final Activity activity){
    AlertDialog alertDialog = new AlertDialog.Builder(activity)
            .setTitle(R.string.recording_starter_title)
            .setMultiChoiceItems(sensorNameList,
                    null,
                    null)
            .setPositiveButton(R.string.btn_positive, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(activity, RecordingActivity.class);
                activity.startActivityForResult(intent,404);
              }
            })
            .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                if(mOnRecordingFinishedListener != null){
                  mOnRecordingFinishedListener.onRecordingFinished(false);
                }
              }
            })
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

  public void finish(){
    //TODO: release resources
    if(mOnRecordingFinishedListener != null){
      mOnRecordingFinishedListener.onRecordingFinished(true);
    }
  }

  //TODO: run on new thread
  @Override
  public void onSensorChanged(CustomSensor sensor, SensorEvent event) {

  }

  private OnRecordingFinishedListener mOnRecordingFinishedListener;

  public void setOnRecordingFinishedListener(OnRecordingFinishedListener listener){
    mOnRecordingFinishedListener = listener;
  }

  public interface OnRecordingFinishedListener{
    void onRecordingFinished(boolean succeed);
  }

}
