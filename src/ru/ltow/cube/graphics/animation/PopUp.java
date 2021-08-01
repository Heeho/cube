package ru.ltow.cube;

public class PopUp extends Animation {
  private float[] state;

  public PopUp(float[] s) {state = s;}

  @Override
  public void perform(Rendered r) {
    if(counter > (int) (1f/step)) {
      finished = true;
      return;
    }
    float scale = step * (float) counter;
    r.state(GLUtils.scaleM(state, scale));
    counter++;
  }
}