package ru.ltow.cube;

import android.app.Application;

public class App extends Application {
  public static PrefsManager prefs;

  @Override
  public void onCreate() {
    super.onCreate();

    prefs = new PrefsManager(this);
    Colors.set(
      getColor(R.color.BG),
      getColor(R.color.X),
      getColor(R.color.O),
      getColor(R.color.V),
      getColor(R.color.E),
      getColor(R.color.U)
    );
  }
}