package com.enter4ward.math;

import org.joml.Vector3f;

public class BoundingCube extends IBoundingBox {

    private float len;
    private float minX, minY, minZ;

    public BoundingCube() {

    }

    @Override
    public String toString() {
        return "{Min:" + "{" + getMinX() + "," + getMinY() + ","
                + getMinZ() + "}, Len:" + "{" + len + "}";
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
        return minX + len * 0.5f;
    }

    @Override
    public float getCenterY() {
        return minY + len * 0.5f;
    }

    @Override
    public float getCenterZ() {
        return minZ + len * 0.5f;
    }

    @Override
    public float getMaxX() {
        return minX + len;
    }

    @Override
    public float getMaxY() {
        return minY + len;
    }

    @Override
    public float getMaxZ() {
        return minZ + len;
    }

    @Override
    public float getMinX() {
        return minX;
    }

    @Override
    public float getMinY() {
        return minY;
    }

    @Override
    public float getMinZ() {
        return minZ;
    }

    public float getLen() {
        return len;
    }

    public void setMin(final Vector3f vec) {
        this.minX = vec.x;
        this.minY = vec.y;
        this.minZ = vec.z;
    }

    public void setMin(float x, float y, float z) {
        this.minX = x;
        this.minY = y;
        this.minZ = z;
    }

    public void setLen(final float len) {
        this.len = len;
    }
}
