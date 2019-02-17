package donlon.android.sensors.sensor;

import android.hardware.Sensor;

import donlon.android.sensors.utils.SensorUtils;

/*
  String primaryName();
  int dataCounts();
  String dataUnitStr();

  String data();
  String sensorInfo();*/
// TODO: use enumeration
public class CustomSensor {
  private static final int FLAG_3D_DATA = 1 << 2;

  private final int position;

  public String primaryName;

  private int flag;

  public int dataDimension;

  @Deprecated
  public SensorStates state = SensorStates.Resting;

  private boolean enabled = false;

  private Sensor m_sensor;

  public CustomSensor(int position, Sensor sensor) {
    this.position = position;
    m_sensor = sensor;
    dataDimension = SensorUtils.getSensorDataDimension(sensor.getType());
    //    m_dataUnitSuffix = " " + SensorUtils.getDataUnit(sensor.getType());

    primaryName = sensor.getName();//TODO

    switch (sensor.getType()) {
      case Sensor.TYPE_ACCELEROMETER:
      case Sensor.TYPE_GRAVITY:
      case Sensor.TYPE_LINEAR_ACCELERATION:
      case Sensor.TYPE_MAGNETIC_FIELD:
        flag |= CustomSensor.FLAG_3D_DATA;
      default:
        break;
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Sensor getSensor() {
    return m_sensor;
  }

  public int getPosition() {
    return position;
  }

  public boolean is3dData() {
    return (flag & FLAG_3D_DATA) != 0;
  }
}
