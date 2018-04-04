package com.enter4ward.math;

import org.joml.Intersectionf;
import org.joml.Planef;
import org.joml.Vector3f;

public abstract class IBoundingBox {
    /**
     * The max.
     */
    protected Vector3f min;

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

    /**
     * Gets the length x.
     *
     * @return the length x
     */
    public abstract float getLengthX();

    public abstract Vector3f getLen(Vector3f vec);

    /**
     * Gets the length y.
     *
     * @return the length y
     */
    public abstract float getLengthY();

    /**
     * Gets the length z.
     *
     * @return the length z
     */
    public abstract float getLengthZ();

    /**
     * Gets the center x.
     *
     * @return the center x
     */
    public abstract float getCenterX();

    /**
     * Gets the center y.
     *
     * @return the center y
     */
    public abstract float getCenterY();

    /**
     * Gets the center z.
     *
     * @return the center z
     */
    public abstract float getCenterZ();

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
        if (Intersectionf.distancePointPlane(getMinX(), getMinY(), getMinZ(), plane.a, plane.b, plane.c, plane.d) < 0) {
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

    public abstract float getMaxX();

    public abstract float getMaxY();

    public abstract float getMaxZ();

    public Vector3f getMin() {
        return min;
    }

    public void setMin(Vector3f min) {
        this.min = min;
    }
}
