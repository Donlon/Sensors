package donlon.android.sensors.recording;

class SensorEvent {
  float values[];
  float accuracy;
  long timeStamp;

  SensorEvent(float values[], float accuracy, long timeStamp) {
    this.values = new float[values.length];
    this.set(values, accuracy, timeStamp);
  }

  SensorEvent(int dataDimension) {
    values = new float[dataDimension];
  }

  void set(float[] values, float accuracy, long timeStamp) {
    System.arraycopy(values, 0, this.values, 0, values.length);
    this.accuracy = accuracy;
    this.timeStamp = timeStamp;
  }
}