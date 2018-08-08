package donlon.android.sensors.utils;

import android.hardware.Sensor;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import donlon.android.sensors.CustomSensor;

public class DataFileWriter {
  private static final int DATA_FILE_VERSION = 1;
  public Object dataBufferLock;

  /**
   * Reference from RecordingManager
   */
  private final Map<CustomSensor, SensorEventsBuffer> mDataCacheMap;// TODO: rename it

  private DataOutputStream mDataFileOutputStream;

  public DataFileWriter(Map<CustomSensor, SensorEventsBuffer> dataCacheMap){
    mDataCacheMap = dataCacheMap;
  }

  public boolean init(){
    dataBufferLock = new Object();
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
      mDataFileOutputStream = new DataOutputStream(new FileOutputStream(file));
    } catch (FileNotFoundException e) {
      return false;// TODO: add warning
    }

    //Write MetaInfo

    try {
      mDataFileOutputStream.writeBytes("DonlonDataFile\0\0");
      mDataFileOutputStream.write(new byte[]{
        0x12, 0x34, 0x56, 0x78, 0, 0, 0, 0, 0, 0, 0, 0
      });
      mDataFileOutputStream.writeInt(DATA_FILE_VERSION);

      appendSensorsInfo(mDataFileOutputStream);

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    frameBuffer = new ByteArrayOutputStream();
    frameBufferOS = new DataOutputStream(frameBuffer);

    return true;
  }

  private void appendSensorsInfo(DataOutputStream os) throws IOException {
    ByteArrayOutputStream sensorsInfoBuffer = new ByteArrayOutputStream();
    DataOutputStream sensorsInfoOS = new DataOutputStream(sensorsInfoBuffer);

    ByteArrayOutputStream singleSensorInfoBuffer = new ByteArrayOutputStream();
    DataOutputStream singleSensorInfoOS = new DataOutputStream(singleSensorInfoBuffer);

    sensorsInfoOS.writeInt(mDataCacheMap.size());

    for (Map.Entry<CustomSensor, SensorEventsBuffer> entry : mDataCacheMap.entrySet()) {
      Sensor sensor = entry.getKey().getSensorObject();

      singleSensorInfoOS.writeInt(entry.getKey().id);
      singleSensorInfoOS.writeInt(entry.getKey().dataDimension);

      singleSensorInfoOS.writeBytes(sensor.getName());
      singleSensorInfoOS.writeByte('\0');
      singleSensorInfoOS.writeBytes(sensor.getVendor());
      singleSensorInfoOS.writeByte('\0');
      singleSensorInfoOS.writeInt(sensor.getVersion());
      singleSensorInfoOS.writeInt(sensor.getType());
      singleSensorInfoOS.writeFloat(sensor.getMaximumRange());
      singleSensorInfoOS.writeFloat(sensor.getResolution());
      singleSensorInfoOS.writeFloat(sensor.getPower());
      singleSensorInfoOS.writeInt(sensor.getMinDelay());

      sensorsInfoOS.writeInt(singleSensorInfoBuffer.size());
//      sensorsInfoOS.writeInt(0xFFFFFF);
      singleSensorInfoBuffer.writeTo(sensorsInfoBuffer);
      singleSensorInfoBuffer.reset();
      //TODO: clear
    }

    int paddingSize = 16 - (sensorsInfoBuffer.size() + 4) & 0x0F;
    while(paddingSize-- >0){
      sensorsInfoOS.write('\377');
    }
    os.writeInt(sensorsInfoBuffer.size());
//    os.writeInt(0xAAAAAA);
    sensorsInfoBuffer.writeTo(os);
    sensorsInfoBuffer.reset();
  }

  public int getWrittenBytes(){
    return mDataFileOutputStream.size();//TODO: should it be synchronized?
  }
  private byte[] separator = {
          1, 2, 3, 4, 5, 6, 7, 8,
          8, 7, 6, 5, 4, 3, 2, 1};

  private int writtenFramesCount;


  private ByteArrayOutputStream frameBuffer;
  private DataOutputStream frameBufferOS;

  public void flush() throws IOException {
    //TODO: just sync with each ArrayList<SensorEvent>
    synchronized (dataBufferLock){//TODO: das ist OK?
      //Test
      mDataFileOutputStream.write(separator);

      mDataFileOutputStream.writeInt(writtenFramesCount);// Index

      int dataGroupCount = 0;
      for (SensorEventsBuffer dataList : mDataCacheMap.values()) {
        if(!dataList.isEmpty()){
          dataGroupCount++;
        }
      }
      frameBufferOS.writeInt(dataGroupCount);

      frameBufferOS.writeLong(System.currentTimeMillis());

      //Each data group
      for (Map.Entry<CustomSensor, SensorEventsBuffer> entry : mDataCacheMap.entrySet()) {
        frameBufferOS.writeInt(entry.getKey().id);
        frameBufferOS.writeInt(entry.getValue().size());

        for(int i=0; i<entry.getValue().size(); i++){
          SensorEventAggregation event = entry.getValue().get(i);
          frameBufferOS.writeLong(event.timeStamp);
          frameBufferOS.writeFloat(event.accuracy);
          for(int j=0; j < entry.getKey().dataDimension; j++){//event.values.length
            frameBufferOS.writeFloat(event.values[j]);
          }
//          LOG.i(event.values[0]+", "+event.values[1]+", "+event.values[2]);
        }

        entry.getValue().clear();
      }

      mDataFileOutputStream.writeInt(frameBuffer.size());
      frameBuffer.writeTo(mDataFileOutputStream);
      frameBuffer.reset();

      writtenFramesCount++;
    }
  }

  public void closeFile() throws IOException {
    mDataFileOutputStream.close();
  }
}
