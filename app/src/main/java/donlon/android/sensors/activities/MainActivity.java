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

  private Switch swUpdate;

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

    FloatingActionButton fabMain = findViewById(R.id.fab_main);

    RecyclerView sensorList = findViewById(R.id.lvSensors);

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(RecyclerView.VERTICAL);
    sensorList.setLayoutManager(layoutManager);
//    sensorList.setOnItemClickListener((parent, view, position, id) -> startSensorDetailsActivity(position));
    sensorList.setAdapter(mSensorListAdapter);
    sensorList.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
    sensorList.setItemAnimator(new DefaultItemAnimator());

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
