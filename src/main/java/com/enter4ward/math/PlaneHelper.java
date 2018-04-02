package com.enter4ward.math;

// TODO: Auto-generated Javadoc

import org.joml.Planef;
import org.joml.Vector3f;

/**
 * The Class PlaneHelper.
 */
public class PlaneHelper {

    /**
     * Classify point.
     *
     * @param point the point
     * @param plane the plane
     * @return the float
     */
    public static float classifyPoint(Vector3f point, Planef plane) {
        return point.x * plane.a + point.y
                * plane.b + point.z
                * plane.c + plane.d;
    }
}
