package donlon.android.sensors;

import android.hardware.Sensor;

import donlon.android.sensors.adapters.SensorsListAdapter;
import donlon.android.sensors.utils.LOG;
import donlon.android.sensors.utils.SensorUtils;

/*
  String primaryName();
  int dataCounts();
  String dataUnitStr();

  String data();
  String sensorInfo();*/
public class CustomSensor {
  public int id = -1;
  public String primaryName;

  public int flag = 0;
  public static final int FLAG_3D_DATA = 1 << 2;

  private String m_dataUnitSuffix;

  public SensorsListAdapter.SensorListViewHolder correlatedPreviewingListWidgets;

  public int dataDimension;

  @Deprecated
  public SensorStates state = SensorStates.Resting;
  public boolean enabled = false;

  private Sensor m_sensor;

  public CustomSensor(Sensor sensor) {
    m_sensor = sensor;
    dataDimension = SensorUtils.getSensorDataDimension(sensor.getType());
    m_dataUnitSuffix = " " + SensorUtils.getDataUnit(sensor.getType());

    primaryName = sensor.getName();//TODO

    LOG.d(primaryName);
  }

  public Sensor getSensorObject() {
    return m_sensor;
  }

  public boolean is3DData() {
    return (flag & FLAG_3D_DATA) != 0;
  }
}
