package ru.ltow.cube;

import android.os.Bundle;

public class XOGL extends BaseA {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GLView gl = new GLView(this);
        setContentView(gl);
    }
}