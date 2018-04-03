package com.enter4ward.math;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class VertexData {
    private final List<Vector3f> positionData = new ArrayList<>();
    private final List<Vector3f> normalData = new ArrayList<>();
    private final List<Vector2f> textureData = new ArrayList<>();
    private final List<Short> indexData = new ArrayList<>();

    public VertexData() {

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

    public void addIndex(short f) {
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

    public List<Short> getIndexData() {
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

    public interface VertexDataHandler {
        void handle(Vector3f position, Vector3f normal, Vector2f texture);
    }

    public void iterate(VertexDataHandler handler) {
        for (int i = 0; i < positionData.size(); ++i) {
            handler.handle(positionData.get(i), normalData.get(i), textureData.get(i));
        }
    }

}
