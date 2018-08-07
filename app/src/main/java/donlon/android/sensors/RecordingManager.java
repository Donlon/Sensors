package donlon.android.sensors;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.support.v7.app.AlertDialog;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import donlon.android.sensors.activities.RecordingActivity;
import donlon.android.sensors.utils.LOG;
import donlon.android.sensors.utils.SensorUtils;

public class RecordingManager implements SensorEventCallback {

//  private Context mContext;
  private SensorsManager mSensorsManager;
  private String[] sensorNameList;
  private Map<CustomSensor, ArrayList<SensorEvent>> mDataCacheMap;

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
      LOG.printStack("");
    }
    return singleTonInstance;
  }


  boolean[] selectSensors = null;
  public void showStarterDialog(final Activity activity){
    selectSensors = new boolean[mSensorsManager.getSensorList().size()];
    AlertDialog alertDialog = new AlertDialog.Builder(activity)
            .setTitle(R.string.recording_starter_title)
            .setMultiChoiceItems(
                    sensorNameList,
                    selectSensors,
                    new DialogInterface.OnMultiChoiceClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                selectSensors[which] = isChecked;
              }
            })
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

  public boolean init(){
    mDataCacheMap = new HashMap<>();
    for(int i = 0; i < mSensorsManager.getSensorList().size(); i++){
      if(selectSensors[i]){
        mDataCacheMap.put(mSensorsManager.getSensorList().get(i),
                new ArrayList<SensorEvent>(500));
        //TODO: differences between Queues
      }
    }

    File file = new File("/sdcard/t.data");
    try {
      if(file.createNewFile()){

      }
    } catch (IOException e) {
      return false;
    }
    mDataFileFOS = new FileOutputStream(file);
    mDataFileDOS = new DataOutputStream(mDataFileFOS);

    //Write MetaInfo
    return true;
  }

  public void startRecording(){
    mSensorsManager.clearCallbacksForAllSensors();

    for (Map.Entry<CustomSensor, ArrayList<SensorEvent>> entry : mDataCacheMap.entrySet()) {
      mSensorsManager.registerCallbackForSensor(entry.getKey(), this);
    }

    mDataWritingThread = new Thread(mDataWritingRunnable);
    mDataWritingThread.start();
  }


  // TODO: use listeners respectively
  // TODO: run on new thread
  @Override
  public void onSensorChanged(CustomSensor sensor, SensorEvent event) {
    mDataCacheMap.get(sensor).add(event);
  }

  public void stopRecording(){
    mSensorsManager.clearCallbacksForAllSensors();
    mDataWritingThread.interrupt();//TODO: use "stop?"
  }

  private boolean mRecording;
  private Thread mDataWritingThread;

  FileOutputStream mDataFileFOS;
  DataOutputStream mDataFileDOS;

  /**
   * A thread that save the data to file continuously.
   */
  private Runnable mDataWritingRunnable = new Runnable() {
    @Override
    public void run() {
      try {
        while(mRecording){
            Thread.sleep(1000);
            //Write Frame

        }
      } catch (InterruptedException e) {
        e.printStackTrace();
        LOG.e(e.getMessage());
      }
    }
  };

  /**
   * Give a callback to the caller activity
   */
  public void finish(){
    //TODO: release resources
    if(mOnRecordingFinishedListener != null){
      mOnRecordingFinishedListener.onRecordingFinished(true);
    }
  }

  private OnRecordingFinishedListener mOnRecordingFinishedListener;

  public void setOnRecordingFinishedListener(OnRecordingFinishedListener listener){
    mOnRecordingFinishedListener = listener;
  }

  public interface OnRecordingFinishedListener{
    void onRecordingFinished(boolean succeed);
  }

}
