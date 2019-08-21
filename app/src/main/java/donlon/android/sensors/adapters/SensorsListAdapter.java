package donlon.android.sensors.adapters;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import donlon.android.sensors.R;
import donlon.android.sensors.activities.SensorDetailsActivity;
import donlon.android.sensors.sensor.CustomSensor;
import donlon.android.sensors.sensor.SensorController;
import donlon.android.sensors.utils.FormatterUtils;
import donlon.android.sensors.utils.SensorUtils;

public class SensorsListAdapter extends RecyclerView.Adapter<SensorsListAdapter.ViewHolder>
    implements CompoundButton.OnCheckedChangeListener, SensorController.OnSensorChangeListener {

  private Runnable mUpdaterRunnable = new Runnable() {
    @Override
    public void run() {
      for (int i = 0; i < mSensorCount; i++) {
        if (mSensorDataCache[i] != null && mSensorDataCache[i].length > 0) {
          notifyItemChanged(i, mSensorDataCache[i]);
        }
      }
      mHandler.postDelayed(this, 200);
    }
  };

  private Handler mHandler = new Handler(Looper.getMainLooper());
  private float[][] mSensorDataCache;

  private Context mContext;
  private int mSensorCount;
  private List<CustomSensor> mSensorList;

  public SensorsListAdapter(Context context, List<CustomSensor> sensorList) {
    mContext = context;
    mSensorCount = sensorList.size();
    mSensorList = sensorList;
    mSensorDataCache = new float[mSensorCount][];
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
//  private OnSensorsListCbxCheckedListener mOnCbxCheckedListener;

//  public void setOnCbxCheckedListener(OnSensorsListCbxCheckedListener listener) {
//    mOnCbxCheckedListener = listener;
//  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//    for (SensorListViewHolder viewHolder : getViewHolderList()) {
//      if (viewHolder.cbxEnabled == buttonView) {
//        if (mOnCbxCheckedListener != null) {
//          mOnCbxCheckedListener.OnSensorsListCbxChecked(viewHolder.position, isChecked);
//          break;
//        }
//      }
//    }
  }

//  public void enableAllCheckBoxes() {
//    for (SensorListViewHolder viewHolder : getViewHolderList()) {
//      viewHolder.cbxEnabled.setEnabled(true);
//    }
//  }

//  public void disableAllCheckBoxes() {
//    for (SensorListViewHolder viewHolder : getViewHolderList()) {
//      viewHolder.cbxEnabled.setEnabled(false);
//    }
//  }

//  public interface OnSensorsListCbxCheckedListener {
//    void OnSensorsListCbxChecked(int pos, boolean selected);
//  }

  @NonNull
  @Override
  public SensorsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.sensors_preview_list_entry, parent, false);
    return new ViewHolder(v);
  }

  private void startSensorDetailsActivity(int position) {
    Intent intent = new Intent(mContext, SensorDetailsActivity.class);
    intent.putExtra(SensorDetailsActivity.EXTRA_TAG_SENSOR_POS, position);
    mContext.startActivity(intent);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position,
                               @NonNull List<Object> payloads) {
    CustomSensor customSensor = mSensorList.get(position);
    if (payloads.isEmpty()) { // TODO: it doesn't work fine
      initView(holder, customSensor);
      updateView(holder, new float[0]);
    } else {
      float[] data = (float[]) payloads.get(0);
      updateView(holder, data);
    }
  }

  private void initView(@NonNull ViewHolder holder, CustomSensor customSensor) {
    Sensor sensor = customSensor.getSensor();
    holder.tvPrimaryName.setText(SensorUtils.getSensorNameByType(sensor.getType()));
    holder.tvSecondaryName.setText(SensorUtils.getSensorEnglishNameByType(sensor.getType()));
    holder.tvUnit.setText(SensorUtils.getDataUnit(sensor.getType()));
    if (customSensor.dataDimension == 3) {
      if (customSensor.is3dData()) {
        holder.tvDataPrefix.setText(R.string.data_prefix_3d);
      }
      holder.tvDataPrefix.setText(R.string.data_prefix_3d);
    } else {
      holder.tvDataPrefix.setVisibility(View.GONE);
    }
    holder.itemView.setOnClickListener(v -> startSensorDetailsActivity(holder.getAdapterPosition()));
  }

  private void updateView(@NonNull ViewHolder holder, float[] data) {
    if (data.length == 3) {
      holder.tvData.setText(FormatterUtils.format3dData(data));
    } else {
      holder.tvData.setText(FormatterUtils.formatVector(data));
    }
  }

  @Override
  public int getItemCount() {
    return mSensorCount;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    TextView tvPrimaryName;
    TextView tvSecondaryName;
    //    TextView tvInfo;
    TextView tvDataPrefix;
    TextView tvData;
    TextView tvUnit;
//    CheckBox cbxEnabled;
//    LinearLayout layoutRight;

    ViewHolder(View rootView) {
      super(rootView);
      tvPrimaryName = rootView.findViewById(R.id.tvSensorPrimaryName);
      tvSecondaryName = rootView.findViewById(R.id.tvSensorSecondaryName);
//      tvInfo = rootView.findViewById(R.id.tvSensorInfo);
      tvData = rootView.findViewById(R.id.tvSensorData);
      tvDataPrefix = rootView.findViewById(R.id.tvSensorDataPrefix);
      tvUnit = rootView.findViewById(R.id.tvSensorDataUnit);
//      layoutRight = rootView.findViewById(R.id.layoutRight);
//      cbxEnabled = rootView.findViewById(R.id.cbxEnabled);
    }
  }
}