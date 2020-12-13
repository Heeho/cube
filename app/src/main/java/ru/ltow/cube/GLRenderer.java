package ru.ltow.cube;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.HashMap;

import java.nio.ByteBuffer;
import java.util.Objects;

public class GLRenderer extends GLUser implements GLSurfaceView.Renderer{
    private final HashMap<Integer,Model> models = new HashMap<>();

    private ArrayList<Rendered> things;

    private HashMap<Integer,ArrayList<Rendered>> instancesOpaque = new HashMap<>();
    private HashMap<Integer,ArrayList<Rendered>> instancesTransp = new HashMap<>();

    private ColorProgram colorP;

    private int fbo;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] vpMatrix = new float[16];

    private int height;

    private float angleX, angleY;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 4.5f, 0, 0, 0, 0, 1.0f, 0);

        colorP = new ColorProgram();

        float size = 1f/6f;
        models.put(Rendered.CELL_E, new Cube(WHITE_T, size));
        models.put(Rendered.CELL_X, new Cube(MAGENTA_W, size));
        models.put(Rendered.CELL_V, new Cube(GREEN_W, size));
        models.put(Rendered.CELL_O, new Cube(YELLOW_W, size));
        models.put(Rendered.CELL_U, new Cube(ORANGE_W, size));

        initInstances();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        checkError("render start");

        for(Rendered r : things) {r.performAnimation();}

        clearBuffers();

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        renderOpaque();
        renderTransparent();

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo);
        renderPick();

        checkError("render end");
    }

    private void clearBuffers() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glClearColor(0, 1f, 1f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo);
        GLES20.glClearColor(0, 0, 0, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);

        checkError("buffers cleared");
    }

    private void renderOpaque() {
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LESS);
        GLES20.glDepthMask(true);
        GLES20.glDisable(GLES20.GL_BLEND);

        for(int model : instancesOpaque.keySet()) {
            colorP.render(
                ColorProgram.COLOR, models.get(model), instancesOpaque.get(model), vpMatrix);
        }
        checkError("opaque done");
    }

    private void renderTransparent() {
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LESS);
        GLES20.glDepthMask(false);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        for(int model : instancesTransp.keySet()) {
            colorP.render(
                ColorProgram.COLOR, models.get(model), instancesTransp.get(model), vpMatrix);
        }
        checkError("simple t done");
    }

    private void renderPick() {
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LESS);
        GLES20.glDepthMask(true);
        GLES20.glDisable(GLES20.GL_BLEND);

        for(int model : instancesOpaque.keySet()) {
            colorP.render(
                ColorProgram.PICK, models.get(model), instancesOpaque.get(model), vpMatrix);
        }
        for(int model : instancesTransp.keySet()) {
            colorP.render(
                ColorProgram.PICK, models.get(model), instancesTransp.get(model), vpMatrix);
        }
        checkError("pick done");
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int w, int h) {
        height = h;

        GLES20.glViewport(0, 0, w, height);

        float ratio = (float) w / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 77);
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        fbo = gen(FBO);
        int rbo = gen(RBO);
        int tex = gen(TEX);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex);
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
                w,
            height,
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            null);
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D,
                tex,
            0);

        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, rbo);
        GLES20.glRenderbufferStorage(
            GLES20.GL_RENDERBUFFER,
            GLES20.GL_DEPTH_COMPONENT16,
                w,
            height);
        GLES20.glFramebufferRenderbuffer(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_DEPTH_ATTACHMENT,
            GLES20.GL_RENDERBUFFER,
                rbo);

        checkFramebuffer();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void setThings(ArrayList<Rendered> r) {
        things = r;
    }

    public void initInstances() {
        int model;
        Rendered thing;
        HashMap<Integer,ArrayList<Rendered>> destination;

        instancesOpaque = new HashMap<>();
        instancesTransp = new HashMap<>();

        for(int i = 0; i < things.size(); i++) {
            thing = things.get(i);
            model = thing.getModel();

            destination = (Objects.requireNonNull(models.get(model)).isOpaque()) ? instancesOpaque : instancesTransp;

            if(destination.get(model) == null) {
                destination.put(model, new ArrayList<>());
            }

            Objects.requireNonNull(destination.get(model)).add(thing);
        }
    }

    public void rotateCamera(float dx, float dy) {
        angleX += dx;
        angleY += dy;

        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.rotateM(vpMatrix, 0, angleY, 1.0f, 0, 0);
        Matrix.rotateM(vpMatrix, 0, angleX, 0, 1.0f, 0);
    }

    public int pickObject(int screenX, int screenY) {
        @SuppressWarnings("UnnecessaryLocalVariable") int texX = screenX;
        int texY = height - screenY;

        ByteBuffer i = allocateBuffer(new byte[4]);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo);
        GLES20.glReadPixels(texX, texY, 1, 1, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, i);

        return i.get(3);
    }
}