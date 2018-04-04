package com.enter4ward.math;

import org.joml.Vector3f;

import java.util.ArrayList;

public class Space {

    private static final int NUMBER_NODES = 27;
    private static final Integer[] OPTIMAL_ORDER = new Integer[]{
            0, 2, 6, 8, 18, 20, 24, 26,
            4, 22, 10, 12, 14, 16,
            1, 3, 5, 7, 9, 11, 15, 17, 19, 21, 23, 25,
            13
    };

    private static final BoundingCube TEMP_BOUNDING_BOX = new BoundingCube(new Vector3f(), 0f);
    private final float minSize;
    private Node root;

    public Space(float minSize) {
        this.minSize = minSize;
        root = new Node();
    }

    public void clear() {
        root = new Node();

    }

    public Node insert(final BoundingSphere sph, final Object obj) {
        root = root.expand(sph);
        final Node node = root.getBestChildNode(sph);
        node.containerAdd(obj);
        root = root.compress();
        return node;
    }

    public Node update(final BoundingSphere sph, Node node, final Object obj) {
        if (!node.containsSphere(sph)) {
            node.containerRemove(obj);
            node = node.getBestParentNode(sph);
            if (node == null) {
                node = root = root.expand(sph);
            }
            node = node.getBestChildNode(sph);
            node.containerAdd(obj);
            root = root.compress();
        }
        return node;
    }

    public void handleVisibleObjects(BoundingFrustum frustum,
                                     VisibleObjectHandler handler) {
        if (root != null) {
            root.handleVisibleObjects(frustum, handler);
        }
    }

    public void handleObjectCollisions(final BoundingSphere sphere,
                                       final ObjectCollisionHandler handler) {
        if (root != null) {
            root.handleObjectCollisions(sphere, handler);
        }
    }

    public IntersectionInfo handleRayCollisions(final Ray ray,
                                                final RayCollisionHandler handler) {
        if (root != null) {
            return root.handleRayCollisions(this, ray, handler);
        }
        return null;
    }

    public class Node extends BoundingCube {

        private ArrayList<Object> container;

        private Node parent;

        private ArrayList<Node> children;

        public Node() {
            super(new Vector3f(-minSize * 0.5f), minSize * 0.5f);
            this.parent = null;
        }

        private Node(Node parent, Vector3f min, float len) {
            super(min, len);
            this.parent = parent;

        }

        private Node(Node node, int i, Vector3f min, float len) {
            super(min, len);
            node.parent = this;
            setChild(i, node);
        }

        private void setChild(int i, Node node) {
            if (this.children == null) {
                this.children = new ArrayList<>(8);
            }
            if (i >= 14 && this.children.size() <= 14) {
                this.children.ensureCapacity(27);
            } else if (i >= 8 && this.children.size() <= 8) {
                this.children.ensureCapacity(14);
            }

            while (this.children.size() <= i) {
                this.children.add(null);
            }
            this.children.set(i, node);
        }

        private int clamp(int val, int min, int max) {
            return val < min ? min : val > max ? max : val;
        }

        private int getNodeIndex(Vector3f position) {
            int px = (int) (2 * (position.x - getMinX()) / getLen());
            int py = (int) (2 * (position.y - getMinY()) / getLen());
            int pz = (int) (2 * (position.z - getMinZ()) / getLen());
            px = clamp(px, 0, 2);
            py = clamp(py, 0, 2);
            pz = clamp(pz, 0, 2);
            return px * 9 + py * 3 + pz;
        }

        private void containerAdd(final Object obj) {
            if (container == null) {
                container = new ArrayList<>(1);
            }
            container.add(obj);
            container.trimToSize();
        }

        private void containerRemove(final Object obj) {
            if (container != null) {
                container.remove(obj);
                if (container.size() == 0) {
                    container = null;
                }
            }
        }

        public boolean contains(final Object obj) {
            if (container != null) {
                return container.contains(obj);
            }
            return false;
        }

        public int containerSize() {
            return container == null ? 0 : container.size();
        }

        private Node buildIfContains(final int i, BoundingSphere sph) {
            final float len = getLen() * 0.5f;

            final int px = (i / 9) % 3;
            final int py = (i / 3) % 3;
            final int pz = (i) % 3;
            Vector3f newMin = i == 0 ? getMin() : new Vector3f(getMin()).add(
                    len * px * 0.5f,
                    len * py * 0.5f,
                    len * pz * 0.5f
            );

            TEMP_BOUNDING_BOX.getMin().set(newMin);
            TEMP_BOUNDING_BOX.setLen(len);
            if (TEMP_BOUNDING_BOX.contains(sph) == ContainmentType.Contains) {
                return new Node(this, newMin, len);
            } else {
                return null;
            }
        }

        private Node getOrCreateChildIfContains(int i, BoundingSphere sph) {
            Node child = getChild(i);
            if (child == null) {
                child = buildIfContains(i, sph);
                if (child != null) {
                    setChild(i, child);
                }
            } else {
                if (!child.containsSphere(sph)) {
                    return null;
                }
            }
            return child;
        }

        private Node getChild(int i) {
            if (this.children == null) {
                return null;
            }
            if (i < this.children.size()) {
                return this.children.get(i);
            }
            return null;
        }

