package donlon.android.sensors;

import android.hardware.Sensor;

public class SensorUtils {
  public static String getSensorNameByType(int type){
    switch (type) {
      case Sensor.TYPE_ACCELEROMETER:
        return "加速度传感器(Accelerometer)";
      case Sensor.TYPE_GRAVITY:
        return "重力感应器(Gravity)";
      case Sensor.TYPE_GYROSCOPE:
        return "陀螺仪传感器(Gyroscope)";
      case Sensor.TYPE_LIGHT:
        return "环境光线传感器(Light)";
      case Sensor.TYPE_LINEAR_ACCELERATION:
        return "线性加速度传感器(Linear Acceleration)";
      case Sensor.TYPE_MAGNETIC_FIELD:
        return "电磁场传感器(Magnetic Field)";
      case Sensor.TYPE_ORIENTATION:
        return "方向传感器(Orientation)";
      case Sensor.TYPE_PRESSURE:
        return "压力传感器(Pressure)";
      case Sensor.TYPE_PROXIMITY:
        return "距离传感器(Proximity)";
      case Sensor.TYPE_ROTATION_VECTOR:
        return "翻转传感器(Rotation Vector)";
      case Sensor.TYPE_TEMPERATURE:
        return "温度传感器(Temperature)";
      default:
        return "未知传感器(Unknown)";
    }
  }
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

}
