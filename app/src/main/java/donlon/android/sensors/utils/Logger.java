package donlon.android.sensors.utils;

import android.util.Log;

public class Logger {
  public static final String TAG = "Sensors_dev";

  public static void d(String log) {
    d(TAG, log);
  }

  public static void d(String tag, String log) {
    Log.d(tag, log);
  }

  public static void ds() {
    d(TAG, Log.getStackTraceString(null));
  }

  public static void ds(String tag) {
    d(tag, Log.getStackTraceString(null));
  }

  public static void i(String log) {
    i(TAG, log);
  }

  public static void i(String tag, String log) {
    Log.i(tag, log);
  }

  public static void is() {
    i(TAG, Log.getStackTraceString(null));
  }

  public static void is(String tag) {
    i(tag, Log.getStackTraceString(null));
  }

  public static void e(String log) {
    e(TAG, log);
  }

  public static void e(String tag, String log) {
    Log.e(tag, log);
  }

  public static void es() {
    e(TAG, Log.getStackTraceString(null));
  }

  public static void es(String tag) {
    e(tag, Log.getStackTraceString(null));
  }
}
