package com.enter4ward.math;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;

// TODO: Auto-generated Javadoc

/**
 * The Class Space.
 */
public class Space {

    private static final Vector3f TEMP_LENGTH = new Vector3f();
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int CENTER = 2;
    private static HashMap<Vector3f, Vector3f> lengths = new HashMap<Vector3f, Vector3f>();
    private static int LENS = 0;

    private float minSize;
    private Node root;

    /**
     * Instantiates a new space.
     */
    public Space(float minSize) {
        this.minSize = minSize;
        root = new Node();
    }

    private static Vector3f recycle(final Vector3f v) {
        Vector3f r = lengths.get(v);
        if (r == null) {
            r = new Vector3f(v);
            lengths.put(r, r);
            ++LENS;
        }
        return r;
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

    /**
     * The Class Node.
     */
    public class Node extends BoundingBox {
        /**
         * The container.
         */
        private ArrayList<Object> container;
        /**
         * The parent.
         */
        private Node parent, left, right, center;

        /**
         * Instantiates a new space node.
         */
        public Node() {
            super(new Vector3f(0), new Vector3f(minSize * 3));
            this.parent = null;
        }

        /**
         * Instantiates a new space node.
         *
         * @param parent the parent
         * @param min    the min
         * @param len    the len
         */
        private Node(Node parent, Vector3f min, Vector3f len) {
            super(min, len);
            this.parent = parent;
        }

        /**
         * Instantiates a new space node.
         *
         * @param node the node
         * @param i    the i
         * @param min  the min
         * @param len  the len
         */
        private Node(Node node, int i, Vector3f min, Vector3f len) {
            super(min, len);
            switch (i) {
                case LEFT:
                    left = node;
                    break;
                case CENTER:
                    center = node;
                    break;
                case RIGHT:
                    right = node;
                    break;
                default:
                    break;
            }

            node.parent = this;
        }

        private int getIndex(float value, float length, float radius) {
            if (value >= radius) {
                return LEFT;
            } else if (-value >= radius) {
                return RIGHT;
            } else if (Math.abs(value) + radius <= length * .25f) {
                return CENTER;
            }
            return -1;
        }

        /**
         * Contains index.
         *
         * @param sphere the sphere
         * @return the int
         */
        private int getContainingNodeIndex(final BoundingSphere sphere) {
            final float sr = sphere.r;
            final float lenX = getLengthX();
            final float lenY = getLengthY();
            final float lenZ = getLengthZ();

            if (lenX >= lenY && lenX >= lenZ) {
                final float dist = getCenterX() - sphere.x;
                return getIndex(dist, lenX, sr);
            } else if (lenY >= lenZ) {
                final float dist = getCenterY() - sphere.y;
                return getIndex(dist, lenY, sr);
            } else {
                final float dist = getCenterZ() - sphere.z;
                return getIndex(dist, lenZ, sr);
            }
        }

        /**
         * Container add.
         *
         * @param obj the obj
         */
        private void containerAdd(final Object obj) {
            if (container == null)
                container = new ArrayList<Object>(1);
            container.add(obj);
            container.trimToSize();
        }

        /**
         * Container remove.
         *
         * @param obj the obj
         */
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

        /**
         * Container size.
         *
         * @return the int
         */
        public int containerSize() {
            return container == null ? 0 : container.size();
        }

        /*
         * (non-Javadoc)
         *
         * @see com.enter4ward.BoundingBox#toString()
         */
        public String toString() {
            return super.toString();
        }

        /**
         * Builds the.
         *
         * @param i the i
         * @return the space node
         */

        private Node build(final int i) {

            final float lenX = getLengthX();
            final float lenY = getLengthY();
            final float lenZ = getLengthZ();

            if (lenX >= lenY && lenX >= lenZ) {
                final Vector3f len = recycle(TEMP_LENGTH.set(lenX * 0.5f, lenY,
                        lenZ));

                if (i == LEFT) {
                    return new Node(this, getMin(), len);
                } else if (i == RIGHT) {
                    return new Node(this, new Vector3f(getMin()) {{
                        x += lenX / 2;
                    }}, len);
                } else {
                    return new Node(this, new Vector3f(getMin()) {{
                        x += lenX / 4;
                    }}, len);
                }
            } else if (lenY >= lenZ) {
                final Vector3f len = recycle(TEMP_LENGTH.set(lenX, lenY * 0.5f,
                        lenZ));

                if (i == LEFT) {
                    return new Node(this, getMin(), len);
                } else if (i == RIGHT) {
                    return new Node(this, new Vector3f(getMin()) {{
                        y += lenY / 2;
                    }}, len);
                } else {
                    return new Node(this, new Vector3f(getMin()) {{
                        y += lenY / 4;
                    }}, len);
                }
            } else {
                final Vector3f len = recycle(TEMP_LENGTH.set(lenX, lenY,
                        lenZ * 0.5f));
                if (i == LEFT) {
                    return new Node(this, getMin(), len);
                } else if (i == RIGHT) {
                    return new Node(this, new Vector3f(getMin()) {{
                        z += lenZ / 2;
                    }}, len);
                } else {
                    return new Node(this, new Vector3f(getMin()) {{
                        z += lenZ / 4;
                    }}, len);
                }
            }
        }

        /**
         * Gets the child.
         *
         * @param i the i
         * @return the child
         */
        private Node getChild(int i) {

            switch (i) {
                case LEFT:
                    if (left == null) {
                        left = build(i);
                    }
                    return left;
                case CENTER:
                    if (center == null) {
                        center = build(i);
                    }
                    return center;
                case RIGHT:
                    if (right == null) {
                        right = build(i);
                    }
                    return right;
                default:
                    break;
            }
            return null;

        }

        /**
         * Child.
         *
         * @param i the i
         * @return the node
         */
        private Node child(int i) {
            switch (i) {
                case LEFT:
                    return left;
                case CENTER:
                    return center;
                case RIGHT:
                    return right;
                default:
                    break;
            }
            return null;

        }

        /**
         * Expand.
         *
         * @param obj the obj
         * @return the space node
         */
        private Node expandAux(final BoundingSphere obj) {
            final float lenX = getLengthX();
            final float lenY = getLengthY();
            final float lenZ = getLengthZ();

            if (lenX < lenY && lenX < lenZ) {
                final Vector3f len = recycle(TEMP_LENGTH.set(lenX * 2, lenY,
                        lenZ));

                if (obj.x >= getCenterX()) {
                    return new Node(this, LEFT, getMin(), len);
                } else {
                    return new Node(this, RIGHT,
                            new Vector3f(getMin()) {{
                                x -= lenX;
                            }}, len);
                }
            } else if (lenY < lenZ) {
                final Vector3f len = recycle(TEMP_LENGTH.set(lenX, lenY * 2,
                        lenZ));

                if (obj.y >= getCenterY()) {
                    return new Node(this, LEFT, getMin(), len);
                } else {
                    return new Node(this, RIGHT,
                            new Vector3f(getMin()) {{
                                y -= lenY;
                            }}, len);

                }
            } else {
                final Vector3f len = recycle(TEMP_LENGTH.set(lenX, lenY,
                        lenZ * 2));

                if (obj.z >= getCenterZ()) {
                    return new Node(this, LEFT, getMin(), len);
                } else {
                    return new Node(this, RIGHT,
                            new Vector3f(getMin()) {{
                                z -= lenZ;
                            }}, len);

                }
            }
        }

        /**
         * Can split.
         *
         * @return true, if successful
         */
        private boolean canSplit() {
            // if(containerSize()==0)
            // return false;

            return left != null || right != null || center != null
                    || getLengthX() > minSize || getLengthY() > minSize || getLengthZ() > minSize;
        }


        /**
         * Iterate.
         *
         * @param frustum the frustum
         * @param handler the handler
         */
        private void handleVisibleObjects(final BoundingFrustum frustum,
                                          final VisibleObjectHandler handler) {

            handler.onObjectVisible(this);
            if (container != null) {
                for (Object obj : container) {
                    handler.onObjectVisible(obj);
                }
            }

            int intersections = 0;

            for (int i = 0; i < 3; ++i) {
                Node node = child(i);
                if (node != null
                        && (intersections == 2 || frustum.contains(node) != ContainmentType.Disjoint)) {
                    ++intersections;
                    node.handleVisibleObjects(frustum, handler);
                }
            }

        }

        /**
         * Removes the.
         *
         * @param obj the obj
         */
        protected void remove(final Object obj) {
            containerRemove(obj);

            Node node = this;
            while (node != null) {
                node.clearChildren();
                node = node.parent;
            }
        }

        /**
         * Clear child.
         */
        protected void clearChildren() {

            if (left != null && left.isEmpty()) {
                left = null;
            }

            if (right != null && right.isEmpty()) {
                right = null;
            }

            if (center != null && center.isEmpty()) {
                center = null;
            }

        }

        /**
         * Update.
         *
         * @param sph the sph
         * @return the node
         */
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

        /**
         * Expand.
         *
         * @param obj the obj
         * @return the node
         */
        private Node expand(final BoundingSphere obj) {
            Node node = this;
            while (!node.containsSphere(obj)) {
                node.clearChildren();
                node = node.expandAux(obj);
            }
            return node;
        }

        /**
         * Iterate.
         *
         * @param sph     the sph
         * @param handler the handler
         */
        private void handleObjectCollisions(final BoundingSphere sph,
                                            final ObjectCollisionHandler handler) {
            if (container != null) {
                for (Object obj : container) {
                    handler.onObjectCollision(obj);
                }
            }
            int intersections = 0;
            for (int i = 0; i < 3; ++i) {
                Node node = child(i);
                if (node != null
                        && (intersections == 2 || node.contains(sph) != ContainmentType.Disjoint)) {
                    ++intersections;
                    node.handleObjectCollisions(sph, handler);
                }
            }

        }

        /**
         * Insert.
         *
         * @param sph the sph
         * @return the space node
         */
        private Node getBestChildNode(final BoundingSphere sph) {
            Node node = this;

            // insertion
            while (true) {
                if (node.canSplit()) {
                    int i = node.getContainingNodeIndex(sph);
                    if (i < 0) {
                        break;
                    }
                    node = node.getChild(i);
                } else {
                    break;
                }
            }

            return node;
        }

        /**
         * Handle ray collisions.
         *
         * @param space   the space
         * @param ray     the ray
         * @param handler the handler
         */
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
            for (int i = 0; i < 3; ++i) {
                Node node = child(i);
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

        /**
         * Checks if is empty.
         *
         * @return true, if is empty
         */
        private boolean isEmpty() {
            return containerSize() == 0
                    && (left == null && center == null && right == null);
        }

        /**
         * Compress.
         *
         * @return the node
         */
        private Node compress() {
            Node node = this;
            while (true) {
                if (node.containerSize() == 0) {
                    boolean emptyLeft = node.left == null;
                    boolean emptyCenter = node.center == null;
                    boolean emptyRight = node.right == null;

                    if (emptyLeft && emptyCenter && !emptyRight) {
                        node = node.right;
                    } else if (emptyLeft && !emptyCenter && emptyRight) {
                        node = node.center;
                    } else if (!emptyLeft && emptyCenter && emptyRight) {
                        node = node.left;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            node.parent = null;
            return node;
        }

        /*
         * public boolean handleRayCollisions(Space space, Ray ray,
         * RayCollisionHandler handler) { float len =
         * ray.getDirection().length(); boolean ret = false; if (container !=
         * null) { IntersectionInfo closestInfo = null; BoundingSphere
         * closestObject = null; for (Object obj2 : container) { Float idist =
         * ray.intersects(obj2); if ((idist != null && idist < len) ||
         * obj2.contains(ray.getPosition())) { IntersectionInfo info =
         * handler.closestTriangle(obj2, ray); if (closestInfo == null ||
         * info.distance < closestInfo.distance) { closestInfo = info;
         * closestObject = obj2; } } } if (closestInfo != null) { ret |=
         * handler.onObjectCollision(space, ray, closestObject, closestInfo); }
         * } if (child != null) { int intersections = 0; for (int i = 0; i < 3;
         * ++i) { SpaceNode node = child[i]; Float idist = null; if (node !=
         * null && node.count > 0 && (intersections == 2 ||
         * node.contains(ray.getPosition()) != ContainmentType.Disjoint ||
         * ((idist = ray .intersects(node)) != null && idist <= len))) {
         * ++intersections; if (idist == null) { idist = 0f; } //
         * System.out.println("#"+node+"#"+ray+"#"+idist+"#"+handler); ret |=
         * node.handleRayCollisions(space, ray, handler); } } } return ret; }
         */
    }


}
