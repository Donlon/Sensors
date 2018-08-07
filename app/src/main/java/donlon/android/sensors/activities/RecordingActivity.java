package donlon.android.sensors.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import donlon.android.sensors.R;
import donlon.android.sensors.RecordingManager;
import donlon.android.sensors.utils.LOG;

public class RecordingActivity extends AppCompatActivity {

  private RecordingManager recordingManager;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    recordingManager = RecordingManager.getInstance();
    LOG.d( "Here we go!");

    initializeUi();
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
      LOG.w("HHH");
    }
  }
}
