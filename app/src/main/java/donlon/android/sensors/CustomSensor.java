package donlon.android.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import donlon.android.sensors.utils.LOG;

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

  public String m_dataUnitSuffix;

  public SensorsListAdapter.SensorListWidgets correlatedWidgets;
  public int m_dataDimension;

  public SensorStates state = SensorStates.Resting;
  public boolean listening = false;

  private Sensor m_sensor;
  private SensorEvent lastEvent;
  private SensorDataQueue dataQueue;

  private static int DATA_QUEUE_SAMPLES_COUNT = 256;

  public CustomSensor(Sensor sensor){
    m_sensor = sensor;
    m_dataDimension = SensorUtils.getSensorDataDimension(sensor.getType());
    dataQueue = new SensorDataQueue(DATA_QUEUE_SAMPLES_COUNT, m_dataDimension);
    m_dataUnitSuffix = " " + SensorUtils.getDataUnit(sensor.getType());

    sensorName = sensor.getName();
    LOG.d(sensorName);
    sensorInfo = "Info";
    data = "data";
  }

  public SensorEventListener listener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
//      MainActivity.log(sensorName+": "+event.timestamp);
      switch (state){
        case Previewing:
          if(correlatedWidgets != null){
            if(m_dataDimension == 0){
              return;
            }

            correlatedWidgets.tvData.setText("");
            for(int i = 0; i < m_dataDimension; i++){
              correlatedWidgets.tvData.append(String.valueOf(event.values[i]));
//              correlatedWidgets.tvData.append("         AAAAAA");
              if(i != m_dataDimension - 1){
                correlatedWidgets.tvData.append("\n");
              }
            }
          }else{
            LOG.w("Unexpected branch");
            //throw new Exception("Unexpected");
          }
          // Feedback to UI
          break;
        case Capturing:
          // Note it down
          // TODO: invoke with event only
          dataQueue.push(event.timestamp, event.values, event.accuracy);
          break;
      }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
  };

  public Sensor getSensorObject(){
    return m_sensor;
  }
}
