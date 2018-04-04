package com.enter4ward.wavefront;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

class MaterialGroup {

    private static final int POSITIONS_LENGTH = 3;

    private static final int NORMALS_LENGTH = 3;

    private static final int TEXTURES_LENGTH = 2;

    private final TreeMap<String, Integer> combinations;

    private final ArrayList<String> positions;

    private final ArrayList<String> textures;

    private final ArrayList<String> normals;

    private final ArrayList<String> indices;

    private final String material;

    private int ii = 0;

    public MaterialGroup(final String mat) {
        combinations = new TreeMap<>();
        positions = new ArrayList<>();
        textures = new ArrayList<>();
        normals = new ArrayList<>();
        indices = new ArrayList<>();
        material = mat;
    }

    public void addPosition(final List<String> list, final int i) {
        positions.add(list.get(i * POSITIONS_LENGTH));
        positions.add(list.get(i * POSITIONS_LENGTH + 1));
        positions.add(list.get(i * POSITIONS_LENGTH + 2));
    }

    public void addNormal(final List<String> list, final int i) {
        normals.add(list.get(i * NORMALS_LENGTH));
        normals.add(list.get(i * NORMALS_LENGTH + 1));
        normals.add(list.get(i * NORMALS_LENGTH + 2));
    }

    public void addNormal() {
        normals.add("0");
        normals.add("0");
        normals.add("0");
    }

    public void addTexture(final List<String> list, final int i) {
        textures.add(list.get(i * TEXTURES_LENGTH));
        textures.add(list.get(i * TEXTURES_LENGTH + 1));
    }

    public void addTexture() {
        textures.add("0");
        textures.add("0");
    }

    public Boolean addIndex(final String vpn) {
        Integer vert = combinations.get(vpn);
        Boolean added = (vert == null);
        if (added) {
            vert = new Integer(ii++);
            combinations.put(vpn, vert);
        }
        indices.add(vert + "");
        return added;
    }

    public JSONObject toJSON() {
        return new JSONObject() {{
            put("mm", material);

            put("vv", new JSONArray() {{
                for (String s : positions) {
                    put(Float.valueOf(s));
                }
            }});

            put("vt", new JSONArray() {{
                for (String s : textures) {
                    put(Float.valueOf(s));
                }
            }});

            put("vn", new JSONArray() {{
                for (String s : normals) {
                    put(Float.valueOf(s));
                }
            }});

            put("ii", new JSONArray() {{
                for (String s : indices) {
                    put(Integer.valueOf(s));
                }
            }});
        }};
    }

}
