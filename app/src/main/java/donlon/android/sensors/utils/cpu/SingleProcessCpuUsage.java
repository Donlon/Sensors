package donlon.android.sensors.utils.cpu;

public class SingleProcessCpuUsage implements CpuUsage {
  public SingleProcessCpuUsage() {

  }

  public SingleProcessCpuUsage(int processId) {

  }

  @Override
  public float requestCpuUsage() {
    return 0f;
  }
}
