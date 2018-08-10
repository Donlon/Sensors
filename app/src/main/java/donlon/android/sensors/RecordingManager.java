package donlon.android.sensors;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.support.v7.app.AlertDialog;
import android.util.ArraySet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import donlon.android.sensors.activities.RecordingActivity;
import donlon.android.sensors.utils.DataFileWriter;
import donlon.android.sensors.utils.LOG;
import donlon.android.sensors.utils.SensorEventsBuffer;
import donlon.android.sensors.utils.SensorUtils;

public class RecordingManager implements SensorEventCallback{
  //  private Context mContext;
  public static final int RECORDING_ACTIVITY_REQUEST_CODE = 0xF401;

  private SensorsManager mSensorsManager;
  private String[] sensorNameList;
  private Map<CustomSensor, SensorEventsBuffer> mDataBufferMap;
  private Map<CustomSensor, SensorEventCounter> mSensorEventHitsCountsMap;// TODO: try AtomicInteger?

  private boolean mIsRecording;
  private boolean mInitialized;

  private String mDataFilePath;

  private boolean[] selectedSensorsArray;
  private Set<CustomSensor> sensorsToRecord;
  private Set<CustomSensor> selectedSensors;

  private RecordingManager(SensorsManager sensorsManager){
    mSensorsManager = sensorsManager;
    sensorsToRecord = new ArraySet<>();
    selectedSensors = new ArraySet<>();
    sensorNameList = new String[sensorsManager.getSensorList().size()];
    int i = 0;
    for(CustomSensor sensor : sensorsManager.getSensorList()){
      sensorNameList[i++] = SensorUtils.getSensorEnglishNameByType(sensor.getSensorObject().getType());
    }
    mIsRecording = false;
    mInitialized = false;
  }
  /**
   * SingleTon creator
   *
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
    if(null == singleTonInstance){
      LOG.printStack("");
    }
    return singleTonInstance;
  }

  private DataFileWriter mDataFileWriter;
  private RecordingActivity.RecordingManagerWidgetsEditor mWidgetsEditor;

  public void setDataFilePath(String path){
    mDataFilePath = path;
  }

  public String getDataFilePath(){
    return  mDataFilePath;
  }

  public void init(){
    mDataBufferMap = new HashMap<>();
    mSensorEventHitsCountsMap = new HashMap<>();//TODO: test ArrayMap

    for(CustomSensor sensor : sensorsToRecord){
      mDataBufferMap.put(sensor, new SensorEventsBuffer(500));//TODO: diversity
      mSensorEventHitsCountsMap.put(sensor, new SensorEventCounter());
      //TODO: differences between Queues
    }

    mSensorEventHitsCountsMap.put(null, new SensorEventCounter());

    selectedSensors.clear();
    mDataFileWriter = new DataFileWriter(mDataBufferMap);
    mDataFileWriter.setDataFilePath(mDataFilePath);
    if(mDataFileWriter.init()){
      mInitialized = true;
    }
  }

  public void setWidgetEditor(RecordingActivity.RecordingManagerWidgetsEditor widgetsEditor){
    mWidgetsEditor = widgetsEditor;
    if(isRecording() && widgetsEditor != null){
      uiUpdateRunnable.run();
    }
  }

  public void startRecording(){
    mSensorsManager.clearCallbacksForAllSensors();

    for(Map.Entry<CustomSensor, SensorEventsBuffer> entry : mDataBufferMap.entrySet()){
      mSensorsManager.registerCallbackForSensor(entry.getKey(), this);
    }

    mIsRecording = true;
    mDataWritingThread = new Thread(mDataWritingRunnable);
    mDataWritingThread.start();

    uiUpdateRunnable2.run();//TODO: Stop it by clearQueue when finished
  }

  // TODO: use listeners respectively
  // TODO: run on new thread
  @Override
  public void onSensorChanged(CustomSensor sensor, SensorEvent event){
    synchronized(mDataFileWriter.dataBufferLock){
      mDataBufferMap.get(sensor).add(event);
    }
    mSensorEventHitsCountsMap.get(sensor).raise();
    mSensorEventHitsCountsMap.get(null).raise();// Count for all sensors
    //TODO: Maybe sync needed
  }

  public void stopRecording(){
    mSensorsManager.clearCallbacksForAllSensors();
    mDataWritingThread.interrupt();//TODO: use "stop?"
    try{
      mDataFileWriter.closeFile();
    }catch(IOException e){
      LOG.e(e.toString());
    }
    mIsRecording = false;
    mInitialized = false;
  }

  /**
   * A thread that save the data to file continuously.
   */
  private Thread mDataWritingThread;

