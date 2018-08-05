package donlon.android.sensors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SensorsListAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
  private Context m_context;
  private List<CustomSensor> m_sensorList;
  private List<TextView> m_dataTextViewsList;
  private List<SensorListWidgets> m_listEntitiesList;
  private ListView m_correlatedView;

  public SensorsListAdapter(Context context, ListView correlatedView, List<CustomSensor> sensorList) {
    m_context = context;
    m_sensorList = sensorList;
    m_dataTextViewsList = new ArrayList<>();
    m_correlatedView = correlatedView;
    initSubViews();
  }


  @Override
  public int getCount() {
    return m_sensorList.size();
  }

  @Override
  public CustomSensor getItem(int position) {
    return m_sensorList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if(convertView != null){
//      TextView tvData = convertView.findViewById(R.id.textViewSensorData);
//      CustomSensor currentSensor = m_sensorList.get(position);
//      tvData.setText(currentSensor.data);
      return convertView;
    }

    return m_listEntitiesList.get(position).view;
  }

  private void initSubViews(){
    m_listEntitiesList = new ArrayList<>();
    for(CustomSensor sensor : m_sensorList){
      SensorListWidgets entity = new SensorListWidgets();
      entity.view = LayoutInflater.from(m_context).inflate(
              R.layout.entry, m_correlatedView, false);
      entity.tvPrimaryName = entity.view.findViewById(R.id.tvSensorPrimaryName);
      entity.tvSecondaryName = entity.view.findViewById(R.id.tvSensorSecondaryName);
      entity.tvInfo = entity.view.findViewById(R.id.tvSensorInfo);
      entity.tvData = entity.view.findViewById(R.id.tvSensorData);
      entity.tvUnit = entity.view.findViewById(R.id.tvSensorDataUnit);
      entity.layoutRight = entity.view.findViewById(R.id.layoutRight);
      entity.cbxEnabled = entity.view.findViewById(R.id.cbxEnabled);

//      entity.tvInfo.setText(sensor.sensorInfo);
      entity.tvPrimaryName.setText(
              SensorUtils.getSensorNameByType(sensor.getSensorObject().getType()));
      entity.tvSecondaryName.setText(
              SensorUtils.getSensorEnglishNameByType(sensor.getSensorObject().getType()));
      entity.tvUnit.setText(SensorUtils.getDataUnit(sensor.getSensorObject().getType()));
//      tvData.setText(sensor.data);
      entity.cbxEnabled.setOnCheckedChangeListener(this);

      sensor.correlatedPreviewingListWidgets = entity;
      m_listEntitiesList.add(entity);
    }
  }

  public void disableAllCheckBoxes(){
    for(SensorListWidgets w : m_listEntitiesList){
      w.cbxEnabled.setEnabled(false);
    }
  }

  public void enableAllCheckBoxes(){
    for(SensorListWidgets w : m_listEntitiesList){
      w.cbxEnabled.setEnabled(true);
    }
  }

  OnSensorsListCbxCheckedListener mOnCbxCheckedListener;

  public void setOnCbxCheckedListener(OnSensorsListCbxCheckedListener listener){
    mOnCbxCheckedListener = listener;
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    int pos = 0;
    for(SensorListWidgets w : m_listEntitiesList){
      if(w.cbxEnabled == buttonView){
        if(mOnCbxCheckedListener != null){
          mOnCbxCheckedListener.OnSensorsListCbxChecked(pos, isChecked);
          break;
        }
      }
      pos++;
    }

  }

  public interface OnSensorsListCbxCheckedListener {
    void OnSensorsListCbxChecked(int pos, boolean selected);
  }

  public class SensorListWidgets{
    public View view;
    public TextView tvPrimaryName;
    public TextView tvSecondaryName;
    public TextView tvInfo;
    public TextView tvData;
    public TextView tvUnit;
    public CheckBox cbxEnabled;
    public LinearLayout layoutRight;
  }
}