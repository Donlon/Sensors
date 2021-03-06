package donlon.android.sensors.utils;

public class FormatterUtils {
  private static final char C1 = ' ';
  private static final char C2 = ':';

  public static String format3dData(float[] val) {
    return val[0] + "\n" + val[1] + "\n" + val[2];
  }

  public static String formatVector(float[] val) {
    if (val == null || val.length == 0) {
      return "";
    }
    if (val.length == 1) {
      return String.valueOf(val[0]);
    }
    StringBuilder sb = new StringBuilder(String.valueOf(val[0]));
    for (int i = 1; i < val.length; i++) {
      sb.append(", ").append(val[i]);
    }
    return sb.toString();
  }

  public static String formatBytes(int bytes) {
    return bytes + " Bytes";//TODO: formatting
  }

  public static String getFormattedTime(long time, boolean parity) {
    int s = (int) (time % 60);
    time /= 60;
    int m = (int) (time % 60);
    time /= 60;
    int h = (int) time;
    StringBuilder sb = new StringBuilder();
    sb.append(h);
    sb.append(parity ? C1 : C2);
    if (m < 10) {
      sb.append('0');
    }
    sb.append(m);
    sb.append(parity ? C1 : C2);
    if (s < 10) {
      sb.append('0');
    }
    sb.append(s);
    return sb.toString();
  }
}
