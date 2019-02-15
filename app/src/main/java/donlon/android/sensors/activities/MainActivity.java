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
import donlon.android.sensors.SensorController;
import donlon.android.sensors.adapters.SensorsListAdapter;
import donlon.android.sensors.utils.Logger;
import donlon.android.sensors.utils.SensorSelectorDialogBuilder;

public class MainActivity extends AppCompatActivity {

  private SharedPreferences sharedPreferences;
  public SensorController mSensorController;
  private SensorsListAdapter mSensorListAdapter;
  private RecordingManager mRecordingManager;

  private ListView sensorListView;
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
      String s = sw.toString();
      Logger.i(s);

      runOnUiThread(() -> new AlertDialog.Builder(getApplicationContext()).setMessage(s).setTitle(throwable.getClass().getName()).show());
    });

    sharedPreferences = getSharedPreferences("Default", Context.MODE_PRIVATE);

    //    mCheckBtnLastChecked = savedInstanceState.getBoolean("updating_btn_checked", true);
    Logger.d("Here we go!");
    mSensorController = SensorController.create(this);
    mSensorListAdapter = new SensorsListAdapter(this, mSensorController.getSensorList());
    mSensorController.setSensorOnChangeCallback(mSensorListAdapter);
    mRecordingManager = RecordingManager.create(mSensorController);
    initializeUi();
  }

  private void initializeUi() {
    setContentView(R.layout.main_activity);
    Logger.d("Start");

    updateSwitch = findViewById(R.id.swUpdate);
    updateSwitch.setOnCheckedChangeListener(this::onSwitchCheckedChanged);

    fabMain = findViewById(R.id.fab_main);

    sensorListView = findViewById(R.id.lvSensors);
    sensorListView.setOnItemClickListener(this::onSensorListItemClick);
    sensorListView.setAdapter(mSensorListAdapter);

    fabMain.setOnClickListener(v -> {
      saveUpdateSwitchState();
      mIsOpeningRecordingActivity = true;
      SensorSelectorDialogBuilder builder = new SensorSelectorDialogBuilder(this, mSensorController).setActivity(this).setOnRecordingFinishListener(this::rollbackUpdateSwitchState);
      builder.show();
    });
  }

  /**
   * It doesn't work.
   *
   * @param outState outState
   */
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    //    outState.putBoolean("updating_btn_checked", mCheckBtnLastChecked);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
  }

  /**
   * Noting for the last state of "Keep Updating" btn.
   */
  //  @Deprecated
  //  private boolean mCheckBtnLastChecked = true;

  @Override
  public void onPause() {
    super.onPause();
    mSensorController.disableAllSensors();
    saveUpdateSwitchState();
  }

  @Override
  public void onResume() {
    super.onResume();
    mSensorController.enableAllSensors();
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

  public void onSwitchCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
      mSensorController.enableAllSensors();
      //        mSensorListAdapter.enableAllCheckBoxes();
    } else {
      mSensorController.disableAllSensors();
      //        mSensorListAdapter.disableAllCheckBoxes();
    }
  }

  private void startSensorDetailsActivity(int position) {
    Intent intent = new Intent(MainActivity.this, SensorDetailsActivity.class);
    intent.putExtra("SensorPos", position);
    startActivity(intent);
  }

  private void onSensorListItemClick(AdapterView<?> parent, View view, int position, long id) {
    startSensorDetailsActivity(position);
  }
}
