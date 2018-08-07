package donlon.android.sensors;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import donlon.android.sensors.activities.RecordingActivity;
import donlon.android.sensors.utils.DataFileWriter;
import donlon.android.sensors.utils.LOG;
import donlon.android.sensors.utils.SensorUtils;

public class RecordingManager implements SensorEventCallback {
//  private Context mContext;
  private SensorsManager mSensorsManager;
  private String[] sensorNameList;
  private final Map<CustomSensor, ArrayList<SensorEvent>> mDataCacheMap = new HashMap<>();

  private RecordingManager(SensorsManager sensorsManager){
    mSensorsManager = sensorsManager;
    sensorNameList = new String[sensorsManager.getSensorList().size()];
    int i = 0;
    for(CustomSensor sensor : sensorsManager.getSensorList()){
      sensorNameList[++i] = SensorUtils.getSensorEnglishNameByType(sensor.getSensorObject().getType());
    }
  }

  /**
   * SingleTon creator
   * @return instance created
   */
  public static RecordingManager create(SensorsManager sensorsManager){
    return singleTonInstance = new RecordingManager(sensorsManager);
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

  private DataFileWriter mDataFileWriter;

  public boolean init(){
    for(int i = 0; i < mSensorsManager.getSensorList().size(); i++){
      if(selectedSensors[i]){
        mDataCacheMap.put(mSensorsManager.getSensorList().get(i),
                new ArrayList<SensorEvent>(500));
        //TODO: differences between Queues
      }
    }
    mDataFileWriter = new DataFileWriter(mDataCacheMap);
    return mDataFileWriter.init();
  }

  public void startRecording(){
    mSensorsManager.clearCallbacksForAllSensors();

    for (Map.Entry<CustomSensor, ArrayList<SensorEvent>> entry : mDataCacheMap.entrySet()) {
      mSensorsManager.registerCallbackForSensor(entry.getKey(), this);
    }

    mRecording = true;
    mDataWritingThread = new Thread(mDataWritingRunnable);
    mDataWritingThread.start();
  }


  // TODO: use listeners respectively
  // TODO: run on new thread
  @Override
  public void onSensorChanged(CustomSensor sensor, SensorEvent event) {
    synchronized (mDataCacheMap){
      mDataCacheMap.get(sensor).add(event);
    }
  }

  public void stopRecording(){
    mSensorsManager.clearCallbacksForAllSensors();
    mDataWritingThread.interrupt();//TODO: use "stop?"
  }

  private boolean mRecording;

  private Thread mDataWritingThread;

  /**
   * A thread that save the data to file continuously.
   */
  private Runnable mDataWritingRunnable = new Runnable() {
    @Override
    public void run() {
      try {
        while(mRecording){
          Thread.sleep(1000);

          //Write frame
          mDataFileWriter.write();
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
    mRecording = false;
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

  private boolean[] selectedSensors;

  public void showStarterDialog(final Activity activity){
    selectedSensors = new boolean[mSensorsManager.getSensorList().size()];
    AlertDialog alertDialog = new AlertDialog.Builder(activity)
            .setTitle(R.string.recording_starter_title)
            .setMultiChoiceItems(
                    sensorNameList,
                    selectedSensors,
                    new DialogInterface.OnMultiChoiceClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        selectedSensors[which] = isChecked;
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
}
