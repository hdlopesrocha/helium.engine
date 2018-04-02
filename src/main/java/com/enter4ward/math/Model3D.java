package com.enter4ward.math;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

// TODO: Auto-generated Javadoc

/**
 * The Class Model3D.
 */
public class Model3D implements IModel3D {

    protected List<Group> groups = new ArrayList<Group>();

    protected TreeMap<String, Material> materials = new TreeMap<String, Material>();

    private BoundingSphere container;
    private List<Vector3f> lights = new ArrayList<Vector3f>();

    public Model3D() {

    }

    public Model3D(String filename, float scale, IBufferBuilder builder,
                   final Quaternionf rot) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(filename);


        try {

            JsonParser jsonParser = new JsonFactory().createParser(stream);
            // loop through the JsonTokens
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String name = jsonParser.getCurrentName();
                if ("materials".equals(name)) {
                    jsonParser.nextToken();
                    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                        String materialName = jsonParser.getCurrentName();
                        Material currentMaterial = new Material(materialName);
                        materials.put(materialName, currentMaterial);
                        jsonParser.nextToken();
                        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                            String key = jsonParser.getCurrentName();
                            jsonParser.nextToken();
                            if ("map_Kd".equals(key)) {
                                String value = jsonParser.getValueAsString();
                                currentMaterial.setTexture(value);
                            } else if ("Tf".equals(key)) {

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

                            } else if ("Ka".equals(key)) {

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

                            } else if ("Kd".equals(key)) {

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

                            } else if ("Ks".equals(key)) {

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

                            } else if ("illum".equals(key)) {
                                Float value = jsonParser.getFloatValue();
                                currentMaterial.illum = value;
                            } else if ("d".equals(key)) {
                                Float value = jsonParser.getFloatValue();
                                currentMaterial.d = value;
                            } else if ("Ns".equals(key)) {
                                Float value = jsonParser.getFloatValue();
                                currentMaterial.Ns = value;
                            } else if ("sharpness".equals(key)) {
                                Float value = jsonParser.getFloatValue();
                                currentMaterial.sharpness = value;
                            } else if ("Ni".equals(key)) {
                                Float value = jsonParser.getFloatValue();
                                currentMaterial.Ni = value;
                            }
                        }
                    }

                } else if ("lights".equals(name)) {
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

                } else if ("groups".equals(name)) {
                    final List<Vector3f> points = new ArrayList<Vector3f>();
                    jsonParser.nextToken(); // {
                    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                        String groupName = jsonParser.getCurrentName();
                        jsonParser.nextToken(); // [
                        Group currentGroup = new Group(groupName);
                        final List<Vector3f> groupPoints = new ArrayList<Vector3f>();

                        groups.add(currentGroup);
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                            // jsonParser.nextToken(); // {
                            IBufferObject currentSubGroup = builder.build();
                            currentGroup.addBuffer(currentSubGroup);
                            // System.out.println("JSON INIT");
                            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                String key = jsonParser.getCurrentName();

                                jsonParser.nextToken(); // [
                                if ("mm".equals(key)) {
                                    String mm = jsonParser.getValueAsString();
                                    Material mat = materials.get(mm);
                                    if (mat != null)
                                        currentSubGroup.setMaterial(mat);
                                } else if ("vv".equals(key)) {
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
                                        currentSubGroup.addVertex(vec.x,
                                                vec.y, vec.z);
                                    }
                                } else if ("vn".equals(key)) {

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
                                        currentSubGroup.addNormal(vec.x,
                                                vec.y, vec.z);
                                    }
                                } else if ("vt".equals(key)) {

                                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                        float x = jsonParser.getFloatValue();
                                        jsonParser.nextToken();
                                        float y = 1f - jsonParser
                                                .getFloatValue();
                                        currentSubGroup.addTexture(x, y);
                                    }
                                } else if ("ii".equals(key)) {

                                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                        short val = jsonParser.getShortValue();
                                        currentSubGroup.addIndex(val);
                                    }
                                }
                            }
                            // System.out.println("JSON END");
                            currentSubGroup.buildBuffer();
                            // System.out.println("JSON BUILD BUFFER");
                        }
                        currentGroup.createFromPoints(groupPoints);
                    }
                    container = new BoundingSphere().createFromPoints(points);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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
