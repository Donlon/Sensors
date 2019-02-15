package donlon.android.sensors;

import android.hardware.SensorEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import donlon.android.sensors.activities.RecordingActivity;
import donlon.android.sensors.utils.DataFileWriter;
import donlon.android.sensors.utils.Logger;
import donlon.android.sensors.utils.SensorEventsBuffer;

public class RecordingManager implements SensorEventCallback {
  //  private Context mContext;

  private SensorController mSensorController;
  private Map<CustomSensor, SensorEventsBuffer> mDataBufferMap;
  private Map<CustomSensor, SensorEventCounter> mSensorEventHitsCountsMap;// TODO: try AtomicInteger?

  private boolean mIsRecording;
  private boolean mInitialized;

  private String mDataFilePath;

  private int[] selectedSensors;
  private Set<CustomSensor> sensorsToRecord;

  private RecordingManager(SensorController sensorController) {
    mSensorController = sensorController;
    mIsRecording = false;
    mInitialized = false;
  }

  /**
   * SingleTon creator
   *
   * @return instance created
   */
  public static RecordingManager create(SensorController sensorController) {
    return singleTonInstance = new RecordingManager(sensorController);
  }

  /**
   * SingleTon instance
   */
  private static RecordingManager singleTonInstance;

  public static /*synchronized */RecordingManager getInstance() {
    if (null == singleTonInstance) {
      Logger.is("");
    }
    return singleTonInstance;
  }

  private DataFileWriter mDataFileWriter;

  private RecordingActivity.RecordingDashBoardViewHolder mWidgetsEditor;
  public void setDataFilePath(String path) {
    mDataFilePath = path;
  }

  public String getDataFilePath() {
    return mDataFilePath;
  }

  public void setSelectedSensors(int[] selectedSensors) {
    this.selectedSensors = selectedSensors;
  }

  public void init() {
    sensorsToRecord = new HashSet<>();
    mDataBufferMap = new HashMap<>();
    mSensorEventHitsCountsMap = new HashMap<>();//TODO: test ArrayMap
    if (selectedSensors == null) {
      throw new IllegalArgumentException();
    }
    for (int pos : selectedSensors) {
      CustomSensor sensor = mSensorController.get(pos);
      mDataBufferMap.put(sensor, new SensorEventsBuffer(500));//TODO: diversity
      mSensorEventHitsCountsMap.put(sensor, new SensorEventCounter());
      sensorsToRecord.add(sensor);
      //TODO: differences between Queues
    }

    mSensorEventHitsCountsMap.put(null, new SensorEventCounter());

    mDataFileWriter = new DataFileWriter(mDataBufferMap);
    mDataFileWriter.setDataFilePath(mDataFilePath);
    if (mDataFileWriter.init()) {
      mInitialized = true;
    }
  }

  public void setWidgetEditor(RecordingActivity.RecordingDashBoardViewHolder widgetsEditor) {
    //    if(widgetsEditor == null){
    if (mWidgetsEditor != null) {
      mWidgetsEditor.removeRunnable(uiTimeUpdateRunnable);
      mWidgetsEditor.removeRunnable(uiContinuousUpdateRunnable);
    }
    //    }
    mWidgetsEditor = widgetsEditor;
    if (isRecording() && widgetsEditor != null) {//resume update
      uiTimeUpdateRunnable.run();
      uiContinuousUpdateRunnable.run();//TODO: Stop it by clearQueue when finished
    }
  }

  public void startRecording() {
    mSensorController.disableAllSensors();

    for (Map.Entry<CustomSensor, SensorEventsBuffer> entry : mDataBufferMap.entrySet()) {
      mSensorController.enableSensor(entry.getKey());
    }

    mIsRecording = true;
    mDataWritingThread = new Thread(mDataWritingRunnable);
    mDataWritingThread.start();

    uiTimeUpdateRunnable.run();
    uiContinuousUpdateRunnable.run();//TODO: Stop it by clearQueue when finished
  }

  // TODO: use listeners respectively
  // TODO: run on new thread
  @Override
  public void onSensorChanged(CustomSensor sensor, SensorEvent event) {
    synchronized (mDataFileWriter) {
      mDataBufferMap.get(sensor).add(event);
    }
    mSensorEventHitsCountsMap.get(sensor).raise();
    mSensorEventHitsCountsMap.get(null).raise();// Count for all sensors
    //TODO: Maybe sync needed
  }

  public void stopRecording() {
    mSensorController.disableAllSensors();
    mDataWritingThread.interrupt();//TODO: use "stop?"
    try {
      mDataFileWriter.closeFile();
    } catch (IOException e) {
      Logger.e(e.toString());
    }
    mIsRecording = false;
    mInitialized = false;
  }

  /**
   * A thread that save the data to file continuously.
   */
  private Thread mDataWritingThread;

