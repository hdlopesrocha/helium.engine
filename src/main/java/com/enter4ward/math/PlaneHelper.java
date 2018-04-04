package com.enter4ward.math;

import org.joml.Planef;
import org.joml.Vector3f;

public class PlaneHelper {

    public static float classifyPoint(Vector3f point, Planef plane) {
        return point.x * plane.a + point.y
                * plane.b + point.z
                * plane.c + plane.d;
    }
}
