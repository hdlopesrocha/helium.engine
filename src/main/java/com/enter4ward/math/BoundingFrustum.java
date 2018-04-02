package com.enter4ward.math;

import org.joml.Intersectionf;
import org.joml.Matrix4f;
import org.joml.Planef;
import org.joml.Vector3f;

public class BoundingFrustum {

    private Planef[] planes = new Planef[6];

    public BoundingFrustum() {
        for (int i = 0; i < 6; ++i) {
            planes[i] = new Planef();
        }
    }

    public Planef Bottom() {
        return planes[0];
    }

    public Planef Far() {
        return planes[1];
    }

    public Planef Left() {
        return planes[2];
    }

    public Planef Near() {
        return planes[4];
    }

    public Planef Right() {
        return planes[3];
    }

    public Planef Top() {
        return planes[5];
    }

    public ContainmentType contains(BoundingBox box) {
        Boolean intersects = false;
        for (int i = 0; i < 6; ++i) {
            PlaneIntersectionType planeIntersectionType = box.intersects(this.planes[i]);
            if (planeIntersectionType == PlaneIntersectionType.Front) {

                return ContainmentType.Disjoint;
            }
            if (planeIntersectionType == PlaneIntersectionType.Intersecting) {
                intersects = true;
            }

        }
        return intersects ? ContainmentType.Intersects
                : ContainmentType.Contains;
    }

    public ContainmentType contains(BoundingSphere sphere) {
        float val;
        ContainmentType result = ContainmentType.Contains;

        for (int i = 0; i < 6; ++i) {
            Planef plane = planes[i];
            val = Intersectionf.distancePointPlane(sphere.x, +sphere.y, sphere.z, plane.a, plane.b, plane.c, plane.d);
            if (val < -sphere.r)
                return ContainmentType.Disjoint;
            else if (val < sphere.r)
                result = ContainmentType.Intersects;
        }
        return result;

    }

    public ContainmentType contains(Vector3f point) {
        for (int i = 0; i < 6; ++i) {
            if (PlaneHelper.classifyPoint(point, planes[i]) > 0)
                return ContainmentType.Disjoint;
        }
        return ContainmentType.Contains;
    }

    public void update(Matrix4f viewProjectionMatrix) {
        // Pre-calculate the different planes needed

        for (int i = 0; i < 6; ++i) {
            planes[i] = viewProjectionMatrix.frustumPlane(i, new Planef());
        }
    }

}
