package com.enter4ward.math;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

public class VertexData {
    private List<Vector3f> positionData = new ArrayList<>();
    private List<Vector3f> normalData = new ArrayList<>();
    private List<Vector2f> textureData = new ArrayList<>();
    private List<Integer> indexData = new ArrayList<>();

    public VertexData() {

    }

    public VertexData compress() {
        Map<Integer, Integer> maps = new HashMap<>();
        Map<String, Integer> search = new HashMap<>();

        final List<Vector3f> newPositionData = new ArrayList<>();
        final List<Vector3f> newNormalData = new ArrayList<>();
        final List<Vector2f> newTextureData = new ArrayList<>();


        for (int i = 0; i < positionData.size(); ++i) {
            Vector3f p1 = positionData.get(i);
            Vector3f n1 = normalData.get(i);
            Vector2f t1 = textureData.get(i);
            String key = toString(p1, n1, t1);
            Integer newIndex = search.get(key);
            if (newIndex == null) {
                newIndex = newPositionData.size();
                search.put(key, newIndex);
                newPositionData.add(p1);
                newNormalData.add(n1);
                newTextureData.add(t1);
            }
            maps.put(i, newIndex);
        }
        for(int i : indexData){
            Integer map = maps.get(i);
            if(map != null){
                indexData.set(i, map);
            }
        }

        this.positionData = newPositionData;
        this.normalData = newNormalData;
        this.textureData = newTextureData;
        return this;
    }

    private String toString(Vector3f p, Vector3f n, Vector2f t) {
        return p.x + "_" + p.y + "_" + p.z + "_" + n.x + "_" + n.y + "_" + n.z + "_" + t.x + "_" + t.y;
    }

    public void addPosition(Vector3f vec) {
        positionData.add(vec);
    }

    public void addNormal(Vector3f vec) {
        normalData.add(vec);
    }

    public void addTexture(Vector2f vec) {
        textureData.add(vec);
    }

    public void addIndex(int f) {
        indexData.add(f);

    }

    public Vector3f getPosition(int i) {
        return positionData.get(i);
    }

    public List<Vector3f> getPositionData() {
        return positionData;
    }

    public List<Vector3f> getNormalData() {
        return normalData;
    }

    public List<Vector2f> getTextureData() {
        return textureData;
    }

    public List<Integer> getIndexData() {
        return indexData;
    }

    public int size() {
        return (positionData.size() * 3 + normalData.size() * 3 + textureData.size() * 2);
    }

    public void clear() {
        this.positionData.clear();
        this.normalData.clear();
        this.textureData.clear();
        this.indexData.clear();
    }

    public void iterate(VertexDataHandler handler) {
        for (int i = 0; i < positionData.size(); ++i) {
            handler.handle(positionData.get(i), normalData.get(i), textureData.get(i));
        }
    }

    public interface VertexDataHandler {
        void handle(Vector3f position, Vector3f normal, Vector2f texture);
    }

}
