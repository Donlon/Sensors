package donlon.android.sensors.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBinderAdapter<H extends DataBinderAdapter.ViewHolder, T> extends BaseAdapter {
  private final LayoutInflater mInflater;

  private ViewBinder<H, T> mViewBinder;
  private ViewHolderCreator<H> mViewHolderCreator;

  private List<T> mData;
  private List<H> mViewHolderList;
  private List<View> mRootViewList;

  private int mResource;

  public DataBinderAdapter(Context context, @LayoutRes int resource, List<T> data) {
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
      H viewHolder = mViewHolderCreator.onCreateViewHolder(position, view);
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

  public ViewBinder<H, T> getViewBinder() {
    return mViewBinder;
  }

  public void setViewBinder(ViewBinder<H, T> viewBinder) {
    mViewBinder = viewBinder;
  }

  public ViewHolderCreator<H> getViewHolderCreator() {
    return mViewHolderCreator;
  }

  public void setViewHolderCreator(ViewHolderCreator<H> viewHolderCreator) {
    mViewHolderCreator = viewHolderCreator;
  }

  public H getViewHolder(int position) {
    return mViewHolderList.get(position);
  }

  public List<H> getViewHolderList() {
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
    public View rootView;

    public ViewHolder(int position, View rootView) {
      this.position = position;
      this.rootView = rootView;
    }
  }
}