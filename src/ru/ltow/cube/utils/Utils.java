package ru.ltow.cube;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

  public static Set<Integer> findDuplicates(ArrayList<Integer> a) {
    Set<Integer> uniques = new HashSet<Integer>();
    Set<Integer> duplicates = new HashSet<Integer>();

    for(int cell : a) {
      if(!uniques.add(cell)) {
        if(!duplicates.add(cell)) {
          //if false, move element to 0
          Set<Integer> duplicates_ = new HashSet<Integer>();
          duplicates_.add(cell);
          duplicates_.addAll(duplicates);
          duplicates = duplicates_;
        }
      }
    }
    return duplicates;
  }

  public static Set<Integer> findUniqueMatches(ArrayList<Integer> a, ArrayList<Integer> b) {
    Set<Integer> set1 = new HashSet<Integer>(a);
    Set<Integer> set2 = new HashSet<Integer>(b);
    Set<Integer> matches = new HashSet<Integer>();

    for(int cell : set1) {
      if(!set2.add(cell)) {
        matches.add(cell);
      }
    }
    return matches;
  }
}