package com.enter4ward.wavefront;

import org.json.JSONObject;

public class Material {

    private String mapKd;

    private Float[] ka;

    private Float[] kd;

    private Float[] ks;

    private Float[] tf;

    private Float illum;

    private Float d;

    private Float ns;

    private Float sharpness;

    private Float ni;

    private String name;

    public Material(final String n) {
        name = n;
    }

    public final String getMapKd() {
        return mapKd;
    }

    public final void setMapKd(final String value) {
        this.mapKd = value;
    }

    public final Float[] getKa() {
        return ka;
    }

    public final void setKa(final Float x, final Float y, final Float z) {
        this.ka = new Float[]{x, y, z};
    }

    public final Float[] getKd() {
        return kd;
    }

    public final void setKd(final Float x, final Float y, final Float z) {
        this.kd = new Float[]{x, y, z};
    }

    public final Float[] getKs() {
        return ks;
    }

    public final void setKs(final Float x, final Float y, final Float z) {
        this.ks = new Float[]{x, y, z};
    }

    public final Float[] getTf() {
        return tf;
    }

    public final void setTf(final Float x, final Float y, final Float z) {
        this.tf = new Float[]{x, y, z};
    }

    public final Float getIllum() {
        return illum;
    }

    public final void setIllum(final Float value) {
        this.illum = value;
    }

    public final Float getD() {
        return d;
    }

    public final void setD(final Float value) {
        this.d = value;
    }

    public final Float getNs() {
        return ns;
    }

    public final void setNs(final Float value) {
        this.ns = value;
    }

    public final Float getSharpness() {
        return sharpness;
    }

    public final void setSharpness(final Float value) {
        this.sharpness = value;
    }

    public final Float getNi() {
        return ni;
    }

    public final void setNi(final Float value) {
        this.ni = value;
    }

    public final String getName() {
        return name;
    }

    public final void setName(final String value) {
        this.name = value;
    }

    public final JSONObject toJSON() {
        JSONObject jmaterial = new JSONObject();

        if (mapKd != null) {
            jmaterial.put("map_Kd", mapKd);
        }
        if (ka != null) {
            jmaterial.put("Ka", ka);
        }
        if (kd != null) {
            jmaterial.put("Kd", kd);
        }
        if (ks != null) {
            jmaterial.put("Ks", ks);
        }
        if (tf != null) {
            jmaterial.put("Tf", tf);
        }
        if (d != null) {
            jmaterial.put("d", d);
        }
        if (ns != null) {
            jmaterial.put("Ns", ns);
        }
        if (sharpness != null) {
            jmaterial.put("sharpness", sharpness);
        }
        if (ni != null) {
            jmaterial.put("Ni", ni);
        }
        return jmaterial;
    }

}
