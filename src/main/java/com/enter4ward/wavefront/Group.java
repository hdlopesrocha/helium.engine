package com.enter4ward.wavefront;

import org.json.JSONArray;

import java.util.TreeMap;

class Group {

    private final TreeMap<String, MaterialGroup> materialGroups;

    private final String name;

    public Group(final String n) {
        materialGroups = new TreeMap<>();
        name = n;
    }

    public String getName() {
        return name;
    }

    public MaterialGroup getMaterialGroup(final String n) {
        MaterialGroup mg = materialGroups.computeIfAbsent(n, MaterialGroup::new);

        return mg;
    }

    public JSONArray toJSON() {
        JSONArray jgroupmaterial = new JSONArray();
        for (MaterialGroup mg : materialGroups.values()) {
            jgroupmaterial.put(mg.toJSON());
        }
        return jgroupmaterial;
    }
}
