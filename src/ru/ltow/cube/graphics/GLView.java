package ru.ltow.cube;

import android.opengl.GLSurfaceView;
import android.content.Context;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.util.AttributeSet;
import android.widget.Toast;
import android.os.Looper;

public class GLView extends GLSurfaceView {
  private Context c;
  private TicTacToe ttt;

  private GLRenderer renderer;
  private static final int EGL_CCV = 2;

  private GestureDetector tap;
  private float currentX, currentY;

  public GLView(Context c) {super(c); ctor(c);}
  public GLView(Context c, AttributeSet a) {super(c, a); ctor(c);}

  public void ctor(Context context) {
    c = context;
    setEGLContextClientVersion(EGL_CCV);
    tap = new GestureDetector(c, new Tap());

    ttt = new TicTacToe(new boolean[]{
      App.prefs.playerIsHuman(PrefsManager.P1_HUMAN),
      App.prefs.playerIsHuman(PrefsManager.P2_HUMAN),
      App.prefs.playerIsHuman(PrefsManager.P3_HUMAN)});

    renderer = new GLRenderer();
    renderer.setThings(ttt.initGame());

    if(ttt.message() == TicTacToe.status.AITURN) toast(c.getText(R.string.aiTurn).toString());

    setRenderer(renderer); //setRenderMode(RENDERMODE_WHEN_DIRTY);
  }

  @Override
  public boolean onTouchEvent(MotionEvent e) {
    float x = e.getX();
    float y = e.getY();

    if(tap.onTouchEvent(e)) {
      queueEvent(new Click(e.getX(), e.getY()));
      //requestRender();
      return true;
    }

    switch(e.getAction()) {
      case MotionEvent.ACTION_MOVE:
        float dx = x - currentX;
        float dy = y - currentY;
        queueEvent(new Drag(dx, dy));
      break;
      default: break;
    }

    currentX = x;
    currentY = y;

    //requestRender();
    return true;
  }

  class Click implements Runnable {
    private final int x, y;

    public Click(float a, float b) {
      x = (int) a;
      y = (int) b;
    }

    @Override
    public void run() {
      renderer.setThings(ttt.makeTurn(renderer.pickObject(x, y)));
      renderer.initInstances();

      String s = null;

      switch(ttt.message()) {
        case GAMEOVER: s = c.getText(R.string.gameOver).toString(); break;
        case AITURN:
        case NONE: break;
      }

      if(s != null) toast(s);
    }
  }

  class Drag implements Runnable {
    private final float dx, dy;
    private final float ROTATION_SPEED = 0.5f;

    public Drag(float a, float b) {
      dx = a * ROTATION_SPEED;
      dy = b * ROTATION_SPEED;
    }

    @Override
    public void run() {renderer.rotateCamera(dx, dy);}
  }

  class Tap extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onDoubleTap(MotionEvent e) {
      return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
      return true;
    }
  }

  private void toast(final String msg) {
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        Looper.prepare();
        Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
        Looper.loop();
      }
    });
    t.start();
  }
}