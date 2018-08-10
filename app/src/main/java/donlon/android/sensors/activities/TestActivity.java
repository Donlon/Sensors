package donlon.android.sensors.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.security.spec.ECField;

import donlon.android.sensors.R;

public class TestActivity extends AppCompatActivity{

  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    try{
      setContentView(R.layout.test_table);
    }catch(Exception e){
      e.printStackTrace();
    }
  }

}
