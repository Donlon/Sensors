package donlon.android.sensors.recording;

import java.io.IOException;

public interface DataFileWriter {
  boolean init();

  void flush() throws IOException;

  int getWrittenBytes();

  int getWrittenFrames();

  void closeFile() throws IOException;
}
