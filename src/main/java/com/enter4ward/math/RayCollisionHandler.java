package com.enter4ward.math;

// TODO: Auto-generated Javadoc

/**
 * The Interface RayCollisionHandler.
 */
public interface RayCollisionHandler {

    /**
     * On object collision.
     *
     * @param space the space
     * @param ray   the ray
     * @param obj2  the obj2
     */
    public IntersectionInfo onObjectCollision(final Space space, final Ray ray,
                                              final Object obj2);

    // public IntersectionInfo closestTriangle(final IBoundingSphere obj, final
    // Ray ray);

}
