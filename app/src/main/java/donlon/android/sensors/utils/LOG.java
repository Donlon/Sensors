package donlon.android.sensors.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LOG {
  private static String LOG_TAG = "Sensors_Dev";

  public static void d(String s){
    android.util.Log.d(LOG_TAG, s);
  }

  public static void e(String s){
    android.util.Log.e(LOG_TAG, s);
  }

  public static void w(String s){
    try{
      throw new CurrentStack(s);
    }catch (Exception e){
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      pw.flush();
      android.util.Log.w(LOG_TAG, "Thrown at " + Thread.currentThread().toString());
      android.util.Log.w(LOG_TAG, sw.toString());
    }
  }

}

class CurrentStack extends Exception{

  private String mTitle;
  CurrentStack(String title){
    super();
    mTitle = title;
  }
  @Override
  public String toString() {
    return mTitle + ":";
  }
}