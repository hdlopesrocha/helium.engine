package com.enter4ward.math;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera extends BoundingFrustum {

    private final Quaternionf rotation;

    private final Vector3f position;

    private final Matrix4f projectionMatrix;

    private float far, near;

    public Camera(int w, int h, float near, float far) {
        position = new Vector3f();
        rotation = new Quaternionf().identity();
        this.far = far;
        this.near = near;
        projectionMatrix = new Matrix4f();
        calculateProjection(w, h);
    }

    // OK
    private Matrix4f getViewMatrix() {
        Vector3f translation = new Vector3f(position).mul(-1f);
        Matrix4f result = new Matrix4f().rotate(rotation);
        result.translate(translation);
        return result;
    }

    public Matrix4f getViewProjectionMatrix() {
        Matrix4f matrix = new Matrix4f(projectionMatrix).mul(getViewMatrix());
        update(matrix);
        return matrix;
    }

    public void rotate(float x, float y, float z, float w) {
        Quaternionf quat = new Quaternionf().fromAxisAngleRad(x, y, z, w).mul(rotation);
        rotation.set(quat).normalize();
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    // OK
    public void lookAt(Vector3f pos, Vector3f lookAt, Vector3f up) {
        position.set(pos);
        rotation.setFromNormalized(new Matrix4f().lookAt(pos, lookAt, up));
    }

    // OK
    private void calculateProjection(int w, int h) {
        projectionMatrix.perspective((float) Math.toRadians(45f), (float) w / (float) h, near, far);
    }

    public void move(Vector3f change) {
        position.add(change);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void move(float front, float down, float right) {
        final Vector3f trans = new Vector3f(right, down, front);
        final Matrix4f mat = new Matrix4f().translation(trans).rotate(rotation).invert();
        final Vector3f delta = new Vector3f();
        mat.getTranslation(delta);
        position.add(delta);
    }

    public Vector3f getPosition() {
        return position;
    }

}
