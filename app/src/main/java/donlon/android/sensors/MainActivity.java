package donlon.android.sensors;
import java.io.StringWriter;
import java.io.PrintWriter;

import android.content.*;
import android.os.*;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.*;
import android.view.*;

import donlon.android.sensors.utils.LOG;

public class MainActivity extends AppCompatActivity {
  public static SensorsManager mSensorsManager;
  private SensorsListAdapter mSensorsListAdapter;
  private ListView sensorsListView;
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
    LOG.d( "Here we go!");
    mSensorsManager = new SensorsManager(this);
    initializeUi();

    mSensorsManager.registerCallbacksForAllSensors(mSensorsListAdapter);
  }

  private void initializeUi() {
    setContentView(R.layout.main);
    LOG.d("Start");

    mSensorsListAdapter = new SensorsListAdapter(this, sensorsListView, mSensorsManager.getSensorList());
    mSensorsListAdapter.setOnCbxCheckedListener(mSensorsManager);
    sensorsListView = findViewById(R.id.lvSensors);
    sensorsListView.setOnItemClickListener(listViewClickListener);
    sensorsListView.setAdapter(mSensorsListAdapter);
//    sensorsListView.requestLayout();
    updateSwitch = findViewById(R.id.swUpdate);
    updateSwitch.setOnCheckedChangeListener(onUpdateSwitchListener);
  }


  /**
   * Noting for the last state of "Keep Updating" btn.
   */
  private boolean mCheckBtnLastChecked = true;

  @Override
  public void onPause(){
    super.onPause();
    mCheckBtnLastChecked = updateSwitch.isChecked();
    updateSwitch.setChecked(false);
  }

  @Override
  public void onResume(){
    super.onResume();
    updateSwitch.setChecked(mCheckBtnLastChecked);
  }

  /**
   * Listener
   */
  private CompoundButton.OnCheckedChangeListener onUpdateSwitchListener = new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      if(isChecked){
        mSensorsManager.registerCallbacksForAllSensors(mSensorsListAdapter);
        mSensorsListAdapter.enableAllCheckBoxes();
      }else{
        mSensorsManager.clearCallbacksForAllSensors();
        mSensorsListAdapter.disableAllCheckBoxes();
      }
    }
  };

  /**
   * Listener
   */
  private AdapterView.OnItemClickListener listViewClickListener = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      startSensorDetailsActivity(position);
    }
  };

  private void startSensorDetailsActivity(int position) {
    Intent intent = new Intent(MainActivity.this, SensorDetailsActivity.class);
    intent.putExtra("SensorPos", position);
    startActivityForResult(intent, 404);
  }

}
