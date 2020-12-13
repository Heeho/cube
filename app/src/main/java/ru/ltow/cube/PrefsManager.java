package ru.ltow.cube;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefsManager {
    private final SharedPreferences sp;

    public static final String SP_NAME = "preferences";

    public static final String P1_HUMAN = "p1human";
    public static final String P2_HUMAN = "p2human";
    public static final String P3_HUMAN = "p3human";

    public PrefsManager(Context c) {
        sp = c.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

    }

    public void setPlayerHuman(String s, boolean yn) {
        Editor e = sp.edit();
        e.putBoolean(s, yn);
        e.apply();
    }

    public boolean playerIsHuman(String s) {
        return sp.getBoolean(s, s == P1_HUMAN);
    }
}