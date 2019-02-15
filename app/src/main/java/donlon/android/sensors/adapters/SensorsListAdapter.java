package donlon.android.sensors.adapters;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import donlon.android.sensors.CustomSensor;
import donlon.android.sensors.R;
import donlon.android.sensors.SensorEventCallback;
import donlon.android.sensors.utils.MathUtils;
import donlon.android.sensors.utils.SensorUtils;

public class SensorsListAdapter extends DataBinderAdapter<SensorsListAdapter.SensorListViewHolder,CustomSensor>
    implements CompoundButton.OnCheckedChangeListener, SensorEventCallback {

  public SensorsListAdapter(Context context, List<CustomSensor> sensorList) {
    super(context, R.layout.sensors_preview_list_entry, sensorList);

    super.setViewHolderCreator(SensorListViewHolder::new);
    super.setViewBinder(this::bindData);
    super.createViews();
  }

  private void bindData(int position, SensorListViewHolder viewHolder, CustomSensor data) {
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
  public void onSensorChanged(CustomSensor sensor, SensorEvent event) {
    if (sensor.dataDimension == 0) {
      return;
    }
    StringBuilder tmpStr = new StringBuilder();
    for (int i = 0; i < sensor.dataDimension; i++) {
      tmpStr.append(String.valueOf(event.values[i]));
      if (i != sensor.dataDimension - 1) {
        tmpStr.append("\n");
      }
    }
    if (sensor.is3dData()) {
      tmpStr.append("\n");
      tmpStr.append(MathUtils.getA(event.values));
    }

    getViewHolder(sensor.getPosition()).tvData.setText(tmpStr.toString());
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

  public class SensorListViewHolder extends DataBinderAdapter.ViewHolder {
    public TextView tvPrimaryName;
    public TextView tvSecondaryName;
    public TextView tvInfo;
    public TextView tvDataPrefix;
    public TextView tvData;
    public TextView tvUnit;
    public CheckBox cbxEnabled;
    public LinearLayout layoutRight;

    public SensorListViewHolder(int position, View rootView) {
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