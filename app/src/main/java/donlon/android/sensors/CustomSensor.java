package donlon.android.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
/*
  String sensorName();
  int dataCounts();
  String dataUnitStr();

  String data();
  String sensorInfo();*/
public class CustomSensor {
  public String sensorName;
  public String data;
  public String sensorInfo;

  public SensorStates state = SensorStates.Resting;
  private Sensor m_sensor;
  private SensorEvent lastEvent;
  private SensorDataQueue dataQueue;

  public CustomSensor(Sensor sensor){
    m_sensor = sensor;

    dataQueue = new SensorDataQueue(256,1);

    sensorName = sensor.getName();
    MainActivity.log(sensorName);
    sensorInfo = "Info";
    data = "data";
  }

  public SensorEventListener listener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      MainActivity.log(sensorName+": "+event.timestamp);
      switch (state){
        case Previewing:
          break;
        case Capturing:
          break;
      }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
  };
  public Sensor getSensor(){
    return m_sensor;
  }
}
