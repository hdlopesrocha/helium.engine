package com.enter4ward.math;

// TODO: Auto-generated Javadoc

import org.joml.Vector3f;

/**
 * The Class Triangle.
 */
public class Pair2 {
    private Vector3f u;
    private Vector3f v;

    // ===================================
    // [2] Projecting 3D Points on a Plane
    // ===================================

    public Pair2(Vector3f n) {
        if (n.x > n.y) {
            u = new Vector3f(n.z, 0, -n.x).div((float) Math.sqrt(n.x * n.x + n.z * n.z));
        } else {
            u = new Vector3f(0, n.z, -n.y).div((float) Math.sqrt(n.y * n.y + n.z * n.z));
        }
        v = new Vector3f(n).cross(u);


    }


}
