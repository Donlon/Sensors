package donlon.android.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

public class SensorsManager {
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

      m_sysSensorManager.registerListener(sensor.listener, sensor.getSensorObject(),
              m_previewDelay);
    }
  }

  public void stopSensorsPreview(){
    for(CustomSensor sensor : m_sensorList){
      sensor.state = SensorStates.Resting;
      m_sysSensorManager.unregisterListener(sensor.listener);
    }
  }

  public List<CustomSensor> getSensorList(){
    return m_sensorList;
  }

}
