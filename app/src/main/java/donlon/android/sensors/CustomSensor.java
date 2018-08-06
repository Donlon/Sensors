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
public class CustomSensor {
  public int id = -1;
  public String sensorName;
  public String data;
  public String sensorInfo;

  public String m_dataUnitSuffix;

  public SensorsListAdapter.SensorListWidgets correlatedPreviewingListWidgets;

  public int dataDimension;

  @Deprecated
  public SensorStates state = SensorStates.Resting;
  public boolean enabled = false;

  private Sensor m_sensor;
  private SensorDataQueue dataQueue; //TODO: Recording manager

  private static int DATA_QUEUE_SAMPLES_COUNT = 256;

  public CustomSensor(Sensor sensor){
    m_sensor = sensor;
    dataDimension = SensorUtils.getSensorDataDimension(sensor.getType());
    dataQueue = new SensorDataQueue(DATA_QUEUE_SAMPLES_COUNT, dataDimension);
    m_dataUnitSuffix = " " + SensorUtils.getDataUnit(sensor.getType());

    sensorName = sensor.getName();
    LOG.d(sensorName);
    sensorInfo = "Info";
    data = "data";
  }

  public Sensor getSensorObject(){
    return m_sensor;
  }
}
