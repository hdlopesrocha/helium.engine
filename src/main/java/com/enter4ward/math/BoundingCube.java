package com.enter4ward.math;

import org.joml.Vector3f;

public class BoundingCube extends IBoundingBox {

    private float len;

    public BoundingCube(Vector3f min, float len) {
        this.min = min;
        this.len = len;
    }

    public BoundingCube() {

    }

    @Override
    public String toString() {
        return "{Min:" + "{" + getMin().x + "," + getMin().y + ","
                + getMin().z + "}, Len:" + "{" + len + "}";
    }

    public boolean equals(Object other) {
        if (other instanceof BoundingCube) {
            return (min.equals(((BoundingCube) other).min)) && (len == (((BoundingCube) other).len));
        }
        return false;
    }

    @Override
    public float getLengthX() {
        return len;
    }

    @Override
    public Vector3f getLen(final Vector3f vec) {
        vec.set(len, len, len);
        return vec;
    }

    @Override
    public float getLengthY() {
        return len;
    }

    @Override
    public float getLengthZ() {
        return len;
    }

    @Override
    public float getCenterX() {
        return min.x + len * 0.5f;
    }

    @Override
    public float getCenterY() {
        return min.y + len * 0.5f;
    }

    @Override
    public float getCenterZ() {
        return min.z + len * 0.5f;
    }

    @Override
    public float getMaxX() {
        return min.x + len;
    }

    @Override
    public float getMaxY() {
        return min.y + len;
    }

    @Override
    public float getMaxZ() {
        return min.z + len;
    }

    public float getLen() {
        return len;
    }

    public void setLen(final float len) {
        this.len = len;
    }
}
