package com.enter4ward.math;

import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class IBufferObject extends BoundingSphere {
    private final static float[] packedData = new float[300000];
    private final static short[] indexData = new short[100000];
    private static int vertexDataPointer = 0, normalDataPointer = 0, textureDataPointer = 0, indexDataPointer = 0;

    protected FloatBuffer vertexBuffer;
    protected ShortBuffer indexBuffer;
    protected int indexCount;
    protected Material material;
    float maxX = 0, maxY = 0, maxZ = 0, minX = 0, minY = 0, minZ = 0;
    boolean inited = false;
    private List<Triangle> triangles = new ArrayList<>();
    private boolean explodeTriangles;

    public IBufferObject(boolean explodeTriangles) {
        this.explodeTriangles = explodeTriangles;
    }

    public void addVertex(float x, float y, float z) {
        packedData[vertexDataPointer * 8 + 0] = x;
        packedData[vertexDataPointer * 8 + 1] = y;
        packedData[vertexDataPointer * 8 + 2] = z;

        vertexDataPointer++;

        if (!inited) {
            minX = maxX = x;
            minY = maxY = y;
            minZ = maxZ = z;
            inited = true;
        } else {
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }
    }

    public void addNormal(float x, float y, float z) {
        packedData[normalDataPointer * 8 + 3] = x;
        packedData[normalDataPointer * 8 + 4] = y;
        packedData[normalDataPointer * 8 + 5] = z;
        normalDataPointer++;
    }

    public void addTexture(float u, float v) {
        packedData[textureDataPointer * 8 + 6] = u;
        packedData[textureDataPointer * 8 + 7] = v;
        textureDataPointer++;
    }

    public void addIndex(short f) {
        indexData[indexDataPointer++] = f;

    }

    private Vector3f getVector3f(int i) {
        float x = packedData[i * 8 + 0];
        float y = packedData[i * 8 + 1];
        float z = packedData[i * 8 + 2];
        return new Vector3f(x, y, z);


    }

    public Material getMaterial() {
        return material;
    }

    public final void setMaterial(final Material f) {
        material = f;
    }

    public void buildBuffer() {
        /* BUILD SPHERE */
        x = ((minX + maxX) / 2f);
        y = ((minY + maxY) / 2f);
        z = ((minZ + maxZ) / 2f);

        r = ((float) (Math
                .sqrt((maxX - minX) * (maxX - minX) + (maxY - minY)
                        * (maxY - minY) + (maxZ - minZ) * (maxZ - minZ)) / 2d));

        /* EXTRACT TRIANGLES */
        if (explodeTriangles) {
            for (int i = 0; i < indexDataPointer; i += 3) {
                Vector3f a = getVector3f(indexData[i]);
                Vector3f b = getVector3f(indexData[i + 1]);
                Vector3f c = getVector3f(indexData[i + 2]);
                triangles.add(new Triangle(a, b, c));
            }
        }

        /* BUILD BUFFERS */

        int pp = 0, ii = 0;
        int size = vertexDataPointer * 8;
        vertexBuffer = ByteBuffer.allocateDirect(size * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        while (pp < size) {
            vertexBuffer.put(packedData[pp++]);
        }
        indexCount = indexDataPointer;
        vertexBuffer.position(0);

        indexBuffer = ByteBuffer.allocateDirect(indexDataPointer * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();


        while (ii < indexDataPointer) {
            indexBuffer.put(indexData[ii++]);
        }

        indexBuffer.position(0);

        // CLEAR
        vertexDataPointer = 0;
        normalDataPointer = 0;
        textureDataPointer = 0;
        indexDataPointer = 0;

    }

    public Iterable<Triangle> getTriangles() {
        return triangles;
    }

}
