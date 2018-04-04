package com.enter4ward.math;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Util {
    public static Vector3f transform(Vector3f vec, Quaternionf rotation) {
        float tx = 2 * (rotation.y * vec.z - rotation.z * vec.y);
        float ty = 2 * (rotation.z * vec.x - rotation.x * vec.z);
        float tz = 2 * (rotation.x * vec.y - rotation.y * vec.x);
        vec.x += tx * rotation.w + (rotation.y * tz - rotation.z * ty);
        vec.y += ty * rotation.w + (rotation.z * tx - rotation.x * tz);
        vec.z += tz * rotation.w + (rotation.x * ty - rotation.y * tx);
        return vec;
    }

}
