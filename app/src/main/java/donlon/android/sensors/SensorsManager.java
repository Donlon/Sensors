package donlon.android.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

import donlon.android.sensors.utils.LOG;

public class SensorsManager implements SensorsListAdapter.OnSensorsListCbxCheckedListener {
  private Context m_context;
  private List<CustomSensor> m_sensorList;
  private SensorManager m_sysSensorManager;

  private int m_previewDelay = 1000;
//  private int m_previewDelay = SensorManager.SENSOR_DELAY_NORMAL;

  public SensorsManager(Context context){
    m_context = context;
    m_sysSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

    List<Sensor> allSensor;
    if (m_sysSensorManager != null) {
      allSensor = m_sysSensorManager.getSensorList(Sensor.TYPE_ALL);
    }else{
      throw new NullPointerException();
    }

    m_sensorList = new ArrayList<>();
    for (Sensor s:allSensor) {
      m_sensorList.add(new CustomSensor(s));
    }

  }

  public void startSensorsPreview(){
    for(CustomSensor sensor : m_sensorList){
      sensor.state = SensorStates.Previewing;
      sensor.enabled = true;
      m_sysSensorManager.registerListener(sensor.listener, sensor.getSensorObject(),
              m_previewDelay);
    }
  }

  public void stopSensorsPreview(){
    for(CustomSensor sensor : m_sensorList){
      sensor.state = SensorStates.Resting;
      sensor.enabled = false;
      m_sysSensorManager.unregisterListener(sensor.listener);
    }
  }

  private static int MIN_UPDATE_INTERVAL = 25;
  private Runnable threadRunnable = new Runnable() {
    double lastUpdateTime;
    @Override
    public void run() {

    }
  };


  public List<CustomSensor> getSensorList(){
    return m_sensorList;
  }

  @Override
  public void OnSensorsListCbxChecked(int pos, boolean selected) {
    CustomSensor sensor = m_sensorList.get(pos);

    if(sensor.state == SensorStates.Previewing && !selected){
      m_sysSensorManager.unregisterListener(sensor.listener);
      sensor.state = SensorStates.Resting;
    }else if(sensor.state == SensorStates.Resting && selected){
      m_sysSensorManager.registerListener(sensor.listener, sensor.getSensorObject(),
              m_previewDelay);
      sensor.state = SensorStates.Previewing;
    }else{
      LOG.w("Unexpected branch");
    }
  }
}
