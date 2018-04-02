package com.enter4ward;

import com.enter4ward.lwjgl.*;
import com.enter4ward.math.BoundingSphere;
import com.enter4ward.math.Camera;
import com.enter4ward.math.IBufferBuilder;
import com.enter4ward.math.Space;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;


public class Main extends Game {

    private static final int NUMBER_OF_OBJECTS = 0;
    //private static final int NUMBER_OF_OBJECTS = 500000;
    //private static final int NUMBER_OF_OBJECTS = 1000000;
    private static final Random RANDOM = new Random();
    /**
     * The buffer builder.
     */
    private static IBufferBuilder bufferBuilder = () -> new BufferObject(true);
    public final int MAP_SIZE = 2048;
    public final float DISTANCE = 32;
    public final float SPEED = 0.5f;
    DrawableSphere sphere;
    LWJGLModel3D boxModel;
    Object3D box;
    Object hit;
    List<Object> tests = new ArrayList<>();
    boolean hyperCubeMode = false;
    boolean boundingSpeheres = false;
    int visibleObjects = 0;
    Camera camera;
    float time = 0;


    /**
     * The space.
     */
    private Space space;
    private DrawableBox cubeModel;
    private Vector3f cameraPos;

    public Main(int w, int h) {
        super(w, h);
    }

    public static void main(String[] args) {
        new Main(1280, 720);
    }