        private int getChildrenCount() {
            return this.children == null ? 0 : this.children.size();
        }

        private Node expandAux(final Vector3f position) {
            final int index = (NUMBER_NODES - 1) - getNodeIndex(position);
            final float len = getLen() * 2.0f;

            float sx = ((index / 9) % 3) * getLen() * 0.5f;
            float sy = ((index / 3) % 3) * getLen() * 0.5f;
            float sz = ((index) % 3) * getLen() * 0.5f;
            Vector3f newMin = index == 0 ? getMin() : new Vector3f(getMin()).sub(sx, sy, sz);
            return new Node(this, index, newMin, len);
        }

        private boolean canSplit() {
            return getLen() > minSize;
        }

        private void handleVisibleObjects(final BoundingFrustum frustum,
                                          final VisibleObjectHandler handler) {

            handler.onObjectVisible(this);
            if (container != null) {
                for (Object obj : container) {
                    handler.onObjectVisible(obj);
                }
            }

            int intersections = 0;
            int childrenCount = getChildrenCount();
            for (int i = 0; i < childrenCount; ++i) {
                Node node = getChild(i);
                if (node != null
                        && (intersections == 2 || frustum.contains(node) != ContainmentType.Disjoint)) {
                    ++intersections;
                    node.handleVisibleObjects(frustum, handler);
                }
            }

        }

        protected void remove(final Object obj) {
            containerRemove(obj);

            Node node = this;
            while (node != null) {
                node.clearChildren();
                node = node.parent;
            }
        }

        protected void clearChildren() {
            if (children != null) {
                int childrenCount = getChildrenCount();
                for (int i = 0; i < childrenCount; ++i) {
                    Node child = getChild(i);
                    if (child != null && child.isEmpty()) {
                        setChild(i, null);
                    }
                }
            }
        }

        private Node getBestParentNode(BoundingSphere sph) {
            Node node = this;
            while (node != null) {
                node.clearChildren();
                if (node.containsSphere(sph)) {
                    break;
                } else {
                    node = node.parent;
                }
            }
            return node;
        }

        private Node expand(final BoundingSphere obj) {
            Node node = this;
            while (!node.containsSphere(obj)) {
                node.clearChildren();
                node = node.expandAux(obj);
                node.clearChildren();
            }
            return node;
        }

        private void handleObjectCollisions(final BoundingSphere sph,
                                            final ObjectCollisionHandler handler) {
            if (container != null) {
                for (Object obj : container) {
                    handler.onObjectCollision(obj);
                }
            }
            int childrenCount = getChildrenCount();
            for (int i = 0; i < childrenCount; ++i) {
                Node node = getChild(i);
                if (node != null && node.contains(sph) != ContainmentType.Disjoint) {
                    node.handleObjectCollisions(sph, handler);
                }
            }

        }

        private Node getBestChildNode(final BoundingSphere sph) {
            Node node = this;
            while (node.canSplit()) {
                Node candidate = null;
                for (int i = 0; i < NUMBER_NODES; ++i) {
                    int j = OPTIMAL_ORDER[i];
                    candidate = node.getOrCreateChildIfContains(j, sph);
                    if (candidate != null) {
                        node = candidate;
                        break;
                    }
                }
                if (candidate == null) {
                    break;
                }
            }
            return node;
        }

        private IntersectionInfo handleRayCollisions(final Space space, final Ray ray,
                                                     final RayCollisionHandler handler) {
            final float len = ray.getDirection().length();
            IntersectionInfo result = null;
            if (container != null) {
                for (Object obj : container) {
                    IntersectionInfo r = handler.onObjectCollision(space, ray, obj);
                    if (r != null && (result == null || r.distance < result.distance)) {
                        result = r;
                    }
                }
            }
            int intersections = 0;
            int childrenCount = getChildrenCount();
            for (int i = 0; i < childrenCount; ++i) {
                Node node = getChild(i);
                Float idist = null;
                if (node != null
                        && (intersections == 2
                        || node.contains(ray.getPosition()) != ContainmentType.Disjoint || ((idist = ray
                        .intersects(node)) != null && idist <= len))) {
                    ++intersections;
                    if (idist == null) {
                        idist = 0f;
                    }
                    IntersectionInfo r = node.handleRayCollisions(space, ray, handler);
                    if (r != null && (result == null || r.distance < result.distance)) {
                        result = r;
                    }
                }
            }
            return result;
        }

        private boolean isEmpty() {
            int childrenCount = getChildrenCount();
            for (int i = 0; i < childrenCount; ++i) {
                if (getChild(i) != null) {
                    return false;
                }
            }
            return containerSize() == 0;
        }

        private Node compress() {
            Node node = this;
            while (node.containerSize() == 0) {
                Node candidate = null;
                int childrenCount = node.getChildrenCount();
                for (int i = 0; i < childrenCount; ++i) {
                    Node child = node.getChild(i);
                    if (child != null) {
                        if (candidate == null) {
                            candidate = child;
                        } else {
                            candidate = null;
                            break;
                        }
                    }
                }
                if (candidate != null) {
                    node = candidate;
                } else {
                    break;
                }
            }
            node.parent = null;
            return node;
        }
    }

}
