package donlon.android.sensors.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import donlon.android.sensors.CustomSensor;
import donlon.android.sensors.R;
import donlon.android.sensors.RecordingManager;
import donlon.android.sensors.utils.LOG;
import donlon.android.sensors.utils.cpu.CpuUsage;
import donlon.android.sensors.utils.cpu.CpuUsageImplement;

public class RecordingActivity extends AppCompatActivity implements RecordingManager.OnRecordingFailedListener {
  private final static int PERMISSIONS_REQUEST_READ_AND_WRITE_FILES = 1;
  private final static String SHARED_PREFERENCES_RECORDING_TIMES = "recording_times";

  private SharedPreferences sharedPreferences;

  private RecordingManager recordingManager;
  private ColorStateList colorTvSavePath;
  private String mDataFilePath;

  //UI Components
  private android.support.v7.app.ActionBar mActionBar;
  private TextView tvSavePath;
  private TextView tvElapsedTime;
  private TextView tvStatus;
  private TextView tvCpuUsage;
  private TextView tvWrittenFrames;
  private TextView tvWrittenBytes;
  private ImageView ivWritingFlashLight;
  private TableLayout tblSensorsInfo;

  private RecordingManagerWidgetsEditor mCurrentScreenEditor;

  private boolean mFirstRecord = true;

  private Runnable mCpuUsageUpdateRunnable = new Runnable() {
    private CpuUsage sysSummaryCpuUsage = CpuUsageImplement.createSysSummaryCpuUsage();
    private CpuUsage currentProcCpuUsage = CpuUsageImplement.createCurrentProcessCpuUsage();
    private float sysSummaryCpuUsageValue;
    private float currentProcCpuUsageValue;
    private DecimalFormat percentageFormatter = new DecimalFormat("##%");

    //    private char parity;
    @SuppressLint("SetTextI18n")
    @Override
    public void run() {
      //      parity++;
      sysSummaryCpuUsageValue = sysSummaryCpuUsage.requestCpuUsage();
      currentProcCpuUsageValue = currentProcCpuUsage.requestCpuUsage();
      tvCpuUsage.setText(percentageFormatter.format(sysSummaryCpuUsageValue) + "/" + percentageFormatter.format(currentProcCpuUsageValue));

      //      if(recordingManager.isRecording()){//TODO:
      tvCpuUsage.postDelayed(this, 873);
      //      }
    }
  };
  //Wondering why mHandler in class Activity can't be obtained...
  private Handler mHandler = new Handler();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LOG.i("Here we go!");
    sharedPreferences = getSharedPreferences("Default", Context.MODE_PRIVATE);
    recordingManager = RecordingManager.getInstance();
    initializeUi();
    mCpuUsageUpdateRunnable.run();

    recordingManager.setOnRecordingFailedListener(this);

