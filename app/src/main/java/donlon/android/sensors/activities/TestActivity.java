package donlon.android.sensors.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import donlon.android.sensors.R;

public class TestActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    try {
      setContentView(R.layout.test_table);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
