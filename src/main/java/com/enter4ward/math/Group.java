package com.enter4ward.math;

import java.util.ArrayList;
import java.util.List;

public class Group extends BoundingSphere {
    private final List<IBufferObject> subGroups = new ArrayList<IBufferObject>();
    private final String name;

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