    if (Build.VERSION.SDK_INT >= 23) {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        onPermissionGranted();
      } else {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_AND_WRITE_FILES);
      }
    } else {
      onPermissionGranted();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST_READ_AND_WRITE_FILES && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
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

    colorTvSavePath = tvSavePath.getTextColors();

    mActionBar = getSupportActionBar();
    if (mActionBar != null) {
      mActionBar.setHomeButtonEnabled(true);
      mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    mCurrentScreenEditor = new RecordingManagerWidgetsEditor();
    mCurrentScreenEditor.tvElapsedTime = tvElapsedTime;
    mCurrentScreenEditor.tvStatus = tvStatus;
    mCurrentScreenEditor.tvWrittenFrames = tvWrittenFrames;
    mCurrentScreenEditor.tvWrittenBytes = tvWrittenBytes;
    mCurrentScreenEditor.listTvLastHits = new HashMap<>();
    mCurrentScreenEditor.listTvAllHits = new HashMap<>();
    mCurrentScreenEditor.ivWritingFlashLight = ivWritingFlashLight;

    // Init TableLayout
    int count = 0;
    for (CustomSensor sensor : recordingManager.getSensorsToRecord()) {//TODO: when another moment?
      View row = getLayoutInflater().inflate(R.layout.recording_activity_details_table_row, tblSensorsInfo, false);

      TextView tvName = row.findViewById(R.id.tvSensorName);
      tvName.setText(sensor.primaryName);
      TextView tvId = row.findViewById(R.id.tvId);
      tvId.setText(String.valueOf(sensor.id));

      tblSensorsInfo.addView(row, ++count);
      mCurrentScreenEditor.listTvLastHits.put(sensor, (TextView) row.findViewById(R.id.tvLastHits));
      mCurrentScreenEditor.listTvAllHits.put(sensor, (TextView) row.findViewById(R.id.tvAllHits));
    }
    TableRow rowAllSensors = findViewById(R.id.rowAllSensors);

    if (recordingManager.getSensorsToRecord().size() == 1) {
      rowAllSensors.setVisibility(View.GONE);
    }
    //Still add "null" key for compatibility
    mCurrentScreenEditor.listTvLastHits.put(null, (TextView) findViewById(R.id.tvAllSensorsLastHits));
    mCurrentScreenEditor.listTvAllHits.put(null, (TextView) findViewById(R.id.tvAllSensorsAllHits));

    if (recordingManager.isRecording()) {
      recordingManager.setWidgetEditor(mCurrentScreenEditor);
      mCpuUsageUpdateRunnable.run();
    }
  }

  private void onPermissionGranted() {
    //following initializeUi()
    //Start to init the manager
    if (recordingManager.isRecording()) {
      mDataFilePath = recordingManager.getDataFilePath();
      startRecording();
    } else {
      mDataFilePath = buildDataFilePath();
      recordingManager.setDataFilePath(mDataFilePath);
      recordingManager.init();

      if (!recordingManager.initialized()) {
        Toast.makeText(this, "RecordingManager init failed.", Toast.LENGTH_SHORT).show();
      }
    }
    tvSavePath.setText("Saved Location: " + mDataFilePath);
  }

  private String buildDataFilePath() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

    return Environment.getExternalStorageDirectory() + "/sensor_data_" + sharedPreferences.getInt(SHARED_PREFERENCES_RECORDING_TIMES, 0) + "_" + sdf.format(new Date()) + ".data";
  }

  private void startRecordingManager() {
    mFirstRecord = false;
    if (!recordingManager.initialized()) {
      Toast.makeText(RecordingActivity.this, "RecordingManager init failed.", Toast.LENGTH_SHORT).show();
    } else {
      startRecording();
      recordingManager.startRecording();

      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putInt(SHARED_PREFERENCES_RECORDING_TIMES, sharedPreferences.getInt(SHARED_PREFERENCES_RECORDING_TIMES, 0) + 1);
      editor.apply();
    }
  }

  private void startRecording() {//won't call RecordingManager
    tvStatus.setText(R.string.status_recording);
    tvStatus.setTextColor(Color.RED);
    keepScreenLongLight(true);
  }

  private void stopRecordingManager() {
    if (recordingManager.isRecording()) {
      keepScreenLongLight(false);
      recordingManager.stopRecording();
      //      tvCpuUsage.removeCallbacks(mCpuUsageUpdateRunnable);
    }
  }

  @Override
  public void onRecordingFailed() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(RecordingActivity.this, "Recording failed.", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (isFinishing()) {
      recordingManager.finish();
    } else {
      LOG.i("RecordingActivity Stopped");
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    recordingManager.setWidgetEditor(null);//TODO: Must set free when appropriate
  }

  @Override
  protected void onResume() {
    super.onResume();
    recordingManager.setWidgetEditor(mCurrentScreenEditor);
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
        if (recordingManager.isRecording()) {
          stopRecordingManager();
          Toast.makeText(RecordingActivity.this, "RecordingManager stopped.", Toast.LENGTH_SHORT).show();
          tvSavePath.setTextColor(Color.RED);
        } else {
          if (!mFirstRecord) {
            recordingManager.init();//TODO: Add Reset btn?
          }
          startRecordingManager();
          Toast.makeText(RecordingActivity.this, "RecordingManager started.", Toast.LENGTH_SHORT).show();
          tvSavePath.setTextColor(colorTvSavePath);
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

  public void keepScreenLongLight(boolean isOpenLight) {
    if (isOpenLight) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    } else {
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
  }

  public class RecordingManagerWidgetsEditor {
    public TextView tvElapsedTime;
    public TextView tvStatus;
    public TextView tvWrittenFrames;
    public TextView tvWrittenBytes;
    public Map<CustomSensor, TextView> listTvLastHits;
    public Map<CustomSensor, TextView> listTvAllHits;
    public ImageView ivWritingFlashLight;

    public void runOnUiThread(Runnable runnable) {
      mHandler.post(runnable);
    }

    public void runOnUiThread(Runnable runnable, int delay) {
      mHandler.postDelayed(runnable, delay);
    }

    public void removeRunnable(Runnable runnable) {
      mHandler.removeCallbacks(runnable);
    }
  }
}
