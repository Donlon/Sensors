package donlon.android.sensors;

import android.hardware.Sensor;

public class SensorUtils {
  public static String getSensorNameByType(int type){
    switch (type) {
      case Sensor.TYPE_ACCELEROMETER:
        return "加速度传感器(Accelerometer)";
      case Sensor.TYPE_ALL:
        return "传感器";
      case Sensor.TYPE_AMBIENT_TEMPERATURE:
        return "环境温度传感器";
      case Sensor.TYPE_GAME_ROTATION_VECTOR:
        return "未校正的方向传感器";
      case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
        return "地磁场旋转传感器";
      case Sensor.TYPE_GRAVITY:
        return "重力感应器(Gravity)";
      case Sensor.TYPE_GYROSCOPE:
        return "陀螺仪传感器(Gyroscope)";
      case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
        return "未校正的陀螺仪传感器";
      case Sensor.TYPE_LIGHT:
        return "环境光传感器(Light)";
      case Sensor.TYPE_LINEAR_ACCELERATION:
        return "线性加速度传感器(Linear Acceleration)";
      case Sensor.TYPE_MAGNETIC_FIELD:
        return "磁场传感器(Magnetic Field)";
      case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
        return "未校正的磁场传感器(Magnetic Field)";
      case Sensor.TYPE_ORIENTATION:
        return "方向传感器(Orientation)";
      case Sensor.TYPE_PRESSURE:
        return "压力传感器(Pressure)";
      case Sensor.TYPE_PROXIMITY:
        return "距离传感器(Proximity)";
      case Sensor.TYPE_RELATIVE_HUMIDITY:
        return "相对湿度传感器";
      case Sensor.TYPE_ROTATION_VECTOR:
        return "翻转传感器(Rotation Vector)";
      case Sensor.TYPE_SIGNIFICANT_MOTION:
        return "运动触发传感器";
      case Sensor.TYPE_STEP_COUNTER:
        return "计步器";
      case Sensor.TYPE_STEP_DETECTOR:
        return "Step Detector Sensor";
      case Sensor.TYPE_TEMPERATURE:
        return "温度传感器(Temperature)";
      default:
        return "未知传感器: " + type;
    }
  }

  @Deprecated
  public static String parseData(int type, float[] value){

    switch (type) {
      case Sensor.TYPE_ACCELEROMETER:
        return value[0] + "," + value[1] + "," + value[2] + " m/s2";
      case Sensor.TYPE_GRAVITY:
        return value[0] + "," + value[1] + "," + value[2] + " m/s2";
      case Sensor.TYPE_GYROSCOPE:
        return value[0] + "," + value[1] + "," + value[2] + " rad/s";
      case Sensor.TYPE_LINEAR_ACCELERATION:
        return value[0] + "," + value[1] + "," + value[2] + " m/s2";
      case Sensor.TYPE_ROTATION_VECTOR:
        return value[0] + "," + value[1] + "," + value[2] + "," + value[3] + "";
      case Sensor.TYPE_MAGNETIC_FIELD:
        return value[0] + "," + value[1] + "," + value[2] + " μT";
      case Sensor.TYPE_ORIENTATION:
        return value[0] + "," + value[1] + "," + value[2] + " Degrees";
      case Sensor.TYPE_PROXIMITY:
        return value[0] + " cm";
      case Sensor.TYPE_AMBIENT_TEMPERATURE:
        return value[0] + " °C";
      case Sensor.TYPE_LIGHT:
        return value[0] + " lx";
      case Sensor.TYPE_PRESSURE:
        return value[0] + " hPa|mbar";
      case Sensor.TYPE_RELATIVE_HUMIDITY:
        return value[0] + " %";
      case Sensor.TYPE_TEMPERATURE:
        return value[0] + " °C";
      default:
        String str="";
        for(int i=0;i<value.length;i++){
          str+=value[i];
          if(i!=value.length-1){
            str+=",";
          }
        }
        return str;
    }
  }

  public static String getDataUnit(int type){
    switch (type) {
      case Sensor.TYPE_ACCELEROMETER:
        return  "m/s2";
      case Sensor.TYPE_GRAVITY:
        return "m/s2";
      case Sensor.TYPE_GYROSCOPE:
        return "rad/s";
      case Sensor.TYPE_LINEAR_ACCELERATION:
        return "m/s2";
      case Sensor.TYPE_ROTATION_VECTOR:
        return "";
      case Sensor.TYPE_MAGNETIC_FIELD:
        return "μT";
      case Sensor.TYPE_ORIENTATION:
        return "Degrees";
      case Sensor.TYPE_PROXIMITY:
        return "cm";
      case Sensor.TYPE_AMBIENT_TEMPERATURE:
        return "°C";
      case Sensor.TYPE_LIGHT:
        return "lx";
      case Sensor.TYPE_PRESSURE:
        return "hPa|mbar";
      case Sensor.TYPE_RELATIVE_HUMIDITY:
        return "%";
      case Sensor.TYPE_TEMPERATURE:
        return "°C";
      default:
        return "Unknown";
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
        return 4;
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
