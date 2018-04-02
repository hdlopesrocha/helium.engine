package com.enter4ward.lwjgl;

import com.enter4ward.math.IBufferObject;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengles.GLES20.GL_ARRAY_BUFFER;

// TODO: Auto-generated Javadoc

/**
 * The Class BufferObject.
 */
public class BufferObject extends IBufferObject {

    // The amount of bytes an element has
    /**
     * The Constant elementBytes.
     */
    public static final int elementBytes = 4;

    // Elements per parameter
    /**
     * The Constant positionElementCount.
     */
    public static final int positionElementCount = 3;

    /**
     * The Constant normalElementCount.
     */
    public static final int normalElementCount = 3;

    /**
     * The Constant textureElementCount.
     */
    public static final int textureElementCount = 2;

    // Bytes per parameter
    /**
     * The Constant positionBytesCount.
     */
    public static final int positionBytesCount = positionElementCount
            * elementBytes;

    /**
     * The Constant normalByteCount.
     */
    public static final int normalByteCount = normalElementCount * elementBytes;

    /**
     * The Constant textureByteCount.
     */
    public static final int textureByteCount = textureElementCount * elementBytes;

    // Byte offsets per parameter
    /**
     * The Constant positionByteOffset.
     */
    public static final int positionByteOffset = 0;

    /**
     * The Constant normalByteOffset.
     */
    public static final int normalByteOffset = positionByteOffset + positionBytesCount;

    /**
     * The Constant textureByteOffset.
     */
    public static final int textureByteOffset = normalByteOffset + normalByteCount;

    // The amount of elements that a vertex has
    /**
     * The Constant elementCount.
     */
    public static final int elementCount = positionElementCount + normalElementCount + textureElementCount;
    // The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
    /**
     * The Constant stride.
     */
    public static final int stride = positionBytesCount + normalByteCount + textureByteCount;

    /*
     * (non-Javadoc)
     *
     * @see com.enter4ward.lwjgl.IBufferObject#getMaterial()
     */


    /**
     * The vao id.
     */
    private int vaoId;

    /**
     * The vboi id.
     */
    private int vboiId;

    /**
     * The vbo id.
     */
    private int vboId;


    /**
     * Instantiates a new buffer object.
     *
     * @param explodeTriangles the explode triangles
     */
    public BufferObject(boolean explodeTriangles) {
        super(explodeTriangles);
    }


    /**
     * Builds the buffer.
     */
    public final void buildBuffer() {
        super.buildBuffer();

        // Create a new Vertex Array Object in memory and select it (bind)
        vaoId = glGenVertexArrays();
        vboiId = glGenBuffers();
        vboId = glGenBuffers();

        glBindVertexArray(vaoId);
        // Create a new Vertex Buffer Object in memory and select it (bind)
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Put the position coordinates in attribute list 0
        glVertexAttribPointer(0, positionElementCount, GL_FLOAT, false,
                stride, positionByteOffset);
        glVertexAttribPointer(1, normalElementCount, GL_FLOAT, false,
                stride, normalByteOffset);
        glVertexAttribPointer(2, textureElementCount, GL_FLOAT, false,
                stride, textureByteOffset);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Deselect (bind to 0) the VAO
        glBindVertexArray(0);

        // Create a new VBO for the indices and select it (bind) - INDICES
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer,
                GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

    }



    /*
     * (non-Javadoc)
     *
     * @see com.enter4ward.lwjgl.IBufferObject#bind(com.enter4ward.lwjgl.ShaderProgram)
     */

    /**
     * Bind.
     *
     * @param shader the shader
     */
    public final void bind(final ShaderProgram shader) {
        int tex = material != null ? material.texture : 0;
        // Bind the texture according to the set texture filter
        if (material != null) {
            if (material.Ns != null)
                shader.setMaterialShininess(material.Ns);
            if (material.Ks != null)
                shader.setMaterialSpecular(material.Ks[0], material.Ks[1], material.Ks[2]);
            if (material.Kd != null)
                shader.setDiffuseColor(material.Kd[0], material.Kd[1], material.Kd[2]);
            if (material.d != null)
                shader.setMaterialAlpha(material.d);
        }

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, tex);

    }

    /**
     * Draw.
     *
     * @param shader the shader
     */
    public final void draw(final ShaderProgram shader) {
        // Bind to the VAO that has all the information about the vertices
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        // Bind to the index VBO that has all the information about the
        // order of
        // the vertices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);

        // Draw the vertices
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_SHORT, 0);

        // Put everything back to default (deselect)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }


}
