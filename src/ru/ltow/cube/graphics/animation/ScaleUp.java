package ru.ltow.cube;

import android.opengl.Matrix;

public class ScaleUp extends Animation {
    private float step;
    private float[] state;

    public ScaleUp(Rendered r, float s) {
        super(r);
        step = s;
        state = r.getState();
    }

    @Override
    public void perform() {
        if(counter > (int) (1f/step)) {
            finished = true;
            return;
        }
        float[] m = new float[16];
        float scale = step * (float) counter;
        Matrix.scaleM(m, 0, state, 0, scale, scale, scale);
        r.setState(m);
        counter++;
    }
}