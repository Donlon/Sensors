package donlon.android.sensors.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import donlon.android.sensors.utils.Logger;

public class SensorDataSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
  private SurfaceHolder mHolder;
  private Canvas mCanvas;//绘图的画布
  private boolean mIsDrawing;//控制绘画线程的标志位

  public SensorDataSurfaceView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  public SensorDataSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView();
  }

  public SensorDataSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    Logger.is("Abnormal");

    initView();
  }

  private void initView() {
    mHolder = getHolder();
    mHolder.addCallback(this);
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    mIsDrawing = true;
//    new Thread(this).start();
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    mIsDrawing = false;
  }

  @Override
  public void run() {
    while (mIsDrawing) {
      draw();
    }
  }

  private void draw() {
    try {
      mCanvas = mHolder.lockCanvas();

      mCanvas.drawColor(Color.BLUE);

    } catch (Exception ignored) {
    } finally {
      if (mCanvas != null) {
        mHolder.unlockCanvasAndPost(mCanvas);
      }
    }
  }
}
