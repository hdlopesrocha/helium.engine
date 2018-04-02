package com.enter4ward.math;


// TODO: Auto-generated Javadoc

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * The Class IObject3D.
 */
public abstract class IObject3D {

    /**
     * The position.
     */
    private Vector3f position;

    /**
     * The rotation.
     */
    private Quaternionf rotation = new Quaternionf().identity();

    /**
     * The model.
     */
    private IModel3D model;

    private Space.Node node;

    /**
     * Instantiates a new i object3 d.
     *
     * @param position the position
     * @param model    the model
     */
    public IObject3D(Vector3f position, IModel3D model) {
        this.position = position;
        this.model = model;
    }

    /**
     * Gets the model matrix.
     *
     * @return the model matrix
     */
    public Matrix4f getModelMatrix() {
        return new Matrix4f().translation(new Vector3f(position)).rotate(rotation);
    }

    /**
     * Gets the model.
     *
     * @return the model
     */
    public IModel3D getModel() {
        return model;
    }

    public void setModel(IModel3D model) {
        this.model = model;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.enter4ward.math.api.ISphere#getPosition()
     */

    /**
     * Gets the bounding sphere.
     *
     * @return the bounding sphere
     */
    public BoundingSphere getBoundingSphere() {
        BoundingSphere cont = model.getContainer();
        BoundingSphere result = new BoundingSphere(model.getContainer());
        result.rotate(rotation);
        result.add(position);
        result.r = cont.r;
        return result;
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Sets the position.
     *
     * @param position the new position
     */
    public void setPosition(final Vector3f position) {
        this.position = position;
    }

    /**
     * Insert.
     *
     * @param space the space
     * @return the i object3 d
     */
    protected Space.Node insert(final Space space) {
        return space.insert(getBoundingSphere(), this);
    }

    /**
     * Gets the rotation.
     *
     * @return the rotation
     */
    public Quaternionf getRotation() {
        return rotation;
    }

    /**
     * Closest triangle.
     *
     * @param ray the ray
     * @return the intersection info
     */
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
