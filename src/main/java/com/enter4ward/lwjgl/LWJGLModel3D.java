package com.enter4ward.lwjgl;

import com.enter4ward.math.*;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.io.FileNotFoundException;

public class LWJGLModel3D extends Model3D {

    public LWJGLModel3D(String filename, float scale, Quaternionf quat, IBufferBuilder builder)
            throws FileNotFoundException {
        super(filename, scale, builder, quat);
        loadTextures();
    }

    public LWJGLModel3D(String filename, float scale, IBufferBuilder builder)
            throws FileNotFoundException {
        super(filename, scale, builder, new Quaternionf().identity());

        loadTextures();
    }

    public void loadTextures() {

        for (final Material m : materials.values()) {
            m.load(() -> new TextureLoader().loadTexture(m.filename));
        }
    }

    public void draw(IObject3D obj, ShaderProgram shader, DrawHandler handler) {
        for (Group g : groups) {
            for (IBufferObject ib : g.getBuffers()) {
                BufferObject b = (BufferObject) ib;

                b.bind(shader);
                Matrix4f mat = handler.onDraw(obj, g, b);
                if (mat != null) {
                    shader.setModelMatrix(mat);
                    b.draw(shader);
                }
            }
        }

    }
}
