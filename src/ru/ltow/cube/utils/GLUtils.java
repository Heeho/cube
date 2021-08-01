package ru.ltow.cube;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.ByteOrder;
import android.opengl.GLES20;
import android.util.Log;
import android.opengl.Matrix;

public class GLUtils {
  public static enum genmode { VAO, VBO, FBO, RBO, TEX };

  public static final int BYTES_PER_INT32 = 4;
  public static final int BYTES_PER_SHORT = 2;
  public static final int BYTES_PER_FLOAT = 4;
  public static final int COORDS_PER_VERTEX = 3;
  public static final int MATRIX_LENGTH = 16;
  public static final int MATRIX_ROW_LENGTH = 4;

  private static final String TAG = "glutils_log";

  public static final float[] NOCOLOR = {0, 0, 0, 0};

  public static int linkProgram(String vertexShaderCode, String fragmentShaderCode) {
    int prog = GLES20.glCreateProgram();

    IntBuffer linked = allocateB(new int[1]);

    GLES20.glAttachShader(prog, loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode));
    GLES20.glAttachShader(prog, loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode));
    GLES20.glLinkProgram(prog);

    GLES20.glGetProgramiv(prog, GLES20.GL_LINK_STATUS, linked);

    if(linked.get(0) == 0) {
      Log.e(TAG, "program link error: " + prog);
      Log.e(TAG, GLES20.glGetProgramInfoLog(prog));
      GLES20.glDeleteProgram(prog);
      prog = 0;
    }

    return prog;
  }

  private static int loadShader(int type, String shaderCode) {
    int shader = GLES20.glCreateShader(type);

    IntBuffer compiled = allocateB(new int[1]);

    GLES20.glShaderSource(shader, shaderCode);
    GLES20.glCompileShader(shader);
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled);

    if(compiled.get(0) == 0) {
      Log.e(TAG, "shader compile error: " + type);
      Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
      GLES20.glDeleteShader(shader);
      shader = 0;
    }

    return shader;
  }

  public static int gen(genmode m) {
    IntBuffer name = allocateB(new int[1]);
    switch(m) {
      case VBO: GLES20.glGenBuffers(1, name); break;
      case FBO: GLES20.glGenFramebuffers(1, name); break;
      case RBO: GLES20.glGenRenderbuffers(1, name); break;
      case TEX: GLES20.glGenTextures(1, name); break;
      default: return 0;
    }
    return name.get(0);
  }

  public static void checkFramebuffer() {
    if(GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE)
    log("framebuffer incomplete: " + GLES20.GL_FRAMEBUFFER);
  }

  private static void log(String s) {
    Log.e(TAG, s);
  }

  public static void checkMax() {
    IntBuffer paramHandle = allocateB(new int[1]);
    GLES20.glGetIntegerv(GLES20.GL_MAX_VERTEX_ATTRIBS, paramHandle);
    Log.e(TAG,
    String.format(
       "current limits: GL_MAX_VERTEX_ATTRIBS: %d",
       paramHandle.get(0)
    ));
  }

  public static void checkCurrentFBO() {
    IntBuffer framebufferHandles = allocateB(new int[1]);
    GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, framebufferHandles);
    Log.e(TAG, "currently bound framebuffer: " + framebufferHandles.get(0));
  }

  public static void checkError(String line) {
    String error = null;
    switch(GLES20.glGetError()) {
      case GLES20.GL_NO_ERROR: return;
      case GLES20.GL_INVALID_ENUM: error = "GL_INVALID_ENUM: An unacceptable value is specified for an enumerated argument. The offending command is ignored and has no other side effect than to set the error flag."; break;
      case GLES20.GL_INVALID_VALUE: error = "GL_INVALID_VALUE: A numeric argument is out of range. The offending command is ignored and has no other side effect than to set the error flag."; break;
      case GLES20.GL_INVALID_OPERATION: error = "GL_INVALID_OPERATION: The specified operation is not allowed in the current state. The offending command is ignored and has no other side effect than to set the error flag."; break;
      case GLES20.GL_INVALID_FRAMEBUFFER_OPERATION: error = "GL_INVALID_FRAMEBUFFER_OPERATION: The framebuffer object is not complete. The offending command is ignored and has no other side effect than to set the error flag."; break;
      case GLES20.GL_OUT_OF_MEMORY: error = "GL_OUT_OF_MEMORY: There is not enough memory left to execute the command. The state of the GL is undefined, except for the state of the error flags, after this error is recorded."; break;
      default: error = "non-enum error occured"; break;
    }
    Log.e(TAG, line + ": " + error);
  }

  public static IntBuffer allocateB(int[] i) {
    IntBuffer ib = null;
    ByteBuffer bb = ByteBuffer.allocateDirect(i.length * BYTES_PER_INT32);
    bb.order(ByteOrder.nativeOrder());
    ib = bb.asIntBuffer();
    ib.put(i);
    ib.position(0);
    return ib;
  }

  public static FloatBuffer allocateB(float[] f) {
    FloatBuffer fb = null;
    ByteBuffer bb = ByteBuffer.allocateDirect(f.length * BYTES_PER_FLOAT);
    bb.order(ByteOrder.nativeOrder());
    fb = bb.asFloatBuffer();
    fb.put(f);
    fb.position(0);
    return fb;
  }

  public static ShortBuffer allocateB(short[] s) {
    ShortBuffer sb = null;
    ByteBuffer bb = ByteBuffer.allocateDirect(s.length * BYTES_PER_SHORT);
    bb.order(ByteOrder.nativeOrder());
    sb = bb.asShortBuffer();
    sb.put(s);
    sb.position(0);
    return sb;
  }

  public static ByteBuffer allocateB(byte[] b) {
    ByteBuffer bb = ByteBuffer.allocateDirect(b.length);
    bb.order(ByteOrder.nativeOrder());
    bb.put(b);
    bb.position(0);
    return bb;
  }

  public static float[] scaleM(float[] state, float scale) {
    float[] m = new float[MATRIX_LENGTH];
    Matrix.scaleM(m, 0, state, 0, scale, scale, scale);
    return m;
  }

  public static float[] matrix(float aX, float aY, float aZ, float tX, float tY, float tZ) {
    float[] idM = new float[MATRIX_LENGTH];
    float[] m = new float[MATRIX_LENGTH];
    Matrix.setIdentityM(idM, 0);
    Matrix.rotateM(idM, 0, aY, 0,1f,0);
    Matrix.rotateM(idM, 0, aX, 1f,0,0);
    Matrix.translateM(m, 0, idM, 0, tX, tY, tZ);
    return m;
  }

  public static float[] rgba32to4fv(int c) {
    float b = 255f;
    String hex = Integer.toHexString(c);

    float[] result = new float[]{
      Integer.parseInt(hex.substring(2,4), 16)/b,
      Integer.parseInt(hex.substring(4,6), 16)/b,
      Integer.parseInt(hex.substring(6,8), 16)/b,
      Integer.parseInt(hex.substring(0,2), 16)/b
    };

    return result;
  }
}