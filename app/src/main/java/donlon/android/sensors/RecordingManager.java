package donlon.android.sensors;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.support.v7.app.AlertDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import donlon.android.sensors.activities.RecordingActivity;
import donlon.android.sensors.utils.DataFileWriter;
import donlon.android.sensors.utils.LOG;
import donlon.android.sensors.utils.SensorEventsBuffer;
import donlon.android.sensors.utils.SensorUtils;

public class RecordingManager implements SensorEventCallback {
//  private Context mContext;
  private SensorsManager mSensorsManager;
  private String[] sensorNameList;
  private Map<CustomSensor, SensorEventsBuffer> mDataBufferMap;
  private Map<CustomSensor, Integer> mSensorEventHitCountsMap;// TODO: try AtomicInteger?

  private RecordingManager(SensorsManager sensorsManager){
    mSensorsManager = sensorsManager;
    sensorNameList = new String[sensorsManager.getSensorList().size()];
    int i = 0;
    for(CustomSensor sensor : sensorsManager.getSensorList()){
      sensorNameList[i++] = SensorUtils.getSensorEnglishNameByType(sensor.getSensorObject().getType());
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
    mDataBufferMap = new HashMap<>();
    mSensorEventHitCountsMap = new HashMap<>();//TODO: test ArrayMap

    for(int i = 0; i < mSensorsManager.getSensorList().size(); i++){
      if(selectedSensors[i]){
        mDataBufferMap.put(mSensorsManager.getSensorList().get(i),
                new SensorEventsBuffer(500));//TODO: diversity
        mSensorEventHitCountsMap.put(mSensorsManager.getSensorList().get(i), 0);
        //TODO: differences between Queues
      }
    }

    mDataFileWriter = new DataFileWriter(mDataBufferMap);
    return mDataFileWriter.init();
  }

  public void startRecording(){
    mSensorsManager.clearCallbacksForAllSensors();

    for (Map.Entry<CustomSensor, SensorEventsBuffer> entry : mDataBufferMap.entrySet()) {
      mSensorsManager.registerCallbackForSensor(entry.getKey(), this);
    }

    mIsRecording = true;
    mDataWritingThread = new Thread(mDataWritingRunnable);
    mDataWritingThread.start();
  }


  // TODO: use listeners respectively
  // TODO: run on new thread
  @Override
  public void onSensorChanged(CustomSensor sensor, SensorEvent event) {
    synchronized (mDataFileWriter.dataBufferLock){
      mDataBufferMap.get(sensor).add(event);
//      LOG.i("RRR  "+event.values[0]+", "+event.values[1]+", "+event.values[2]);
    }
//    mSensorEventHitCountsMap.put() = mSensorEventHitCountsMap.get(sensor) + 1;
    //TODO: Maybe sync needed
  }

  public void stopRecording(){
    mSensorsManager.clearCallbacksForAllSensors();
    mDataWritingThread.interrupt();//TODO: use "stop?"
    try {
      mDataFileWriter.closeFile();
    } catch (IOException e) {
      LOG.e(e.toString());
    }
  }

  private boolean mIsRecording;

  /**
   * A thread that save the data to file continuously.
   */
  private Thread mDataWritingThread;

  private Runnable mDataWritingRunnable = new Runnable() {
    @Override
    public void run() {
      try {
        while(mIsRecording){
          Thread.sleep(1000);
          //Write frame
          mDataFileWriter.flush();
        }
      } catch (InterruptedException e) {
        LOG.i("Thread Interrupted");
      } catch (IOException e){
        if(mOnRecordingFailedListener != null) {
          mOnRecordingFailedListener.onRecordingFailed();
        }
      }
    }
  };

  /**
   * Give a callback to the caller activity
   */
  public void finish(){
    mIsRecording = false;
    //TODO: release resources
  }

  /**
   * OnRecordingFinishedListener
   */
  private OnRecordingCanceledListener mOnRecordingCanceledListener;

  public void setOnRecordingCanceledListener(OnRecordingCanceledListener listener){
    mOnRecordingCanceledListener = listener;
  }

  public interface OnRecordingCanceledListener{
    void onRecordingCanceled(boolean succeed);
  }

  /**
   * OnRecordingFailedListener
   */
  private OnRecordingFailedListener mOnRecordingFailedListener;

  public void setOnRecordingFailedListener(OnRecordingFailedListener listener){
    mOnRecordingFailedListener = listener;
  }

  public interface OnRecordingFailedListener{
    void onRecordingFailed();
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
                if(mOnRecordingCanceledListener != null){
                  mOnRecordingCanceledListener.onRecordingCanceled(false);
                }
              }
            })
            .show();
  }

  public boolean isRecording(){
    return mIsRecording;
  }
}
