package donlon.android.sensors.utils.cpu;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CpuUsageImplement{
  public static CpuUsage createSysSummaryCpuUsage(){
    return new SysSummaryCpuUsage();
  }

  public static CpuUsage createSingleProcessCpuUsage(int processId){
    return new SingleProcessCpuUsage(processId);
  }

  public static CpuUsage createCurrentProcessCpuUsage(){
    return new SingleProcessCpuUsage(2333);
  }
}

class SysSummaryCpuUsage implements CpuUsage{
  private static final String FILE_PROC_STAT = "/proc/stat";
  private static final String ID_CPU = "cpu";
  private static final String PARAM_DELIMITER = " ";
  private static final String PARAM_SPLIT_REGEXP = " +";
  private static final int PROC_STAT_PARAMS_COUNT = 7;
  private static final int VALUABLE_PROCSTAT_PARAMETERS_COUNT = 3;

  private long[] mLastRawData;
  private long[] mNewRawData;//TODO: save workLoad & totalLoad only

  SysSummaryCpuUsage(){
    mLastRawData = new long[PROC_STAT_PARAMS_COUNT];
    mNewRawData = new long[PROC_STAT_PARAMS_COUNT];
    getAllCpuStat();
  }

  /**
   * Parse values form /proc/stat and write them to mNewRawData
   */
  private void getAllCpuStat(){
    String[] tokens = null;

    try{
      BufferedReader reader = new BufferedReader(new FileReader(FILE_PROC_STAT));
      String line;
      while((line = reader.readLine()) != null){
        if(line.startsWith(ID_CPU + PARAM_DELIMITER)){
          tokens = line.split(PARAM_SPLIT_REGEXP);
          break;
        }
      }
      reader.close();
    }catch(IOException e1){
      e1.printStackTrace();
    }

    if(tokens == null){
      onParseFailed();
      return;
    }

    for(int j = 0; j < PROC_STAT_PARAMS_COUNT; j++){
      try{
        mNewRawData[j] = Long.parseLong(tokens[j + 1]);
      }catch(NumberFormatException e){
        e.printStackTrace();
        mNewRawData[j] = 0L;
        //TODO: return null;
      }
    }
  }

  private void onParseFailed(){
    if(mOnFailedListener != null){
      mOnFailedListener.onFailed();
    }
  }

  @Override
  public float requestCpuUsage(){
    System.arraycopy(
            mNewRawData, 0,
            mLastRawData, 0,
            PROC_STAT_PARAMS_COUNT);
    getAllCpuStat();

    long prevTotal = getTotalLoad(mLastRawData);
    long nowTotal  = getTotalLoad(mNewRawData);
    long prevWork  = getWorkLoad(mLastRawData);
    long nowWork   = getWorkLoad(mNewRawData);

    if(nowTotal - prevTotal == 0){
      onParseFailed();
      return 0f;
    }
    return (nowWork - prevWork)/ (float) (nowTotal - prevTotal);
  }

  private static long getTotalLoad(long[] args){
    long result = 0;
    for(long arg : args){
      result += arg;
    }
    return result;
  }

  private static long getWorkLoad(long[] args){
    long result = 0;
    for(int i = 0; i < VALUABLE_PROCSTAT_PARAMETERS_COUNT; i++){
      result += args[i];
    }
    return result;
  }

  private OnCpuUsageGetFailedListener mOnFailedListener;

  @Override
  public void setOnFailedListener(OnCpuUsageGetFailedListener listener){
    mOnFailedListener = listener;
  }

}

class SingleProcessCpuUsage implements CpuUsage{
  SingleProcessCpuUsage(int processId){

  }
  @Override
  public float requestCpuUsage(){
    return 0f;
  }

  private OnCpuUsageGetFailedListener mOnFailedListener;

  @Override
  public void setOnFailedListener(OnCpuUsageGetFailedListener listener){
    mOnFailedListener = listener;
  }
}