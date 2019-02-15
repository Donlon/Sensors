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

public class SensorController {
  private List<CustomSensor> mSensorList = new ArrayList<>();
  private Map<Sensor, CustomSensor> mSensorMap = new HashMap<>(20);
  private SensorManager mSysSensorManager;

  private static final int DEFAULT_PREVIEW_DELAY = SensorManager.SENSOR_DELAY_FASTEST;

  private SensorEventCallback mSensorEventCallback;

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
    assert singleTonInstance != null;
    return singleTonInstance;
  }

  private SensorController(Context context) {
    mSysSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

    if (mSysSensorManager == null) {
      throw new UnsupportedOperationException();
    }
    List<Sensor> allSensor = mSysSensorManager.getSensorList(Sensor.TYPE_ALL);

    int count = 0;
    for (Sensor s : allSensor) {
      CustomSensor sensor = new CustomSensor(count, s);
      mSensorList.add(sensor);
      mSensorMap.put(s, sensor);
      count++;
    }
    //TODO: test the efficiency of ArrayMap or wrap the listener
  }

  public void enableSensor(CustomSensor sensor) {
    if (sensor.isEnabled()) {
      throw new IllegalStateException();
    }
    sensor.setEnabled(true);
    mSysSensorManager.registerListener(mSensorListener, sensor.getSensor(), DEFAULT_PREVIEW_DELAY);
  }

  public void disableSensor(CustomSensor sensor) {
    if (!sensor.isEnabled()) {
      throw new IllegalStateException();
    }
    sensor.setEnabled(false);
    mSysSensorManager.unregisterListener(mSensorListener, sensor.getSensor());
  }

  public void setSensorOnChangeCallback(SensorEventCallback callback) {
    mSensorEventCallback = callback;
  }

  public void enableAllSensors() {
    for (CustomSensor sensor : mSensorList) {
      if (!sensor.isEnabled()) {
        enableSensor(sensor);
      }
    }
  }

  public void disableAllSensors() {
    for (CustomSensor sensor : mSensorList) {
      if (sensor.isEnabled()) {
        disableSensor(sensor);
      }
    }
  }

  /**
   * Centric Listener bus for all sensors.
   */
  private SensorEventListener mSensorListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      CustomSensor sensor = mSensorMap.get(event.sensor);
      if (mSensorEventCallback != null) {
        mSensorEventCallback.onSensorChanged(sensor, event);
      }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
  };

  public List<CustomSensor> getSensorList() {
    return mSensorList;
  }

  public CustomSensor get(int index) {
    return mSensorList.get(index);
  }
}
