package donlon.android.sensors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import donlon.android.sensors.utils.Logger;

public class RecordingService extends Service {
  public RecordingService() {
  }

  @Override
  public void onCreate() {
    Logger.i("onCreate - Thread ID = " + Thread.currentThread().getId());
    super.onCreate();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Logger.i("onStartCommand - startId = " + startId + ", Thread ID = " + Thread.currentThread().getId());
    return super.onStartCommand(intent, flags, startId);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    Logger.i("onBind - Thread ID = " + Thread.currentThread().getId());
    return null;
  }

  @Override
  public void onDestroy() {
    Logger.i("onDestroy - Thread ID = " + Thread.currentThread().getId());
    super.onDestroy();
  }

}
