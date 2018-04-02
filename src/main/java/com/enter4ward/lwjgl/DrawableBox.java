package com.enter4ward.lwjgl;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class DrawableBox {

    BufferObject obj = new BufferObject(false);

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
            obj.addVertex(packedVector[i + 0], packedVector[i + 1],
                    packedVector[i + 2]);

            obj.addNormal(packedVector[i + 3], packedVector[i + 4],
                    packedVector[i + 5]);
            obj.addTexture(packedVector[i + 6], packedVector[i + 7]);

        }

        for (int i = 0; i < ii.length; ++i) {
            obj.addIndex(ii[i]);
        }

        obj.buildBuffer();
    }

    /**
     * Draw.
     *
     * @param shader the shader
     */
    public final void draw(final ShaderProgram shader, final Vector3f min,
                           final Vector3f max) {

        glDisable(GL_CULL_FACE);

        Vector3f scale = new Vector3f(max).sub(min);
        Matrix4f modelMatrix = new Matrix4f().translate(min).scale(scale);
        shader.setModelMatrix(modelMatrix);

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        obj.draw(shader);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        glEnable(GL_CULL_FACE);

    }

}
