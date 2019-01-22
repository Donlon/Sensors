package donlon.android.sensors.utils;

public class SensorEventAggregation {
  public float values[];
  public float accuracy;
  public long timeStamp;

  SensorEventAggregation(float values[], float accuracy, long timeStamp) {
    this.values = new float[values.length];
    this.set(values, accuracy, timeStamp);
  }

  SensorEventAggregation(int dataDimension) {
    values = new float[dataDimension];
  }

  public void set(float[] values, float accuracy, long timeStamp) {
    System.arraycopy(values, 0, this.values, 0, values.length);
    this.accuracy = accuracy;
    this.timeStamp = timeStamp;
  }
}