  private Runnable mDataWritingRunnable = new Runnable() {
    @Override
    public void run() {
      updateUiOnFrame();
      try {
        while (mIsRecording) {
          Thread.sleep(403);//TODO: write this delay to data file as metadata
          //Write frame
          mDataFileWriter.flush();
          updateUiOnFrame();
          for (Map.Entry<CustomSensor, SensorEventCounter> entry : mSensorEventHitsCountsMap.entrySet()) {
            entry.getValue().makeAsFrame();
          }
        }
      } catch (InterruptedException e) {
        Logger.i("Thread Interrupted");
      } catch (IOException e) {
        if (mOnRecordingFailedListener != null) {
          mOnRecordingFailedListener.onRecordingFailed();
        }
      }
      updateUiOnFrame();
    }
  };

  private void updateUiOnFrame() {
    if (mWidgetsEditor != null) {
      mWidgetsEditor.runOnUiThread(uiOnFrameUpdateRunnable);
    }
  }

  /**
   * Triggered each frame (each writing), indirectly called from DataWritingThread
   */
  private Runnable uiOnFrameUpdateRunnable = new Runnable() {
    @Override
    public void run() {
      if (mWidgetsEditor != null) {//weird
        mWidgetsEditor.tvWrittenBytes.setText(DataFileWriter.formatBytes(mDataFileWriter.getWrittenBytes()));
        mWidgetsEditor.tvWrittenFrames.setText(String.valueOf(mDataFileWriter.getWrittenFrames()));
        mWidgetsEditor.ivWritingFlashLight.setImageResource(android.R.drawable.star_big_on);

        for (Map.Entry<CustomSensor, SensorEventCounter> entry : mSensorEventHitsCountsMap.entrySet()) {
          mWidgetsEditor.listTvLastHits.get(entry.getKey()).setText(String.valueOf(entry.getValue().getIncrement()));
        }

        mWidgetsEditor.ivWritingFlashLight.postDelayed(new Runnable() {
          @Override
          public void run() {
            mWidgetsEditor.ivWritingFlashLight.setImageResource(android.R.drawable.star_off);
            //TODO: what if ivWritingFlashLight is destroyed now?
          }
        }, 100);
      }
    }
  };

  /**
   * Quickly triggered Runnable, called from UI Thread
   */
  private Runnable uiContinuousUpdateRunnable = new Runnable() {
    @Override
    public void run() {
      if (mWidgetsEditor != null) {//weird
        for (Map.Entry<CustomSensor, SensorEventCounter> entry : mSensorEventHitsCountsMap.entrySet()) {
          mWidgetsEditor.listTvAllHits.get(entry.getKey()).setText(String.valueOf(entry.getValue().get()));
        }
        mWidgetsEditor.runOnUiThread(this, 33);
      }
    }
  };

  /**
   * Called from UI Thread
   */
  private Runnable uiTimeUpdateRunnable = new Runnable() {
    private int time = 0;
    private static final String C1 = " ";
    private static final String C2 = ":";
    private boolean parity;

    StringBuilder builder = new StringBuilder();

    private void appendTimeChars(StringBuilder builder, int t) {
      if (t < 10) {
        builder.append('0');
        builder.append(t);
      } else {
        builder.append(t);
      }
    }

    @Override
    public void run() {
      if (mWidgetsEditor != null) {//weird
        mWidgetsEditor.runOnUiThread(this, 500);


        appendTimeChars(builder, time / 3600);
        builder.append(parity ? C1 : C2);
        appendTimeChars(builder, (time % 3600) / 60);
        builder.append(parity ? C1 : C2);
        appendTimeChars(builder, time % 60);

        builder.append(" (Estimated)");

        mWidgetsEditor.tvElapsedTime.setText(builder.toString());
        builder.setLength(0);

        if (parity) {
          time++;
        }

        parity = !parity;
      }
    }
  };

  /**
   * Give a callback to the caller activity
   */
  @Deprecated
  public void finish() {
    //TODO: release resources
  }

  /**
   * OnRecordingFailedListener
   */
  private OnRecordingFailedListener mOnRecordingFailedListener;

  public void setOnRecordingFailedListener(OnRecordingFailedListener listener) {
    mOnRecordingFailedListener = listener;
  }

  public interface OnRecordingFailedListener {
    void onRecordingFailed();
  }

  public Set<CustomSensor> getSensorsToRecord() {
    return sensorsToRecord;
  }

  public boolean isRecording() {
    return mIsRecording;
  }

  public boolean initialized() {
    return mInitialized;
  }

  public class SensorEventCounter {
    int count = 0;
    int countOnLastFrame = 0;
    int lastFrameSize = 0;
    byte[] mLock;

    SensorEventCounter() {
      mLock = new byte[0];
    }

    void raise() {
      //      synchronized(mLock)
      count++;
    }

    int get() {
      return count;
    }

    void makeAsFrame() {
      lastFrameSize = count - countOnLastFrame;
      countOnLastFrame = count;
    }

    int getIncrement() {
      return lastFrameSize;
    }
  }
}
