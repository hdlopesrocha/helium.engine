package com.enter4ward.lwjgl;

import com.enter4ward.math.*;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.io.FileNotFoundException;

// TODO: Auto-generated Javadoc

/**
 * The Class Model3D.
 */
public class LWJGLModel3D extends Model3D {

    /**
     * Instantiates a new model3 d.
     *
     * @param geometry the geometry
     * @param scale    the scale
     * @param builder  the builder
     * @throws FileNotFoundException the file not found exception
     */


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

    /**
     * Load textures.
     */
    public void loadTextures() {


        for (final Material m : materials.values()) {

            m.load(new ITextureLoader() {

                @Override
                public int load() {

                    return new TextureLoader().loadTexture(m.filename);
                }
            });
        }
    }

    /**
     * Draw.
     *
     * @param obj     the obj
     * @param shader  the shader
     * @param handler the handler
     */
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
