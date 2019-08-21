package donlon.android.sensors.recording;

import java.io.IOException;
import java.util.Map;

import donlon.android.sensors.sensor.CustomSensor;

public interface DataFileWriter {
  void setBuffer(Map<CustomSensor, SensorEventsBuffer> buffer);

  boolean init() throws IOException;

  Object acquireLockObject();

  void flush() throws IOException;

  int getWrittenBytes();

  int getWrittenFrames();

  void closeFile() throws IOException;
}
