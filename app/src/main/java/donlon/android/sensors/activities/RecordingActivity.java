package donlon.android.sensors.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import donlon.android.sensors.R;
import donlon.android.sensors.RecordingManager;
import donlon.android.sensors.utils.LOG;

public class RecordingActivity extends AppCompatActivity implements RecordingManager.OnRecordingFailedListener{
  private final static int PERMISSIONS_REQUEST_READ_AND_WRITE_FILES = 1;

  private RecordingManager recordingManager;
  private boolean mRecordingManagerInited = false;

  @Override
  public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    recordingManager = RecordingManager.getInstance();
    LOG.i("Here we go!");
    initializeUi();

    recordingManager.setOnRecordingFailedListener(this);


    if(Build.VERSION.SDK_INT >= 23){
      if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
              == PackageManager.PERMISSION_GRANTED &&
              ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                      == PackageManager.PERMISSION_GRANTED){
        onPermissionGranted();
      }else{
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                PERMISSIONS_REQUEST_READ_AND_WRITE_FILES);
      }
    }else{
      onPermissionGranted();
    }


  }

  @Override
  public void onRequestPermissionsResult(
          int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if(requestCode == PERMISSIONS_REQUEST_READ_AND_WRITE_FILES
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED){
      onPermissionGranted();
    }
  }

  private void onPermissionGranted(){
    if(!recordingManager.init()){
      Toast.makeText(this, "RecordingManager init failed.", Toast.LENGTH_SHORT).show();
    }else{
      mRecordingManagerInited = true;
    }
  }

  private FloatingActionButton fabStart;

  private void initializeUi(){
    setContentView(R.layout.recording_activity);

    fabStart = findViewById(R.id.fabStart);
    fabStart.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        if(mRecordingManagerInited){
          if(recordingManager.isRecording()){
            recordingManager.stopRecording();
            Toast.makeText(RecordingActivity.this,
                    "RecordingManager stopped.", Toast.LENGTH_SHORT).show();
          }else{
            recordingManager.startRecording();
            Toast.makeText(RecordingActivity.this,
                    "RecordingManager started.", Toast.LENGTH_SHORT).show();
          }
        }else{
          Toast.makeText(RecordingActivity.this,
                  "RecordingManager init failed.", Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  @Override
  public void onRecordingFailed(){
    runOnUiThread(new Runnable(){
      @Override
      public void run(){
        Toast.makeText(RecordingActivity.this,
                "Recording failed.", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  protected void onStop(){
    super.onStop();
    if(isFinishing()){
      recordingManager.finish();
    }else{
      LOG.i("HHH");
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
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
  public boolean onOptionsItemSelected(MenuItem item){
    switch(item.getItemId()){
      case R.id.set_storage_folder:
      case R.id.set_delay:
      case android.R.id.home: // back button
        break;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
