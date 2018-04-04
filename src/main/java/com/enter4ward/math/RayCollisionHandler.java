package com.enter4ward.math;

public interface RayCollisionHandler {

    IntersectionInfo onObjectCollision(final Space space, final Ray ray,
                                              final Object obj2);

}
