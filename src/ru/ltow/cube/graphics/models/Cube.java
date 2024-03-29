package ru.ltow.cube;

public class Cube extends Model {
  public Cube(float[] color, float size) {
    super(
      new float[]{
       -size,  size, -size,
       -size, -size, -size,
        size, -size, -size,
        size,  size, -size,

       -size,  size,  size,
       -size, -size,  size,
        size, -size,  size,
        size,  size,  size
      },
      new short[]{
        0,1,2,0,2,3,
        0,4,5,0,5,1,
        1,5,6,1,6,2,
        2,6,7,2,7,3,
        3,7,4,3,4,0,
        4,7,6,4,6,5
      },
      color
    );
  }
}