package com.enter4ward.math;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera extends BoundingFrustum {
    private final static Matrix4f TEMP_LOOK_MATRIX = new Matrix4f();
    private final static Matrix4f TEMP_MOVE_MATRIX = new Matrix4f();
    private static final Matrix4f TEMP_VIEW_MATRIX = new Matrix4f();
    private static final Quaternionf TEMP_AXIS_ROTATION = new Quaternionf();
    private final Quaternionf rotation;

    private final Vector3f position;

    private final Matrix4f projectionMatrix;

    private final float far;
    private final float near;

    public Camera(int w, int h, float near, float far) {
        position = new Vector3f();
        rotation = new Quaternionf().identity();
        this.far = far;
        this.near = near;
        projectionMatrix = new Matrix4f();
        calculateProjection(w, h);
    }

    private Matrix4f getViewMatrix(Matrix4f result) {
        result.rotation(rotation).translate(-position.x, -position.y, -position.z);
        return result;
    }

    public Matrix4f getViewProjectionMatrix(Matrix4f result) {
        result.set(projectionMatrix).mul(getViewMatrix(TEMP_VIEW_MATRIX));
        update(result);
        return result;
    }

    public void rotate(float x, float y, float z, float w) {
        Quaternionf quat = TEMP_AXIS_ROTATION.fromAxisAngleRad(x, y, z, w).mul(rotation);
        rotation.set(quat).normalize();
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void lookAt(Vector3f pos, Vector3f lookAt, Vector3f up) {
        position.set(pos);
        rotation.setFromNormalized(TEMP_LOOK_MATRIX.lookAt(pos, lookAt, up));
    }

    public void calculateProjection(int w, int h) {
        projectionMatrix.identity().perspective((float) Math.toRadians(45f), (float) w / (float) h, near, far);
    }

    public void move(Vector3f change) {
        position.add(change);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void move(float front, float down, float right) {
        TEMP_MOVE_MATRIX.translationRotate(right, down, front, rotation).translate(position.mul(-1)).invert();
        TEMP_MOVE_MATRIX.getTranslation(position);
    }

    public Vector3f getPosition() {
        return position;
    }

}