  private Runnable mDataWritingRunnable = new Runnable(){
    @Override
    public void run(){
      updateUi();
      try{
        while(mIsRecording){
          Thread.sleep(403);//TODO: write this delay to data file as metadata
          //Write frame
          mDataFileWriter.flush();
          updateUi();
          for(Map.Entry<CustomSensor, SensorEventCounter> entry : mSensorEventHitsCountsMap.entrySet()){
            entry.getValue().makeAsFrame();
          }
        }
      }catch(InterruptedException e){
        LOG.i("Thread Interrupted");
      }catch(IOException e){
        if(mOnRecordingFailedListener != null){
          mOnRecordingFailedListener.onRecordingFailed();
        }
      }
      updateUi();
    }
  };

  private void updateUi(){
    if(mWidgetsEditor != null){
      mWidgetsEditor.runOnUiThread(uiUpdateRunnable);
    }
  }

  /**
   * Triggered each frame (each writing)
   */
  private Runnable uiUpdateRunnable = new Runnable(){
    @Override
    public void run(){
      if(mWidgetsEditor != null){//weird
        mWidgetsEditor.tvWrittenBytes.setText(
                DataFileWriter.formatBytes(mDataFileWriter.getWrittenBytes()));
        mWidgetsEditor.tvWrittenFrames.setText(
                String.valueOf(mDataFileWriter.getWrittenFrames()));
        mWidgetsEditor.ivWritingFlashLight.setImageResource(android.R.drawable.star_big_on);

        for(Map.Entry<CustomSensor, SensorEventCounter> entry : mSensorEventHitsCountsMap.entrySet()){
          mWidgetsEditor.listTvLastHits.get(entry.getKey())
                  .setText(String.valueOf(entry.getValue().getIncrement()));
        }

        mWidgetsEditor.ivWritingFlashLight.postDelayed(new Runnable(){
          @Override
          public void run(){
            mWidgetsEditor.ivWritingFlashLight.setImageResource(android.R.drawable.star_off);
            //TODO: what if ivWritingFlashLight is destroyed now?
          }
        }, 100);
      }
    }
  };

  /**
   * Quickly triggered Runnable
   */
  private Runnable uiUpdateRunnable2 = new Runnable(){
    @Override
    public void run(){
      if(mWidgetsEditor != null){//weird
        for(Map.Entry<CustomSensor, SensorEventCounter> entry : mSensorEventHitsCountsMap.entrySet()){
          mWidgetsEditor.listTvAllHits.get(entry.getKey())
                  .setText(String.valueOf(entry.getValue().get()));
        }
        mWidgetsEditor.runOnUiThread(this, 33);
      }
    }
  };

  /**
   * Give a callback to the caller activity
   */
  @Deprecated
  public void finish(){
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

  public void showStarterDialog(final Activity activity){
    selectedSensors.clear();
    selectedSensorsArray = new boolean[mSensorsManager.getSensorList().size()];
    AlertDialog alertDialog = new AlertDialog.Builder(activity)
            .setTitle(R.string.recording_starter_title)
            .setMultiChoiceItems(sensorNameList, selectedSensorsArray, new DialogInterface.OnMultiChoiceClickListener(){
      @Override
      public void onClick(DialogInterface dialog, int which, boolean isChecked){
        selectedSensorsArray[which] = isChecked;
        selectedSensors.add(mSensorsManager.getSensorList().get(which));//TODO: not advanced enough?
      }
    }).setPositiveButton(R.string.btn_positive, new DialogInterface.OnClickListener(){
      @Override
      public void onClick(DialogInterface dialog, int which){
        mInitialized = false;
        sensorsToRecord.clear();
        sensorsToRecord.addAll(selectedSensors);
        Intent intent = new Intent(activity, RecordingActivity.class);
        activity.startActivityForResult(intent, RECORDING_ACTIVITY_REQUEST_CODE);
      }
    }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener(){
      @Override
      public void onClick(DialogInterface dialog, int which){
        if(mOnRecordingCanceledListener != null){
          mOnRecordingCanceledListener.onRecordingCanceled(false);
        }
      }
    }).show();
  }

  public Set<CustomSensor> getSensorsToRecord(){
    return sensorsToRecord;
  }

  public boolean isRecording(){
    return mIsRecording;
  }

  public boolean initialized(){
    return mInitialized;
  }

  public class SensorEventCounter{
    int count = 0;
    int countOnLastFrame = 0;
    int lastFrameSize = 0;
    byte[] mLock;

    SensorEventCounter(){
      mLock = new byte[0];
    }

    void raise(){
//      synchronized(mLock)
      count++;
    }

    int get(){
      return count;
    }

    void makeAsFrame(){
      lastFrameSize = count - countOnLastFrame;
      countOnLastFrame = count;
    }

    int getIncrement(){
      return lastFrameSize;
    }
  }
}
