package com.enter4ward.math;


public class Octree {

    private static int[] MC_ORDER = {0, 1, 4, 5, 3, 2, 7, 6};


    public interface MatchHandler {
        ContainmentType objectContains(float x, float y, float z, float l);

        ContainmentType nodeContains(float x, float y, float z, float l);
    }

    private final float minSize;
    private Node root;
    private final BoundingCube rootBox = new BoundingCube();

    private final BoundingSphere sphere = new BoundingSphere();

    public Octree(float minSize) {
        this.minSize = minSize;
        rootBox.setMin(-minSize * .5f, -minSize * .5f, -minSize * .5f);
        rootBox.setLen(minSize);
        root = new Node();
    }

    public void add(final BoundingSphere cube, final MatchHandler handler) {
        root = expand(cube, handler);
        root.addVolume(0, rootBox.getMinX(), rootBox.getMinY(), rootBox.getMinZ(), rootBox.getLen(), handler);
    }

    public void remove(final BoundingSphere cube, final MatchHandler handler) {
        root = expand(cube, handler);
        root.removeVolume(0, rootBox.getMinX(), rootBox.getMinY(), rootBox.getMinZ(), rootBox.getLen(), handler);
    }

    private void buildTriangles(int level, int type, float x, float y, float z, float l, TriangleVoxelHandler triHandler) {
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

    public void extractTrianglesAux(int level, float x, float y, float z, float l, TriangleVoxelHandler triHandler) {

        int type = 0;
        for (int i = 0; i < 8; ++i) {
            float newX = x + (0.5f + ((i >> 2) % 2)) * l;
            float newY = y + (0.5f + ((i >> 1) % 2)) * l;
            float newZ = z + (0.5f + ((i >> 0) % 2)) * l;
            if (getValueAtLevel(newX, newY, newZ, level) != ContainmentType.Disjoint) {
                type += (1 << MC_ORDER[i]);
            }
        }
        buildTriangles(level, type, x, y, z, l, triHandler);
        if (canSplit(l) && type != 0) {
            float newL = l / 2;
            for (int i = 0; i < 8; ++i) {
                float newX = x + ((i >> 2) % 2) * newL;
                float newY = y + ((i >> 1) % 2) * newL;
                float newZ = z + ((i >> 0) % 2) * newL;
                extractTrianglesAux(level + 1, newX, newY, newZ, newL, triHandler);
            }
        }

    }


    public void extractTriangles(final TriangleVoxelHandler triHandler) {
        extractTrianglesAux(0, rootBox.getMinX(), rootBox.getMinY(), rootBox.getMinZ(), rootBox.getLen(), triHandler);
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
            root.handleVisibleObjects(frustum, rootBox.getMinX(), rootBox.getMinY(), rootBox.getMinZ(), rootBox.getLen(), 0, handler);
        }
    }

    public boolean canSplit(float len) {
        return len > minSize;
    }


    private int clamp(int val, int min, int max) {
        return val < min ? min : val > max ? max : val;
    }

    private int getNodeIndex(float tx, float ty, float tz, float x, float y, float z, float l) {
        int px = clamp(Math.round((tx - x) / l), 0, 1);
        int py = clamp(Math.round((ty - y) / l), 0, 1);
        int pz = clamp(Math.round((tz - z) / l), 0, 1);

        return px * 4 + py * 2 + pz;
    }

    private ContainmentType getValueAtLevel(float px, float py, float pz, int level) {
        return root.getValueAtLevelAux(px, py, pz, rootBox.getMinX(), rootBox.getMinY(), rootBox.getMinZ(), rootBox.getLen(), level);
    }


    private Octree.Node expand(final BoundingSphere obj, final MatchHandler handler) {
        Octree.Node node = root;
        float x = rootBox.getMinX();
        float y = rootBox.getMinY();
        float z = rootBox.getMinZ();
        float l = rootBox.getLen();

        while (true) {
            ContainmentType cont = handler.nodeContains(x, y, z, l);
            if (cont == ContainmentType.Contains) {
                break;
            }

            int i = 7 - getNodeIndex(obj.x, obj.y, obj.z, x, y, z, l);
            x = x - ((i >> 2) % 2) * l;
            y = y - ((i >> 1) % 2) * l;
            z = z - ((i >> 0) % 2) * l;
            l = l * 2;

            Node newNode = new Node();
            newNode.setChild(i, node);
            node = newNode;

        }
        rootBox.setMin(x, y, z);
        rootBox.setLen(l);
        return node;
    }

