package com.enter4ward.math;


public class Octree {

    private static int[] MC_ORDER = {0, 1, 4, 5, 3, 2, 7, 6};


    public interface MatchHandler {
        boolean intersects(float x, float y, float z, float l);
        boolean contains(float x, float y, float z);
    }

    private final float minSize;
    private Node root;
    private final BoundingCube rootBox = new BoundingCube();

    private final BoundingSphere sphere = new BoundingSphere();

    public Octree(float minSize) {
        this.minSize = minSize;
        rootBox.setMin(-minSize*.5f,-minSize*.5f,-minSize*.5f);
        rootBox.setLen(minSize);
        root = new Node(0);
    }

    public void add(final BoundingCube cube, final MatchHandler handler) {
        root = expand(cube, handler);
        root.buildOctree(0, rootBox.getMinX(), rootBox.getMinY(), rootBox.getMinZ(), rootBox.getLen(), handler);
    }

    public void remove(final BoundingCube cube, final MatchHandler matchHandler) {

    }


    public void extractTriangles(final TriangleVoxelHandler triHandler) {
        root.extractTriangles(0, rootBox.getMinX(), rootBox.getMinY(), rootBox.getMinZ(), rootBox.getLen(), triHandler);
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

    private static int classifyNode(float x, float y, float z, float l, final MatchHandler handler) {
        int index = 0;
        for (int i = 0; i < 8; ++i) {
            float vX = x + ((i / 4) % 2) * l;
            float vY = y + ((i / 2) % 2) * l;
            float vZ = z + ((i / 1) % 2) * l;
            if (handler.contains(vX, vY, vZ)) {
                index += (1 << MC_ORDER[i]);
            }
        }
        return index;
    }

    private int clamp(int val, int min, int max) {
        return val < min ? min : val > max ? max : val;
    }

    private int getNodeIndex(float tx, float ty, float tz, float x, float y, float z, float l) {
        int px = Math.round((tx - x) / l);
        int py = Math.round((ty - y) / l);
        int pz = Math.round((tz - z) / l);
        px = clamp(px, 0, 1);
        py = clamp(py, 0, 1);
        pz = clamp(pz, 0, 1);
        return px * 4 + py * 2 + pz;
    }

    private Octree.Node expand(final BoundingCube obj, final MatchHandler handler) {
        Octree.Node node = root;
        float x = rootBox.getMinX();
        float y = rootBox.getMinY();
        float z = rootBox.getMinZ();
        float l = rootBox.getLen();

        while (true) {
            node.type = classifyNode(x, y, z, l, handler);
            ContainmentType cont = node.type == 0 ? ContainmentType.Contains :
                    handler.intersects(x, y, z, l) ? ContainmentType.Intersects: ContainmentType.Disjoint;

            if(cont != ContainmentType.Contains ){
                int i = 7 - getNodeIndex(obj.getCenterX(), obj.getCenterY(), obj.getCenterZ(), x, y, z, l);
                x = x - ((i / 4) % 2) * l;
                y = y - ((i / 2) % 2) * l;
                z = z - ((i / 1) % 2) * l;
                l = l*2;

                Node newNode = new Node(0);
                newNode.setChild(i, node);
                node = newNode;
            }
            else {
                break;
            }
        }
        rootBox.setMin(x,y,z);
        rootBox.setLen(l);
        return node;
    }

    public class Node {

        private int type = 0;
        private Node[] children = new Node[8];


        private Node(int type) {
            this.type = type;
        }

        private void setChild(int i, Node node) {
            this.children[i] = node;
        }

        private Node getChild(int i) {
            return this.children == null ? null : this.children[i];
        }

        private void handleVisibleObjects(final BoundingFrustum frustum, float x, float y, float z, float l,
                                          final VisibleVoxelHandler handler) {

            handler.onObjectVisible(x, y, z, l, this, type);
            float newL = l / 2;
            for (int mx = 0; mx < 2; ++mx) {
                for (int my = 0; my < 2; ++my) {
                    for (int mz = 0; mz < 2; ++mz) {
                        float newX = x + mx * newL;
                        float newY = y + my * newL;
                        float newZ = z + mz * newL;
                        int index = mx * 4 + my * 2 + mz;
                        Node node = getChild(index);
                        if (node != null && frustum.contains(newX, newY, newZ, newL) != ContainmentType.Disjoint) {
                            node.handleVisibleObjects(frustum, newX, newY, newZ, newL, handler);
                        }
                    }
                }
            }
        }

        private void buildOctree(int level, float x, float y, float z, float l, final MatchHandler handler) {
            if (canSplit(l)) {
                float newL = l / 2;
                for (int mx = 0; mx < 2; ++mx) {
                    for (int my = 0; my < 2; ++my) {
                        for (int mz = 0; mz < 2; ++mz) {
                            float newX = x + mx * newL;
                            float newY = y + my * newL;
                            float newZ = z + mz * newL;
                            int type = classifyNode(newX, newY, newZ, newL, handler);
                            ContainmentType cont = type==255 ? ContainmentType.Contains :
                                    handler.intersects(newX, newY, newZ, newL) ? ContainmentType.Intersects: ContainmentType.Disjoint;

                            if (cont != ContainmentType.Disjoint) {
                                Node newNode = new Node(type);
                                int index = mx * 4 + my * 2 + mz;
                                setChild(index, newNode);
                                if (cont == ContainmentType.Intersects) {
                                    newNode.buildOctree(level + 1, newX, newY, newZ, newL, handler);
                                }
                            }
                        }
                    }
                }
            }
        }

        private void buildTriangles(int level, float x, float y, float z, float l, TriangleVoxelHandler triHandler) {
            int[] tris = MarchingCubeHelper.triTable[type];
            float[][] edges = MarchingCubeHelper.edgeTable;
            for (int i = 0; i < tris.length; i += 3) {
                int a = tris[i];
                int b = tris[i + 2];
                int c = tris[i + 1];

                float ax = x + edges[a][0] * l;
                float ay = y + edges[a][1] * l;
                float az = z + edges[a][2] * l;

                float bx = x + edges[b][0] * l;
                float by = y + edges[b][1] * l;
                float bz = z + edges[b][2] * l;

                float cx = x + edges[c][0] * l;
                float cy = y + edges[c][1] * l;
                float cz = z + edges[c][2] * l;

                triHandler.onTriangle(level, ax, ay, az, bx, by, bz, cx, cy, cz);

            }
        }

        public void extractTriangles(int level, float x, float y, float z, float l, TriangleVoxelHandler triHandler) {
            buildTriangles(level, x, y, z, l, triHandler);
            float newL = l / 2;
            for (int mx = 0; mx < 2; ++mx) {
                for (int my = 0; my < 2; ++my) {
                    for (int mz = 0; mz < 2; ++mz) {
                        float newX = x + mx * newL;
                        float newY = y + my * newL;
                        float newZ = z + mz * newL;
                        int index = mx * 4 + my * 2 + mz;
                        Node child = getChild(index);
                        if (child != null) {
                            child.extractTriangles(level + 1, newX, newY, newZ, newL, triHandler);
                        }
                    }
                }
            }

        }


    }
}
