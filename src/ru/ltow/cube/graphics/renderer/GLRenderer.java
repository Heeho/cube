package ru.ltow.cube;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.HashMap;

import java.nio.ByteBuffer;

public class GLRenderer implements GLSurfaceView.Renderer {
  private HashMap<Integer,Model> models = new HashMap<>();

  private ArrayList<Rendered> things = new ArrayList<>();

  private HashMap<Integer,ArrayList<Rendered>> instancesOpaque = new HashMap<>();
  private HashMap<Integer,ArrayList<Rendered>> instancesTransp = new HashMap<>();

  private ColorProgram colorP;

  private int fbo, rbo, tex;

  private final float[] projectionMatrix = new float[16];
  private final float[] viewMatrix = new float[16];
  private final float[] vpMatrix = new float[16];

  private int width, height;

  private float angleX, angleY;

  @Override
  public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    Matrix.setLookAtM(viewMatrix, 0, 0, 0, 4.5f, 0, 0, 0, 0, 1.0f, 0);

    colorP = new ColorProgram();

    float size = 1f/6f;
    models.put(0, new Cube(Colors.X, size));
    models.put(1, new Cube(Colors.O, size));
    models.put(2, new Cube(Colors.V, size));
    models.put(3, new Cube(Colors.E, size));
    models.put(4, new Cube(Colors.U, size));

    initInstances();
  }

  @Override
  public void onDrawFrame(GL10 unused) {
    GLUtils.checkError("render start");

    for(Rendered r : things) {r.performAnimation();}

    clearBuffers();

    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    renderOpaque();
    renderTransparent();

    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo);
    renderPick();

    GLUtils.checkError("render end");
  }

  private void clearBuffers() {
    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    GLES20.glClearColor(Colors.BG[0], Colors.BG[1], Colors.BG[2], Colors.BG[3]);
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);

    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo);
    GLES20.glClearColor(0, 0, 0, 1f);
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);

    GLUtils.checkError("buffers cleared");
  }

  private void renderOpaque() {
    GLES20.glEnable(GLES20.GL_CULL_FACE);
    GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    GLES20.glDepthFunc(GLES20.GL_LESS);
    GLES20.glDepthMask(true);
    GLES20.glDisable(GLES20.GL_BLEND);

    for(int model : instancesOpaque.keySet()) {
      colorP.render(
        ColorProgram.COLOR, models.get(model), instancesOpaque.get(model), vpMatrix
      );
    }
    GLUtils.checkError("opaque done");
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
    GLUtils.checkError("simple t done");
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
    GLUtils.checkError("pick done");
  }

  @Override
  public void onSurfaceChanged(GL10 unused, int w, int h) {
    width = w;
    height = h;

    GLES20.glViewport(0, 0, width, height);

    float ratio = (float) width / height;
    Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 11);
    Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

    fbo = GLUtils.gen(GLUtils.genmode.FBO);
    rbo = GLUtils.gen(GLUtils.genmode.RBO);
    tex = GLUtils.gen(GLUtils.genmode.TEX);

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
      width,
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
      width,
      height);
    GLES20.glFramebufferRenderbuffer(
      GLES20.GL_FRAMEBUFFER,
      GLES20.GL_DEPTH_ATTACHMENT,
      GLES20.GL_RENDERBUFFER,
      rbo);
    
    GLUtils.checkFramebuffer();

    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
  }

  public ArrayList<Rendered> things() {return things;}

  public void initInstances() {
    int model = 0;
    Rendered thing = null;
    HashMap<Integer,ArrayList<Rendered>> destination = null;

    instancesOpaque = new HashMap<Integer,ArrayList<Rendered>>();
    instancesTransp = new HashMap<Integer,ArrayList<Rendered>>();

    for(int i = 0; i < things.size(); i++) {
      thing = things.get(i);
      model = thing.model();

      destination = (models.get(model).isOpaque()) ? instancesOpaque : instancesTransp;

      if(destination.get(model) == null) {
        destination.put(model, new ArrayList<Rendered>());
      }

      destination.get(model).add(thing);
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
    int texX = screenX;
    int texY = height - screenY;

    ByteBuffer i = GLUtils.allocateB(new byte[4]);

    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo);
    GLES20.glReadPixels(texX, texY, 1, 1, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, i);

    return i.get(3);
  }
}