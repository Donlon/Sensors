package donlon.android.sensors.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class DataBinderAdapter<VH extends DataBinderAdapter.ViewHolder, T> extends ArrayAdapter<T> {
  private final LayoutInflater mInflater;

  private ViewBinder<VH, T> mViewBinder;
  private ViewHolderCreator<VH> mViewHolderCreator;

  private List<T> mData;
  private List<VH> mViewHolderList;
  private List<View> mRootViewList;

  private int mResource;

  public DataBinderAdapter(Context context, @LayoutRes int resource, List<T> data) {
    super(context, resource, data);
    mData = data;
    mResource = resource;
    mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    mViewHolderList = new ArrayList<>(data.size());
    mRootViewList = new ArrayList<>(data.size());
  }

  @Override
  public int getCount() {
    return mData.size();
  }

  @Override
  public T getItem(int position) {
    return mData.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public @NonNull
  View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    return convertView == null ? mRootViewList.get(position) : convertView;
  }

  public void createViews() {
    for (int position = 0; position < mData.size(); position++) {
      View v = mInflater.inflate(mResource, null, false);
      mRootViewList.add(v);
      bindView(position, v);
    }
  }

  private void bindView(int position, View view) {
    if (mViewHolderCreator != null) {
      VH viewHolder = mViewHolderCreator.onCreateViewHolder(position, view);
      mViewHolderList.add(viewHolder);
      T dataSet = mData.get(position);
      if (dataSet == null) {
        return;
      }
      if (mViewBinder != null) {
        mViewBinder.bindData(position, viewHolder, dataSet);
      }
    }
  }

  public ViewBinder<VH, T> getViewBinder() {
    return mViewBinder;
  }

  public void setViewBinder(ViewBinder<VH, T> viewBinder) {
    mViewBinder = viewBinder;
  }

  public ViewHolderCreator<VH> getViewHolderCreator() {
    return mViewHolderCreator;
  }

  public void setViewHolderCreator(ViewHolderCreator<VH> viewHolderCreator) {
    mViewHolderCreator = viewHolderCreator;
  }

  public VH getViewHolder(int position) {
    return mViewHolderList.get(position);
  }

  public List<VH> getViewHolderList() {
    return mViewHolderList;
  }

  public interface ViewBinder<V extends ViewHolder, D> {
    void bindData(int position, V viewHolder, D data);
  }

  public interface ViewHolderCreator<V extends ViewHolder> {
    V onCreateViewHolder(int position, View rootView);
  }

  public static class ViewHolder {
    public int position;
    public View view;

    public ViewHolder(int position, View view) {
      this.position = position;
      this.view = view;
    }
  }
}