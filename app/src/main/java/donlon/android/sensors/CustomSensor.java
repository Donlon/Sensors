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

  public SensorsListAdapter.SensorListWidgets correlatedPreviewingListWidgets;
  public SensorDetailsActivity.SensorDetailsActivityWidgets correlatedDetailsWndWidgets;


  public int m_dataDimension;

  public SensorStates state = SensorStates.Resting;
  public boolean enabled = false;

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

  StringBuilder mTmpStr = new StringBuilder();

  public SensorEventListener listener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
//      MainActivity.log(sensorName+": "+event.timestamp);
      switch (state){//TODO: give control to files respectively
        case Previewing:
          if(correlatedPreviewingListWidgets != null){
            if(m_dataDimension == 0){
              return;
            }
            mTmpStr.delete(0, mTmpStr.length());

            for(int i = 0; i < m_dataDimension; i++){
              mTmpStr.append(String.valueOf(event.values[i]));
              if(i != m_dataDimension - 1){
                mTmpStr.append("\n");
              }
            }
            correlatedPreviewingListWidgets.tvData.setText(mTmpStr.toString());
          }else{
            LOG.w("Unexpected branch");
          }
          // Feedback to UI
          break;
        case Capturing:
        case Viewing:
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
