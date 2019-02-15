package donlon.android.sensors.activities;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import donlon.android.sensors.CustomSensor;
import donlon.android.sensors.R;
import donlon.android.sensors.RecordingManager;
import donlon.android.sensors.SensorController;
import donlon.android.sensors.utils.Logger;
import donlon.android.sensors.utils.SensorUtils;

public class SensorDetailsActivity extends AppCompatActivity implements RecordingManager.OnRecordingCanceledListener {
  private static final int DATA_QUEUE_SAMPLES_COUNT = 256;

  private SensorController sensorManager;
  private RecordingManager recordingManager;

  private int mSensorPos;
  private CustomSensor mSensor;

  private TextView tvSensorPrimaryName;
  private TextView tvSensorSecondaryName;
  private TextView tvValue_1;
  private TextView tvValue_2;
  private TextView tvValue_3;
  private MenuItem menuPause;

  private ActionBar mActionBar;

  private Handler mHandler = new Handler();

  private final Runnable mUiUpdaterRunnable = new Runnable() {
    @Override
    public void run() {
      mActionBar.setTitle("Hits per second: " + eventHits);
      eventHits = 0;
      mHandler.postDelayed(this, 1000);
    }
  };

  //TODO: recycling
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initializeUi();
    initializeSensor();
    recordingManager = RecordingManager.getInstance();

    new Thread().start();
  }

  @Override
  protected void onResume() {
    super.onResume();
    sensorManager.enableSensor(mSensor);
    sensorManager.setSensorOnChangeCallback(this::onSensorChanged);
    mUiUpdaterRunnable.run();
  }

  @Override
  protected void onPause() {
    super.onPause();
    sensorManager.disableSensor(mSensor);
    sensorManager.setSensorOnChangeCallback(null);
    mHandler.removeCallbacks(mUiUpdaterRunnable);
  }

  private void initializeUi() {
    setContentView(R.layout.sensor_details_activity);

    tvSensorPrimaryName = findViewById(R.id.tvSensorPrimaryName);
    tvSensorSecondaryName = findViewById(R.id.tvSensorSecondaryName);
    tvValue_1 = findViewById(R.id.tvValue_1);
    tvValue_2 = findViewById(R.id.tvValue_2);
    tvValue_3 = findViewById(R.id.tvValue_3);

    mActionBar = getSupportActionBar();
    if (mActionBar != null) {
      mActionBar.setHomeButtonEnabled(true);
      mActionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  private void initializeSensor() {
    mSensorPos = getIntent().getIntExtra("SensorPos", -1);
    sensorManager = SensorController.getInstance();
    if (mSensorPos >= sensorManager.getSensorList().size()) {
      Logger.is("Sensor position is unexpectedly wrong");
      finish();
      return;
    }

    mSensor = sensorManager.getSensorList().get(mSensorPos);// TODO: fashion singleton
    Sensor sensorInternal = mSensor.getSensor();

    tvSensorPrimaryName.setText(SensorUtils.getSensorNameByType(sensorInternal.getType()));
    tvSensorSecondaryName.setText(sensorInternal.getName() + " By " + sensorInternal.getVendor());

    switch (mSensor.dataDimension) {
      case 1:
        tvValue_2.setVisibility(View.GONE);
      case 2:
        tvValue_3.setVisibility(View.GONE);
      case 3:
        break;
      default:
        Logger.is("Unexpected data dimension");
        break;
    }
    //TODO: if Landscape...
    mActionBar.setTitle(sensorInternal.getName());
  }

  private int eventHits = 0;

  public void onSensorChanged(CustomSensor sensor, SensorEvent event) {
    if (sensor != mSensor) {
      throw new IllegalArgumentException();
    }
    eventHits++;

    tvValue_1.setText(String.valueOf(event.values[0]));
    try {
      switch (mSensor.dataDimension) {
        case 3:
          tvValue_3.setText(String.valueOf(event.values[2]));
        case 2:
          tvValue_2.setText(String.valueOf(event.values[1]));
        case 1:
          tvValue_1.setText(String.valueOf(event.values[0]));
          break;
        default:
          Logger.is("Unexpected data dimension");
          break;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.sensor_details_activity_menu, menu);
    menuPause = menu.findItem(R.id.menuPause);
    return true;
  }

  private boolean mViewingPaused = true;

  /**
   * Event listener for clicks on title bar.
   *
   * @param item menu item
   * @return what should be returned
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menuPause:
        if (mViewingPaused) {
          resumeViewing();
        } else {
          pauseViewing();
        }
        break;
      case R.id.menuRecord:
        pauseViewing();
        recordingManager.setOnRecordingCanceledListener(this);
        recordingManager.showStarterDialog(this, mSensorPos);
        break;
      case android.R.id.home: // back button
        finish();
        break;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private void pauseViewing() {
    menuPause.setTitle(R.string.start);
    mViewingPaused = true;
  }

  private void resumeViewing() {
    menuPause.setTitle(R.string.pause);
    mViewingPaused = false;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onRecordingCanceled() {
    resumeViewing();
  }
}