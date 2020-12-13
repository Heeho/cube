package ru.ltow.cube;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Main extends BaseA {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        CheckBoxListener cbl = new CheckBoxListener();

        CheckBox p1human = findViewById(R.id.p1CB);
        p1human.setChecked(App.prefs.playerIsHuman(PrefsManager.P1_HUMAN));
        p1human.setOnCheckedChangeListener(cbl);

        CheckBox p2human = findViewById(R.id.p2CB);
        p2human.setChecked(App.prefs.playerIsHuman(PrefsManager.P2_HUMAN));
        p2human.setOnCheckedChangeListener(cbl);

        CheckBox p3human = findViewById(R.id.p3CB);
        p3human.setChecked(App.prefs.playerIsHuman(PrefsManager.P3_HUMAN));
        p3human.setOnCheckedChangeListener(cbl);
    }

    public void gotoActivity(View v) {
        final int id = v.getId();
        Class<?> c = (id == R.id.xoglB) ? XOGL.class : (id == R.id.aboutB) ? About.class : getClass();

        startActivity(new Intent(this, c));
    }

    private static class CheckBoxListener implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
            switch(cb.getId()) {
                case(R.id.p1CB):
                    App.prefs.setPlayerHuman(PrefsManager.P1_HUMAN, isChecked);
                    break;
                case(R.id.p2CB):
                    App.prefs.setPlayerHuman(PrefsManager.P2_HUMAN, isChecked);
                    break;
                case(R.id.p3CB):
                    App.prefs.setPlayerHuman(PrefsManager.P3_HUMAN, isChecked);
                    break;
            }
        }
    }
}