package ru.ltow.cube;

import java.util.ArrayList;

public class Rendered {
  private float[] state;
  private int id;
  private int model;

  private ArrayList<Animation> animations = new ArrayList<Animation>();

  public static final int CELL_X = 0;
  public static final int CELL_O = 1;
  public static final int CELL_V = 2;
  public static final int CELL_E = 3;
  public static final int CELL_U = 4;

  public Rendered(float[] s, int i, int m) {
    state(s);
    id(i);
    model(m);
  }

  public float[] state() {return state;}
  public int id() {return id;}
  public int model() {return model;}

  public void id(int i) {id = i;}
  public void model(int m) {model = m;}
  public void state(float[] s) {state = s;}
  public void state(ArrayList<Float> s) {state = Utils.ltof(s);}
  public void addAnimation(Animation a) {animations.add(a);}
  public void remAnimation(Animation a) {animations.remove(a);}
  public void clearAnimations() {animations.clear();}
  public void performAnimation() {
    Animation a = null;
    for(int i = animations.size() - 1; i >= 0; i--) {
      a = animations.get(i);
      a.perform(this);
      if(a.finished()) animations.remove(i);
    }
  }
}