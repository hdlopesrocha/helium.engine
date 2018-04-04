package com.enter4ward.math;

import org.joml.Vector3f;

public class BoundingSphere extends Vector3f {

    public float r;

    public BoundingSphere(Vector3f position, float radius) {
        set(position);
        this.r = radius;
    }

    public BoundingSphere(BoundingSphere sph) {
        set(sph);
        this.r = sph.r;
    }

    public BoundingSphere() {
        this.r = 0f;
    }

    public BoundingSphere set(final BoundingSphere sph) {
        this.r = sph.r;
        this.x = sph.x;
        this.y = sph.y;
        this.z = sph.z;
        return this;
    }

    public boolean contains(Vector3f vec) {
        return distanceSquared(vec) <= r * r;
    }

    public Boolean intersects(BoundingSphere sphere) {
        return distance(sphere) < r + sphere.r;
    }

    public BoundingSphere createFromPoints(Iterable<Vector3f> points) {
        float maxX = 0, maxY = 0, maxZ = 0, minX = 0, minY = 0, minZ = 0;

        boolean inited = false;

        for (Vector3f vec : points) {
            if (!inited) {
                minX = maxX = vec.x;
                minY = maxY = vec.y;
                minZ = maxZ = vec.z;

                inited = true;
            }
            minX = Math.min(minX, vec.x);
            minY = Math.min(minY, vec.y);
            minZ = Math.min(minZ, vec.z);
            maxX = Math.max(maxX, vec.x);
            maxY = Math.max(maxY, vec.y);
            maxZ = Math.max(maxZ, vec.z);
        }
        x = ((minX + maxX) / 2f);
        y = ((minY + maxY) / 2f);
        z = ((minZ + maxZ) / 2f);
        r = (0f);

        for (Vector3f vec : points) {
            float dist = vec.distance(this);
            r = (Math.max(r, dist));
        }

        return this;
    }
}
