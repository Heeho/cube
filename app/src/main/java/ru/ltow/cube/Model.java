package ru.ltow.cube;

import java.nio.ShortBuffer;
import java.nio.FloatBuffer;

public class Model extends GLUser {
    private final ShortBuffer indexB;
    private final FloatBuffer vertexB;
    private final FloatBuffer colorB;

    private final boolean opaque;

    protected Model(float[] vertices, short[] indices, float[] color) {
        opaque = color[3] == 1.0f;
        colorB = allocateBuffer(color);
        indexB = allocateBuffer(indices);
        vertexB = allocateBuffer(vertices);
    }

    public ShortBuffer getIndexB() {
        return indexB;
    }

    public FloatBuffer getVertexB() {
        return vertexB;
    }

    public FloatBuffer getColorB() {
        return colorB;
    }

    public boolean isOpaque() {
        return opaque;
    }
}