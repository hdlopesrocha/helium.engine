package com.enter4ward.lwjgl;

import com.enter4ward.math.VertexData;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class DrawableBox {

    private static final Vector3f TEMP_SCALE = new Vector3f();
    private static final Matrix4f TEMP_MATRIX = new Matrix4f();
    final BufferObject buffer = new BufferObject(false);
    final VertexData data = new VertexData();

    public DrawableBox() {

        float[] packedVector = {0, 0, 0, 0, 0, -1, 1, 0, 0, 1, 0, 0, 0, -1, 1, 1,
                1, 1, 0, 0, 0, -1, 0, 1, 1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 1, 0, 0, 1, 0,
                0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 1, 0,
                1, 0, 0, 0, 0, -1, 0, 0, 0, 1, 0, 0, 0, -1, 0, 1, 0, 1, 0, 1, 0, -1, 0,
                1, 1, 0, 0, 1, 0, -1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 1, 0,
                0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1,
                0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1,
                0, 0, 1, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 1, -1,
                0, 0, 1, 1, 0, 1, 1, -1, 0, 0, 0, 1};
        short[] ii = {0, 1, 1, 1, 1, 2, 2, 3, 3, 3, 3, 0, 4, 5, 5, 5, 5, 6, 6, 7,
                7, 7, 7, 4, 8, 9, 9, 9, 9, 10, 10, 11, 11, 11, 11, 8, 12, 13, 13, 13,
                13, 14, 14, 15, 15, 15, 15, 12, 16, 17, 17, 17, 17, 18, 18, 19, 19, 19,
                19, 16, 20, 21, 21, 21, 21, 22, 22, 23, 23, 23, 23, 20};

        for (int i = 0; i < packedVector.length; i += 8) {
            data.addPosition(new Vector3f(packedVector[i], packedVector[i + 1], packedVector[i + 2]));
            data.addNormal(new Vector3f(packedVector[i + 3], packedVector[i + 4], packedVector[i + 5]));
            data.addTexture(new Vector2f(packedVector[i + 6], packedVector[i + 7]));
        }

        for (int i = 0; i < ii.length; ++i) {
            data.addIndex(ii[i]);
        }

        buffer.buildBuffer(data);
    }

    public final void draw(final ShaderProgram shader, final Vector3f min,
                           final Vector3f max) {
        glDisable(GL_CULL_FACE);
        TEMP_SCALE.set(max).sub(min);
        TEMP_MATRIX.translation(min).scale(TEMP_SCALE);
        shader.setModelMatrix(TEMP_MATRIX);
        glLineWidth(3);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        buffer.draw(shader);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glEnable(GL_CULL_FACE);
    }

}
