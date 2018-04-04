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

        for (Group g : model.getGroups()) {
            for (IBufferObject b : g.getBuffers()) {

                for (Triangle t : b.getTriangles()) {
                    final Float i = ray.intersects(t);
                    if (i != null && (info == null || i < info.distance)) {
                        if (info == null)
                            info = new IntersectionInfo();
                        info.distance = i;
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
