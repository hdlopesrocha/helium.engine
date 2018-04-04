package com.enter4ward.math;

import com.enter4ward.lwjgl.DrawableSphere;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Model3D implements IModel3D {

    protected final List<Group> groups = new ArrayList<>();

    protected final TreeMap<String, Material> materials = new TreeMap<>();
    private final DrawableSphere sphere;

    private BoundingSphere container;
    private final List<Vector3f> lights = new ArrayList<>();

    public Model3D() {
        sphere = new DrawableSphere(true, false);
    }

    public Model3D(String filename, float scale, IBufferBuilder builder,
                   final Quaternionf rot) {
        this();
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(filename);

        try {

            JsonParser jsonParser = new JsonFactory().createParser(stream);
            // loop through the JsonTokens
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String name = jsonParser.getCurrentName();
                switch (name) {
                    case "materials":
                        jsonParser.nextToken();
                        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                            String materialName = jsonParser.getCurrentName();
                            Material currentMaterial = new Material(materialName);
                            materials.put(materialName, currentMaterial);
                            jsonParser.nextToken();
                            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                String key = jsonParser.getCurrentName();
                                jsonParser.nextToken();
                                switch (key) {
                                    case "map_Kd": {
                                        String value = jsonParser.getValueAsString();
                                        currentMaterial.setTexture(value);
                                        break;
                                    }
                                    case "Tf":

                                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                            float x = jsonParser.getFloatValue();
                                            jsonParser.nextToken();
                                            float y = jsonParser.getFloatValue();
                                            jsonParser.nextToken();
                                            float z = jsonParser.getFloatValue();
                                            currentMaterial.Tf = new Float[3];
                                            currentMaterial.Tf[0] = x;
                                            currentMaterial.Tf[1] = y;
                                            currentMaterial.Tf[2] = z;
                                        }

                                        break;
                                    case "Ka":

                                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                            float x = jsonParser.getFloatValue();
                                            jsonParser.nextToken();
                                            float y = jsonParser.getFloatValue();
                                            jsonParser.nextToken();
                                            float z = jsonParser.getFloatValue();
                                            currentMaterial.Ka = new Float[3];
                                            currentMaterial.Ka[0] = x;
                                            currentMaterial.Ka[1] = y;
                                            currentMaterial.Ka[2] = z;
                                        }

                                        break;
                                    case "Kd":

                                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                            float x = jsonParser.getFloatValue();
                                            jsonParser.nextToken();
                                            float y = jsonParser.getFloatValue();
                                            jsonParser.nextToken();
                                            float z = jsonParser.getFloatValue();
                                            currentMaterial.Kd = new Float[3];
                                            currentMaterial.Kd[0] = x;
                                            currentMaterial.Kd[1] = y;
                                            currentMaterial.Kd[2] = z;
                                        }

                                        break;
                                    case "Ks":

                                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                            float x = jsonParser.getFloatValue();
                                            jsonParser.nextToken();
                                            float y = jsonParser.getFloatValue();
                                            jsonParser.nextToken();
                                            float z = jsonParser.getFloatValue();
                                            currentMaterial.Ks = new Float[3];
                                            currentMaterial.Ks[0] = x;
                                            currentMaterial.Ks[1] = y;
                                            currentMaterial.Ks[2] = z;
                                        }

                                        break;
                                    case "illum": {
                                        Float value = jsonParser.getFloatValue();
                                        currentMaterial.illum = value;
                                        break;
                                    }
                                    case "d": {
                                        Float value = jsonParser.getFloatValue();
                                        currentMaterial.d = value;
                                        break;
                                    }
                                    case "Ns": {
                                        Float value = jsonParser.getFloatValue();
                                        currentMaterial.Ns = value;
                                        break;
                                    }
                                    case "sharpness": {
                                        Float value = jsonParser.getFloatValue();
                                        currentMaterial.sharpness = value;
                                        break;
                                    }
                                    case "Ni": {
                                        Float value = jsonParser.getFloatValue();
                                        currentMaterial.Ni = value;
                                        break;
                                    }
                                }
                            }
                        }

                        break;
                    case "lights":
                        jsonParser.nextToken(); // [

                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) { // [[
                            while (jsonParser.nextToken() != JsonToken.END_ARRAY) { // [[(...)]
                                float x = jsonParser.getFloatValue() * scale;
                                jsonParser.nextToken();
                                float y = jsonParser.getFloatValue() * scale;
                                jsonParser.nextToken();
                                float z = jsonParser.getFloatValue() * scale;
                                Vector3f vec = new Vector3f(x, y, z);
                                lights.add(vec);
                            }
                        }

                        break;
                    case "groups":
                        final List<Vector3f> points = new ArrayList<>();
                        jsonParser.nextToken(); // {

                        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                            String groupName = jsonParser.getCurrentName();
                            jsonParser.nextToken(); // [
                            Group currentGroup = new Group(groupName);
                            final List<Vector3f> groupPoints = new ArrayList<>();

                            groups.add(currentGroup);
                            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                // jsonParser.nextToken(); // {
                                IBufferObject currentSubGroup = builder.build();
                                VertexData vertexData = new VertexData();

                                currentGroup.addBuffer(currentSubGroup);
                                // System.out.println("JSON INIT");
                                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                    String key = jsonParser.getCurrentName();

                                    jsonParser.nextToken(); // [
                                    switch (key) {
                                        case "mm":
                                            String mm = jsonParser.getValueAsString();
                                            Material mat = materials.get(mm);
                                            if (mat != null)
                                                currentSubGroup.setMaterial(mat);
                                            break;
                                        case "vv":
                                            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                                float x = jsonParser.getFloatValue()
                                                        * scale;

                                                jsonParser.nextToken();
                                                float y = jsonParser.getFloatValue()
                                                        * scale;
                                                jsonParser.nextToken();
                                                float z = jsonParser.getFloatValue()
                                                        * scale;
                                                Vector3f vec = new Vector3f(x, y, z);

                                                if (rot != null) {
                                                    Util.transform(vec, rot);
                                                }
                                                points.add(vec);
                                                groupPoints.add(vec);
                                                vertexData.addPosition(vec);
                                            }
                                            break;
                                        case "vn":

                                            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                                float x = jsonParser.getFloatValue();
                                                jsonParser.nextToken();
                                                float y = jsonParser.getFloatValue();
                                                jsonParser.nextToken();
                                                float z = jsonParser.getFloatValue();
                                                Vector3f vec = new Vector3f(x, y, z);
                                                if (rot != null) {
                                                    Util.transform(vec, rot);
                                                }
                                                vertexData.addNormal(vec);
                                            }
                                            break;
                                        case "vt":

                                            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                                float x = jsonParser.getFloatValue();
                                                jsonParser.nextToken();
                                                float y = 1f - jsonParser
                                                        .getFloatValue();
                                                vertexData.addTexture(new Vector2f(x, y));
                                            }
                                            break;
                                        case "ii":

                                            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                                short val = jsonParser.getShortValue();
                                                vertexData.addIndex(val);
                                            }
                                            break;
                                    }
                                }
                                // System.out.println("JSON END");
                                currentSubGroup.buildBuffer(vertexData);
                                // System.out.println("JSON BUILD BUFFER");
                            }
                            currentGroup.createFromPoints(groupPoints);
                        }
                        container = new BoundingSphere().createFromPoints(points);

                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public DrawableSphere getSphere() {
        return sphere;
    }

    public Iterable<Group> getGroups() {
        return groups;
    }

    public List<Vector3f> getLights() {
        return lights;
    }

    @Override
    public BoundingSphere getContainer() {
        return container;
    }

}
