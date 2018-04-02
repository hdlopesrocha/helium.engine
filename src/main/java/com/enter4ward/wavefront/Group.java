package com.enter4ward.wavefront;

import org.json.JSONArray;

import java.util.TreeMap;

/**
 * The Class Group.
 */
class Group {

    /**
     * The material groups.
     */
    private TreeMap<String, MaterialGroup> materialGroups;

    /**
     * The name.
     */
    private String name;

    /**
     * Instantiates a new group.
     *
     * @param n the n
     */
    public Group(final String n) {
        materialGroups = new TreeMap<String, MaterialGroup>();
        name = n;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the material group.
     *
     * @param n the name
     * @return the material group
     */
    public MaterialGroup getMaterialGroup(final String n) {
        MaterialGroup mg = materialGroups.get(n);

        if (mg == null) {
            mg = new MaterialGroup(n);
            materialGroups.put(n, mg);
        }

        return mg;
    }

    /**
     * To json.
     *
     * @return the JSON object
     * the JSON exception
     */
    public JSONArray toJSON() {
        JSONArray jgroupmaterial = new JSONArray();
        for (MaterialGroup mg : materialGroups.values()) {
            jgroupmaterial.put(mg.toJSON());
        }
        return jgroupmaterial;
    }
}
