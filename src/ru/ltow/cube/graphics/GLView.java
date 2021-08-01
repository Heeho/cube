package ru.ltow.cube;

import android.opengl.GLSurfaceView;
import android.content.Context;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.util.AttributeSet;
import android.widget.Toast;
import android.os.Looper;
import android.app.Activity;

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
      App.prefs.playerIsHuman(PrefsManager.P3_HUMAN)
    });

    renderer = new GLRenderer();

    ttt.initGame();
    setThings();

    if(ttt.status() == TicTacToe.Status.AITURN) toast(c.getText(R.string.aiTurn).toString());

    setRenderer(renderer);
  }

  @Override
  public boolean onTouchEvent(MotionEvent e) {
    float x = e.getX();
    float y = e.getY();

    if(tap.onTouchEvent(e)) {
      queueEvent(new Click(e.getX(), e.getY()));
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
      ttt.makeTurn(renderer.pickObject(x, y));
      updateThings();
      setPlayer();

      if(ttt.status() == TicTacToe.Status.GAMEOVER)
      toast(c.getText(R.string.gameOver).toString());
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

  public void setPlayer() {
    final int p = ttt.nextMark();
    ((Activity) c).runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ((Activity) c).findViewById(R.id.playerV).setBackgroundColor(Colors.player(p));
      }
    });
  }

  private void updateThings() {
    for(int i = 0; i < ttt.field().length; i++) {
      if(renderer.things().get(i).model() != ttt.field()[i]) {
        renderer.things().get(i).model(ttt.field()[i]);
        renderer.things().get(i).addAnimation(new ScaleUp(renderer.things().get(i).state()));
      }
    }
    renderer.initInstances();
  }

  private void setThings() {
    int k = ttt.field().length / TicTacToe.FIELDSIZE;
    float fieldAngle = 30f;
    float cellShift = 2f*1f/6f + 0.001f;
    int offsetX = (TicTacToe.DIMS > 0) ? TicTacToe.FIELDSIZE/2 : 0;
    int offsetY = (TicTacToe.DIMS > 1) ? TicTacToe.FIELDSIZE/2 : 0;
    int offsetZ = (TicTacToe.DIMS > 2) ? TicTacToe.FIELDSIZE/2 : 0;

    for(int i = 0; i < ttt.field().length; i++) {
      renderer.things().add(new Cell(
        GLUtils.matrix(
          fieldAngle, fieldAngle, 0,
          cellShift * (i % TicTacToe.FIELDSIZE - offsetX),
          cellShift * ((i - i % k) / k - offsetY),
          cellShift * (offsetZ - ((i - i % TicTacToe.FIELDSIZE) - (i - i % k)) / TicTacToe.FIELDSIZE)
        ),
        i,
        ttt.field()[i]
      ));
    }
  }
}