package donlon.android.sensors;

import android.hardware.Sensor;

import donlon.android.sensors.utils.LOG;
import donlon.android.sensors.utils.SensorUtils;

/*
  String sensorName();
  int dataCounts();
  String dataUnitStr();

  String data();
  String sensorInfo();*/
public class CustomSensor{
  public int id = -1;
  public String sensorName;
  public String sensorInfo;

  private String m_dataUnitSuffix;

  public SensorsListAdapter.SensorListWidgets correlatedPreviewingListWidgets;

  public int dataDimension;

  @Deprecated
  public SensorStates state = SensorStates.Resting;
  public boolean enabled = false;

  private Sensor m_sensor;

  public CustomSensor(Sensor sensor){
    m_sensor = sensor;
    dataDimension = SensorUtils.getSensorDataDimension(sensor.getType());
    m_dataUnitSuffix = " " + SensorUtils.getDataUnit(sensor.getType());

    sensorName = sensor.getName();
    LOG.d(sensorName);
    sensorInfo = "Info";
  }

  public Sensor getSensorObject(){
    return m_sensor;
  }
}
