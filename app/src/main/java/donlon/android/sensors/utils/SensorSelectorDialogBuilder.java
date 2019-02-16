package donlon.android.sensors.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import donlon.android.sensors.CustomSensor;
import donlon.android.sensors.R;
import donlon.android.sensors.SensorController;
import donlon.android.sensors.activities.RecordingActivity;

import static donlon.android.sensors.activities.RecordingActivity.RECORDING_ACTIVITY_REQUEST_CODE;

public class SensorSelectorDialogBuilder extends AlertDialog.Builder {
  private Activity mActivity;
  private SensorController mSensorController;
  private AlertDialog mDialog;

  private static OnRecordingFinishListener mOnRecordingFinishListener;

  private boolean[] selectedSensorArray;
  //  private Set<CustomSensor> selectedSensors = new ArraySet<>();

  public SensorSelectorDialogBuilder(@NonNull Context context, SensorController sensorController) {
    super(context);
    mSensorController = sensorController;
    init();
  }

  public SensorSelectorDialogBuilder(@NonNull Context context, SensorController sensorController, int themeResId) {
    super(context, themeResId);
    mSensorController = sensorController;
    init();
  }

  private void init() {
    int size = mSensorController.getSensorList().size();
    selectedSensorArray = new boolean[size + 1];
    sensorNameList = new String[size + 1];
    sensorNameList[0] = "All Sensors";//TODO: i18n
    for (int i = 0; i < selectedSensorArray.length; i++) {
      selectedSensorArray[i] = false;
      if (i != 0) {
        sensorNameList[i] = SensorUtils.getSensorEnglishNameByType(mSensorController.get(i - 1).getSensor().getType());
      }
    }
  }

  public SensorSelectorDialogBuilder setActivity(Activity activity) {
    mActivity = activity;
    return this;
  }

  public SensorSelectorDialogBuilder setOnRecordingFinishListener(OnRecordingFinishListener onRecordingFinishListener) {
    mOnRecordingFinishListener = onRecordingFinishListener;
    return this;
  }

  public SensorSelectorDialogBuilder setSensorSelected(CustomSensor sensor) {
    selectedSensorArray[sensor.getPosition() + 1] = true;
    return this;
  }

  public SensorSelectorDialogBuilder setSensorUnselected(CustomSensor sensor) {
    selectedSensorArray[sensor.getPosition() + 1] = false;
    return this;
  }

  private void onNegativeButtonClick() {
    if (mOnRecordingFinishListener != null) {
      mOnRecordingFinishListener.onRecordingCanceled();
    }
  }

  private void onPositiveButtonClick() {
    List<Integer> selectedPos = new ArrayList<>();
    for (int i = 1; i < selectedSensorArray.length; i++) {
      if (selectedSensorArray[i]) {
        selectedPos.add(mSensorController.get(i).getPosition());
      }
    }
    if (selectedPos.isEmpty()) {
      Toast.makeText(getContext(), "Please select more...", Toast.LENGTH_SHORT).show();
      return;
    }
    int[] selectedPosArr = new int[selectedPos.size()];
    for (int i = 0; i < selectedPosArr.length; i++) {
      selectedPosArr[i] = selectedPos.get(i);
    }
    Intent intent = new Intent(getContext(), RecordingActivity.class);
    intent.putExtra(RecordingActivity.EXTRA_SELECTED_SENSORS, selectedPosArr);
    mActivity.startActivityForResult(intent, RECORDING_ACTIVITY_REQUEST_CODE);
  }

  private String[] sensorNameList;

  private void onMultiChoiceItems(int which, boolean isChecked) {
    if (which == 0) {
      for (int i = 1; i < selectedSensorArray.length; i++) {
        if (selectedSensorArray[i] != isChecked) { // assimilate all selections
          mDialog.getListView().setItemChecked(i, isChecked);
          selectedSensorArray[i] = isChecked;
        }
      }
    } else {
      if (selectedSensorArray[0]) { // 'all' was selected & indicating that isChecked==false
        mDialog.getListView().setItemChecked(0, false);
        selectedSensorArray[0] = false;
      } else {
        if (isChecked) {
          boolean selectedAll = true;
          for (int i = 1; i < selectedSensorArray.length; i++) {
            selectedAll &= selectedSensorArray[i];
          }
          if (selectedAll) {
            mDialog.getListView().setItemChecked(0, true);
            selectedSensorArray[0] = true;
          }
        }
      }
    }
  }

  @Override
  public AlertDialog create() {
    boolean selectedAll = true;
    for (int i = 1; i < selectedSensorArray.length; i++) {
      selectedAll &= selectedSensorArray[i];
    }
    selectedSensorArray[0] = selectedAll; // duplicated

    this.setTitle(R.string.recording_starter_title)
        .setMultiChoiceItems(sensorNameList, selectedSensorArray,
            (dialog, which, isChecked) -> onMultiChoiceItems(which, isChecked))
        .setPositiveButton(R.string.btn_positive, (dialog, which) -> onPositiveButtonClick())
        .setNegativeButton(R.string.btn_cancel, (dialog, which) -> onNegativeButtonClick());
    mDialog = super.create();
//    mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> onPositiveButtonClick());
//    mDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> onNegativeButtonClick());
    return mDialog;
  }

  public interface OnRecordingFinishListener {
    void onRecordingCanceled();
  }
}
