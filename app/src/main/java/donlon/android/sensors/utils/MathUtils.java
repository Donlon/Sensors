package donlon.android.sensors.utils;

public class MathUtils{

  public static float getA(float[] value){//TODO: use native code
    return (float) Math.sqrt(value[0] * value[0] + value[1] * value[1] + value[2] * value[2]);
  }
}
