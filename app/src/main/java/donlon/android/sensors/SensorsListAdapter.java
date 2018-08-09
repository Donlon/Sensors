package donlon.android.sensors;

import android.content.Context;
import android.hardware.SensorEvent;
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

import donlon.android.sensors.utils.SensorUtils;

public class SensorsListAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, SensorEventCallback{
  private Context m_context;
  private List<CustomSensor> m_sensorList;
  private List<SensorListWidgets> m_listWidgetsList;//TODO: use map fashion
  private ListView m_correlatedView;

  public SensorsListAdapter(Context context, ListView correlatedView, List<CustomSensor> sensorList){
    m_context = context;
    m_sensorList = sensorList;
    m_correlatedView = correlatedView;
    initSubViews();
  }


  @Override
  public int getCount(){
    return m_sensorList.size();
  }

  @Override
  public CustomSensor getItem(int position){
    return m_sensorList.get(position);
  }

  @Override
  public long getItemId(int position){
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent){
    /*if(convertView != null){
      return convertView;
    }*/
    return m_listWidgetsList.get(position).view;
  }

  private void initSubViews(){
    m_listWidgetsList = new ArrayList<>();
    for(CustomSensor sensor : m_sensorList){
      SensorListWidgets widgets = new SensorListWidgets();

      widgets.view = LayoutInflater.from(m_context).inflate(R.layout.sensors_preview_list_entry, m_correlatedView, false);

      widgets.tvPrimaryName = widgets.view.findViewById(R.id.tvSensorPrimaryName);
      widgets.tvSecondaryName = widgets.view.findViewById(R.id.tvSensorSecondaryName);
      widgets.tvInfo = widgets.view.findViewById(R.id.tvSensorInfo);
      widgets.tvData = widgets.view.findViewById(R.id.tvSensorData);
      widgets.tvDataPrefix = widgets.view.findViewById(R.id.tvSensorDataPrefix);
      widgets.tvUnit = widgets.view.findViewById(R.id.tvSensorDataUnit);
      widgets.layoutRight = widgets.view.findViewById(R.id.layoutRight);
      widgets.cbxEnabled = widgets.view.findViewById(R.id.cbxEnabled);
      widgets.cbxEnabled.setOnCheckedChangeListener(this);

      setStarterText(widgets, sensor);

      sensor.correlatedPreviewingListWidgets = widgets;
      m_listWidgetsList.add(widgets);
    }
  }

  private void setStarterText(SensorListWidgets widgets, CustomSensor sensor){
    widgets.tvPrimaryName.setText(SensorUtils.getSensorNameByType(sensor.getSensorObject().getType()));
    widgets.tvSecondaryName.setText(SensorUtils.getSensorEnglishNameByType(sensor.getSensorObject().getType()));
    widgets.tvUnit.setText(SensorUtils.getDataUnit(sensor.getSensorObject().getType()));

    if(sensor.dataDimension == 3){
      widgets.tvDataPrefix.setText(R.string.data_prefix_3d);
    }else{
      widgets.tvDataPrefix.setVisibility(View.GONE);
    }
  }

  /**
   * Callback from Sensor SensorManager
   *
   * @param sensor sensor
   * @param event event
   */
  @Override
  public void onSensorChanged(CustomSensor sensor, SensorEvent event){
    /*if(sensor.dataDimension == 0){
      return;
    }
    StringBuilder tmpStr = new StringBuilder();
    for(int i = 0; i < sensor.dataDimension; i++){
      tmpStr.append(String.valueOf(event.values[i]));
      if(i != sensor.dataDimension - 1){
        tmpStr.append("\n");
      }
    }
    m_listWidgetsList.get(sensor.id).tvData.setText(tmpStr.toString());*/
  }

  //TODO: clean codes below.
  private OnSensorsListCbxCheckedListener mOnCbxCheckedListener;

  public void setOnCbxCheckedListener(OnSensorsListCbxCheckedListener listener){
    mOnCbxCheckedListener = listener;
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
    int pos = 0;
    for(SensorListWidgets w : m_listWidgetsList){
      if(w.cbxEnabled == buttonView){
        if(mOnCbxCheckedListener != null){
          mOnCbxCheckedListener.OnSensorsListCbxChecked(pos, isChecked);
          break;
        }
      }
      pos++;
    }
  }

  public void enableAllCheckBoxes(){
    for(SensorListWidgets w : m_listWidgetsList){
      w.cbxEnabled.setEnabled(true);
    }
  }

  public void disableAllCheckBoxes(){
    for(SensorListWidgets w : m_listWidgetsList){
      w.cbxEnabled.setEnabled(false);
    }
  }

  public interface OnSensorsListCbxCheckedListener{
    void OnSensorsListCbxChecked(int pos, boolean selected);
  }

  public class SensorListWidgets{
    public View view;
    public TextView tvPrimaryName;
    public TextView tvSecondaryName;
    public TextView tvInfo;
    public TextView tvDataPrefix;
    public TextView tvData;
    public TextView tvUnit;
    public CheckBox cbxEnabled;
    public LinearLayout layoutRight;
  }
}