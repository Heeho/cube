package ru.ltow.cube;

import android.opengl.GLSurfaceView;
import android.content.Context;
import android.view.MotionEvent;
import android.view.GestureDetector;

public class GLView extends GLSurfaceView {
    private final TicTacToe ttt;

    private final GLRenderer renderer;
    private static final int EGL_CCV = 2;

    private final GestureDetector tap;
    private float currentX, currentY, nextX, nextY;

    public GLView(Context c) {
        super(c);
        setEGLContextClientVersion(EGL_CCV);
        tap = new GestureDetector(c, new Tap());

        ttt = new TicTacToe(c, new boolean[]{
            App.prefs.playerIsHuman(PrefsManager.P1_HUMAN),
            App.prefs.playerIsHuman(PrefsManager.P2_HUMAN),
            App.prefs.playerIsHuman(PrefsManager.P3_HUMAN)});

        renderer = new GLRenderer();
        renderer.setThings(ttt.initGame());

        setRenderer(renderer);
        //setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        queueEvent(new Click(nextX, nextY));
        requestRender();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();

        nextX = e.getX();
        nextY = e.getY();

        if(tap.onTouchEvent(e) && (action == MotionEvent.ACTION_UP)) {
            return performClick();
        }

        if (action == MotionEvent.ACTION_MOVE) {
            float dx = nextX - currentX;
            float dy = nextY - currentY;
            queueEvent(new Drag(dx, dy));
        }

        currentX = nextX;
        currentY = nextY;

        requestRender();
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
            int pick = renderer.pickObject(x, y);
            renderer.setThings(ttt.makeTurn(pick));
            renderer.initInstances();
        }
    }

    class Drag implements Runnable {
        private final float dx, dy;

        public Drag(float a, float b) {
            float ROTATION_SPEED = 0.5f;
            dx = a * ROTATION_SPEED;
            dy = b * ROTATION_SPEED;
        }

        @Override
        public void run() {renderer.rotateCamera(dx, dy);}
    }

    static class Tap extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }
    }
}