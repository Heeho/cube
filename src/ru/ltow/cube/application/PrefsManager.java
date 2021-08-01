package ru.ltow.cube;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefsManager {
  private SharedPreferences sp;
  private Editor e;

  public static final String SP_NAME = "preferences";

  public static final String P1_HUMAN = "p1human";
  public static final String P2_HUMAN = "p2human";
  public static final String P3_HUMAN = "p3human";

  public PrefsManager(Context c) {
    sp = c.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    e = sp.edit();
  }

  public SharedPreferences getPrefs() {
    return sp;
  }

  public void setPlayerHuman(String s, boolean yn) {
    e.putBoolean(s, yn);
    e.apply();
  }

  public boolean playerIsHuman(String s) {
    return sp.getBoolean(s, (s == P1_HUMAN));
  }
}