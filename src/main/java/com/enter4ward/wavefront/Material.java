package com.enter4ward.wavefront;

import org.json.JSONObject;

/**
 * The Class Material.
 */
public class Material {

    /**
     * The map_ kd.
     */
    private String mapKd;

    /**
     * The Ka.
     */
    private Float[] ka;

    /**
     * The Kd.
     */
    private Float[] kd;

    /**
     * The Ks.
     */
    private Float[] ks;

    /**
     * The Tf.
     */
    private Float[] tf;
    /**
     * The illum.
     */
    private Float illum;
    /**
     * The d.
     */
    private Float d;
    /**
     * The Ns.
     */
    private Float ns;
    /**
     * The sharpness.
     */
    private Float sharpness;
    /**
     * The Ni.
     */
    private Float ni;
    /**
     * The name.
     */
    private String name;

    /**
     * Instantiates a new material.
     *
     * @param n the n
     */
    public Material(final String n) {
        name = n;
    }

    /**
     * Gets the map kd.
     *
     * @return the map kd
     */
    public final String getMapKd() {
        return mapKd;
    }

    /**
     * Sets the map kd.
     *
     * @param value the new map kd
     */
    public final void setMapKd(final String value) {
        this.mapKd = value;
    }

    /**
     * Gets the ka.
     *
     * @return the ka
     */
    public final Float[] getKa() {
        return ka;
    }

    /**
     * Sets the ka.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     */
    public final void setKa(final Float x, final Float y, final Float z) {
        this.ka = new Float[]{x, y, z};
    }

    /**
     * Gets the kd.
     *
     * @return the kd
     */
    public final Float[] getKd() {
        return kd;
    }

    /**
     * Sets the kd.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     */
    public final void setKd(final Float x, final Float y, final Float z) {
        this.kd = new Float[]{x, y, z};
    }

    /**
     * Gets the ks.
     *
     * @return the ks
     */
    public final Float[] getKs() {
        return ks;
    }

    /**
     * Sets the ks.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     */
    public final void setKs(final Float x, final Float y, final Float z) {
        this.ks = new Float[]{x, y, z};
    }

    /**
     * Gets the tf.
     *
     * @return the tf
     */
    public final Float[] getTf() {
        return tf;
    }

    /**
     * Sets the tf.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     */
    public final void setTf(final Float x, final Float y, final Float z) {
        this.tf = new Float[]{x, y, z};
    }

    /**
     * Gets the illum.
     *
     * @return the illum
     */
    public final Float getIllum() {
        return illum;
    }

    /**
     * Sets the illum.
     *
     * @param value the new illum
     */
    public final void setIllum(final Float value) {
        this.illum = value;
    }

    /**
     * Gets the d.
     *
     * @return the d
     */
    public final Float getD() {
        return d;
    }

    /**
     * Sets the d.
     *
     * @param value the new d
     */
    public final void setD(final Float value) {
        this.d = value;
    }

    /**
     * Gets the ns.
     *
     * @return the ns
     */
    public final Float getNs() {
        return ns;
    }

    /**
     * Sets the ns.
     *
     * @param value the new ns
     */
    public final void setNs(final Float value) {
        this.ns = value;
    }

    /**
     * Gets the sharpness.
     *
     * @return the sharpness
     */
    public final Float getSharpness() {
        return sharpness;
    }

    /**
     * Sets the sharpness.
     *
     * @param value the new sharpness
     */
    public final void setSharpness(final Float value) {
        this.sharpness = value;
    }

    /**
     * Gets the ni.
     *
     * @return the ni
     */
    public final Float getNi() {
        return ni;
    }

    /**
     * Sets the ni.
     *
     * @param value the new ni
     */
    public final void setNi(final Float value) {
        this.ni = value;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param value the new name
     */
    public final void setName(final String value) {
        this.name = value;
    }

    /**
     * To json.
     *
     * @return the JSON object
     * the JSON exception
     */
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
