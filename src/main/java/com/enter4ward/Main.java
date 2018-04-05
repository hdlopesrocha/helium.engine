package com.enter4ward;

import com.enter4ward.lwjgl.*;
import com.enter4ward.math.*;
import org.joml.Intersectionf;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Main extends Game {

    private static final int NUMBER_OF_OBJECTS = 0;
    //private static final int NUMBER_OF_OBJECTS = 500000;
    //private static final int NUMBER_OF_OBJECTS = 1000000;

    private static final BoundingSphere TEMP_BOUNDING_SPHERE = new BoundingSphere();
    private static final BoundingSphere TEMP_BOUNDING_SPHERE_2 = new BoundingSphere();
    private static final Vector3f TEMP_MIN = new Vector3f();
    private static final Vector3f TEMP_MAX = new Vector3f();
    private static final Random RANDOM = new Random();

    private static final IBufferBuilder bufferBuilder = () -> new BufferObject(true);
    public final int MAP_SIZE = 2048;
    public final float DISTANCE = 32;
    public final float SPEED = 0.5f;
    DrawableSphere sphere;
    LWJGLModel3D boxModel;
    Object3D box;
    final List<Object> hits = new ArrayList<>();
    final List<Object> tests = new ArrayList<>();
    boolean hyperCubeMode = false;
    boolean boundingSpheres = false;
    boolean boundingBoxes = true;
    boolean rotationEnabled = true;
    int visibleObjects = 0;
    Camera camera;
    float time = 0;
    float cameraMovementVelocity = 0.2f;
    float cameraRotationVelocity = 0.01f;
    final Quaternionf objRotation = new Quaternionf().fromAxisAngleRad(3f, 7f, 11f, (float) Math.PI / 77f);

    private Space space;
    private DrawableBox cubeModel;
    private LWJGLModel3D skyModel;
    private Object3D sky;
    private Octree voxel = new Octree(0.5f);


    public Main() {
        BoundingSphere sphere = new BoundingSphere(new Vector3f(), 16);
        Vector3f vec = new Vector3f();

        voxel.build(sphere, (x, y, z, l) -> {

            boolean intersects = Intersectionf.testAabSphere(
                    x,y,z,x+l,y+l,z+l,
                    sphere.x,sphere.y,sphere.z,sphere.r*sphere.r);

            int count =0;
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 2; ++j) {
                    for (int k = 0; k < 2; ++k) {
                        vec.set(x, y, z).add(i * l,j * l,k * l);
                        if(sphere.contains(vec)){
                            ++count;
                        }
                    }
                }
            }
            boolean contains =  count ==8;
            return intersects && !contains;
        });
    }

    public static void main(String[] args) {
        new Main().start(1280, 720);
    }

    @Override
    public void setup() {
        camera = new Camera(getWidth(), getHeight(), 0.1f, 256);

        space = new Space(16);
        cubeModel = new DrawableBox();

        boxModel = new LWJGLModel3D("box.json", 1f, bufferBuilder);

        sphere = new DrawableSphere(true, false);
        skyModel = new LWJGLModel3D("sphere.json", 10f, bufferBuilder);
        sky = new Object3D(new Vector3f(1, 1, 1), skyModel);

        box = new Object3D(new Vector3f(0, 0, 0), boxModel);
        box.setNode(space.insert(box.getBoundingSphere(TEMP_BOUNDING_SPHERE), box));

        new Object3D(new Vector3f(0, 0, 0), boxModel) {{
            setNode(space.insert(getBoundingSphere(TEMP_BOUNDING_SPHERE), this));
        }};
        new Object3D(new Vector3f(DISTANCE, 0, 0), boxModel) {{
            setNode(space.insert(getBoundingSphere(TEMP_BOUNDING_SPHERE), this));
        }};
        new Object3D(new Vector3f(-DISTANCE, 0, 0), boxModel) {{
            setNode(space.insert(getBoundingSphere(TEMP_BOUNDING_SPHERE), this));
        }};
        new Object3D(new Vector3f(0, 0, DISTANCE), boxModel) {{
            setNode(space.insert(getBoundingSphere(TEMP_BOUNDING_SPHERE), this));
        }};
        new Object3D(new Vector3f(0, 0, -DISTANCE), boxModel) {{
            setNode(space.insert(getBoundingSphere(TEMP_BOUNDING_SPHERE), this));
        }};

        for (int i = 0; i < NUMBER_OF_OBJECTS; ++i) {
            insertRandomBox();
        }

        camera.lookAt(
                new Vector3f(48, 24, 48),
                new Vector3f(0, 0, 0),
                new Vector3f(0, 1, 0)
        );
        getProgram().setAmbientColor(0, 0, 0);
        getProgram().setDiffuseColor(1, 1, 1);
        getProgram().setMaterialShininess(1000);
        getProgram().setLightColor(0, new Vector3f(1, 1, 1));
        getProgram().setLightPosition(0, new Vector3f(-5, 2, 10));
    }

    public void insertRandomBox() {
        new Object3D(new Vector3f(
                (RANDOM.nextFloat() - 0.5f) * MAP_SIZE,
                (RANDOM.nextFloat() - 0.5f) * MAP_SIZE,
                (RANDOM.nextFloat() - 0.5f) * MAP_SIZE), boxModel) {{
            setNode(space.insert(getBoundingSphere(TEMP_BOUNDING_SPHERE), this));
        }};
    }

    public void insertRandomBoxInsideSphere(BoundingSphere sphere) {
        float r = RANDOM.nextFloat() * sphere.r;
        float t = (float) (RANDOM.nextFloat() * Math.PI);
        float s = (float) (2 * RANDOM.nextFloat() * Math.PI);

        new Object3D(new Vector3f(
                sphere.x + (float) (r * Math.sin(t) * Math.cos(s)),
                sphere.y + (float) (r * Math.sin(t) * Math.sin(s)),
                sphere.z + (float) (r * Math.cos(t))
        ), boxModel) {{
            setNode(space.insert(getBoundingSphere(TEMP_BOUNDING_SPHERE), this));
        }};
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        getProgram().setTime(time);

        box.getPosition().set(DISTANCE * (float) Math.sin(SPEED * time), 0f, DISTANCE * (float) Math.cos(SPEED * time));
        box.setNode(space.update(box.getBoundingSphere(TEMP_BOUNDING_SPHERE), box.getNode(), box));
        hits.clear();
        tests.clear();
        BoundingSphere boxSphere = box.getBoundingSphere(TEMP_BOUNDING_SPHERE);
        space.handleObjectCollisions(boxSphere, collisionHandler);

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_LEFT)) {
            camera.rotate(0, 1, 0, -cameraRotationVelocity);
        }
        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
            camera.rotate(0, 1, 0, cameraRotationVelocity);
        }
        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_UP)) {
            camera.rotate(1, 0, 0, -cameraRotationVelocity);
        }
        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_DOWN)) {
            camera.rotate(1, 0, 0, cameraRotationVelocity);
        }

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_Q)) {
            camera.rotate(0, 0, 1, -cameraRotationVelocity);
        }

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_E)) {
            camera.rotate(0, 0, 1, cameraRotationVelocity);
        }

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_W)) {
            camera.move(cameraMovementVelocity, 0, 0);
        }

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_S)) {
            camera.move(-cameraMovementVelocity, 0, 0);
        }

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_A)) {
            camera.move(0, 0, cameraMovementVelocity);
        }

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_D)) {
            camera.move(0, 0, -cameraMovementVelocity);
        }

        if (getKeyboardManager().hasKeyReleased(GLFW.GLFW_KEY_1)) {
            this.boundingSpheres = !this.boundingSpheres;
        }

        if (getKeyboardManager().hasKeyReleased(GLFW.GLFW_KEY_2)) {
            this.boundingBoxes = !this.boundingBoxes;
        }

        if (getKeyboardManager().hasKeyReleased(GLFW.GLFW_KEY_3)) {
            this.hyperCubeMode = !this.hyperCubeMode;
        }

        if (getKeyboardManager().hasKeyReleased(GLFW.GLFW_KEY_4)) {
            this.rotationEnabled = !this.rotationEnabled;
        }

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_5)) {
            this.cameraMovementVelocity -= 0.01f;
        }

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_6)) {
            this.cameraMovementVelocity += 0.01f;
        }

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_7)) {
            this.cameraRotationVelocity -= 0.0005f;
        }

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_8)) {
            this.cameraRotationVelocity += 0.0005f;
        }

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_R)) {
            for (int i = 0; i < 256; ++i) {
                insertRandomBox();
            }
        }

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_T)) {
            for (int i = 0; i < 16; ++i) {
                insertRandomBoxInsideSphere(new BoundingSphere(camera.getPosition(), 64));
            }
        }

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_Y)) {
            for (int i = 0; i < 8; ++i) {
                insertRandomBoxInsideSphere(new BoundingSphere(camera.getPosition(), 4));
            }
        }

        if (getKeyboardManager().isKeyDown(GLFW.GLFW_KEY_U)) {
            space.clear();
            box.setNode(space.insert(box.getBoundingSphere(TEMP_BOUNDING_SPHERE), box));
        }

        sky.getPosition().set(camera.getPosition());
    }

    @Override
    protected void onWindowResized() {
        camera.calculateProjection(getWidth(), getHeight());
    }

    @Override
    public void draw() {
        getProgram().update(camera);
        getProgram().use();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        visibleObjects = 0;
        useDefaultShader();

        // DRAW SKY

        getProgram().reset();
        getProgram().setLightEnabled(false);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        sky.draw(getProgram(), camera);

        // DRAW OBJECTS
        space.handleVisibleObjects(camera, visibleObjectHandler);
        space.handleVisibleObjects(camera, visibleWireframeHandler);
        voxel.handleVisibleObjects(camera, new VisibleVoxelHandler() {
            @Override
            public void onObjectVisible(float x, float y, float z, float l, Object obj) {
                 if (!voxel.canSplit(l)) {
                    getProgram().setAmbientColor(1f, 0f, 0f);
                    getProgram().setMaterialAlpha(1f);
                    getProgram().setOpaque(true);

                    TEMP_MIN.set(x,y,z);
                    TEMP_MAX.set(x+l,y+l,z+l);
                    glBindTexture(GL_TEXTURE_2D, 0);
                    getProgram().setLightEnabled(false);
                    cubeModel.draw(getProgram(), TEMP_MIN, TEMP_MAX);
                 }
            }
        });
        glUseProgram(0);
    }

    private ObjectCollisionHandler collisionHandler = new ObjectCollisionHandler() {
        @Override
        public void onObjectCollision(BoundingSphere sphere, Object obj) {
            Object3D o3d = (Object3D) obj;
            if (box != o3d && sphere.intersects(o3d.getBoundingSphere(TEMP_BOUNDING_SPHERE_2))) {
                hits.add(o3d);
            } else {
                tests.add(o3d);
            }
        }
    };

    private VisibleObjectHandler visibleObjectHandler = obj -> {
        getProgram().reset();
        if (obj instanceof Space.Node && boundingBoxes) {
            Space.Node node = (Space.Node) obj;
            if (node.containerSize() > 0 && node.contains(box)) {
                getProgram().setAmbientColor(0f, 1f, 0f);
                getProgram().setMaterialAlpha(1f);
                getProgram().setOpaque(true);

                TEMP_MIN.set(node.getMinX(), node.getMinY(), node.getMinZ());
                TEMP_MAX.set(node.getMaxX(), node.getMaxY(), node.getMaxZ());
                glBindTexture(GL_TEXTURE_2D, 0);
                getProgram().setLightEnabled(false);
                cubeModel.draw(getProgram(), TEMP_MIN, TEMP_MAX);
            }
        } else if (obj instanceof Object3D) {

            Object3D obj3d = (Object3D) obj;
            if (camera.contains(obj3d.getBoundingSphere(TEMP_BOUNDING_SPHERE)) != ContainmentType.Disjoint) {
                if (hits.contains(obj3d)) {
                    getProgram().setAmbientColor(1f, 0f, 0f);
                } else if (obj3d == box) {
                    getProgram().setAmbientColor(0f, 0f, 1f);
                } else if (tests.contains(obj3d)) {
                    getProgram().setAmbientColor(1f, 1f, 0f);
                } else {
                }
                if (rotationEnabled) {
                    obj3d.getRotation().mul(objRotation).normalize();
                }
                ++visibleObjects;
                obj3d.draw(getProgram(), camera);
                if (boundingSpheres) {
                    getProgram().setLightEnabled(false);
                    obj3d.drawBoundingSpheres(getProgram(), camera);
                }
            }
        }
    };


    private VisibleObjectHandler visibleWireframeHandler = obj -> {


        getProgram().reset();

        if (obj instanceof Space.Node && boundingBoxes) {
            getProgram().setMaterialAlpha(.2f);
            getProgram().setOpaque(false);

            Space.Node node = (Space.Node) obj;
            getProgram().setAmbientColor(1f, 1f, 1f);
            getProgram().setLightEnabled(false);

            if (hyperCubeMode) {
                float hyperFactor = 2;
                TEMP_MIN.set(node.getMinX(), node.getMinY(), node.getMinZ());
                float shiftX = hyperFactor * (float) (Math.sin(node.getMaxX()) * Math.sin(time + node.getMaxX()));
                float shiftY = hyperFactor * (float) (Math.cos(node.getMaxY()) * Math.sin(time + node.getMaxY()));
                float shiftZ = hyperFactor * (float) (Math.cos(node.getMaxZ()) * Math.cos(time + node.getMaxZ()));
                TEMP_MAX.set(
                        node.getMaxX() + shiftX,
                        node.getMaxY() + shiftY,
                        node.getMaxZ() + shiftZ);
            } else {
                TEMP_MIN.set(node.getMinX(), node.getMinY(), node.getMinZ());
                TEMP_MAX.set(node.getMaxX(), node.getMaxY(), node.getMaxZ());
            }
            glBindTexture(GL_TEXTURE_2D, 0);
            cubeModel.draw(getProgram(), TEMP_MIN, TEMP_MAX);
        }
    };
}