package donlon.android.sensors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import donlon.android.sensors.utils.LOG;

public class RecordingService extends Service {
  public RecordingService() {
  }

  @Override
  public void onCreate() {
    LOG.w("onCreate - Thread ID = " + Thread.currentThread().getId());
    super.onCreate();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    LOG.w("onStartCommand - startId = " + startId + ", Thread ID = " + Thread.currentThread().getId());
    return super.onStartCommand(intent, flags, startId);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    LOG.w("onBind - Thread ID = " + Thread.currentThread().getId());
    return null;
  }

  @Override
  public void onDestroy() {
    LOG.w("onDestroy - Thread ID = " + Thread.currentThread().getId());
    super.onDestroy();
  }

}
