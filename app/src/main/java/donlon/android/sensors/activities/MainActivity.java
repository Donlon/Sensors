package donlon.android.sensors.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Switch;

import donlon.android.sensors.R;
import donlon.android.sensors.adapters.SensorsListAdapter;
import donlon.android.sensors.sensor.SensorController;
import donlon.android.sensors.utils.Logger;
import donlon.android.sensors.utils.SensorSelectorDialogBuilder;
import donlon.android.sensors.utils.StorageUtils;

public class MainActivity extends AppCompatActivity {
  private SensorController mSensorController;
  private SensorsListAdapter mSensorListAdapter;

  private ListView lvSensors;
  private Switch swUpdate;
  private FloatingActionButton fabMain;

  private boolean mIsPreviewSwitchOn;
  private boolean mIsViewing = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> throwable.printStackTrace());

    Logger.d("Here we go!");
    mSensorController = SensorController.create(this);
    mSensorListAdapter = new SensorsListAdapter(this, mSensorController.getSensorList());
    mSensorController.setOnSensorChangeListener(mSensorListAdapter);
    mIsPreviewSwitchOn = StorageUtils.getDefaultViewingState(this);
    initializeUi();
  }

  private void initializeUi() {
    setContentView(R.layout.main_activity);
    Logger.d("Start");

    swUpdate = findViewById(R.id.swUpdate);
    swUpdate.setOnCheckedChangeListener((buttonView, isChecked) -> onSwitchCheckedChange(isChecked));

    fabMain = findViewById(R.id.fab_main);

    lvSensors = findViewById(R.id.lvSensors);
    lvSensors.setOnItemClickListener((parent, view, position, id) -> startSensorDetailsActivity(position));
    lvSensors.setAdapter(mSensorListAdapter);

    fabMain.setOnClickListener(v -> {
      if (mIsViewing) {
        pauseViewing();
      }
      SensorSelectorDialogBuilder builder = new SensorSelectorDialogBuilder(this, mSensorController)
          .setActivity(this)
          .setOnRecordingFinishListener(() -> {
            if (mIsPreviewSwitchOn) {
              resumeViewing();
            }
          });
      builder.show();
    });
  }

  public void onSwitchCheckedChange(boolean isChecked) {
    if (isChecked != mIsViewing) { // actively update
      if (mIsViewing) {
        pauseViewing();
      } else {
        resumeViewing();
      }
      StorageUtils.saveDefaultViewingState(this, isChecked);
      mIsPreviewSwitchOn = isChecked;
    }
  }

  private void startSensorDetailsActivity(int position) {
    Intent intent = new Intent(MainActivity.this, SensorDetailsActivity.class);
    intent.putExtra("SensorPos", position);
    startActivity(intent);
  }

  private void resumeViewing() {
    if (mIsViewing) {
      throw new IllegalStateException();
    }
    mIsViewing = true;
    swUpdate.setChecked(true);
    mSensorController.enableAllSensors();
    mSensorController.setOnSensorChangeListener(mSensorListAdapter);
    mSensorListAdapter.startUpdating();
  }

  private void pauseViewing() {
    if (!mIsViewing) {
      throw new IllegalStateException();
    }
    mIsViewing = false;
    swUpdate.setChecked(false);
    mSensorController.disableAllSensors();
    mSensorController.setOnSensorChangeListener(null);
    mSensorListAdapter.stopUpdating();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mIsPreviewSwitchOn) {
      resumeViewing();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (mIsViewing) {
      pauseViewing();
    }
  }
}
