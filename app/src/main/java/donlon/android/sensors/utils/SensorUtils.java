package donlon.android.sensors.utils;

import android.hardware.Sensor;

public class SensorUtils {
  public static String getSensorNameByType(int type){
    switch (type) {
      case Sensor.TYPE_ACCELEROMETER:
        return "加速度传感器";
      case Sensor.TYPE_ALL:
        return "传感器";
      case Sensor.TYPE_AMBIENT_TEMPERATURE:
        return "环境温度传感器";
      case Sensor.TYPE_GAME_ROTATION_VECTOR:
        return "未校正的方向传感器";
      case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
        return "地磁场旋转传感器";
      case Sensor.TYPE_GRAVITY:
        return "重力传感器";
      case Sensor.TYPE_GYROSCOPE:
        return "陀螺仪";
      case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
        return "未校正的陀螺仪";
      case Sensor.TYPE_LIGHT:
        return "环境光传感器";
      case Sensor.TYPE_LINEAR_ACCELERATION:
        return "线性加速度传感器";
      case Sensor.TYPE_MAGNETIC_FIELD:
        return "磁场强度传感器";
      case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
        return "未校正的磁场强度传感器";
      case Sensor.TYPE_ORIENTATION:
        return "方向传感器";
      case Sensor.TYPE_PRESSURE:
        return "压力传感器";
      case Sensor.TYPE_PROXIMITY:
        return "距离传感器";
      case Sensor.TYPE_RELATIVE_HUMIDITY:
        return "相对湿度传感器";
      case Sensor.TYPE_ROTATION_VECTOR:
        return "翻转传感器";
      case Sensor.TYPE_SIGNIFICANT_MOTION:
        return "运动触发传感器";
      case Sensor.TYPE_STEP_COUNTER:
        return "计步器";
      case Sensor.TYPE_STEP_DETECTOR:
        return "Step Detector";
      case Sensor.TYPE_TEMPERATURE:
        return "温度传感器";
      default:
        return "未知传感器: " + type;
    }
  }
  public static String getSensorEnglishNameByType(int type){
    switch (type) {
      case Sensor.TYPE_ACCELEROMETER:
        return "Accelerometer";
      case Sensor.TYPE_ALL:
        return "All Sensor";
      case Sensor.TYPE_AMBIENT_TEMPERATURE:
        return "Ambient Temperature Sensor";
      case Sensor.TYPE_GAME_ROTATION_VECTOR:
        return "Rotation Vector Sensor (Uncalibrated)";
      case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
        return "Geo-magnetic Rotation Vector";
      case Sensor.TYPE_GRAVITY:
        return "Gravity Sensor";
      case Sensor.TYPE_GYROSCOPE:
        return "Gyroscope";
      case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
        return "Gyroscope (Uncalibrated)";
      case Sensor.TYPE_LIGHT:
        return "Light Sensor";
      case Sensor.TYPE_LINEAR_ACCELERATION:
        return "Linear Acceleration Sensor";
      case Sensor.TYPE_MAGNETIC_FIELD:
        return "Magnetic Field Sensor";
      case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
        return "Magnetic Field Sensor (Uncalibrated)";
      case Sensor.TYPE_ORIENTATION:
        return "Orientation Sensor";
      case Sensor.TYPE_PRESSURE:
        return "Pressure Sensor";
      case Sensor.TYPE_PROXIMITY:
        return "Proximity Sensor";
      case Sensor.TYPE_RELATIVE_HUMIDITY:
        return "Relative Humidity Sensor";
      case Sensor.TYPE_ROTATION_VECTOR:
        return "Rotation Vector Sensor";
      case Sensor.TYPE_SIGNIFICANT_MOTION:
        return "Significant Motion Sensor";
      case Sensor.TYPE_STEP_COUNTER:
        return "Step Counter";
      case Sensor.TYPE_STEP_DETECTOR:
        return "Step Detector";
      case Sensor.TYPE_TEMPERATURE:
        return "Temperature Sensor";
      default:
        return "Unknown: " + type;
    }
  }

  public static String getDataUnit(int type){
    switch (type) {
      case Sensor.TYPE_GYROSCOPE:
        return "rad/s";
      case Sensor.TYPE_ACCELEROMETER:
      case Sensor.TYPE_GRAVITY:
      case Sensor.TYPE_LINEAR_ACCELERATION:
        return "m/s²";
      case Sensor.TYPE_ROTATION_VECTOR:
        return "°";
      case Sensor.TYPE_MAGNETIC_FIELD:
        return "μT";
      case Sensor.TYPE_ORIENTATION:
        return "Degrees";
      case Sensor.TYPE_PROXIMITY:
        return "cm";
      case Sensor.TYPE_AMBIENT_TEMPERATURE:
      case Sensor.TYPE_TEMPERATURE:
        return "℃";
      case Sensor.TYPE_LIGHT:
        return "lx";
      case Sensor.TYPE_PRESSURE:
        return "hPa|mbar";
      case Sensor.TYPE_RELATIVE_HUMIDITY:
        return "%";
      default:
        return "";
    }
  }

  public static int getSensorDataDimension(int type) {
    switch (type) {
      case Sensor.TYPE_ACCELEROMETER:
      case Sensor.TYPE_GRAVITY:
      case Sensor.TYPE_GYROSCOPE:
      case Sensor.TYPE_LINEAR_ACCELERATION:
        return 3;
      case Sensor.TYPE_ROTATION_VECTOR:
        return 3;//TODO: it's actually of 5 dimensions
      case Sensor.TYPE_MAGNETIC_FIELD:
      case Sensor.TYPE_ORIENTATION:
        return 3;
      case Sensor.TYPE_PROXIMITY:
      case Sensor.TYPE_AMBIENT_TEMPERATURE:
      case Sensor.TYPE_LIGHT:
      case Sensor.TYPE_PRESSURE:
      case Sensor.TYPE_RELATIVE_HUMIDITY:
      case Sensor.TYPE_TEMPERATURE:
        return 1;
      default:
        return 1;
    }
  }
}
