package donlon.android.sensors.utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Environment;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

import donlon.android.sensors.CustomSensor;
import donlon.android.sensors.SensorEventCallback;

public class DataFileWriter {
  private static final int DATA_FILE_VERSION = 1;

  /**
   * Reference from RecordingManager
   */
  private final Map<CustomSensor, ArrayList<SensorEvent>> mDataCacheMap;// TODO: rename it

  private FileOutputStream mDataFileFOS;
  private DataOutputStream mDataFileDOS;

  public DataFileWriter(Map<CustomSensor, ArrayList<SensorEvent>> dataCacheMap){
    mDataCacheMap = dataCacheMap;
  }

  public boolean init(){
/*
    File file = new File(Environment.getExternalStorageDirectory() + "/t.data");
    try {
      if(!file.exists()) {
        if(!file.createNewFile()){
          return false;
        }
      }else{
        if(!file.isFile()){
          return false;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    try {
      mDataFileFOS = new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      return false;// TODO: add warning
    }
    mDataFileDOS = new DataOutputStream(mDataFileFOS);

    //Write MetaInfo

    try {
      mDataFileDOS.writeBytes("DonlonDataFile\0\0"+"\u1234\u5678\0\0\0\0");
      mDataFileDOS.writeInt(DATA_FILE_VERSION);

      mDataFileDOS.writeInt(mDataCacheMap.size());

      StringWriter sw = new StringWriter();
      Sensor a;
      a.getName()
      a.getVendor()
      a.getVersion()
      a.getType()
      a.getMaxRange()
      a.getResolution()
      a.getPower()
      a.getMinDelay()

      StringBuilder
      for (Map.Entry<CustomSensor, ArrayList<SensorEvent>> entry : mDataCacheMap.entrySet()) {
        entry.getKey()
      }

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }*/
    return true;
  }

  public void WriteMetaInfo(){

  }

  public int getWrittenBytes(){
    return 0;//TODO: should it be synchronized?
  }

  private byte[] generateSensorInfo(Sensor sensor){
    return null;
  }

  public void write(){
    //TODO: just sync with each ArrayList<SensorEvent>
    synchronized (mDataCacheMap){//TODO: das ist OK?
      for (ArrayList<SensorEvent> dataList : mDataCacheMap.values()) {
        for(SensorEvent event : dataList){
          for(int i=0; i < event.values.length; i++){
            //event.values[i];
          }
        }
      }
    }
  }
}
