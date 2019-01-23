package donlon.android.sensors.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.io.PrintWriter;
import java.io.StringWriter;

import donlon.android.sensors.R;
import donlon.android.sensors.RecordingManager;
import donlon.android.sensors.SensorsListAdapter;
import donlon.android.sensors.SensorsManager;
import donlon.android.sensors.utils.LOG;

public class MainActivity extends AppCompatActivity implements RecordingManager.OnRecordingCanceledListener {

  private SharedPreferences sharedPreferences;
  public SensorsManager mSensorsManager;
  private SensorsListAdapter mSensorsListAdapter;
  private RecordingManager mRecordingManager;

  private ListView sensorsListView;
  private Switch updateSwitch;
  private FloatingActionButton fabMain;
  private boolean mIsOpeningRecordingActivity;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
      throwable.printStackTrace();
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      throwable.printStackTrace(pw);
      pw.flush();
      final String s = sw.toString();
      LOG.i(s);

      runOnUiThread(() -> new AlertDialog.Builder(MainActivity.this).setMessage(s).setTitle(throwable.getClass().getName()).show());
    });

    sharedPreferences = getSharedPreferences("Default", Context.MODE_PRIVATE);

    //    mCheckBtnLastChecked = savedInstanceState.getBoolean("updating_btn_checked", true);
    LOG.d("Here we go!");
    mSensorsManager = SensorsManager.create(this);
    mRecordingManager = RecordingManager.create(mSensorsManager);
    initializeUi();

    mSensorsManager.registerCallbacksForAllSensors(mSensorsListAdapter);
  }

  private void initializeUi() {
    setContentView(R.layout.main_activity);
    LOG.d("Start");

    mSensorsListAdapter = new SensorsListAdapter(this, sensorsListView, mSensorsManager.getSensorList());
    mSensorsListAdapter.setOnCbxCheckedListener(mSensorsManager);
    sensorsListView = findViewById(R.id.lvSensors);
    updateSwitch = findViewById(R.id.swUpdate);
    fabMain = findViewById(R.id.fab_main);

    sensorsListView.setOnItemClickListener(listViewClickListener);
    sensorsListView.setAdapter(mSensorsListAdapter);
    updateSwitch.setOnCheckedChangeListener(onUpdateSwitchListener);
    fabMain.setOnClickListener(v -> {
      saveUpdateSwitchState();
      mIsOpeningRecordingActivity = true;
      mRecordingManager.showStarterDialog(MainActivity.this);
    });
    mRecordingManager.setOnRecordingCanceledListener(this);
  }

  /**
   * It doesn't work.
   *
   * @param outState outState
   */
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean("updating_btn_checked", mCheckBtnLastChecked);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
  }

  /**
   * Noting for the last state of "Keep Updating" btn.
   */
  @Deprecated
  private boolean mCheckBtnLastChecked = true;

  @Override
  public void onPause() {
    super.onPause();
    saveUpdateSwitchState();
  }

  @Override
  public void onResume() {
    super.onResume();
    mIsOpeningRecordingActivity = false;
    rollbackUpdateSwitchState();
  }

  @Override
  public void overridePendingTransition(int enterAnim, int exitAnim) {
    super.overridePendingTransition(enterAnim, 0);
  }

  private void saveUpdateSwitchState() {
    if (mIsOpeningRecordingActivity) {
      return;
    }
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean("updating_btn_checked", updateSwitch.isChecked());
    editor.apply();
    updateSwitch.setChecked(false);
  }

  private void rollbackUpdateSwitchState() {
    updateSwitch.setChecked(sharedPreferences.getBoolean("updating_btn_checked", true));
  }

  @Override
  public void onRecordingCanceled(boolean succeed) {
    rollbackUpdateSwitchState();
  }

  /**
   * Listener
   */
  private CompoundButton.OnCheckedChangeListener onUpdateSwitchListener = new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      if (isChecked) {
        mSensorsManager.registerCallbacksForAllSensors(mSensorsListAdapter);
        mSensorsListAdapter.enableAllCheckBoxes();
      } else {
        mSensorsManager.clearCallbacksForAllSensors();
        mSensorsListAdapter.disableAllCheckBoxes();
      }
    }
  };

  /**
   * Listener
   */
  private AdapterView.OnItemClickListener listViewClickListener = (parent, view, position, id) -> startSensorDetailsActivity(position);

  private void startSensorDetailsActivity(int position) {
    Intent intent = new Intent(MainActivity.this, SensorDetailsActivity.class);
    intent.putExtra("SensorPos", position);
    startActivity(intent);
  }
}
