package ru.ltow.cube;

import java.util.ArrayList;

public class Utils {
  public static int[] ltoi(ArrayList<Integer> list) {
    int size = list.size();
    int[] i = new int[size];
    for(int n = 0; n < size; n++) {
      i[n] = list.get(n).intValue();
    }
    return i;
  }

  public static float[] ltof(ArrayList<Float> list) {
    int size = list.size();
    float[] f = new float[size];
    for(int i = 0; i < size; i++) {
      f[i] = list.get(i).floatValue();
    }
    return f;
  }

  public static ArrayList<Float> atoAL(float[] f) {
    ArrayList<Float> result = new ArrayList<Float>();
    for(int i = 0; i < f.length; i++) {
      result.add(f[i]);
    }
    return result;
  }

  public static ArrayList<Integer> atoAL(int[] n) {
    ArrayList<Integer> result = new ArrayList<Integer>();
    for(int i = 0; i < n.length; i++) {
      result.add(n[i]);
    }
    return result;
  }
}