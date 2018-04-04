package com.enter4ward.lwjgl;

import com.enter4ward.math.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

import java.io.InputStream;
import java.util.Scanner;

import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.GL11.*;

// TODO: Auto-generated Javadoc

/**
 * The Class ShaderProgram.
 */
public class ShaderProgram {


    /**
     * The m position handle.
     */
    public int mPositionHandle;

    /**
     * The m normal handle.
     */
    public int mNormalHandle;

    /**
     * The m texture coordinate handle.
     */
    public int mTextureCoordinateHandle;

    /**
     * The view matrix location.
     */
    private int viewProjectionMatrixLocation = 0;

    /**
     * The model matrix location.
     */
    private int modelMatrixLocation = 0;

    /**
     * The camera position location.
     */
    private int cameraPositionLocation = 0;

    /**
     * The material shininess location.
     */
    private int materialShininessLocation = 0;

    /**
     * The material alpha location.
     */
    private int materialAlphaLocation = 0;

    /**
     * The material specular location.
     */
    private int materialSpecularLocation = 0;

    /**
     * The time location.
     */
    private int timeLocation = 0;

    /**
     * The opaque location.
     */
    private int opaqueLocation = 0;
    private int lightEnabledLocation = 0;

    /**
     * The ambient color location.
     */
    private int ambientColorLocation = 0;

    /**
     * The diffuse color location.
     */
    private int diffuseColorLocation = 0;

    /**
     * The light position location.
     */
    private int[] lightPositionLocation = new int[10];

    /**
     * The light specular color location.
     */
    private int[] lightSpecularColorLocation = new int[10];
    /**
     * The model matrix.
     */
    private Vector3f[] lightPosition = new Vector3f[10];

    /**
     * The light specular color.
     */
    private Vector3f[] lightSpecularColor = new Vector3f[10];

    /**
     * The program.
     */
    private int program = 0;

