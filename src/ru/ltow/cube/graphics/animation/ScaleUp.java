package ru.ltow.cube;

public class ScaleUp extends Animation {
  private float[] state;

  public ScaleUp(float[] s) {
    state = s;
  }

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