package com.enter4ward.lwjgl;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;

public class TextureLoader {

    private static Map<String, Integer> loadedTextures = new TreeMap<String, Integer>();

    public int loadTexture(String filename) {
        Integer res = loadedTextures.get(filename);
        if (res == null) {
            res = loadPNGTexture(filename, GL13.GL_TEXTURE0);
            loadedTextures.put(filename, res);
        }
        return res;
    }


    /**
     * Load png texture.
     *
     * @param filename    the filename
     * @param textureUnit the texture unit
     * @return the int
     */
    private int loadPNGTexture(String filename, int textureUnit) {
        ByteBuffer buf = null;
        int tWidth = 0;
        int tHeight = 0;

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(filename);

        try {
            // Link the PNG decoder to this stream
            PNGDecoder decoder = new PNGDecoder(stream);

            // Get the width and height of the texture
            tWidth = decoder.getWidth();
            tHeight = decoder.getHeight();

            // Decode the PNG file in a ByteBuffer
            buf = ByteBuffer.allocateDirect(4 * decoder.getWidth()
                    * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
            buf.flip();

            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Create a new texture object in memory and bind it
        int texId = GL11.glGenTextures();
        GL13.glActiveTexture(textureUnit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

        // All RGB bytes are aligned to each other and each component is 1 byte
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        // Upload the texture data and generate mip maps (for scaling)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, tWidth, tHeight,
                0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        // Setup the ST coordinate system
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
                GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
                GL11.GL_REPEAT);

        // Setup what to do when the texture has to be scaled
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR_MIPMAP_LINEAR);


        return texId;
    }


}