    private boolean contains(float px, float py, float pz, float x, float y, float z, float l) {
        return x <= px && px <= x + l && y <= py && py <= y + l && z <= pz && pz <= z + l;
    }

    public class Node {

        private ContainmentType nodeContains;
        private ContainmentType objectContains;
        private Node[] children = new Node[8];


        private Node() {
            this.nodeContains = ContainmentType.Disjoint;
        }

        private void setChild(int i, Node node) {
            this.children[i] = node;
        }

        private Node getChild(int i) {
            return this.children == null ? null : this.children[i];
        }

        private void handleVisibleObjects(final BoundingFrustum frustum, float x, float y, float z, float l, int lvl,
                                          final VisibleVoxelHandler handler) {

            handler.onObjectVisible(x, y, z, l, lvl, this);
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
                            node.handleVisibleObjects(frustum, newX, newY, newZ, newL, lvl + 1, handler);
                        }
                    }
                }
            }
        }

        private void clearChildren(){
            children = new Node[8];
        }

        private void addVolume(int level, float x, float y, float z, float l, final MatchHandler handler) {
            // the new object is not going to add anything new
            if (objectContains == ContainmentType.Contains) {
                return;
            }
            ContainmentType oc = handler.objectContains(x, y, z, l);
            ContainmentType nc = handler.nodeContains(x, y, z, l);
            nodeContains = nodeContains == ContainmentType.Intersects ? nodeContains : nc;
            objectContains = oc;

            // the new object contains previous ones
            if(oc == ContainmentType.Contains){
                clearChildren();
                return;
            }

            if (nc != ContainmentType.Disjoint && canSplit(l)) {
                float newL = l / 2;
                for (int mx = 0; mx < 2; ++mx) {
                    for (int my = 0; my < 2; ++my) {
                        for (int mz = 0; mz < 2; ++mz) {
                            float newX = x + mx * newL;
                            float newY = y + my * newL;
                            float newZ = z + mz * newL;
                            int index = mx * 4 + my * 2 + mz;
                            Node node = getChild(index);
                            if (node == null) {
                                node = new Node();
                                setChild(index, node);
                            }
                            node.addVolume(level + 1, newX, newY, newZ, newL, handler);
                        }
                    }
                }
            }
        }


        private void removeVolume(int level, float x, float y, float z, float l, final MatchHandler handler) {
            // the new object is not going to add anything new
            if (objectContains == ContainmentType.Contains) {
                return;
            }
            ContainmentType oc = handler.objectContains(x, y, z, l);
            ContainmentType nc = handler.nodeContains(x, y, z, l);
            nodeContains = nodeContains == ContainmentType.Intersects ? nodeContains : nc;
            objectContains = oc;

            // the new object contains previous ones
            if(oc == ContainmentType.Contains){
                clearChildren();
                return;
            }

            if (nc != ContainmentType.Disjoint && canSplit(l)) {
                float newL = l / 2;
                for (int mx = 0; mx < 2; ++mx) {
                    for (int my = 0; my < 2; ++my) {
                        for (int mz = 0; mz < 2; ++mz) {
                            float newX = x + mx * newL;
                            float newY = y + my * newL;
                            float newZ = z + mz * newL;
                            int index = mx * 4 + my * 2 + mz;
                            Node node = getChild(index);

                            if (node == null) {
                                node = new Node();
                                setChild(index, node);
                            }
                            node.removeVolume(level + 1, newX, newY, newZ, newL, handler);
                        }
                    }
                }
            }
        }           // an object is already containing this node


        private ContainmentType getValueAtLevelAux(float px, float py, float pz, float x, float y, float z, float l, int level) {
            if (!contains(px, py, pz, x, y, z, l)) {
                return ContainmentType.Disjoint;
            }
            if (level > 0) {
                int i = getNodeIndex(px, py, pz, x, y, z, l);
                Node node = getChild(i);
                if (node != null) {
                    l = l * 0.5f;
                    x = x + ((i >> 2) % 2) * l;
                    y = y + ((i >> 1) % 2) * l;
                    z = z + ((i >> 0) % 2) * l;
                    return node.getValueAtLevelAux(px, py, pz, x, y, z, l, level - 1);
                }
            }
            return nodeContains;
        }

        public ContainmentType getNodeContains() {
            return nodeContains;
        }

        public ContainmentType getObjectContains() {
            return objectContains;
        }
    }
}
