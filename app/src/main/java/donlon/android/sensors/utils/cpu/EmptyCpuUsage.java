package donlon.android.sensors.utils.cpu;

public class EmptyCpuUsage implements CpuUsage {
  @Override
  public float requestCpuUsage() {
    return 0f;
  }
}
