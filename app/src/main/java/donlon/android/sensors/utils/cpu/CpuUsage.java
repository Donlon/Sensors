package donlon.android.sensors.utils.cpu;

public interface CpuUsage {
  float requestCpuUsage();

  void setOnFailedListener(OnCpuUsageGetFailedListener listener);
}