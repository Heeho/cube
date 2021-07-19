package ru.ltow.cube;

import java.util.ArrayList;

public class Cell {
  private Rendered r;
  private int value;

  public Cell(ArrayList<Float> stateM, int mark) {
    r = new Rendered(stateM, 0);
    setValue(mark);
  }

  public Cell(float[] stateM, int mark) {
    r = new Rendered(stateM, 0);
    setValue(mark);
  }

  public void setValue(int mark) {
    value = mark;

    int model =
      (mark == TicTacToe.X) ? Rendered.CELL_X :
      (mark == TicTacToe.O) ? Rendered.CELL_O :
      (mark == TicTacToe.V) ? Rendered.CELL_V :
      (mark == TicTacToe.UNUSED) ? Rendered.CELL_U :
      Rendered.CELL_E;

    r.setModel(model);
  }

  public void setId(int n) {r.setId(n);}
  public int getValue() {return value;}
  public Rendered getRendered() {return r;}
}