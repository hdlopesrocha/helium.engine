package com.enter4ward.wavefront;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * The Class MaterialGroup.
 */
class MaterialGroup {

    /**
     * The Constant POSITIONS_LENGTH.
     */
    private static final int POSITIONS_LENGTH = 3;

    /**
     * The Constant NORMALS_LENGTH.
     */
    private static final int NORMALS_LENGTH = 3;

    /**
     * The Constant TEXTURES_LENGTH.
     */
    private static final int TEXTURES_LENGTH = 2;

    /**
     * The indexs.
     */
    private TreeMap<String, Integer> combinations;

    /**
     * The vv_a.
     */
    private ArrayList<String> positions;

    /**
     * The vt_a.
     */
    private ArrayList<String> textures;

    /**
     * The vn_a.
     */
    private ArrayList<String> normals;

    /**
     * The ii_a.
     */
    private ArrayList<String> indices;

    /**
     * The material.
     */
    private String material;

    /**
     * The ii.
     */
    private int ii = 0;

    /**
     * Instantiates a new material group.
     *
     * @param mat the mat
     */
    public MaterialGroup(final String mat) {
        combinations = new TreeMap<>();
        positions = new ArrayList<>();
        textures = new ArrayList<>();
        normals = new ArrayList<>();
        indices = new ArrayList<>();
        material = mat;
    }

    /**
     * Adds the position.
     *
     * @param list the list
     * @param i    the i
     */
    public void addPosition(final List<String> list, final int i) {
        positions.add(list.get(i * POSITIONS_LENGTH + 0));
        positions.add(list.get(i * POSITIONS_LENGTH + 1));
        positions.add(list.get(i * POSITIONS_LENGTH + 2));
    }

    /**
     * Adds the normal.
     *
     * @param list the list
     * @param i    the i
     */
    public void addNormal(final List<String> list, final int i) {
        normals.add(list.get(i * NORMALS_LENGTH + 0));
        normals.add(list.get(i * NORMALS_LENGTH + 1));
        normals.add(list.get(i * NORMALS_LENGTH + 2));
    }


    public void addNormal() {
        normals.add("0");
        normals.add("0");
        normals.add("0");
    }

    /**
     * Adds the texture.
     *
     * @param list the list
     * @param i    the i
     */
    public void addTexture(final List<String> list, final int i) {
        textures.add(list.get(i * TEXTURES_LENGTH + 0));
        textures.add(list.get(i * TEXTURES_LENGTH + 1));
    }

    public void addTexture() {
        textures.add("0");
        textures.add("0");
    }

    /**
     * Adds the index.
     *
     * @param vpn the vpn
     * @return the boolean
     */
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

    /**
     * To json.
     *
     * @return the JSON object
     * the JSON exception
     */
    public JSONObject toJSON() {
        return new JSONObject() {{
            ;

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
