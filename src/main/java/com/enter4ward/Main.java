package com.enter4ward;

import com.enter4ward.lwjgl.*;
import com.enter4ward.math.*;
import org.joml.Intersectionf;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class Main extends Game {

    private static final int NUMBER_OF_OBJECTS = 0;
    //private static final int NUMBER_OF_OBJECTS = 500000;
    //private static final int NUMBER_OF_OBJECTS = 1000000;

    private static final BoundingSphere TEMP_BOUNDING_SPHERE = new BoundingSphere();
    private static final BoundingSphere TEMP_BOUNDING_SPHERE_2 = new BoundingSphere();
    private static final Vector3f TEMP_MIN = new Vector3f();
    private static final Vector3f TEMP_MAX = new Vector3f();
    private static final Random RANDOM = new Random();

    private static final IBufferBuilder bufferBuilder = () -> new BufferObject(false);
    public final int MAP_SIZE = 2048;
    public final float DISTANCE = 32;
    public final float SPEED = 0.5f;
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
    private Octree voxel;
    private int voxelLevelSelector = 0;

    public Main() {

    }

    List<BufferObject> voxelBuffers = new ArrayList<>();

    public static void main(String[] args) {
        new Main().start(1280, 720);
    }

    @Override
    public void setup() {
        camera = new Camera(getWidth(), getHeight(), 0.1f, 256);

        space = new Space(16);
        cubeModel = new DrawableBox();

        boxModel = new LWJGLModel3D("box.json", 1f, bufferBuilder);
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

        new Thread(() -> {
            for (int i = 0; i < NUMBER_OF_OBJECTS; ++i) {
                insertRandomBox();
            }
        }).start();


        camera.lookAt(
                new Vector3f(48, 24, 48),
                new Vector3f(0, 0, 0),
                new Vector3f(0, 1, 0)
        );

        setupVoxel();
        getProgram().setAmbientColor(0, 0, 0);
        getProgram().setDiffuseColor(1, 1, 1);
        getProgram().setMaterialShininess(1000);
        getProgram().setLightColor(0, new Vector3f(1, 1, 1));
        getProgram().setLightPosition(0, new Vector3f(-500, 200, 1000));
    }

    private static Vector3f TEMP_VECTOR = new Vector3f();
    private static BoundingCube TEMP_BOX = new BoundingCube();

    public ContainmentType intersects(float x, float y, float z, float l, BoundingSphere sphere) {
        int count = 0;
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                for (int k = 0; k < 2; ++k) {
                    TEMP_VECTOR.set(x, y, z).add(i * l, j * l, k * l);
                    if (sphere.contains(TEMP_VECTOR)) {
                        ++count;
                    }
                }
            }
        }
        boolean contains = count == 8;
        if (contains) {
            return ContainmentType.Contains;
        }
        boolean intersects = Intersectionf.testAabSphere(
                x, y, z, x + l, y + l, z + l,
                sphere.x, sphere.y, sphere.z, sphere.r * sphere.r);

        return intersects ? ContainmentType.Intersects : ContainmentType.Disjoint;
    }

    private ContainmentType contains(float x, float y, float z, float l, BoundingSphere sphere) {
        TEMP_BOX.setMin(x, y, z);
        TEMP_BOX.setLen(l);
        return TEMP_BOX.contains(sphere);
    }

    public void setupVoxel() {
        voxel = new Octree(1f);

        BoundingSphere sphere = new BoundingSphere(new Vector3f(), 16);
        BoundingSphere sphere2 = new BoundingSphere(new Vector3f(16, 0, 0), 8);


        voxel.add(sphere, new Octree.MatchHandler() {
            @Override
            public ContainmentType objectContains(float x, float y, float z, float l) {
                return Main.this.intersects(x, y, z, l, sphere);
            }

            @Override
            public ContainmentType nodeContains(float x, float y, float z, float l) {
                return Main.this.contains(x, y, z, l, sphere);
            }
        });

        voxel.remove(sphere2, new Octree.MatchHandler() {
            @Override
            public ContainmentType objectContains(float x, float y, float z, float l) {
                return Main.this.intersects(x, y, z, l, sphere2);
            }

            @Override
            public ContainmentType nodeContains(float x, float y, float z, float l) {
                return Main.this.contains(x, y, z, l, sphere2);
            }
        });

        List<VertexData> vertexDataList = new ArrayList<>();
        voxel.extractTriangles((level, ix, iy, iz, jx, jy, jz, kx, ky, kz) -> {
            VertexData vertexData;
            while (level >= vertexDataList.size()) {
                vertexData = new VertexData();
                vertexDataList.add(vertexData);
            }
            vertexData = vertexDataList.get(level);


            Vector3f normal = new Vector3f(ix - jx, iy - jy, iz - jz).cross(ix - kx, iy - ky, iz - kz);

            vertexData.addIndex(vertexData.getIndexData().size());
            vertexData.addPosition(new Vector3f(ix, iy, iz));
            vertexData.addNormal(normal);
            vertexData.addTexture(new Vector2f());

            vertexData.addIndex(vertexData.getIndexData().size());
            vertexData.addPosition(new Vector3f(jx, jy, jz));
            vertexData.addNormal(normal);
            vertexData.addTexture(new Vector2f());

            vertexData.addIndex(vertexData.getIndexData().size());
            vertexData.addPosition(new Vector3f(kx, ky, kz));
            vertexData.addNormal(normal);
            vertexData.addTexture(new Vector2f());
        });
        int vd = 0;
        for (VertexData vertexData : vertexDataList) {
            System.out.println("Processing voxel level:" + vd++);
            BufferObject voxelBuffer = (BufferObject) bufferBuilder.build();
            voxelBuffer.buildBuffer(vertexData.compress());
            voxelBuffers.add(voxelBuffer);
            vertexData.clear();
        }

        System.gc();
    }

    public void insertRandomBox() {
        synchronized (space) {
            new Object3D(new Vector3f(
                    (RANDOM.nextFloat() - 0.5f) * MAP_SIZE,
                    (RANDOM.nextFloat() - 0.5f) * MAP_SIZE,
                    (RANDOM.nextFloat() - 0.5f) * MAP_SIZE), boxModel) {{
                setNode(space.insert(getBoundingSphere(TEMP_BOUNDING_SPHERE), this));
            }};
        }
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
        synchronized (space) {
            box.setNode(space.update(box.getBoundingSphere(TEMP_BOUNDING_SPHERE), box.getNode(), box));
        }
        hits.clear();
        tests.clear();
        BoundingSphere boxSphere = box.getBoundingSphere(TEMP_BOUNDING_SPHERE);
        synchronized (space) {
            space.handleObjectCollisions(boxSphere, collisionHandler);
        }
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


        if (getKeyboardManager().hasKeyReleased(GLFW.GLFW_KEY_9)) {
            voxelLevelSelector = (voxelLevelSelector - 1 + voxelBuffers.size()) % voxelBuffers.size();
        }

        if (getKeyboardManager().hasKeyReleased(GLFW.GLFW_KEY_0)) {
            voxelLevelSelector = (voxelLevelSelector + 1 + voxelBuffers.size()) % voxelBuffers.size();
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
        synchronized (space) {
            space.handleVisibleObjects(camera, visibleObjectHandler);
            space.handleVisibleObjects(camera, visibleWireframeHandler);
        }
        getProgram().reset();
        getProgram().setMaterialColor(1, 1, 1);
     /*   getProgram().setMaterialColor(1, 0, 0);
        getProgram().setAmbientColor(1f, 0f, 0f);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glDisable(GL_CULL_FACE);
        */
        BufferObject voxelo = voxelBuffers.get(voxelLevelSelector);
        if (voxelo.canDraw()) {
            voxelo.draw(getProgram());
        }


        getProgram().reset();

        getProgram().setLightEnabled(false);


        voxel.handleVisibleObjects(camera, (x, y, z, l, lvl, obj) -> {

            Octree.Node node = (Octree.Node) obj;
            if (lvl == 7) {
                if (node.getObjectContains() == ContainmentType.Contains) {
                    getProgram().setMaterialColor(0, 1, 0);
                } else  if (node.getObjectContains() == ContainmentType.Intersects) {
                    getProgram().setMaterialColor(0, 0, 1);
                } else {
                    getProgram().setMaterialColor(1, 0, 0);
                }
                TEMP_MIN.set(x, y, z);
                TEMP_MAX.set(x + l, y + l, z + l);
                cubeModel.draw(getProgram(), TEMP_MIN, TEMP_MAX);
            }

        });


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
                    getProgram().setAmbientColor(1f, 1f, 1f);
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

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