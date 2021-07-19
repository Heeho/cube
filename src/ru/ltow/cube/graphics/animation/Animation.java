package ru.ltow.cube;

public abstract class Animation {
    public final Rendered r;
    public int counter;
    public boolean finished;

    public Animation(Rendered r) {
        this.r = r;
    }

    public abstract void perform();
    public boolean isFinished() {return finished;}
}