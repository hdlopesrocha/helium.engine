package com.enter4ward.math;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class IObject3D {

    private static final BoundingSphere TEMP_BOUNDING_SPHERE = new BoundingSphere();
    private Vector3f position;
    private final Quaternionf rotation = new Quaternionf().identity();
    private IModel3D model;
    private Space.Node node;

    public IObject3D(Vector3f position, IModel3D model) {
        this.position = position;
        this.model = model;
    }

    public Matrix4f getModelMatrix(Matrix4f result) {
        return result.translation(position).rotate(getRotation());
    }

    public IModel3D getModel() {
        return model;
    }

    public void setModel(IModel3D model) {
        this.model = model;
    }

    public BoundingSphere getBoundingSphere(BoundingSphere result) {
        BoundingSphere cont = model.getContainer();
        result.set(model.getContainer());
        result.rotate(rotation);
        result.add(position);
        result.r = cont.r;
        return result;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(final Vector3f position) {
        this.position = position;
    }

    protected Space.Node insert(final Space space) {
        return space.insert(getBoundingSphere(TEMP_BOUNDING_SPHERE), this);
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public IntersectionInfo closestTriangle(final Ray ray) {
        IntersectionInfo info = null;
        final Model3D model = (Model3D) getModel();
        int groupCount = model.getGroups().size();
        for (int i = 0; i < groupCount; ++i) {
            Group g = model.getGroups().get(i);
            int bufferCount = g.getBuffers().size();
            for (int j = 0; j < bufferCount; ++j) {
                IBufferObject b = g.getBuffers().get(j);
                int triangleCount = b.getTriangles().size();
                for (int k = 0; k < triangleCount; ++k) {
                    Triangle t = b.getTriangles().get(k);
                    final Float d = ray.intersects(t);
                    if (d != null && (info == null || i < info.distance)) {
                        if (info == null)
                            info = new IntersectionInfo();
                        info.distance = d;
                        info.triangle = t;
                    }
                }
            }
        }
        return info;
    }

    public Space.Node getNode() {
        return node;
    }

    public void setNode(Space.Node node) {
        this.node = node;
    }
}
