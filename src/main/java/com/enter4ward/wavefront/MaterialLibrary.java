package com.enter4ward.wavefront;

import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * The Class MaterialLibrary.
 */
public class MaterialLibrary {

    /**
     * The materials.
     */
    private ArrayList<Material> materials = new ArrayList<Material>();

    /**
     * Instantiates a new material library.
     *
     * @param file the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public MaterialLibrary(final File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        Material currentMaterial = null;
        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("\\s+", " ");

            StringTokenizer tok = new StringTokenizer(line, " ");

            if (tok.hasMoreTokens()) {
                String cmd = tok.nextToken();
                if (tok.hasMoreElements()) {
                    if (cmd.equals("newmtl")) {
                        currentMaterial = new Material(tok.nextToken());
                        materials.add(currentMaterial);
                    } else if (cmd.equals("map_Kd")) {
                        currentMaterial.setMapKd(tok.nextToken());
                    } else if (cmd.equals("Ka")) {
                        currentMaterial.setKa(Float.valueOf(tok.nextToken()),
                                Float.valueOf(tok.nextToken()),
                                Float.valueOf(tok.nextToken()));
                    } else if (cmd.equals("Kd")) {
                        currentMaterial.setKd(Float.valueOf(tok.nextToken()),
                                Float.valueOf(tok.nextToken()),
                                Float.valueOf(tok.nextToken()));
                    } else if (cmd.equals("Ks")) {
                        currentMaterial.setKs(Float.valueOf(tok.nextToken()),
                                Float.valueOf(tok.nextToken()),
                                Float.valueOf(tok.nextToken()));
                    } else if (cmd.equals("Tf")) {
                        currentMaterial.setTf(Float.valueOf(tok.nextToken()),
                                Float.valueOf(tok.nextToken()),
                                Float.valueOf(tok.nextToken()));
                    } else if (cmd.equals("illum")) {
                        currentMaterial
                                .setIllum(Float.valueOf(tok.nextToken()));
                    } else if (cmd.equals("d")) {
                        currentMaterial.setD(Float.valueOf(tok.nextToken()));
                    } else if (cmd.equals("Ns")) {
                        currentMaterial.setNs(Float.valueOf(tok.nextToken()));
                    } else if (cmd.equals("sharpness")) {
                        currentMaterial.setSharpness(Float.valueOf(tok
                                .nextToken()));
                    } else if (cmd.equals("Ni")) {
                        currentMaterial.setNi(Float.valueOf(tok.nextToken()));
                    }
                }
            }

        }
        reader.close();

    }

    /**
     * Materials to json.
     *
     * @return the string
     * the JSON exception
     */
    public final JSONObject toJSON() {
        JSONObject jmaterials = new JSONObject();

        for (Material mat : materials) {
            jmaterials.put(mat.getName(), mat.toJSON());
        }

        return jmaterials;
    }

    /**
     * To file.
     *
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public final File toFile() throws IOException {
        File file = File.createTempFile("file", "temp");
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(file));
        bw1.write(toJSON().toString());
        bw1.close();
        return file;
    }

}
