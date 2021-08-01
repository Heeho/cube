package ru.ltow.cube;

public class Cell extends Rendered {
  public Cell(float[] s, int i, int m) {
    super(s, i, m);
    addAnimation(new ScaleUp(s));
  }
}