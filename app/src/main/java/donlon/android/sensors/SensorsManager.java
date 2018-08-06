package donlon.android.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import donlon.android.sensors.utils.LOG;

public class SensorsManager implements SensorsListAdapter.OnSensorsListCbxCheckedListener {
  private Context m_context;
  private List<CustomSensor> m_sensorList;
  private SensorManager m_sysSensorManager;

  private int m_previewDelay = SensorManager.SENSOR_DELAY_FASTEST;
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
    int count = 0;
    for (Sensor s:allSensor) {
      CustomSensor sensor = new CustomSensor(s);
      sensor.id = count;
      m_sensorList.add(sensor);
      count++;
    }
    mCallbacksSet = new HashMap<>();//TODO: testify the efficiency of ArrayMap
  }

  private Map<Sensor, CallbackPair> mCallbacksSet;

  public void registerCallbackForSensor(CustomSensor sensor, SensorEventCallback callback){
    if(mCallbacksSet.containsKey(sensor.getSensorObject())){
      mCallbacksSet.get(sensor.getSensorObject()).callback = callback;
    }else{
      mCallbacksSet.put(sensor.getSensorObject(), new CallbackPair(sensor, callback));
      m_sysSensorManager.registerListener(mSensorListener, sensor.getSensorObject(),
              m_previewDelay);
    }
  }

  public void registerCallbacksForAllSensors(SensorEventCallback callback){
    for(CustomSensor sensor : m_sensorList){
      registerCallbackForSensor(sensor, callback);
    }
  }

  public void clearCallbackForSensor(CustomSensor sensor){
    mCallbacksSet.remove(sensor.getSensorObject());
    m_sysSensorManager.unregisterListener(mSensorListener, sensor.getSensorObject());
  }

  public void clearCallbacksForAllSensors(){
    for(CustomSensor sensor : m_sensorList){
      clearCallbackForSensor(sensor);
    }
  }

  /**
   * Centric Listener bus for all sensors.
   */
  private SensorEventListener mSensorListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      CallbackPair p = mCallbacksSet.get(event.sensor);
      p.callback.onSensorChanged(p.sensor, event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
  };

  /**
   * Integrated form of sensor-callback
   */
  private class CallbackPair{
    CustomSensor sensor;
    SensorEventCallback callback;
    CallbackPair(CustomSensor sensor, SensorEventCallback callback){
      this.sensor = sensor;
      this.callback = callback;
    }
  }

  public List<CustomSensor> getSensorList(){
    return m_sensorList;
  }

  @Override
  public void OnSensorsListCbxChecked(int pos, boolean selected) {
    /*CustomSensor sensor = m_sensorList.get(pos);

    if(sensor.state == SensorStates.Previewing && !selected){
      m_sysSensorManager.unregisterListener(sensor.listener);
      sensor.state = SensorStates.Resting;
    }else if(sensor.state == SensorStates.Resting && selected){
      m_sysSensorManager.registerListener(sensor.listener, sensor.getSensorObject(),
              m_previewDelay);
      sensor.state = SensorStates.Previewing;
    }else{
      LOG.w("Unexpected branch");
    }*/
  }
}
