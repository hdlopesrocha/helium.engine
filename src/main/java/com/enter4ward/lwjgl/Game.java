package com.enter4ward.lwjgl;


import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

// TODO: Auto-generated Javadoc

public abstract class Game {

    // The window handle
    private long window;
    // Shader variables
    private ShaderProgram program;
    private KeyboardManager keyboardManager = new KeyboardManager();
    private int width, height;

    public Game() {
        System.out.println("java.library.path=" + System.getProperty("java.library.path"));
    }

    public ShaderProgram getProgram() {
        return program;
    }

    public void setProgram(ShaderProgram program) {
        this.program = program;
    }

    public long getTime() {
        return System.nanoTime() / 1000000;
    }

    public void useDefaultShader() {
        program.use();
    }

    public abstract void update(float deltaTime);

    protected abstract void onWindowResized();

    public abstract void draw();

    public void start(int width, int height){
        this.width = width;
        this.height = height;
        init("Space3D");
        setup();
        loop();
    }

    public abstract void setup();

    private void init(String title) {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            keyboardManager.setKeyPressed(key, action != GLFW_RELEASE);
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        createCapabilities();

        try {
            program = new ShaderProgram("vertex.glsl", "fragment.glsl");
        } catch (Exception e) {
            e.printStackTrace();
        }

        glClearColor(0.2f, 0.2f, 0.2f, 0f);
        glViewport(0, 0, width, height);
        glEnable(GL_DEPTH_TEST); // Enables Depth Testing
        glEnable(GL_CULL_FACE);
        // Enable transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback(){
            @Override
            public void invoke(long window, int width, int height){
                Game.this.width = width;
                Game.this.height = height;
                Game.this.onWindowResized();

                glViewport(0, 0, width, height);
            }
        });
    }


    private void loop() {
        long time = getTime();
        long oldTime = time;


        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            // Do a single loop (logic/render)
            time = getTime();
            keyboardManager.update();
            update((time - oldTime) / 1000f);

            // Let the CPU synchronize with the GPU if GPU is tagging behind
            program.use();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            draw();

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            oldTime = time;
        }

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    public KeyboardManager getKeyboardManager() {
        return keyboardManager;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
