package donlon.android.sensors.activities;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import donlon.android.sensors.R;
import donlon.android.sensors.sensor.CustomSensor;
import donlon.android.sensors.sensor.SensorController;
import donlon.android.sensors.utils.Logger;
import donlon.android.sensors.utils.SensorSelectorDialogBuilder;
import donlon.android.sensors.utils.SensorUtils;

public class SensorDetailsActivity extends AppCompatActivity {
  private SensorController mSensorManager;
  private CustomSensor mSensor;

  private boolean mIsViewing = true;
  private boolean mIsMenuCreated = false;
  private int eventHits = 0;

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
    initializeSensor();
    initializeUi();
  }

  private void initializeUi() {
    setContentView(R.layout.sensor_details_activity);

    tvSensorPrimaryName = findViewById(R.id.tvSensorPrimaryName);
    tvSensorSecondaryName = findViewById(R.id.tvSensorSecondaryName);
    tvValue_1 = findViewById(R.id.tvValue_1);
    tvValue_2 = findViewById(R.id.tvValue_2);
    tvValue_3 = findViewById(R.id.tvValue_3);

    Sensor sensorInternal = mSensor.getSensor();
    tvSensorPrimaryName.setText(SensorUtils.getSensorNameByType(sensorInternal.getType()));
    tvSensorSecondaryName.setText(String.format("%s By %s", sensorInternal.getName(), sensorInternal.getVendor()));

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

    mActionBar = getSupportActionBar();
    if (mActionBar != null) {
      mActionBar.setHomeButtonEnabled(true);
      mActionBar.setDisplayHomeAsUpEnabled(true);
      //TODO: if Landscape...
      mActionBar.setTitle(sensorInternal.getName());
    }
  }

  private void initializeSensor() {
    mSensorManager = SensorController.getInstance();
    int mSensorPos = getIntent().getIntExtra("SensorPos", -1);
    mSensor = mSensorManager.getSensorList().get(mSensorPos);// TODO: fashion singleton
    assert mSensor != null;
  }

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
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menuPause:
        if (mIsViewing) {
          pauseViewing();
        } else {
          resumeViewing();
        }
        break;
      case R.id.menuRecord:
        if (mIsViewing) {
          pauseViewing();
        }
        SensorSelectorDialogBuilder builder = new SensorSelectorDialogBuilder(this, mSensorManager)
            .setActivity(this)
            .setOnRecordingFinishListener(this::resumeViewing)
            .setSensorSelected(mSensor);
        builder.show();
        break;
      case android.R.id.home: // back button
        finish();
        break;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private void resumeViewing() {
    mIsViewing = true;
    mSensorManager.enableSensor(mSensor);
    mSensorManager.setOnSensorChangeListener(this::onSensorChanged);
    mUiUpdaterRunnable.run();
    if (mIsMenuCreated) {
      menuPause.setTitle(R.string.pause);
    }
  }

  private void pauseViewing() {
    mIsViewing = false;
    mSensorManager.disableSensor(mSensor);
    mSensorManager.setOnSensorChangeListener(null);
    mHandler.removeCallbacks(mUiUpdaterRunnable);
    menuPause.setTitle(R.string.start);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.sensor_details_activity_menu, menu);
    menuPause = menu.findItem(R.id.menuPause);
    mIsMenuCreated = true;
    return true;
  }

  @Override
  protected void onResume() {
    super.onResume();
    resumeViewing();
  }

  @Override
  protected void onPause() {
    super.onPause();
    pauseViewing();
  }
}