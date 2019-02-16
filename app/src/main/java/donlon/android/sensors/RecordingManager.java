package donlon.android.sensors;

import android.hardware.SensorEvent;
import android.os.Handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import donlon.android.sensors.utils.DataFileWriter;
import donlon.android.sensors.utils.Logger;
import donlon.android.sensors.utils.SensorEventsBuffer;

public class RecordingManager {
  private SensorController mSensorController;
  private final Map<CustomSensor, SensorEventsBuffer> mDataBufferMap = new HashMap<>();

  private boolean mIsRecording;
  private boolean mInitialized;

  private String mDataFilePath;

  private List<CustomSensor> mSensorsToRecord;

  private Handler mUiHandler = new Handler();

  private Thread mDataWritingThread;

  private DataFileWriter mDataFileWriter;

  private Runnable mOnNewFrameListener;

  private Runnable mOnRecordingFailedListener;

  public RecordingManager(SensorController sensorController) {
    mSensorController = sensorController;
    mIsRecording = false;
    mInitialized = false;
  }

  public void setSensorsToRecord(List<CustomSensor> sensorsToRecord) {
    this.mSensorsToRecord = sensorsToRecord;
  }

  public void init() {
    if (mSensorsToRecord == null) {
      throw new IllegalArgumentException();
    }
    for (CustomSensor sensor : mSensorsToRecord) {
      mDataBufferMap.put(sensor, new SensorEventsBuffer(500));
    }
    mInitialized = true;
  }

  public void startRecording() {
    mDataFileWriter = new DataFileWriter(mDataFilePath, mDataBufferMap);
    if (!mDataFileWriter.init()) {
      mOnRecordingFailedListener.run(); //TODO: ...
    }
    mSensorController.disableAllSensors();

    for (Map.Entry<CustomSensor, SensorEventsBuffer> entry : mDataBufferMap.entrySet()) {
      mSensorController.enableSensor(entry.getKey());
    }

    mIsRecording = true;
    mDataWritingThread = new Thread(mDataWritingRunnable);
    mDataWritingThread.start();

    mSensorController.setOnSensorChangeListener(this::onSensorChanged);
  }

  private void onSensorChanged(CustomSensor sensor, SensorEvent event) {
    synchronized (mDataBufferMap) {
      SensorEventsBuffer buffer = mDataBufferMap.get(sensor);
      buffer.add(event);
    }
    //TODO: Maybe sync needed
  }

  private Runnable mDataWritingRunnable = new Runnable() {
    @Override
    public void run() {
      try {
        while (mIsRecording) {
          updateUiOnFrame();
          Thread.sleep(2371);//TODO: write this delay to data file as metadata
          //Write frame
          mDataFileWriter.flush();
        }
      } catch (InterruptedException e) {
        Logger.i("Thread Interrupted");
      } catch (IOException e) {
        e.printStackTrace();
        if (mOnRecordingFailedListener != null) {
          mUiHandler.post(mOnRecordingFailedListener);
        }
      }
      updateUiOnFrame();
    }
  };

  private void updateUiOnFrame() {
    if (mOnNewFrameListener != null) {
      mUiHandler.post(mOnNewFrameListener);
    }
  }

  public void stopRecording() {
    mSensorController.setOnSensorChangeListener(null);
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

  public Map<CustomSensor, SensorEventsBuffer> getDataBufferMap() {
    return mDataBufferMap;
  }

  public String getDataFilePath() {
    return mDataFilePath;
  }

  public void setDataFilePath(String path) {
    mDataFilePath = path;
  }

  public int getWrittenFrames() {
    return mDataFileWriter.getWrittenFrames();
  }

  public int getWrittenBytes() {
    return mDataFileWriter.getWrittenBytes();
  }

  public boolean isRecording() {
    return mIsRecording;
  }

  public boolean initialized() {
    return mInitialized;
  }

  public void setOnRecordingFailedListener(Runnable listener) {
    mOnRecordingFailedListener = listener;
  }

  public void setOnNewFrameListener(Runnable onNewFrameListener) {
    this.mOnNewFrameListener = onNewFrameListener;
  }
}
