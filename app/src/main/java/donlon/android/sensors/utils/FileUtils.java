package donlon.android.sensors.utils;

import android.content.Context;
import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {
  private static final SimpleDateFormat DATE_FORMAT
      = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);

  public static String getNewDataFilePath(Context context) {
    int times = StorageUtils.getRecordingTimes(context);
    times++;
    StorageUtils.updateRecordingTimes(context, times);
    return String.format(Locale.ENGLISH, "%s/sensor_data_%d_%s.data",
        Environment.getExternalStorageDirectory(),
        times,
        DATE_FORMAT.format(new Date()));
  }
}
