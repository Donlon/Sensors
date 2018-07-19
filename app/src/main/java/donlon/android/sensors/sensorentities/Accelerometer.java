package donlon.android.sensors.sensorentities;

import donlon.android.sensors.SensorInterface;

public class Accelerometer implements SensorInterface {

  @Override
  public String sensorName() {
    return "加速度传感器(Accelerometer)";
  }

  @Override
  public int dataCounts() {
    return 3;
  }

  @Override
  public String dataUnitStr() {
    return "m/s2";
  }
}
