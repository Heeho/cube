package ru.ltow.cube;

import java.nio.ShortBuffer;
import java.nio.FloatBuffer;

public class Model {
  private ShortBuffer indexB;
  private FloatBuffer vertexB, colorB;

  private boolean opaque;

  protected Model(float[] vertices, short[] indices, float[] color) {
    opaque = (color[3] == 1.0f) ? true : false;
    colorB = GLUtils.allocateB(color);
    indexB = GLUtils.allocateB(indices);
    vertexB = GLUtils.allocateB(vertices);
  }

  public ShortBuffer getIndexB() {return indexB;}
  public FloatBuffer getVertexB() {return vertexB;}
  public FloatBuffer getColorB() {return colorB;}
  public boolean isOpaque() {return opaque;}
}