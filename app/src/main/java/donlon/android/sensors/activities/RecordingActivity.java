package donlon.android.sensors.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import donlon.android.sensors.R;
import donlon.android.sensors.RecordingManager;
import donlon.android.sensors.RecordingService;
import donlon.android.sensors.utils.LOG;

public class RecordingActivity extends AppCompatActivity {

  private RecordingManager recordingManager;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    recordingManager = RecordingManager.getInstance();
    LOG.i( "Here we go!");
    initializeUi();

    LOG.d("Thread ID = " + Thread.currentThread().getId());
    LOG.d("before StartService");

    //连续启动Service
    Intent intentOne = new Intent(this, RecordingService.class);
    startService(intentOne);
    Intent intentTwo = new Intent(this, RecordingService.class);
    startService(intentTwo);
    Intent intentThree = new Intent(this, RecordingService.class);
    startService(intentThree);

    //停止Service
    Intent intentFour = new Intent(this, RecordingService.class);
    stopService(intentFour);

    //再次启动Service
    Intent intentFive = new Intent(this, RecordingService.class);
    startService(intentFive);

    LOG.d("after StartService");

  }

  private FloatingActionButton fabStart;

  private void initializeUi() {
    setContentView(R.layout.recording_activity);

    fabStart = findViewById(R.id.fabStart);
    fabStart.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });
  }

  @Override
  protected void onStop() {
    super.onStop();
    if(isFinishing()){
      recordingManager.finish();
    }else{
      LOG.printStack("HHH")  ;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.recording_activity_menu, menu);
    return true;
  }

  /**
   * Event listener for clicks on title bar.
   * @param item menu item
   * @return what should be returned
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
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
