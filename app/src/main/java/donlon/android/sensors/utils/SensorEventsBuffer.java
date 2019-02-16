package donlon.android.sensors.utils;

import android.hardware.SensorEvent;

import java.util.ArrayList;
import java.util.List;

public class SensorEventsBuffer {
  private List<SensorEventAggregation> mBuffer;
  private int mCurrentPosition = 0;

  private int mCount = 0;

  private int mLastFrameSize = 0;
  public SensorEventsBuffer(int dataDimension) {
    this(100, dataDimension);
  }

  public SensorEventsBuffer(int initialCapacity, int dataDimension) {
    mBuffer = new ArrayList<>(initialCapacity);
    int d = initialCapacity;
    while (d-- > 0) {
      mBuffer.add(new SensorEventAggregation(dataDimension));
    }
  }

  public void add(SensorEvent event) {
    add(event.values, event.accuracy, event.timestamp);
  }

  public void add(float values[], float accuracy, long timeStamp) {
    if (mCurrentPosition < mBuffer.size()) {
      mBuffer.get(mCurrentPosition).set(values, accuracy, timeStamp);
    } else {
      mBuffer.add(new SensorEventAggregation(values, accuracy, timeStamp));
    }
    mCurrentPosition++;
    mCount++;
  }

  public int getCount() {
    return mCount;
  }

  public SensorEventAggregation get(int i) {
    return mBuffer.get(i);
  }

  public void clear() {
    mLastFrameSize = mCurrentPosition;
    mCurrentPosition = 0;
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