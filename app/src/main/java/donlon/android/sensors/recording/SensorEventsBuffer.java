package donlon.android.sensors.recording;

import java.util.ArrayList;
import java.util.List;

public class SensorEventsBuffer {
  private List<SensorEvent> mBuffer;
  private int mCurrentPosition = 0;

  private int mCount = 0;

  private int mLastFrameSize = 0;

  SensorEventsBuffer(int dataDimension) {
    this(100, dataDimension);
  }

  private SensorEventsBuffer(int initialCapacity, int dataDimension) {
    mBuffer = new ArrayList<>(initialCapacity);
    int d = initialCapacity;
    while (d-- > 0) {
      mBuffer.add(new SensorEvent(dataDimension));
    }
  }

  void add(android.hardware.SensorEvent event) {
    add(event.values, event.accuracy, event.timestamp);
  }

  private void add(float[] values, float accuracy, long timeStamp) {
    if (mCurrentPosition < mBuffer.size()) {
      mBuffer.get(mCurrentPosition).set(values, accuracy, timeStamp);
    } else {
      mBuffer.add(new SensorEvent(values, accuracy, timeStamp));
    }
    mCurrentPosition++;
    mCount++;
  }

  SensorEvent get(int i) {
    return mBuffer.get(i);
  }

  void clear() {
    mLastFrameSize = mCurrentPosition;
    mCurrentPosition = 0;
  }

  public int getCount() {
    return mCount;
  }

  public boolean isEmpty() {
    return mCurrentPosition == 0;
  }

  public int size() {
    return mCurrentPosition;
  }

  public int getLastFrameSize() {
    return mLastFrameSize;
  }
}