package com.enter4ward.math;

// TODO: Auto-generated Javadoc

import org.joml.Vector2f;
import org.joml.Vector3f;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * The Class Triangle.
 */
public class Triangle {

    /**
     * The c.
     */
    private Vector3f a, b, c;

    /**
     * Instantiates a new triangle.
     *
     * @param a the a
     * @param b the b
     * @param c the c
     */
    public Triangle(Vector3f a, Vector3f b, Vector3f c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Vector3f getA() {
        return a;
    }

    public Vector3f getB() {
        return b;
    }

    public Vector3f getC() {
        return c;
    }

    /**
     * Contains.
     *
     * @param point the point
     * @return true, if successful
     */
    public boolean contains(Vector3f point) {
        Vector3f aa = getA();
        Vector3f bb = getB();
        Vector3f cc = getC();

        float thisArea = getArea();
        float area1 = new Triangle(aa, bb, point).getArea();
        float area2 = new Triangle(aa, cc, point).getArea();
        float area3 = new Triangle(bb, cc, point).getArea();
        return area1 + area2 + area3 < thisArea + 0.001f;
    }

    /**
     * Gets the area.
     *
     * @return the area
     */
    public float getArea() {
        float abx = b.x - a.x;
        float aby = b.y - a.y;
        float acx = c.x - a.x;
        float acy = c.y - a.y;
        return 0.5f * (aby * acx - abx * acy);
    }

    // returns an interval of normalized time when an intersection existed
    public Vector2f intersection(BoundingSphere sphere, Vector3f d) {
        throw new NotImplementedException();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{a:" + a + ", b:" + b + ", " + c + "}";
    }

}
