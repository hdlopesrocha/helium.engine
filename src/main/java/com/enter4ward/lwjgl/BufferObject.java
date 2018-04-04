package com.enter4ward.lwjgl;

import com.enter4ward.math.IBufferObject;
import com.enter4ward.math.VertexData;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengles.GLES20.GL_ARRAY_BUFFER;

public class BufferObject extends IBufferObject {

    public static final int elementBytes = 4;

    public static final int positionElementCount = 3;

    public static final int normalElementCount = 3;

    public static final int textureElementCount = 2;

    public static final int positionBytesCount = positionElementCount * elementBytes;

    public static final int normalByteCount = normalElementCount * elementBytes;

    public static final int textureByteCount = textureElementCount * elementBytes;

    public static final int positionByteOffset = 0;

    public static final int normalByteOffset = positionByteOffset + positionBytesCount;

    public static final int textureByteOffset = normalByteOffset + normalByteCount;

    public static final int elementCount = positionElementCount + normalElementCount + textureElementCount;

    public static final int stride = positionBytesCount + normalByteCount + textureByteCount;

    private int vaoId;

    private int vboiId;

    private int vboId;

    public BufferObject(boolean explodeTriangles) {
        super(explodeTriangles);
    }

    public final void buildBuffer(VertexData data) {
        super.buildBuffer(data);

        // Create a new Vertex Array Object in memory and select it (bind)
        vaoId = glGenVertexArrays();
        vboiId = glGenBuffers();
        vboId = glGenBuffers();

        glBindVertexArray(vaoId);
        // Create a new Vertex Buffer Object in memory and select it (bind)
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Put the position coordinates in attribute list 0
        glVertexAttribPointer(0, positionElementCount, GL_FLOAT, false, stride, positionByteOffset);
        glVertexAttribPointer(1, normalElementCount, GL_FLOAT, false, stride, normalByteOffset);
        glVertexAttribPointer(2, textureElementCount, GL_FLOAT, false, stride, textureByteOffset);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Deselect (bind to 0) the VAO
        glBindVertexArray(0);

        // Create a new VBO for the indices and select it (bind) - INDICES
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

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
