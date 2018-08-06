package donlon.android.sensors.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import donlon.android.sensors.R;
import donlon.android.sensors.utils.LOG;

public class RecordingActivity extends AppCompatActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LOG.d( "Here we go!");

    initializeUi();
  }

  private void initializeUi() {
    setContentView(R.layout.main_activity);
  }
}
