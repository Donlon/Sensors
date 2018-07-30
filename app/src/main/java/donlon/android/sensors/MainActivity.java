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
  //public static List<CustomSensor> sensorsList;
  public static SensorsManager sensorsManager;
  private ListView sensorsListView;
  private ListAdapter sensorsListAdapter;
  private Switch updateSwitch;

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
        log(s);
      }
    });
    Log.d("Sensors_Dev", "GO ON!");
    sensorsManager = new SensorsManager(this);
    initializeUi();
  }

  private void initializeUi() {
    //	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.main);
    log("Start");

    sensorsListAdapter = new SensorsListAdapter(this, sensorsManager.getSensorList());

    sensorsListView = findViewById(R.id.lvSensors);
    sensorsListView.setOnItemClickListener(listViewClickListener);
    sensorsListView.setAdapter(sensorsListAdapter);
    sensorsListView.requestLayout();
    sensorsManager.startSensorsPreview();

    updateSwitch = findViewById(R.id.swUpdate);
    updateSwitch.setOnCheckedChangeListener(onUpdateSwitchListener);
  }

  private CompoundButton.OnCheckedChangeListener onUpdateSwitchListener = new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      if(isChecked){
        sensorsManager.startSensorsPreview();
      }else{
        sensorsManager.stopSensorsPreview();
      }
    }
  };

  private AdapterView.OnItemClickListener listViewClickListener = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      log("Sensor Event: id=" + id);
      startSensorDetailsActivity(position);
    }
  };

  private void startSensorDetailsActivity(int position) {
    sensorsManager.stopSensorsPreview();

    Intent intent = new Intent(MainActivity.this, SensorDetailsActivity.class);
    intent.putExtra("SensorPos", position);
    startActivity(intent);
  }

  static void log(String str){
    Log.i("Sensors_Dev", str);
  }
}
