package donlon.android.sensors;

import android.hardware.SensorEvent;

/**
 * Simple interface for callback
 */
public interface SensorEventCallback {
  void onSensorChanged(CustomSensor sensor, SensorEvent event);
}
