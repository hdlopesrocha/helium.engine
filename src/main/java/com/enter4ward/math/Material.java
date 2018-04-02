package com.enter4ward.math;

public class Material {
    public Float[] Ka;
    public Float[] Kd;
    public Float[] Ks;
    public Float[] Tf;
    public Float illum;
    public Float d;
    public Float Ns;
    public Float sharpness;
    public Float Ni;
    public int texture = 0;
    public String name;
    public String filename;

    public Material(String n) {
        name = n;
    }

    public void setTexture(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void load(ITextureLoader loader) {
        texture = loader.load();
    }
}
