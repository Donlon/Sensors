package donlon.android.sensors.activities;

import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import donlon.android.sensors.R;
import donlon.android.sensors.adapters.SensorsListAdapter;
import donlon.android.sensors.sensor.SensorController;
import donlon.android.sensors.utils.Logger;
import donlon.android.sensors.utils.SensorSelectorDialogBuilder;
import donlon.android.sensors.utils.StorageUtils;

public class MainActivity extends AppCompatActivity {
  private SensorController mSensorController;
  private SensorsListAdapter mSensorListAdapter;

  private Switch updateSwitch;

  private boolean mIsLastViewing;
  private boolean mIsViewing = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> throwable.printStackTrace());

    Logger.d("Here we go!");
    mSensorController = SensorController.create(this);
    mSensorListAdapter = new SensorsListAdapter(this, mSensorController.getSensorList());
    mSensorController.setOnSensorChangeListener(mSensorListAdapter);

    mIsLastViewing = StorageUtils.getDefaultViewingState(this);
    initializeUi();
  }

  private void initializeUi() {
    setContentView(R.layout.main_activity);

    updateSwitch = findViewById(R.id.swUpdate);
    updateSwitch.setChecked(mIsLastViewing);
    updateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> onSwitchCheckedChange(isChecked));

    FloatingActionButton fabMain = findViewById(R.id.fab_main);

    RecyclerView sensorList = findViewById(R.id.lvSensors);

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(RecyclerView.VERTICAL);
    sensorList.setLayoutManager(layoutManager);
    sensorList.setAdapter(mSensorListAdapter);
    sensorList.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
    sensorList.setItemAnimator(new DefaultItemAnimator());

    fabMain.setOnClickListener(v -> onFloatBtnClick());
  }

  private void onFloatBtnClick() {
    if (mIsViewing) {
      pauseViewing();
    }
    SensorSelectorDialogBuilder builder = new SensorSelectorDialogBuilder(this, mSensorController)
        .setActivity(this)
        .setOnCancelListener(() -> {
          if (mIsLastViewing) {
            resumeViewing();
          }
        });
    builder.show();
  }

  public void onSwitchCheckedChange(boolean isChecked) {
    if (isChecked != mIsViewing) { // actively update
      if (mIsViewing) {
        pauseViewing();
      } else {
        resumeViewing();
      }
      StorageUtils.saveDefaultViewingState(this, isChecked);
      mIsLastViewing = isChecked;
    }
  }

  private void resumeViewing() {
    if (mIsViewing) {
      throw new IllegalStateException();
    }
    mIsViewing = true;
    updateSwitch.setChecked(true);
    mSensorController.enableAllSensors();
    mSensorController.setOnSensorChangeListener(mSensorListAdapter);
    mSensorListAdapter.startUpdating();
  }

  private void pauseViewing() {
    if (!mIsViewing) {
      throw new IllegalStateException();
    }
    mIsViewing = false;
    updateSwitch.setChecked(false);
    mSensorController.disableAllSensors();
    mSensorController.setOnSensorChangeListener(null);
    mSensorListAdapter.stopUpdating();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mIsLastViewing) {
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
