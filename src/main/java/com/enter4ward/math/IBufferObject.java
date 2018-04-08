package com.enter4ward.math;

import org.joml.Vector3f;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

public abstract class IBufferObject extends BoundingSphere {

    protected FloatBuffer vertexBuffer;
    protected IntBuffer indexBuffer;
    protected int indexCount;
    protected Material material;

    private final List<Triangle> triangles = new ArrayList<>();
    private final boolean explodeTriangles;

    public IBufferObject(boolean explodeTriangles) {
        this.explodeTriangles = explodeTriangles;
    }

    public Material getMaterial() {
        return material;
    }

    public final void setMaterial(final Material f) {
        material = f;
    }

    public void buildBuffer(VertexData vertexData) {
        /* EXTRACT TRIANGLES */
        if (explodeTriangles) {
            for (int i = 0; i < vertexData.getIndexData().size(); i += 3) {
                Vector3f a = vertexData.getPosition(vertexData.getIndexData().get(i));
                Vector3f b = vertexData.getPosition(vertexData.getIndexData().get(i + 1));
                Vector3f c = vertexData.getPosition(vertexData.getIndexData().get(i + 2));
                triangles.add(new Triangle(a, b, c));
            }
        }

        /* BUILD BUFFERS */

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.size() * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        Vector3f min = null, max = null;

        for (Vector3f position : vertexData.getPositionData()) {
            if (min == null || max == null) {
                min = new Vector3f(position.x, position.y, position.z);
                max = new Vector3f(position.x, position.y, position.z);
            } else {
                min.min(position, min);
                max.max(position, max);
            }
        }

        vertexData.iterate((position, normal, texture) -> {
            vertexBuffer.put(position.x);
            vertexBuffer.put(position.y);
            vertexBuffer.put(position.z);
            vertexBuffer.put(normal.x);
            vertexBuffer.put(normal.y);
            vertexBuffer.put(normal.z);
            vertexBuffer.put(texture.x);
            vertexBuffer.put(texture.y);
        });

        indexCount = vertexData.getIndexData().size();
        vertexBuffer.position(0);

        indexBuffer = ByteBuffer.allocateDirect(vertexData.getIndexData().size() * 4)
                .order(ByteOrder.nativeOrder()).asIntBuffer();

        for (int i : vertexData.getIndexData()) {
            indexBuffer.put(i);
        }
        indexBuffer.position(0);
        vertexData.clear();

        /* BUILD SPHERE */
        if (min != null && max != null) {
            x = (min.x + max.x) * .5f;
            y = (min.y + max.y) * .5f;
            z = (min.z + max.z) * .5f;
            r = min.distance(max) * .5f;
        }
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

}
