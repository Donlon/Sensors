package donlon.android.sensors;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.widget.*;
import android.view.*;
import android.hardware.*;

public class MainActivity extends Activity {
  public static List<sensor> sensorList;
  private SensorManager sensorManager;

  private String[] nameList;
  private String[] infoList;

  private ListView sensorsListView;
  private TextView tv;
  private Boolean re = true;
  private ListAdapter adapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread thread, Throwable throwable) {
        throwable.printStackTrace();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        String s = sw.toString();
        Log.i("Sensors_Dev", s);
      }
    });

    initializeUi();
  }

  private void initializeUi() {
    //	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.activity_main);
    Log.d("Sensors_Dev", "Start");
    re = true;

    tv = (TextView)findViewById(R.id.sensorData);
    sensorsListView = (ListView)findViewById(R.id.mainListView1);

    sensorsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // re=!re;
        Log.d("Sensors_Dev", "Sensor Event: id=" + id);
        Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT).show();
        // Log.d("2333",position+"");
        Log.d("Sensors_Dev", "1");
        // listView.removeViewAt(position);
        Log.d("Sensors_Dev", "2");
        viewDetails(position);
      }
    });

    sensorList = new ArrayList<sensor>();

    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);// 从系统服务中获得传感器管理器




    List<Sensor>  allSensor = sensorManager.getSensorList(Sensor.TYPE_ALL);// 从传感器管理器中获得全部的传感器
    tv.setText("该手机共有"+allSensor.size()+"个传感器");
    nameList = new String[allSensor.size()];
    infoList = new String[allSensor.size()];

    int count = 0;
    for (Sensor s:allSensor) {
      sensorList.add(new sensor(s,count));

      nameList[count] = SensorUtils.getSensorNameByType(s.getType());
      infoList[count] = "设备名称:" + s.getName() + "\n设备版本:" + s.getVersion() + "\n供应商:" + s.getVendor();
      count++;
    }



    adapter = new sensorsListAdapter(this, nameList);
    sensorsListView.setAdapter(adapter);
  }

  private void viewDetails(int position) {
    //setContentView(R.layout.sensor_data);
    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

		/*TextView sensorName = (TextView) findViewById(R.id.textViewSensorName);
		sensorName.setText(position + "  "+nameList[position]);

		TextView sensorData = (TextView) findViewById(R.id.sensorData);
		sensorData.setText(sensorList.get(position).str+"\n"+sensorList.get(position).lastEvent.timestamp);

		Button back = (Button) findViewById(R.id.buttonBack);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mainUI();
				updateText(-1);
			}
		});*/
    for (sensor s:sensorList) {
      s.unregisterListener();
    }

    Intent intent = new Intent(MainActivity.this, SensorDetailsActivity.class);
    intent.putExtra("content", sensorList.get(position).str+"\n"+sensorList.get(position).lastEvent.timestamp);
    intent.putExtra("SensorType",sensorList.get(position).sensor.getType()+"");
    intent.putExtra("SensorID",position+"");
    startActivity(intent);
  }

  private void updateText(int id) {
    /*
     * ListAdapter adapter = new
     * ArrayAdapter<String>(this,R.layout.entry,R.id.entryTextView1,
     * companies);
     *
     * ListView listView = (ListView) findViewById(R.id.mainListView1);
     * listView.setAdapter(adapter);
     */

    /*return;*///

    if (re) {

    }
    re = false;
  }

  private class sensorsListAdapter extends ArrayAdapter<String> {
    public sensorsListAdapter(Context context, String[] values) {
      super(context, R.layout.entry, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

      View view = LayoutInflater.from(getContext()).inflate(R.layout.entry, parent, false);

      String text = getItem(position);

      TextView tvName = (TextView) view.findViewById(R.id.textViewSensorName);
      tvName.setText(text);//Write sensor name

      sensor konoSensor = sensorList.get(position);

      TextView tvInfo = (TextView) view.findViewById(R.id.textViewSensorInfo);
      tvInfo.setText(infoList[position]);

      TextView tvData = (TextView) view.findViewById(R.id.textViewSensorData);
      tvData.setText(konoSensor.str + "");

      return view;
    }
  }

  public class sensor {
    public int id;
    public Sensor sensor;
    public String str;
    public SensorEvent lastEvent;
    private sensorEventListener listener;
    public int type=-1;
    public boolean listening=false;//
    sensor(Sensor _s,int _id) {
      sensor = _s;
      id=_id;
      str = "";
      type=_s.getType();
      listener=new sensorEventListener();
      registerListener();
    }

    public void unregisterListener(){
      if(listening){
        sensorManager.unregisterListener(listener);
        listening=false;
      }
    }

    public void registerListener(){
      if(!listening){
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        listening=true;
      }
    }

    class sensorEventListener implements SensorEventListener {
      @Override
      public void onSensorChanged(SensorEvent event) {
        lastEvent=event;
        str = SensorUtils.praseData(sensor.getType(),event.values);

        updateText(id);
        Log.d("Sensors_Dev","Upd");
      }
      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
      }
    }
  }


}
