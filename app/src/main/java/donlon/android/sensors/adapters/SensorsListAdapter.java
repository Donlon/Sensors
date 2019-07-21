package donlon.android.sensors.adapters;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import donlon.android.sensors.R;
import donlon.android.sensors.sensor.CustomSensor;
import donlon.android.sensors.sensor.SensorController;
import donlon.android.sensors.utils.FormatterUtils;
import donlon.android.sensors.utils.SensorUtils;

public class SensorsListAdapter extends DataBinderAdapter<SensorsListAdapter.SensorListViewHolder, CustomSensor>
    implements CompoundButton.OnCheckedChangeListener, SensorController.OnSensorChangeListener {
  private float[][] mSensorDataCache;

  private int mSensorCount;

  private Handler mHandler = new Handler();

  private Runnable mUpdaterRunnable = new Runnable() {
    @Override
    public void run() {
      for (int i = 0; i < mSensorCount; i++) {
        if (mSensorDataCache[i] != null && mSensorDataCache[i].length > 0) {
          SensorListViewHolder viewHolder = getViewHolder(i);
          if (mSensorDataCache[i].length == 3) {
            viewHolder.tvData.setText(FormatterUtils.format3dData(mSensorDataCache[i]));
          } else {
            viewHolder.tvData.setText(String.valueOf(mSensorDataCache[i][0]));
          }
        }
      }
      mHandler.postDelayed(this, 20);
    }
  };

  public SensorsListAdapter(Context context, List<CustomSensor> sensorList) {
    super(context, R.layout.sensors_preview_list_entry, sensorList);
    super.setViewHolderCreator(SensorListViewHolder::new);
    super.setViewBinder((position, viewHolder, data) -> bindData(viewHolder, data));
    super.createViews();
    mSensorCount = sensorList.size();
    mSensorDataCache = new float[mSensorCount][];
  }

  private void bindData(SensorListViewHolder viewHolder, CustomSensor data) {
    Sensor sensor = data.getSensor();
    viewHolder.tvPrimaryName.setText(SensorUtils.getSensorNameByType(sensor.getType()));
    viewHolder.tvSecondaryName.setText(SensorUtils.getSensorEnglishNameByType(sensor.getType()));
    viewHolder.tvUnit.setText(SensorUtils.getDataUnit(sensor.getType()));

    if (data.dataDimension == 3) {
      if (data.is3dData()) {
        viewHolder.tvDataPrefix.setText(R.string.data_prefix_3d);
      }
      viewHolder.tvDataPrefix.setText(R.string.data_prefix_3d);
    } else {
      viewHolder.tvDataPrefix.setVisibility(View.GONE);
    }
  }

  /**
   * Callback from Sensor SensorManager
   *
   * @param sensor sensor
   * @param event  event
   */
  @Override
  public void onSensorChange(CustomSensor sensor, SensorEvent event) {
    mSensorDataCache[sensor.getPosition()] = event.values;
  }

  public void startUpdating() {
    mHandler.post(mUpdaterRunnable);
  }

  public void stopUpdating() {
    mHandler.removeCallbacks(mUpdaterRunnable);
  }

  //TODO: clean codes below.
  private OnSensorsListCbxCheckedListener mOnCbxCheckedListener;

  public void setOnCbxCheckedListener(OnSensorsListCbxCheckedListener listener) {
    mOnCbxCheckedListener = listener;
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    for (SensorListViewHolder viewHolder : getViewHolderList()) {
      if (viewHolder.cbxEnabled == buttonView) {
        if (mOnCbxCheckedListener != null) {
          mOnCbxCheckedListener.OnSensorsListCbxChecked(viewHolder.position, isChecked);
          break;
        }
      }
    }
  }

  public void enableAllCheckBoxes() {
    for (SensorListViewHolder viewHolder : getViewHolderList()) {
      viewHolder.cbxEnabled.setEnabled(true);
    }
  }

  public void disableAllCheckBoxes() {
    for (SensorListViewHolder viewHolder : getViewHolderList()) {
      viewHolder.cbxEnabled.setEnabled(false);
    }
  }

  public interface OnSensorsListCbxCheckedListener {
    void OnSensorsListCbxChecked(int pos, boolean selected);
  }

  static class SensorListViewHolder extends DataBinderAdapter.ViewHolder {
    TextView tvPrimaryName;
    TextView tvSecondaryName;
    TextView tvInfo;
    TextView tvDataPrefix;
    TextView tvData;
    TextView tvUnit;
    CheckBox cbxEnabled;
    LinearLayout layoutRight;

    SensorListViewHolder(int position, View rootView) {
      super(position, rootView);
      tvPrimaryName = rootView.findViewById(R.id.tvSensorPrimaryName);
      tvSecondaryName = rootView.findViewById(R.id.tvSensorSecondaryName);
      tvInfo = rootView.findViewById(R.id.tvSensorInfo);
      tvData = rootView.findViewById(R.id.tvSensorData);
      tvDataPrefix = rootView.findViewById(R.id.tvSensorDataPrefix);
      tvUnit = rootView.findViewById(R.id.tvSensorDataUnit);
      layoutRight = rootView.findViewById(R.id.layoutRight);
      cbxEnabled = rootView.findViewById(R.id.cbxEnabled);
    }
  }
}