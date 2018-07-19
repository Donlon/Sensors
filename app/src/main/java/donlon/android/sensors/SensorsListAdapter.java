package donlon.android.sensors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SensorsListAdapter extends BaseAdapter {
  Context m_context;
  List<CustomSensor> m_sensorList;
  List<View> m_listEntitiesList;

  public SensorsListAdapter(Context context, List<CustomSensor> sensorList) {
    m_context = context;
    m_sensorList = sensorList;

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
    View view = LayoutInflater.from(m_context).inflate(R.layout.entry, parent, false);
    CustomSensor currentSensor = m_sensorList.get(position);
    TextView tvName = view.findViewById(R.id.textViewSensorName);
    tvName.setText(currentSensor.sensorName);

    TextView tvInfo = view.findViewById(R.id.textViewSensorInfo);
    tvInfo.setText(currentSensor.sensorInfo);

    TextView tvData = view.findViewById(R.id.textViewSensorData);
    tvData.setText(currentSensor.data);

    return view;
  }

  public void updateItem(int pos){

  }
}