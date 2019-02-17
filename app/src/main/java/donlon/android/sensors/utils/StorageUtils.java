package donlon.android.sensors.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class StorageUtils {
  private static final String SP_DEFAULT = "Default";
  private static final String KEY_VIEWING_ON = "ViewingOn";
  private final static String KEY_RECORDING_TIMES = "RecordingTimes";

  public static boolean getDefaultViewingState(Context context) {
    SharedPreferences sp = context.getSharedPreferences(SP_DEFAULT, Context.MODE_PRIVATE);
    return sp.getBoolean(KEY_VIEWING_ON, true);
  }

  public static void saveDefaultViewingState(Context context, boolean isOn) {
    SharedPreferences.Editor editor = context.getSharedPreferences(SP_DEFAULT, Context.MODE_PRIVATE).edit();
    editor.putBoolean(KEY_VIEWING_ON, isOn);
    editor.apply();
  }

  public static int getRecordingTimes(Context context) {
    SharedPreferences sp = context.getSharedPreferences(SP_DEFAULT, Context.MODE_PRIVATE);
    return sp.getInt(KEY_RECORDING_TIMES, 0);
  }

  public static void updateRecordingTimes(Context context, int n) {
    SharedPreferences.Editor editor = context.getSharedPreferences(SP_DEFAULT, Context.MODE_PRIVATE).edit();
    editor.putInt(KEY_RECORDING_TIMES, n);
    editor.apply();
  }
}
