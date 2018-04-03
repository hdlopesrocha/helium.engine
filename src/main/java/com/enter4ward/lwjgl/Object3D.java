package com.enter4ward.lwjgl;

import com.enter4ward.math.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Object3D extends IObject3D {


    public Object3D(Vector3f position, IModel3D model) {
        super(position, model);
    }

    public void draw(ShaderProgram program, BoundingFrustum frustum) {
        final Matrix4f matrix = getModelMatrix();
        final LWJGLModel3D model = (LWJGLModel3D) getModel();
        for (final Group g : model.getGroups()) {
            for (final IBufferObject ib : g.getBuffers()) {
                BufferObject b = (BufferObject) ib;
                final BoundingSphere sph = new BoundingSphere(b) {{
                    add(matrix.getTranslation(new Vector3f()));
                }};

                if (frustum.contains(sph) != ContainmentType.Disjoint) {
                    b.bind(program);
                    program.setModelMatrix(matrix);
                    b.draw(program);
                }
            }
        }
    }

    public void drawBoundingSpheres(ShaderProgram program, BoundingFrustum frustum) {
        final Matrix4f matrix = getModelMatrix();
        final LWJGLModel3D model = (LWJGLModel3D) getModel();
        for (final Group g : model.getGroups()) {
            for (final IBufferObject ib : g.getBuffers()) {
                BufferObject b = (BufferObject) ib;
                final BoundingSphere sph = new BoundingSphere(b) {{
                    add(matrix.getTranslation(new Vector3f()));
                    r = 1;
                }};

                if (frustum.contains(sph) != ContainmentType.Disjoint) {
                    model.getSphere().draw(program, sph, null);
                }
            }
        }
    }
}
