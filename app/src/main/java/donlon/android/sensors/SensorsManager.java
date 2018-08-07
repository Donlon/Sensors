package donlon.android.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import donlon.android.sensors.utils.LOG;

public class SensorsManager implements SensorsListAdapter.OnSensorsListCbxCheckedListener {
  private List<CustomSensor> mSensorList;
  private SensorManager mSysSensorManager;

  private int mPreviewDelay = SensorManager.SENSOR_DELAY_FASTEST;
//  private int mPreviewDelay = SensorManager.SENSOR_DELAY_NORMAL;

  private SensorsManager(Context context){
    mSysSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

    List<Sensor> allSensor;
    if (mSysSensorManager != null) {
      allSensor = mSysSensorManager.getSensorList(Sensor.TYPE_ALL);
    }else{
      throw new NullPointerException();
    }

    mSensorList = new ArrayList<>();
    int count = 0;
    for (Sensor s:allSensor) {
      CustomSensor sensor = new CustomSensor(s);
      sensor.id = count;
      mSensorList.add(sensor);
      count++;
    }
    mCallbacksSet = new HashMap<>();//TODO: testify the efficiency of ArrayMap
  }

  /**
   * SingleTon creator
   * @param context context
   * @return instance created
   */
  public static SensorsManager create(Context context){
    return singleTonInstance = new SensorsManager(context);
  }

  /**
   * SingleTon instance
   */
  private static SensorsManager singleTonInstance;

  public static /*synchronized */SensorsManager getInstance(){
    if (null==singleTonInstance){
      LOG.w("");
    }
    return singleTonInstance;
  }

  private Map<Sensor, CallbackPair> mCallbacksSet;

  public void registerCallbackForSensor(@NonNull CustomSensor sensor, SensorEventCallback callback){
    if(mCallbacksSet.containsKey(sensor.getSensorObject())){
      mCallbacksSet.get(sensor.getSensorObject()).callback = callback;
    }else{
      mCallbacksSet.put(sensor.getSensorObject(), new CallbackPair(sensor, callback));
      mSysSensorManager.registerListener(mSensorListener, sensor.getSensorObject(),
              mPreviewDelay);
    }
  }

  public void registerCallbacksForAllSensors(SensorEventCallback callback){
    for(CustomSensor sensor : mSensorList){
      registerCallbackForSensor(sensor, callback);
    }
  }

  public void clearCallbackForSensor(CustomSensor sensor){
    mCallbacksSet.remove(sensor.getSensorObject());
    mSysSensorManager.unregisterListener(mSensorListener, sensor.getSensorObject());
  }

  public void clearCallbacksForAllSensors(){
    for(CustomSensor sensor : mSensorList){
      clearCallbackForSensor(sensor);
    }
  }

  /**
   * Centric Listener bus for all sensors.
   */
  private SensorEventListener mSensorListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      //callback and listener must are set simultaneously
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
    return mSensorList;
  }

  @Override
  public void OnSensorsListCbxChecked(int pos, boolean selected) {
    /*CustomSensor sensor = mSensorList.get(pos);

    if(sensor.state == SensorStates.Previewing && !selected){
      mSysSensorManager.unregisterListener(sensor.listener);
      sensor.state = SensorStates.Resting;
    }else if(sensor.state == SensorStates.Resting && selected){
      mSysSensorManager.registerListener(sensor.listener, sensor.getSensorObject(),
              mPreviewDelay);
      sensor.state = SensorStates.Previewing;
    }else{
      LOG.w("Unexpected branch");
    }*/
  }
}
