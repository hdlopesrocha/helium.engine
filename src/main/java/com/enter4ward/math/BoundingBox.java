package com.enter4ward.math;

import org.joml.Vector3f;

public class BoundingBox extends IBoundingBox {

    private Vector3f len;
    private Vector3f min;

    public BoundingBox(Vector3f min, Vector3f len) {
        this.min = min;
        this.len = len;
    }

    public BoundingBox() {
    }

    @Override
    public String toString() {
        return "{Min:" + "{" + getMinX() + "," + getMinY() + ","
                + getMinZ() + "}, Len:" + "{" + len.toString() + "}";
    }

    public BoundingBox createFromPoints(Vector3f[] points) {
        len.set(Float.MIN_VALUE);
        min.set(Float.MAX_VALUE);

        for (Vector3f Vector3f : points) {
            len.max(Vector3f);
            min.min(Vector3f);
        }

        len.sub(min);

        return this;
    }

    public BoundingBox createFromSphere(BoundingSphere sphere) {
        float r = sphere.r;
        min.set(sphere).sub(r, r, r);
        len.set(r * 2);
        return this;
    }

    public BoundingBox merge(BoundingBox box) {
        float minX = Math.min(min.x, box.min.x);
        float minY = Math.min(min.y, box.min.y);
        float minZ = Math.min(min.z, box.min.z);

        float maxX = Math.max(min.x + len.x,
                box.min.x + box.len.x);
        float maxY = Math.max(min.y + len.y,
                box.min.y + box.len.y);
        float maxZ = Math.max(min.z + len.z,
                box.min.z + box.len.z);

        min.set(minX, minY, minZ);
        len.set(maxX - minZ, maxY - minY, maxZ - minZ);

        return this;
    }

    public boolean equals(Object other) {
        if (other instanceof BoundingBox) {
            return (min.equals(((BoundingBox) other).min)) && (len.equals(((BoundingBox) other).len));
        }
        return false;
    }

    @Override
    public float getLengthX() {
        return len.x;
    }

    @Override
    public Vector3f getLen(final Vector3f vec) {
        vec.set(len);
        return vec;
    }

    @Override
    public float getLengthY() {
        return len.y;
    }

    @Override
    public float getLengthZ() {
        return len.z;
    }

    @Override
    public float getCenterX() {
        return min.x + len.x * 0.5f;
    }

    @Override
    public float getCenterY() {
        return min.y + len.y * 0.5f;
    }

    @Override
    public float getCenterZ() {
        return min.z + len.z * 0.5f;
    }

    @Override
    public float getMaxX() {
        return min.x + len.x;
    }

    @Override
    public float getMaxY() {
        return min.y + len.y;
    }

    @Override
    public float getMaxZ() {
        return min.z + len.z;
    }

    @Override
    public float getMinX() {
        return min.x;
    }

    @Override
    public float getMinY() {
        return min.y;
    }

    @Override
    public float getMinZ() {
        return min.z;
    }


}
