package com.enter4ward.math;

// TODO: Auto-generated Javadoc

import org.joml.Intersectionf;
import org.joml.Planef;
import org.joml.Vector3f;

/**
 * The Class Box.
 */
public class BoundingBox {

    /**
     * The max.
     */
    private Vector3f min;
    private Vector3f len;

    /**
     * Instantiates a new box.
     *
     * @param min the min
     * @param max the max
     */
    public BoundingBox(Vector3f min, Vector3f len) {
        this.min = min;
        this.len = len;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.enter4ward.math.IBox#getLengthX()
     */

    /**
     * Instantiates a new bounding box.
     */
    public BoundingBox() {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.enter4ward.math.IBox#getLengthY()
     */

    /**
     * Contains.
     *
     * @param point the point
     * @return the containment type
     */
    public ContainmentType contains(final Vector3f point) {
        // first we get if point is out of box
        if (point.x < getMin().x || point.x > getMaxX()
                || point.y < getMin().y || point.y > getMaxY()
                || point.z < getMin().z || point.z > getMaxZ()) {
            return ContainmentType.Disjoint;
        }// or if point is on box because coordonate of point is lesser or equal
        else if (point.x == getMin().x || point.x == getMaxX()
                || point.y == getMin().y || point.y == getMaxY()
                || point.z == getMin().z || point.z == getMaxZ())
            return ContainmentType.Intersects;
        else
            return ContainmentType.Contains;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.enter4ward.math.IBox#getLengthZ()
     */

    /**
     * Gets the length x.
     *
     * @return the length x
     */
    public float getLengthX() {
        return len.x;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.enter4ward.math.IBox#getCenterX()
     */

    /**
     * Gets the length y.
     *
     * @return the length y
     */
    public float getLengthY() {
        return len.y;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.enter4ward.math.IBox#getCenterY()
     */

    /**
     * Gets the length z.
     *
     * @return the length z
     */
    public float getLengthZ() {
        return len.z;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.enter4ward.math.IBox#getCenterZ()
     */

    /**
     * Gets the center x.
     *
     * @return the center x
     */
    public float getCenterX() {
        return getMin().x + len.x * 0.5f;
    }

    /**
     * Gets the center y.
     *
     * @return the center y
     */
    public float getCenterY() {
        return getMin().y + len.y * 0.5f;
    }

    /**
     * Gets the center z.
     *
     * @return the center z
     */
    public float getCenterZ() {
        return getMin().z + len.z * 0.5f;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.enter4ward.math.IBox#toString()
     */
    public String toString() {
        return "{Min:" + "{" + getMin().x + "," + getMin().y + ","
                + getMin().z + "}, Len:" + "{" + len.toString() + "}";
    }

    /**
     * Contains.
     *
     * @param box the box
     * @return the containment type
     */
    public ContainmentType contains(BoundingBox box) {
        // test if all corner is in the same side of a face by just checking min
        // and max
        if (box.getMaxX() < getMin().x || box.getMin().x > getMaxX()
                || box.getMaxY() < getMin().y
                || box.getMin().y > getMaxY()
                || box.getMaxZ() < getMin().z
                || box.getMin().z > getMaxZ())
            return ContainmentType.Disjoint;

        if (box.getMin().x >= getMin().x
                && box.getMaxX() <= getMaxX()
                && box.getMin().y >= getMin().y
                && box.getMaxY() <= getMaxY()
                && box.getMin().z >= getMin().z
                && box.getMaxZ() <= getMaxZ())
            return ContainmentType.Contains;

        return ContainmentType.Intersects;
    }

    /**
     * Contains.
     *
     * @param sphere the sphere
     * @return the containment type
     * @author MonoGame
     */
    public ContainmentType contains(final BoundingSphere sphere) {
        final float sx = sphere.x;
        final float sy = sphere.y;
        final float sz = sphere.z;
        final float sr = sphere.r;

        if (sx - getMin().x >= sr && sy - getMin().y >= sr
                && sz - getMin().z >= sr && getMaxX() - sx >= sr
                && getMaxY() - sy >= sr && getMaxZ() - sz >= sr)
            return ContainmentType.Contains;
        double dmin = 0;
        double e = sx - getMin().x;
        if (e < 0) {
            if (e < -sr) {
                return ContainmentType.Disjoint;
            }
            dmin += e * e;
        } else {
            e = sx - getMaxX();
            if (e > 0) {
                if (e > sr) {
                    return ContainmentType.Disjoint;
                }
                dmin += e * e;
            }
        }
        e = sy - getMin().y;
        if (e < 0) {
            if (e < -sr) {
                return ContainmentType.Disjoint;
            }
            dmin += e * e;
        } else {
            e = sy - getMaxY();
            if (e > 0) {
                if (e > sr) {
                    return ContainmentType.Disjoint;
                }
                dmin += e * e;
            }
        }
        e = sz - getMin().z;
        if (e < 0) {
            if (e < -sr) {
                return ContainmentType.Disjoint;
            }
            dmin += e * e;
        } else {
            e = sz - getMaxZ();
            if (e > 0) {
                if (e > sr) {
                    return ContainmentType.Disjoint;
                }
                dmin += e * e;
            }
        }
        if (dmin <= sr * sr)
            return ContainmentType.Intersects;
        return ContainmentType.Disjoint;
    }

    /**
     * Only contains.
     *
     * @param sphere the sphere
     * @return true, if successful
     */
    public boolean containsSphere(final BoundingSphere sphere) {
        final float sx = sphere.x;
        final float sy = sphere.y;
        final float sz = sphere.z;
        final float sr = sphere.r;

        return (getMin().x <= sx - sr &&
                getMin().y <= sy - sr &&
                getMin().z <= sz - sr &&
                getMaxX() >= sx + sr &&
                getMaxY() >= sy + sr &&
                getMaxZ() >= sz + sr);
    }

    public PlaneIntersectionType intersects(Planef plane) {
        boolean inter = Intersectionf.testAabPlane(
                getMinX(), getMinY(), getMinZ(),
                getMaxX(), getMaxY(), getMaxZ(),
                plane.a, plane.b, plane.c, plane.d);
        if (inter) {
            return PlaneIntersectionType.Intersecting;
        }
        if (Intersectionf.distancePointPlane(getMinX(), getMinY(), getMinZ(), plane.a, plane.b, plane.c, plane.d) > 0) {
            return PlaneIntersectionType.Back;
        } else {
            return PlaneIntersectionType.Front;
        }
    }

    /**
     * Intersects.
     *
     * @param box the box
     * @return true, if successful
     */
    public boolean intersects(BoundingBox box) {
        if ((getMaxX() >= box.getMin().x)
                && (getMin().x <= box.getMaxX())) {
            return !((getMaxY() < box.getMin().y) || (getMin().y > box
                    .getMaxY()))
                    && (getMaxZ() >= box.getMin().z)
                    && (getMin().z <= box.getMaxZ());
        }
        return false;
    }

    public float getMinX() {
        return getMin().x;
    }

    public void setMinX(float min) {
        this.getMin().x = min;
    }

    public float getMinY() {
        return getMin().y;
    }

    public void setMinY(float min) {
        this.getMin().y = min;
    }

    public float getMinZ() {
        return getMin().z;
    }

    public void setMinZ(float min) {
        this.getMin().z = min;
    }

    public float getMaxX() {
        return getMin().x + len.x;
    }

    public float getMaxY() {
        return getMin().y + len.y;
    }

    public float getMaxZ() {
        return getMin().z + len.z;
    }

    /**
     * Creates the from points.
     *
     * @param points the points
     * @return the bounding box
     */
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

    /**
     * Creates the from sphere.
     *
     * @param sphere the sphere
     * @return the bounding box
     */
    public BoundingBox createFromSphere(BoundingSphere sphere) {
        float r = sphere.r;
        min.set(sphere).sub(r, r, r);
        len.set(r * 2);
        return this;
    }

    /**
     * Creates the merged.
     *
     * @param original   the original
     * @param additional the additional
     * @return the bounding box
     */
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

    /**
     * Equals.
     *
     * @param other the other
     * @return true, if successful
     */
    public boolean equals(Object other) {
        if (other instanceof BoundingBox) {
            return (min.equals(((BoundingBox) other).min)) && (len.equals(((BoundingBox) other).len));
        }
        return false;
    }

    public Vector3f getMin() {
        return min;
    }

    public void setMin(Vector3f min) {
        this.min = min;
    }

}