    @Override
    public void setup() {
        camera = new Camera(1280, 720, 0.1f, 256);
        cameraPos = new Vector3f(48, 24, 48);
        space = new Space(16);
        cubeModel = new DrawableBox();

        try {
            boxModel = new LWJGLModel3D("box.json", 1f, bufferBuilder);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        sphere = new DrawableSphere();
        box = new Object3D(new Vector3f(0, 0, 0), boxModel);
        box.setNode(space.insert(box.getBoundingSphere(), box));

        new Object3D(new Vector3f(0, 0, 0), boxModel) {{
            space.insert(getBoundingSphere(), this);
        }};
        new Object3D(new Vector3f(DISTANCE, 0, 0), boxModel) {{
            space.insert(getBoundingSphere(), this);
        }};
        new Object3D(new Vector3f(-DISTANCE, 0, 0), boxModel) {{
            space.insert(getBoundingSphere(), this);
        }};
        new Object3D(new Vector3f(0, 0, DISTANCE), boxModel) {{
            space.insert(getBoundingSphere(), this);
        }};
        new Object3D(new Vector3f(0, 0, -DISTANCE), boxModel) {{
            space.insert(getBoundingSphere(), this);
        }};

        for (int i = 0; i < NUMBER_OF_OBJECTS; ++i) {
            new Object3D(new Vector3f((RANDOM.nextFloat() - 0.5f) * MAP_SIZE, (RANDOM.nextFloat() - 0.5f) * MAP_SIZE, (RANDOM.nextFloat() - 0.5f) * MAP_SIZE), boxModel) {{
                space.insert(getBoundingSphere(), this);
            }};
        }


        camera.lookAt(cameraPos, new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
        getProgram().setLightPosition(0, new Vector3f(3, 3, 3));
        getProgram().setAmbientColor(0, 0, 0);
        getProgram().setDiffuseColor(1, 1, 1);
        getProgram().setMaterialShininess(1000);
        getProgram().setLightColor(0, new Vector3f(1, 1, 1));
        getProgram().setLightPosition(0, new Vector3f(128, 128, 128));
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        getProgram().setTime(time);

        Vector3f boxPos = new Vector3f(DISTANCE * (float) Math.sin(SPEED * time), 0f, DISTANCE * (float) Math.cos(SPEED * time));
        box.setPosition(boxPos);
        box.setNode(space.update(box.getBoundingSphere(), box.getNode(), box));
        tests = new ArrayList<>();
        hit = null;
        tests.clear();
        BoundingSphere boxSphere = box.getBoundingSphere();
        space.handleObjectCollisions(boxSphere, obj -> {
            Object3D o3d = (Object3D) obj;

            if (box != o3d && boxSphere.intersects(o3d.getBoundingSphere())) {
                hit = o3d;
            } else {
                tests.add(o3d);
            }
        });

        // moving.insert(space);
        float tSense = 0.2f;
        float rSense = 0.01f;

        if (isKeyDown(GLFW.GLFW_KEY_LEFT)) {
            camera.rotate(0, 1, 0, -rSense);
        }
        if (isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
            camera.rotate(0, 1, 0, rSense);
        }
        if (isKeyDown(GLFW.GLFW_KEY_UP)) {
            camera.rotate(1, 0, 0, -rSense);
        }
        if (isKeyDown(GLFW.GLFW_KEY_DOWN)) {
            camera.rotate(1, 0, 0, rSense);
        }


        if (isKeyDown(GLFW.GLFW_KEY_Q)) {
            camera.rotate(0, 0, 1, -rSense);
        }

        if (isKeyDown(GLFW.GLFW_KEY_E)) {
            camera.rotate(0, 0, 1, rSense);
        }

        if (isKeyDown(GLFW.GLFW_KEY_W)) {
            camera.move(tSense, 0, 0);
        }

        if (isKeyDown(GLFW.GLFW_KEY_S)) {
            camera.move(-tSense, 0, 0);
        }

        if (isKeyDown(GLFW.GLFW_KEY_A)) {
            camera.move(0, 0, tSense);
        }

        if (isKeyDown(GLFW.GLFW_KEY_D)) {
            camera.move(0, 0, -tSense);
        }

        if (isKeyDown(GLFW.GLFW_KEY_H)) {
            this.hyperCubeMode = !this.hyperCubeMode;
        }

        if (isKeyDown(GLFW.GLFW_KEY_B)) {
            this.boundingSpeheres = !this.boundingSpeheres;
        }
    }

    @Override
    public void draw() {
        getProgram().update(camera);

        getProgram().setLightPosition(0, box.getPosition());
        getProgram().use();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        visibleObjects = 0;
        useDefaultShader();
        space.handleVisibleObjects(camera, obj -> {
            getProgram().reset();
            getProgram().setMaterialAlpha(1f);
            getProgram().setOpaque(true);

            if (obj instanceof Space.Node) {
                Space.Node node = (Space.Node) obj;
                if (node.containerSize() > 0 && node.contains(box)) {
                    getProgram().setAmbientColor(0f, 1f, 0f);
                } else {
                    return;
                }

                Vector3f min = new Vector3f(node.getMinX(), node.getMinY(), node.getMinZ());
                Vector3f max = new Vector3f(node.getMaxX(), node.getMaxY(), node.getMaxZ());
                cubeModel.draw(getProgram(), min, max);
            } else if (obj instanceof Object3D) {

                Object3D obj3d = (Object3D) obj;
                if (obj3d == hit) {
                    getProgram().setAmbientColor(1f, 0f, 0f);
                } else if (obj3d == box) {
                    getProgram().setAmbientColor(0f, 0f, 1f);
                } else if (tests.contains(obj3d)) {
                    getProgram().setAmbientColor(1f, 1f, 0f);
                }
                ++visibleObjects;
                obj3d.draw(getProgram(), camera);
                if (boundingSpeheres) {
                    getProgram().setOpaque(false);
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    sphere.draw(getProgram(), obj3d.getBoundingSphere());
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                }
            }
        });

        space.handleVisibleObjects(camera, obj -> {
            getProgram().reset();
            getProgram().setMaterialAlpha(.2f);
            getProgram().setOpaque(false);

            if (obj instanceof Space.Node) {
                Space.Node node = (Space.Node) obj;
                if (node.containerSize() == 0) {
                    getProgram().setAmbientColor(1f, 1f, 1f);
                } else {
                    return;
                }

                if (hyperCubeMode) {
                    Vector3f min = new Vector3f(node.getMinX(), node.getMinY(), node.getMinZ());
                    float shiftX = (float) (Math.sin(node.getMaxX()) * Math.sin(time + node.getMaxX()));
                    float shiftY = (float) (Math.cos(node.getMaxY()) * Math.sin(time + node.getMaxY()));
                    float shiftZ = (float) (Math.cos(node.getMaxZ()) * Math.cos(time + node.getMaxZ()));

                    Vector3f max = new Vector3f(
                            node.getMaxX() + shiftX,
                            node.getMaxY() + shiftY,
                            node.getMaxZ() + shiftZ);
                    cubeModel.draw(getProgram(), min, max);
                } else {
                    Vector3f min = new Vector3f(node.getMinX(), node.getMinY(), node.getMinZ());
                    Vector3f max = new Vector3f(node.getMaxX(), node.getMaxY(), node.getMaxZ());
                    cubeModel.draw(getProgram(), min, max);
                }
            }
        });

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        sphere.draw(getProgram(), new BoundingSphere(box.getPosition(), 1));
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        //   getFont().drawString(0,0, "Visible objects: "+visibleObjects, Color.white);
        getProgram().setAmbientColor(0f, 0f, 0f);
        glUseProgram(0);
        // setTitle();
        System.out.println(visibleObjects);

    }

}