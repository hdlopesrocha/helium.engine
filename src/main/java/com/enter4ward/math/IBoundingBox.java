package com.enter4ward.math;

import org.joml.Intersectionf;
import org.joml.Planef;
import org.joml.Vector3f;

public abstract class IBoundingBox {



    public ContainmentType contains(final Vector3f point) {

        if (point.x < getMinX() || point.x > getMaxX()
                || point.y < getMinY() || point.y > getMaxY()
                || point.z < getMinZ() || point.z > getMaxZ()) {
            return ContainmentType.Disjoint;
        }// or if point is on box because coordonate of point is lesser or equal
        else if (point.x == getMinX() || point.x == getMaxX()
                || point.y == getMinY() || point.y == getMaxY()
                || point.z == getMinZ() || point.z == getMaxZ())
            return ContainmentType.Intersects;
        else
            return ContainmentType.Contains;
    }

    public abstract float getLengthX();

    public abstract Vector3f getLen(Vector3f vec);

    public abstract float getLengthY();

    public abstract float getLengthZ();

    public abstract float getCenterX();

    public abstract float getCenterY();

    public abstract float getCenterZ();

    public ContainmentType contains(BoundingBox box) {
        // test if all corner is in the same side of a face by just checking min
        // and max
        if (box.getMaxX() < getMinX() || box.getMinX() > getMaxX()
                || box.getMaxY() < getMinY()
                || box.getMinY() > getMaxY()
                || box.getMaxZ() < getMinZ()
                || box.getMinZ() > getMaxZ())
            return ContainmentType.Disjoint;

        if (box.getMinX() >= getMinX()
                && box.getMaxX() <= getMaxX()
                && box.getMinY() >= getMinY()
                && box.getMaxY() <= getMaxY()
                && box.getMinZ() >= getMinZ()
                && box.getMaxZ() <= getMaxZ())
            return ContainmentType.Contains;

        return ContainmentType.Intersects;
    }

    public ContainmentType contains(final BoundingSphere sphere) {
        final float sx = sphere.x;
        final float sy = sphere.y;
        final float sz = sphere.z;
        final float sr = sphere.r;
        if (sx - getMinX() >= sr && sy - getMinY() >= sr
                && sz - getMinZ() >= sr && getMaxX() - sx >= sr
                && getMaxY() - sy >= sr && getMaxZ() - sz >= sr)
            return ContainmentType.Contains;
        double dmin = 0;
        double e = sx - getMinX();
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
        e = sy - getMinY();
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
        e = sz - getMinZ();
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

    public boolean containsSphere(final BoundingSphere sphere) {
        final float sx = sphere.x;
        final float sy = sphere.y;
        final float sz = sphere.z;
        final float sr = sphere.r;

        return (getMinX() <= sx - sr &&
                getMinY() <= sy - sr &&
                getMinZ() <= sz - sr &&
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

    public boolean intersects(BoundingBox box) {
        if ((getMaxX() >= box.getMinX())
                && (getMinX() <= box.getMaxX())) {
            return !((getMaxY() < box.getMinY()) || (getMinY() > box
                    .getMaxY()))
                    && (getMaxZ() >= box.getMinZ())
                    && (getMinZ() <= box.getMaxZ());
        }
        return false;
    }
    
    public abstract float getMaxX();

    public abstract float getMaxY();

    public abstract float getMaxZ();

    public abstract float getMinX();

    public abstract float getMinY();

    public abstract float getMinZ();
}
