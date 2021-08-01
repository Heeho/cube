package ru.ltow.cube;

public abstract class Animation {
  protected int counter;
  protected boolean finished;
  protected final float step = 0.2f;

  public abstract void perform(Rendered r);
  public boolean finished() {return finished;}
}