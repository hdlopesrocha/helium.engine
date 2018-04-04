package com.enter4ward.math;

import org.joml.Planef;
import org.joml.Vector3f;

public class Ray {

    private static final Vector3f TEMP_IP = new Vector3f();
    private static final Vector3f TEMP_IU = new Vector3f();
    private static final Vector3f TEMP_IV = new Vector3f();
    private static final Vector3f TEMP_IW = new Vector3f();
    private static final Vector3f TEMP_DIFFERENCE = new Vector3f();

    private Vector3f direction;

    private Vector3f position;

    public Ray(Vector3f Vector3f, Vector3f Vector3f2) {
        this.position = Vector3f;
        this.direction = Vector3f2;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Ray) {
            Ray other = (Ray) obj;
            return this.position.equals(other.position)
                    && this.direction.equals(other.direction);
        }
        return false;
    }

    public Float intersects(IBoundingBox box) {
        float Epsilon = 1e-6f;

        Float tMin = null, tMax = null;

        if (Math.abs(direction.x) < Epsilon) {
            if (position.x < box.getMinX()
                    || position.x > box.getMaxX())
                return null;
        } else {
            tMin = (box.getMinX() - position.x) / direction.x;
            tMax = (box.getMaxX() - position.x) / direction.x;

            if (tMin > tMax) {
                float temp = tMin;
                tMin = tMax;
                tMax = temp;
            }
        }

        if (Math.abs(direction.y) < Epsilon) {
            if (position.y < box.getMinY()
                    || position.y > box.getMaxY())
                return null;
        } else {
            float tMinY = (box.getMinY() - position.y) / direction.y;
            float tMaxY = (box.getMaxY() - position.y) / direction.y;

            if (tMinY > tMaxY) {
                float temp = tMinY;
                tMinY = tMaxY;
                tMaxY = temp;
            }

            if ((tMin != null && tMin > tMaxY)
                    || (tMax != null && tMinY > tMax))
                return null;

            if (tMin == null || tMinY > tMin)
                tMin = tMinY;
            if (tMax == null || tMaxY < tMax)
                tMax = tMaxY;
        }

        if (Math.abs(direction.z) < Epsilon) {
            if (position.z < box.getMinZ()
                    || position.z > box.getMaxZ())
                return null;
        } else {
            float tMinZ = (box.getMinZ() - position.z) / direction.z;
            float tMaxZ = (box.getMaxZ() - position.z) / direction.z;

            if (tMinZ > tMaxZ) {
                float temp = tMinZ;
                tMinZ = tMaxZ;
                tMaxZ = temp;
            }

            if ((tMin != null && tMin > tMaxZ)
                    || (tMax != null && tMinZ > tMax))
                return null;

            if (tMin == null || tMinZ > tMin)
                tMin = tMinZ;
            if (tMax == null || tMaxZ < tMax)
                tMax = tMaxZ;
        }

        // having a positive tMin and a negative tMax means the ray is inside
        // the box
        // we expect the intesection distance to be 0 in that case
        if (tMin != null && tMin < 0) {
            if (tMax > 0) {
                return (tMax > 0) ? 0f : null;
            }
        }

        return tMin;
    }

    public Float intersects(Planef plane) {

        return null;
    }

    public synchronized Float intersects(final Triangle triangle) {
        return null;
    }

    public Float intersects(BoundingSphere sphere) {
        // Find the vector between where the ray starts the the sphere's centre
        Vector3f difference = TEMP_DIFFERENCE.set(sphere)
                .sub(this.position);

        float differenceLengthSquared = difference.lengthSquared();
        float sphereRadiusSquared = sphere.r * sphere.r;

        float distanceAlongRay;

        // If the distance between the ray start and the sphere's centre is less
        // than
        // the radius of the sphere, it means we've intersected. N.B. checking
        // the LengthSquared is faster.
        if (differenceLengthSquared < sphereRadiusSquared) {
            return 0.0f;
        }

        distanceAlongRay = direction.dot(difference);
        // If the ray is pointing away from the sphere then we don't ever
        // intersect
        if (distanceAlongRay < 0) {
            return null;
        }

        // Next we kinda use Pythagoras to check if we are within the bounds of
        // the sphere
        // if x = radius of sphere
        // if y = distance between ray position and sphere centre
        // if z = the distance we've travelled along the ray
        // if x^2 + z^2 - y^2 < 0, we do not intersect
        float dist = sphereRadiusSquared + distanceAlongRay * distanceAlongRay
                - differenceLengthSquared;

        return (dist < 0) ? null : distanceAlongRay - (float) Math.sqrt(dist);
    }

    public String toString() {
        return "{{Position:" + position.toString() + " Direction:"
                + direction.toString() + "}}";
    }

}
