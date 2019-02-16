package donlon.android.sensors.utils;

public class FormatterUtils {
  public static String format3dData(float[] val) {
    return String.valueOf(val[0]) + "\n" + String.valueOf(val[1]) + "\n" + String.valueOf(val[2]);
  }

  public static String formatBytes(int bytes) {
    return bytes + " Bytes";//TODO: formatting
  }
}
