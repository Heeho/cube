package ru.ltow.cube;
//save/change in prefs?
public class Colors {
  private static int[] players;
  public static float[] BG, X, O, V, E, U;

  public static void set(int bg, int x, int o, int v, int e, int u) {
    players = new int[]{x, o, v};

    BG = GLUtils.rgba32to4fv(bg);
    X = GLUtils.rgba32to4fv(x);
    O = GLUtils.rgba32to4fv(o);
    V = GLUtils.rgba32to4fv(v);
    E = GLUtils.rgba32to4fv(e);
    U = GLUtils.rgba32to4fv(u);
  }

  public static int player(int i) {return players[i];}
}