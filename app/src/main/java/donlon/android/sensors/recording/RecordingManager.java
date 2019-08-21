package donlon.android.sensors.recording;

import java.util.List;
import java.util.Map;

import donlon.android.sensors.sensor.CustomSensor;

public interface RecordingManager {
  RecordingManager setSensorsToRecord(List<CustomSensor> sensorsToRecord);

  RecordingManager setOnNewFrameListener(Runnable onNewFrameListener);

  void init();

  void startRecording();

  void stopRecording();

  Map<CustomSensor, SensorEventsBuffer> getDataBufferMap();

  String getDataFilePath();

  void setDataFilePath(String path);

  int getWrittenFrames();

  int getWrittenBytes();

  boolean isRecording();

  boolean initialized();

  void setOnRecordingFailedListener(Runnable listener);
}
