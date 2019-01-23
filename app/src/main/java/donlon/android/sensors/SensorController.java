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

public class SensorController {
  private List<CustomSensor> mSensorList;
  private SensorManager mSysSensorManager;

  private static final int DEFAULT_PREVIEW_DELAY = SensorManager.SENSOR_DELAY_FASTEST;

  private SensorController(Context context) {
    mSysSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

    List<Sensor> allSensor;
    if (mSysSensorManager != null) {
      allSensor = mSysSensorManager.getSensorList(Sensor.TYPE_ALL);
    } else {
      throw new NullPointerException();
    }

    mSensorList = new ArrayList<>();
    int count = 0;
    for (Sensor s : allSensor) {
      CustomSensor sensor = new CustomSensor(s);
      sensor.id = count;
      initSensor(sensor);
      mSensorList.add(sensor);
      count++;
    }
    mCallbacksSet = new HashMap<>();
    //TODO: testify the efficiency of ArrayMap or wrap the listener

  }

  private void initSensor(CustomSensor sensor) {
    switch (sensor.getSensorObject().getType()) {
      case Sensor.TYPE_ACCELEROMETER:
      case Sensor.TYPE_GRAVITY:
      case Sensor.TYPE_LINEAR_ACCELERATION:
      case Sensor.TYPE_MAGNETIC_FIELD:
        sensor.flag |= CustomSensor.FLAG_3D_DATA;
      default:
        break;
    }
  }

  /**
   * SingleTon creator
   *
   * @param context context
   * @return instance created
   */
  public static SensorController create(Context context) {
    return singleTonInstance = new SensorController(context);
  }

  /**
   * SingleTon instance
   */
  private static SensorController singleTonInstance;

  public static /*synchronized */SensorController getInstance() {
    if (null == singleTonInstance) {
      LOG.printStack("");
    }
    return singleTonInstance;
  }

  private Map<Sensor, CallbackPair> mCallbacksSet;

  public void registerCallbackForSensor(@NonNull CustomSensor sensor, SensorEventCallback callback) {
    Sensor _sensor = sensor.getSensorObject();
    SensorController.CallbackPair _callback = mCallbacksSet.get(_sensor);
    if (_callback != null) {
      _callback.callback = callback;
    } else {
      mCallbacksSet.put(_sensor, new CallbackPair(sensor, callback));
      mSysSensorManager.registerListener(mSensorListener, _sensor, DEFAULT_PREVIEW_DELAY);
    }
  }

  public void registerCallbacksForAllSensors(SensorEventCallback callback) {
    for (CustomSensor sensor : mSensorList) {
      registerCallbackForSensor(sensor, callback);
    }
  }

  public void clearCallbackForSensor(CustomSensor sensor) {
    mCallbacksSet.remove(sensor.getSensorObject());
    mSysSensorManager.unregisterListener(mSensorListener, sensor.getSensorObject());
  }

  public void clearCallbacksForAllSensors() {
    for (CustomSensor sensor : mSensorList) {
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
  private class CallbackPair {
    CustomSensor sensor;
    SensorEventCallback callback;

    CallbackPair(CustomSensor sensor, SensorEventCallback callback) {
      this.sensor = sensor;
      this.callback = callback;
    }
  }

  public List<CustomSensor> getSensorList() {
    return mSensorList;
  }

}
