package ru.ltow.cube;

import android.opengl.GLES20;
import java.util.ArrayList;

class ColorProgram extends GLUser{
    private final int programL;
    private final int indexBL;
    private final int vertexBL;

    public static final int COLOR = 0;
    public static final int PICK = 1;

    public ColorProgram() {
        String vs =
            "#version 100\n" +

            "uniform mat4 vpM;" +
            "uniform mat4 stateM;" +
            "attribute vec4 position;" +

            "void main() {" +
            "  gl_Position = vpM * stateM * position;" +
            "}";

        String fs =
            "#version 100\n" +
            "precision mediump float;" +

            "uniform vec4 color;" +

            "void main() {" +
            "  gl_FragColor = color;" +
            "}";

        programL = linkProgram(vs, fs);

        indexBL = gen(VBO);
        vertexBL = gen(VBO);
    }

    public void render(int mode, Model model, ArrayList<Rendered> instances, float[] vpMatrix) {
        GLES20.glUseProgram(programL);

        int vpMatrixL = GLES20.glGetUniformLocation(programL, "vpM");
        GLES20.glUniformMatrix4fv(vpMatrixL, 1, false, allocateBuffer(vpMatrix));

        int colorL = GLES20.glGetUniformLocation(programL, "color");
        if(mode == COLOR) GLES20.glUniform4fv(colorL, 1, model.getColorB());

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBL);
        GLES20.glBufferData(
            GLES20.GL_ELEMENT_ARRAY_BUFFER,
            model.getIndexB().capacity() * BYTES_PER_SHORT,
            model.getIndexB(),
            GLES20.GL_STREAM_DRAW);

        int positionL = GLES20.glGetAttribLocation(programL, "position");
        GLES20.glEnableVertexAttribArray(positionL);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBL);
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER,
            model.getVertexB().capacity() * BYTES_PER_FLOAT,
            model.getVertexB(),
            GLES20.GL_STREAM_DRAW);
        GLES20.glVertexAttribPointer(
                positionL,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            COORDS_PER_VERTEX * BYTES_PER_FLOAT,
            0);

        for(Rendered r : instances) {
            if(mode == PICK) GLES20.glUniform4fv(colorL, 1, allocateBuffer(new float[]{
                0, 0, 0, r.getId() / 255f
            }));

            int stateL = GLES20.glGetUniformLocation(programL, "stateM");
            GLES20.glUniformMatrix4fv(stateL, 1, false, allocateBuffer(r.getState()));

            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                model.getIndexB().capacity(),
                GLES20.GL_UNSIGNED_SHORT,
                0);
        }

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}