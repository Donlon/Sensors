package donlon.android.sensors.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import donlon.android.sensors.R;
import donlon.android.sensors.recording.RecordingManager;
import donlon.android.sensors.recording.SensorEventsBuffer;
import donlon.android.sensors.sensor.CustomSensor;
import donlon.android.sensors.sensor.SensorController;
import donlon.android.sensors.utils.FileUtils;
import donlon.android.sensors.utils.FormatterUtils;
import donlon.android.sensors.utils.Logger;
import donlon.android.sensors.utils.cpu.CpuUsage;
import donlon.android.sensors.utils.cpu.EmptyCpuUsage;

public class RecordingActivity extends AppCompatActivity {
  public static final int RECORDING_ACTIVITY_REQUEST_CODE = 0xF401;
  public static final String EXTRA_SELECTED_SENSORS = "SelectedSensors";
  private final static int PERMISSIONS_REQUEST_READ_AND_WRITE_FILES = 1;

  private RecordingManager mRecordingManager;
  private SensorController mSensorController;
  private ColorStateList colorTvSavePath;

  private List<CustomSensor> mSensorsToRecord = new ArrayList<>();
  private Handler mHandler = new Handler();

  //UI Components
  private ActionBar mActionBar;
  private TextView tvSavePath;
  private TextView tvElapsedTime;
  private TextView tvStatus;
  private TextView tvCpuUsage;
  private TextView tvWrittenFrames;
  private TextView tvWrittenBytes;
  private ImageView ivWritingFlashLight;
  private TableLayout tblSensorsInfo;
  private Map<CustomSensor, SensorInfoViewHolder> mViewHolderMap = new HashMap<>();
  private TextView tvAllSensorsLastHits;
  private TextView tvAllSensorsAllHits;

  private Runnable mCpuUsageUpdateRunnable = new Runnable() {
    private CpuUsage sysSummaryCpuUsage = new EmptyCpuUsage();
    private CpuUsage currentProcCpuUsage = new EmptyCpuUsage();
    private float currentProcCpuUsageValue;
    private float sysSummaryCpuUsageValue;
    @Override
    public void run() {
      currentProcCpuUsageValue = currentProcCpuUsage.requestCpuUsage();
      sysSummaryCpuUsageValue = sysSummaryCpuUsage.requestCpuUsage();
      tvCpuUsage.setText(String.format(Locale.ENGLISH, "%.2f%%/%.2f%%",
          currentProcCpuUsageValue,
          sysSummaryCpuUsageValue));
      tvCpuUsage.postDelayed(this, 873);
    }
  };

  /**
   * Triggered each frame (each writing), indirectly called from DataWritingThread
   */
  private Runnable mOnNewFrameRunnable = new Runnable() {
    @Override
    public void run() {
      tvWrittenBytes.setText(FormatterUtils.formatBytes(mRecordingManager.getWrittenBytes()));
      tvWrittenFrames.setText(String.valueOf(mRecordingManager.getWrittenFrames()));
      ivWritingFlashLight.setImageResource(android.R.drawable.star_big_on);

      int count = 0;
      for (Map.Entry<CustomSensor, SensorEventsBuffer> entry : mRecordingManager.getDataBufferMap().entrySet()) {
        int size = entry.getValue().getLastFrameSize();
        mViewHolderMap.get(entry.getKey()).tvLastHits.setText(String.valueOf(size));
        count += size;
      }
      tvAllSensorsLastHits.setText(String.valueOf(count));
      mHandler.postDelayed(() -> {
        ivWritingFlashLight.setImageResource(android.R.drawable.star_off);
        //TODO: what if ivWritingFlashLight is destroyed now?
      }, 100);
    }
  };

  /**
   * Called from UI Thread
   */
  private Runnable mUiTimeUpdateRunnable = new Runnable() {
    private int time = 0;
    private boolean parity;
    @Override
    public void run() {
      mHandler.postDelayed(this, 500);
      tvElapsedTime.setText(String.format("%s (Estimated)", FormatterUtils.getFormattedTime(time, parity)));
      if (parity) {
        time++;
      }
      parity = !parity;
    }
  };

  /**
   * Quickly triggered Runnable, called from UI Thread
   */
  private Runnable uiContinuousUpdateRunnable = new Runnable() {
    @Override
    public void run() {
      int count = 0;
      for (Map.Entry<CustomSensor, SensorEventsBuffer> entry : mRecordingManager.getDataBufferMap().entrySet()) {
        int size = entry.getValue().getCount();
        mViewHolderMap.get(entry.getKey()).tvAllHits.setText(String.valueOf(size));
        count += size;
      }
      tvAllSensorsAllHits.setText(String.valueOf(count));
      mHandler.postDelayed(this, 33);
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Logger.i("Here we go!");
    mSensorController = SensorController.getInstance();
    mRecordingManager = new RecordingManager(mSensorController);
    int[] selectedSensors = getIntent().getIntArrayExtra(EXTRA_SELECTED_SENSORS);
    for (int pos : selectedSensors) {
      CustomSensor sensor = mSensorController.get(pos);
      mSensorsToRecord.add(sensor);
    }
    initializeUi();
    mCpuUsageUpdateRunnable.run();

    mRecordingManager.setOnRecordingFailedListener(this::onRecordingFailed);

    if (Build.VERSION.SDK_INT >= 23) {
      if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
          | ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
          == PackageManager.PERMISSION_GRANTED) {
        onPermissionGranted();
      } else {
        ActivityCompat.requestPermissions(
            this,
            new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},
            PERMISSIONS_REQUEST_READ_AND_WRITE_FILES);
      }
    } else {
      onPermissionGranted();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST_READ_AND_WRITE_FILES
        && (grantResults[0] | grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
      onPermissionGranted();
    }
  }

