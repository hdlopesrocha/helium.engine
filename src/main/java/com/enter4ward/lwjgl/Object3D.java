package com.enter4ward.lwjgl;

import com.enter4ward.math.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Object3D extends IObject3D {
    private static final BoundingSphere TEMP_BOUNDING_SPHERE = new BoundingSphere();
    private static final Vector3f TEMP_VECTOR3F = new Vector3f();
    private static final Matrix4f TEMP_MODEL_MATRIX = new Matrix4f();

    public Object3D(Vector3f position, IModel3D model) {
        super(position, model);
    }

    public void draw(ShaderProgram program, BoundingFrustum frustum) {
        final Matrix4f matrix = getModelMatrix(TEMP_MODEL_MATRIX);
        final LWJGLModel3D model = (LWJGLModel3D) getModel();

        int groupCount = model.getGroups().size();
        for (int i = 0; i < groupCount; ++i) {
            Group g = model.getGroups().get(i);
            int bufferCount = g.getBuffers().size();
            for (int j = 0; j < bufferCount; ++j) {
                BufferObject b =  (BufferObject) g.getBuffers().get(j);
                final BoundingSphere sph = TEMP_BOUNDING_SPHERE.set(b);
                sph.add(matrix.getTranslation(TEMP_VECTOR3F));
                if (frustum.contains(sph) != ContainmentType.Disjoint) {
                    b.bind(program);
                    program.setModelMatrix(matrix);
                    b.draw(program);
                }
            }
        }
    }

    public void drawBoundingSpheres(ShaderProgram program, BoundingFrustum frustum) {
        final Matrix4f matrix = getModelMatrix(TEMP_MODEL_MATRIX);
        final LWJGLModel3D model = (LWJGLModel3D) getModel();
        int groupCount = model.getGroups().size();
        for (int i = 0; i < groupCount; ++i) {
            Group g = model.getGroups().get(i);
            int bufferCount = g.getBuffers().size();
            for (int j = 0; j < bufferCount; ++j) {
                BufferObject b =  (BufferObject) g.getBuffers().get(j);
                final BoundingSphere sph = TEMP_BOUNDING_SPHERE.set(b);
                sph.add(matrix.getTranslation(TEMP_VECTOR3F));

                if (frustum.contains(sph) != ContainmentType.Disjoint) {
                    model.getSphere().draw(program, sph, null);
                }
            }
        }
    }
}
