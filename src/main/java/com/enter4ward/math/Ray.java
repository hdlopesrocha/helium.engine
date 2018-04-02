package com.enter4ward.math;

// TODO: Auto-generated Javadoc
//MIT License - Copyright (C) The Mono.Xna Team
//This file is subject to the terms and conditions defined in
//file 'LICENSE.txt', which is part of this source code package.

import org.joml.Planef;
import org.joml.Vector3f;

/**
 * The Class Ray.
 */
public class Ray {

    private static final Vector3f TEMP_IP = new Vector3f();
    private static final Vector3f TEMP_IU = new Vector3f();
    private static final Vector3f TEMP_IV = new Vector3f();
    private static final Vector3f TEMP_IW = new Vector3f();
    private static final Vector3f TEMP_DIFFERENCE = new Vector3f();
    /**
     * The Direction.
     */
    private Vector3f direction;
    /**
     * The Position.
     */
    private Vector3f position;

    /**
     * Instantiates a new ray.
     *
     * @param Vector3f  the position
     * @param Vector3f2 the direction
     */
    public Ray(Vector3f Vector3f, Vector3f Vector3f2) {
        this.position = Vector3f;
        this.direction = Vector3f2;
    }

    // adapted from
    // http://www.scratchapixel.com/lessons/3d-basic-lessons/lesson-7-intersecting-simple-shapes/ray-box-intersection/

    /**
     * Gets the direction.
     *
     * @return the direction
     */
    public Vector3f getDirection() {
        return direction;
    }

    /*
     * public float? Intersects(BoundingFrustum frustum) { if (frustum == null)
     * { throw new ArgumentNullException("frustum"); }
     *
     * return frustum.Intersects(this); }
     */

    /**
     * Sets the direction.
     *
     * @param direction the new direction
     */
    public void setDirection(Vector3f direction) {
        this.direction = direction;
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
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     * Equals.
     *
     * @param other the other
     * @return true, if successful
     */
    public boolean equals(Object obj) {
        if (obj instanceof Ray) {
            Ray other = (Ray) obj;
            return this.position.equals(other.position)
                    && this.direction.equals(other.direction);
        }
        return false;
    }

    /**
     * Intersects.
     *
     * @param box the box
     * @return the float
     */
    public Float intersects(BoundingBox box) {
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

    /**
     * Intersects.
     *
     * @param plane the plane
     * @return the float
     */
    public Float intersects(Planef plane) {

        return null;
    }

    /**
     * Intersects.
     *
     * @param triangle the triangle
     * @return the i Vector3f
     */
    public synchronized Float intersects(final Triangle triangle) {
        return null;
    }

    /**
     * Intersects.
     *
     * @param sphere the sphere
     * @return the float
     */
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

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "{{Position:" + position.toString() + " Direction:"
                + direction.toString() + "}}";
    }

}
