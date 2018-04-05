package com.enter4ward.math;

public class Octree {

    public interface MatchHandler {
        boolean isMatch(float x, float y, float z, float l);
    }

    private static final int NUMBER_NODES = 8;
    private static final Integer[] OPTIMAL_ORDER = new Integer[]{
            0, 1, 2, 3, 4, 5, 6, 7
    };

    private static final BoundingCube TEMP_BOUNDING_BOX = new BoundingCube();
    private final float minSize;
    private Node root;
    private final BoundingCube rootBox = new BoundingCube();

    private final BoundingSphere sphere = new BoundingSphere();

    public Octree(float minSize) {
        this.minSize = minSize;
        root = new Node();
    }

    public void build(final BoundingSphere sph, final MatchHandler handler) {
        rootBox.setMin(sph.x - sph.r, sph.y - sph.r, sph.z - sph.r);
        rootBox.setLen(sph.r * 2);
        root.buildVoxel(rootBox.getMinX(), rootBox.getMinY(), rootBox.getMinZ(), rootBox.getLen(), handler);
        return;
    }

    public IBoundingBox getBoundingBox() {
        return rootBox;
    }

    public BoundingSphere getBoundingSphere() {
        sphere.set(rootBox.getCenterX(), rootBox.getCenterY(), rootBox.getCenterZ());
        sphere.r = (float) (rootBox.getLen() * Math.sqrt(2));
        return sphere;
    }

    public void handleVisibleObjects(BoundingFrustum frustum,
                                     VisibleVoxelHandler handler) {
        if (root != null) {
            root.handleVisibleObjects(frustum, rootBox.getMinX(), rootBox.getMinY(), rootBox.getMinZ(), rootBox.getLen(), handler);
        }
    }

    public boolean canSplit(float len) {
        return len > minSize;
    }

    public class Node {

        private Node[] children = new Node[8];
        private Node() {

        }

        private void setChild(int i, Node node) {
            this.children[i] = node;
        }

        private Node getChild(int i) {
            return this.children == null ? null : this.children[i];
        }

        private void handleVisibleObjects(final BoundingFrustum frustum, float x, float y, float z, float l,
                                          final VisibleVoxelHandler handler) {

            handler.onObjectVisible(x,y,z,l,this);
            float newL = l / 2;
            for (int mx = 0; mx < 2; ++mx) {
                for (int my = 0; my < 2; ++my) {
                    for (int mz = 0; mz < 2; ++mz) {
                        float newX = x + mx * newL;
                        float newY = y + my * newL;
                        float newZ = z + mz * newL;
                        int index = mx * 4 + my * 2 + mz;
                        Node node =  getChild(index);
                        if (node != null && frustum.contains(newX, newY, newZ, newL) != ContainmentType.Disjoint) {
                            node.handleVisibleObjects(frustum, newX, newY, newZ, newL, handler);
                        }
                    }
                }
            }
        }

        private void buildVoxel(float x, float y, float z, float l, final MatchHandler handler) {
            if (canSplit(l)) {
                float newL = l / 2;
                for (int mx = 0; mx < 2; ++mx) {
                    for (int my = 0; my < 2; ++my) {
                        for (int mz = 0; mz < 2; ++mz) {
                            float newX = x + mx * newL;
                            float newY = y + my * newL;
                            float newZ = z + mz * newL;
                            boolean cont = handler.isMatch(newX, newY, newZ, newL);
                            if(cont) {
                                Node newNode = new Node();
                                int index = mx * 4 + my * 2 + mz;
                                setChild(index, newNode);
                                newNode.buildVoxel(newX, newY, newZ, newL, handler);
                            }
                        }
                    }
                }
            }
        }
    }
}