    /**
     * Instantiates a new shader program.
     *
     * @param vertexShader the vertex shader
     * @param fragShader   the frag shader
     * @throws Exception the exception
     */
    public ShaderProgram(String vertexShader, String fragShader)
            throws Exception {

        // Load the vertex shader
        int vsId = this.createShader("vertex.glsl", ARBVertexShader.GL_VERTEX_SHADER_ARB);
        // Load the fragment shader
        int fsId = this.createShader("fragment.glsl", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);

        // Create a new shader program that links both shaders
        program = glCreateProgramObjectARB();

        /*
         * if the vertex and fragment shaders setup sucessfully, attach them to
         * the shader program, link the sahder program (into the GL context I
         * suppose), and validate
         */
        glAttachObjectARB(program, vsId);
        glAttachObjectARB(program, fsId);

        glLinkProgramARB(program);
        if (glGetObjectParameteriARB(program, GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            System.err.println(getLogInfo(program));
            return;
        }

        glValidateProgramARB(program);
        if (glGetObjectParameteriARB(program, GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
            System.err.println(getLogInfo(program));
            return;
        }
        /*
         * // Position information will be attribute 0
         * GL20.glBindAttribLocation(program, 0, "gl_Position"); // Color
         * information will be attribute 1 GL20.glBindAttribLocation(program, 1,
         * "gl_Color"); // Textute information will be attribute 2
         * GL20.glBindAttribLocation(program, 2, "gl_TextureCoord");
         */
        use();
        glValidateProgramARB(program);

        // Get matrices uniform locations
        viewProjectionMatrixLocation = glGetUniformLocationARB(program, "viewProjectionMatrix");
        modelMatrixLocation = glGetUniformLocationARB(program, "modelMatrix");
        ambientColorLocation = glGetUniformLocationARB(program, "ambientColor");
        timeLocation = glGetUniformLocationARB(program, "ftime");

        diffuseColorLocation = glGetUniformLocationARB(program, "diffuseColor");
        cameraPositionLocation = glGetUniformLocationARB(program,
                "cameraPosition");
        opaqueLocation = glGetUniformLocationARB(program, "opaque");
        lightEnabledLocation = glGetUniformLocationARB(program, "lightEnabled");

        // material locations
        materialShininessLocation = glGetUniformLocationARB(program,
                "materialShininess");
        materialAlphaLocation = glGetUniformLocationARB(program,
                "materialAlpha");
        materialSpecularLocation = glGetUniformLocationARB(program,
                "materialSpecular");

        for (int i = 0; i < 10; ++i) {
            lightPositionLocation[i] = glGetUniformLocationARB(program,
                    "lightPosition[" + i + "]");
            lightSpecularColorLocation[i] = glGetUniformLocationARB(program,
                    "lightSpecularColor[" + i + "]");

        }
        setMaterialAlpha(1f);
    }

    /**
     * Gets the log info.
     *
     * @param obj the buffer
     * @return the log info
     */
    private static String getLogInfo(int obj) {
        return glGetInfoLogARB(obj,
                glGetObjectParameteriARB(obj, GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    /**
     * Use default shader.
     */
    public void use() {
        glUseProgramObjectARB(program);
    }

    private static final Matrix4f TEMP_VIEW_PROJECTION_MATRIX = new Matrix4f();
    private static final Matrix4f TEMP_MODEL_MATRIX = new Matrix4f();
    private static final float[] TEMP_MATRIX_BUFFER = new float[16];

    public void update(Camera camera) {
        // Upload matrices to the uniform variables
        Matrix4f viewProjectionMatrix = camera.getViewProjectionMatrix(TEMP_VIEW_PROJECTION_MATRIX);
        float[] mat = viewProjectionMatrix.get(TEMP_MATRIX_BUFFER);
        glUniformMatrix4fvARB(viewProjectionMatrixLocation, false, mat);


        final Vector3f cameraPosition = camera.getPosition();
        glUniform3fARB(cameraPositionLocation, cameraPosition.x, cameraPosition.y, cameraPosition.z);

        for (int i = 0; i < 10; ++i) {
            final Vector3f position = lightPosition[i];
            if (position != null) {
                glUniform3fARB(lightPositionLocation[i], position.x,
                        position.y, position.z);
            }

            final Vector3f specularColor = lightSpecularColor[i];
            if (specularColor != null) {
                glUniform3fARB(lightSpecularColorLocation[i], specularColor.x, specularColor.y, specularColor.z);
            }
        }
    }

    /**
     * Sets the material alpha.
     *
     * @param value the new material alpha
     */
    public void setMaterialAlpha(float value) {

        glUniform1fARB(materialAlphaLocation, value);
    }

    /**
     * Sets the material shininess.
     *
     * @param value the new material shininess
     */
    public void setMaterialShininess(float value) {
        glUniform1fARB(materialShininessLocation, value);
    }

    /**
     * Sets the time.
     *
     * @param value the new time
     */
    public void setTime(float value) {
        glUniform1fARB(timeLocation, value);
    }

    /**
     * Sets the ambient color.
     *
     * @param r the r
     * @param g the g
     * @param b the b
     */
    public void setAmbientColor(float r, float g, float b) {
        glUniform3fARB(ambientColorLocation, r, g, b);
    }

    /**
     * Sets the material specular.
     *
     * @param r the r
     * @param g the g
     * @param b the b
     */
    public void setMaterialSpecular(float r, float g, float b) {
        glUniform3fARB(materialSpecularLocation, r, g, b);
    }

    /**
     * Sets the diffuse color.
     *
     * @param r the r
     * @param g the g
     * @param b the b
     */
    public void setDiffuseColor(float r, float g, float b) {
        glUniform3fARB(diffuseColorLocation, r, g, b);
    }

    /**
     * Sets the light position.
     *
     * @param index         the index
     * @param lightPosition the light position
     */
    public void setLightPosition(int index, Vector3f lightPosition) {
        this.lightPosition[index] = lightPosition;
    }

    /**
     * Sets the light color.
     *
     * @param index      the index
     * @param lightColor the light color
     */
    public void setLightColor(int index, Vector3f lightColor) {
        this.lightSpecularColor[index] = lightColor;
    }

    /*
     * With the exception of syntax, setting up vertex and fragment shaders is
     * the same.
     *
     * @param the name and path to the vertex shader
     */

    /**
     * Sets the opaque.
     *
     * @param value the new opaque
     */
    public void setOpaque(Boolean value) {
        if (value)
            glEnable(GL_CULL_FACE);
        else
            glDisable(GL_CULL_FACE);

        glUniform1iARB(opaqueLocation, value ? 1 : 0);
    }

    public void setLightEnabled(Boolean value) {
        glUniform1iARB(lightEnabledLocation, value ? 1 : 0);
    }

    /**
     * Creates the shader.
     *
     * @param filename   the filename
     * @param shaderType the shader type
     * @return the int
     * @throws Exception the exception
     */
    private int createShader(String filename, int shaderType) throws Exception {
        int shader = 0;
        try {
            shader = glCreateShaderObjectARB(shaderType);

            if (shader == 0)
                return 0;

            glShaderSourceARB(shader, readFileAsString(filename));
            glCompileShaderARB(shader);

            if (glGetObjectParameteriARB(shader, GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
                throw new RuntimeException("Error creating shader: "
                        + getLogInfo(shader));

            return shader;
        } catch (Exception exc) {
            glDeleteObjectARB(shader);
            throw exc;
        }
    }

    /**
     * Read file as string.
     *
     * @param filename the filename
     * @return the string
     * @throws Exception the exception
     */
    private String readFileAsString(String filename) throws Exception {
        StringBuilder source = new StringBuilder();
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream stream = classLoader.getResourceAsStream(filename);

        Exception exception = null;
        Scanner reader;
        try {
            reader = new Scanner(stream);

            Exception innerExc = null;
            try {
                while (reader.hasNextLine()) {
                    String line = reader.nextLine();
                    source.append(line).append('\n');
                }
            } catch (Exception exc) {
                exception = exc;
            } finally {
                try {
                    reader.close();
                } catch (Exception exc) {
                    if (innerExc == null)
                        innerExc = exc;
                    else
                        exc.printStackTrace();
                }
            }

            if (innerExc != null)
                throw innerExc;
        } catch (Exception exc) {
            exception = exc;
        } finally {
            if (exception != null)
                throw exception;
        }

        return source.toString();
    }

    /**
     * Push matrix.
     */
    public void setModelMatrix(Matrix4f matrix) {
        use();
        glUniformMatrix4fvARB(modelMatrixLocation, false, matrix.get(TEMP_MATRIX_BUFFER));
    }

    public void reset() {
        setModelMatrix(TEMP_MODEL_MATRIX.identity());
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        setLightEnabled(true);
        setDiffuseColor(1, 1, 1);
        setMaterialAlpha(1f);
        setAmbientColor(0, 0, 0);
        setOpaque(true);
    }

}
