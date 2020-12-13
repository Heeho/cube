package ru.ltow.cube;

import java.util.ArrayList;

public class Rendered extends GLUser {
    private float[] state;

    private int model;
    private int id;

    private final ArrayList<Animation> animations = new ArrayList<>();

    public static final int CELL_E = 0;
    public static final int CELL_X = 1;
    public static final int CELL_O = 2;
    public static final int CELL_V = 3;
    public static final int CELL_U = 4;

    public Rendered(ArrayList<Float> stateM, int m) {
        setState(stateM);
        setModel(m);
    }

    public void setId(int n) {
        id = n;
    }

    public void setModel(int m) {
        model = m;
    }

    public void setState(float[] s) {
        state = s;
    }

    public void setState(ArrayList<Float> s) {
        state = listToFloatArray(s);
    }

    public int getId() {
        return id;
    }

    public int getModel() {
        return model;
    }

    public float[] getState() {
        return state;
    }

    public void addAnimation(Animation a) {
        animations.add(a);
    }

    public void performAnimation() {
        Animation a;
        for(int i = animations.size() - 1; i >= 0; i--) {
            a = animations.get(i);
            a.perform();
            if(a.isFinished()) animations.remove(i);
        }
    }
}