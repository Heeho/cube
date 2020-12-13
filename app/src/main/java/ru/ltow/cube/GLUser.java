package ru.ltow.cube;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.ByteOrder;

import java.util.ArrayList;

import android.opengl.GLES20;
import android.util.Log;

public class GLUser {
    protected final int VBO = 2;
    protected final int FBO = 3;
    protected final int RBO = 4;
    protected final int TEX = 5;

    protected final int BYTES_PER_INT32 = 4;
    protected final int BYTES_PER_SHORT = 2;
    protected final int BYTES_PER_FLOAT = 4;
    protected final int COORDS_PER_VERTEX = 3;

    private final String TAG = "cube_gl_log";

    private static final float ALPHA2 = 0.15f;
    private static final float W = 160f/255f;

    public static final float[] ORANGE_W = {1.0f, 0.5f, W, 1.0f};
    public static final float[] GREEN_W = {W, 1.0f, W, 1.0f};
    public static final float[] MAGENTA_W = {1.0f, W, 1.0f, 1.0f};
    public static final float[] YELLOW_W = {1.0f, 1.0f, W, 1.0f};

    public static final float[] WHITE_T = {1.0f, 1.0f, 1.0f, ALPHA2};

    protected int linkProgram(String vertexShaderCode, String fragmentShaderCode) {
        int prog = GLES20.glCreateProgram();

        IntBuffer linked = allocateBuffer(new int[1]);

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

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        IntBuffer compiled = allocateBuffer(new int[1]);

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

    protected int gen(int obj) {
        IntBuffer name = allocateBuffer(new int[1]);
        switch(obj) {
            case VBO: GLES20.glGenBuffers(1, name); break;
            case FBO: GLES20.glGenFramebuffers(1, name); break;
            case RBO: GLES20.glGenRenderbuffers(1, name); break;
            case TEX: GLES20.glGenTextures(1, name); break;
            default: return 0;
        }
        return name.get(0);
    }

    protected void checkFramebuffer() {
        if(GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE)
        Log.e(TAG, "framebuffer incomplete: " + GLES20.GL_FRAMEBUFFER);
    }

    protected void log(String s) {
        Log.e(TAG, s);
    }

    protected void checkError(String line) {
        String error;
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

    protected IntBuffer allocateBuffer(int[] i) {
        IntBuffer ib;
        ByteBuffer bb = ByteBuffer.allocateDirect(i.length * BYTES_PER_INT32);
        bb.order(ByteOrder.nativeOrder());
        ib = bb.asIntBuffer();
        ib.put(i);
        ib.position(0);
        return ib;
    }

    protected FloatBuffer allocateBuffer(float[] f) {
        FloatBuffer fb;
        ByteBuffer bb = ByteBuffer.allocateDirect(f.length * BYTES_PER_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        fb = bb.asFloatBuffer();
        fb.put(f);
        fb.position(0);
        return fb;
    }

    protected ShortBuffer allocateBuffer(short[] s) {
        ShortBuffer sb;
        ByteBuffer bb = ByteBuffer.allocateDirect(s.length * BYTES_PER_SHORT);
        bb.order(ByteOrder.nativeOrder());
        sb = bb.asShortBuffer();
        sb.put(s);
        sb.position(0);
        return sb;
    }

    protected ByteBuffer allocateBuffer(byte[] b) {
        ByteBuffer bb = ByteBuffer.allocateDirect(b.length);
        bb.order(ByteOrder.nativeOrder());
        bb.put(b);
        bb.position(0);
        return bb;
    }

    protected float[] listToFloatArray(ArrayList<Float> list) {
        int size = list.size();
        float[] f = new float[size];
        for(int i = 0; i < size; i++) {
            f[i] = list.get(i);
        }
        return f;
    }

}