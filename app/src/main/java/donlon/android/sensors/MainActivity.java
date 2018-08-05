package donlon.android.sensors;
import java.io.StringWriter;
import java.io.PrintWriter;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v7.app.AlertDialog;
import android.widget.*;
import android.view.*;

import donlon.android.sensors.utils.LOG;

public class MainActivity extends Activity {
  //public static List<CustomSensor> sensorsList;
  public static SensorsManager sensorsManager;
  private ListView sensorsListView;
  private SensorsListAdapter sensorsListAdapter;
  private Switch updateSwitch;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(final Thread thread, final Throwable throwable) {
        throwable.printStackTrace();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        final String s = sw.toString();
        LOG.d(s);

        MainActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(s)
                    .setTitle(throwable.getClass().getName())
                    .show();
          }
        });
      }
    });
    LOG.d( "GO ON!");
    sensorsManager = new SensorsManager(this);
    initializeUi();

    sensorsManager.startSensorsPreview();
  }

  private void initializeUi() {
    //	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.main);
    LOG.d("Start");

    sensorsListAdapter = new SensorsListAdapter(this, sensorsListView, sensorsManager.getSensorList());
    sensorsListAdapter.setOnCbxCheckedListener(sensorsManager);
    sensorsListView = findViewById(R.id.lvSensors);
    sensorsListView.setOnItemClickListener(listViewClickListener);
    sensorsListView.setAdapter(sensorsListAdapter);
//    sensorsListView.requestLayout();
    updateSwitch = findViewById(R.id.swUpdate);
    updateSwitch.setOnCheckedChangeListener(onUpdateSwitchListener);
  }

  private CompoundButton.OnCheckedChangeListener onUpdateSwitchListener = new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      if(isChecked){
        sensorsManager.startSensorsPreview();
        sensorsListAdapter.enableAllCheckBoxes();
      }else{
        sensorsManager.stopSensorsPreview();
        sensorsListAdapter.disableAllCheckBoxes();
      }
    }
  };

  private AdapterView.OnItemClickListener listViewClickListener = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      LOG.d("Sensor item clicked: id=" + id);
      updateSwitch.setChecked(false);
      startSensorDetailsActivity(position);
    }
  };

  private void startSensorDetailsActivity(int position) {
    sensorsManager.stopSensorsPreview();

    Intent intent = new Intent(MainActivity.this, SensorDetailsActivity.class);
    intent.putExtra("SensorPos", position);
    startActivity(intent);
  }
}
