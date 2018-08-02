package donlon.android.sensors.views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

public class StretchedTextView extends AppCompatTextView {
  public StretchedTextView(Context context) {
    super(context);
  }

  public StretchedTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    //setMeasuredDimension(600, 70);
  }

}
