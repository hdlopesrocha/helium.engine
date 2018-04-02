package com.enter4ward.math;

import java.util.ArrayList;
import java.util.List;

public class Group extends BoundingSphere {
    private List<IBufferObject> subGroups = new ArrayList<IBufferObject>();
    private String name;

    public Group(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }


    public void addBuffer(IBufferObject buffer) {
        subGroups.add(buffer);
    }

    public Iterable<IBufferObject> getBuffers() {
        return subGroups;
    }

}