  private void initializeUi() {
    setContentView(R.layout.recording_activity);
    tvSavePath = findViewById(R.id.tvSavePath);
    tvElapsedTime = findViewById(R.id.tvElapsedTime);
    tvStatus = findViewById(R.id.tvStatus);
    tvCpuUsage = findViewById(R.id.tvCpuUsage);
    tvWrittenFrames = findViewById(R.id.tvWrittenFrames);
    tvWrittenBytes = findViewById(R.id.tvWrittenBytes);
    ivWritingFlashLight = findViewById(R.id.ivWritingFlashLight);
    tblSensorsInfo = findViewById(R.id.tblSensorsInfo);
    mActionBar = getSupportActionBar();
    if (mActionBar != null) {
      mActionBar.setHomeButtonEnabled(true);
      mActionBar.setDisplayHomeAsUpEnabled(true);
    }
    colorTvSavePath = tvSavePath.getTextColors();

    // Init TableLayout
    int count = 0;
    for (CustomSensor sensor : mSensorsToRecord) {//TODO: when another moment?
      View row = getLayoutInflater().inflate(R.layout.recording_activity_details_table_row, tblSensorsInfo, false);
      SensorInfoViewHolder viewHolder = new SensorInfoViewHolder(row);
      viewHolder.tvName.setText(sensor.primaryName);
      viewHolder.tvId.setText(String.valueOf(sensor.getPosition()));
      mViewHolderMap.put(sensor, viewHolder);
      tblSensorsInfo.addView(row, ++count);
    }
    TableRow rowAllSensors = findViewById(R.id.rowAllSensors);
    if (mSensorsToRecord.size() == 1) {
      rowAllSensors.setVisibility(View.GONE);
    }
    tvAllSensorsLastHits = rowAllSensors.findViewById(R.id.tvAllSensorsLastHits);
    tvAllSensorsAllHits = rowAllSensors.findViewById(R.id.tvAllSensorsAllHits);
  }

  private void onPermissionGranted() {
    // following initializeUi()
    // Start to init the manager
    if (mRecordingManager.isRecording()) {
      recordingStartedSetUi();
    } else {
      mRecordingManager.setSensorsToRecord(mSensorsToRecord);
      mRecordingManager.setOnNewFrameListener(mOnNewFrameRunnable);
      mRecordingManager.init();
    }
  }

  private void startRecording() {
    if (!mRecordingManager.initialized()) {
      Toast.makeText(this, "RecordingManager init failed.", Toast.LENGTH_SHORT).show();
      return;
    }
    if (mRecordingManager.isRecording()) {
      throw new IllegalStateException();
    }
    mRecordingManager.setDataFilePath(FileUtils.getNewDataFilePath(this));
    mRecordingManager.startRecording();
    recordingStartedSetUi();
  }

  private void stopRecording() {
    if (!mRecordingManager.isRecording()) {
      throw new IllegalStateException();
    }
    mRecordingManager.stopRecording();
    recordingStoppedSetUi();
  }

  private void recordingStartedSetUi() {
    tvStatus.setVisibility(View.VISIBLE);
    tvSavePath.setTextColor(colorTvSavePath);
    tvSavePath.setText(String.format("Saved Location: %s", mRecordingManager.getDataFilePath()));
    keepScreenLongLight(true);
    startUiUpdating();
    Toast.makeText(this, "RecordingManager started.", Toast.LENGTH_SHORT).show();
  }

  private void recordingStoppedSetUi() {
    tvStatus.setVisibility(View.GONE);
    tvSavePath.setTextColor(Color.RED);
    keepScreenLongLight(false);
    stopUiUpdating();
    Toast.makeText(this, "RecordingManager stopped.", Toast.LENGTH_SHORT).show();
  }

  private void startUiUpdating() {
    mUiTimeUpdateRunnable.run();
    uiContinuousUpdateRunnable.run();
    mCpuUsageUpdateRunnable.run();
  }

  private void stopUiUpdating() {
    mHandler.removeCallbacks(mUiTimeUpdateRunnable);
    mHandler.removeCallbacks(uiContinuousUpdateRunnable);
    mHandler.removeCallbacks(mCpuUsageUpdateRunnable);
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (mRecordingManager.isRecording()) {
      stopUiUpdating();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mRecordingManager.isRecording()) {
      startUiUpdating();
    }
  }

  public void onRecordingFailed() {
    Toast.makeText(this, "Recording failed.", Toast.LENGTH_SHORT).show();
    if (mRecordingManager.isRecording()) {
      stopUiUpdating();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.recording_activity_menu, menu);
    return true;
  }

  /**
   * Event listener for clicks on title bar.
   *
   * @param item menu item
   * @return what should be returned
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.set_storage_folder:
        break;
      case R.id.set_delay:
        break;
      case R.id.start_or_stop:
        if (mRecordingManager.isRecording()) {
          stopRecording();
        } else {
          startRecording();
        }
        break;
      case android.R.id.home: // back button
        finish();
        break;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  public void keepScreenLongLight(boolean isOn) {
    if (isOn) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    } else {
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
  }

  static class SensorInfoViewHolder {
    TextView tvName;
    TextView tvId;
    TextView tvLastHits;
    TextView tvAllHits;
    SensorInfoViewHolder(View view) {
      tvName = view.findViewById(R.id.tvSensorName);
      tvId = view.findViewById(R.id.tvId);
      tvLastHits = view.findViewById(R.id.tvLastHits);
      tvAllHits = view.findViewById(R.id.tvAllHits);
    }
  }
}
