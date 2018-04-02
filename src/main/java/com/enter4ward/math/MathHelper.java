package com.enter4ward.math;

/**
 * Created by Henrique on 20/02/2015.
 */
public class MathHelper {

    public static float clamp(float val, float min, float max) {
        return val < min ? min : val > max ? max : val;
    }


    public static int clamp(int val, int min, int max) {
        return val < min ? min : val > max ? max : val;
    }

    public static float lerp(float value1, float value2, float amount) {
        return value1 + (value2 - value1) * amount;
    }
}
