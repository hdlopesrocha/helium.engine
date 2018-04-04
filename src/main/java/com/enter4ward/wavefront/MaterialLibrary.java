package com.enter4ward.wavefront;

import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MaterialLibrary {

    private final ArrayList<Material> materials = new ArrayList<>();

    public MaterialLibrary(final InputStream file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
        String line;
        Material currentMaterial = null;
        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("\\s+", " ");

            StringTokenizer tok = new StringTokenizer(line, " ");

            if (tok.hasMoreTokens()) {
                String cmd = tok.nextToken();
                if (tok.hasMoreElements()) {
                    switch (cmd) {
                        case "newmtl":
                            currentMaterial = new Material(tok.nextToken());
                            materials.add(currentMaterial);
                            break;
                        case "map_Kd":
                            currentMaterial.setMapKd(tok.nextToken());
                            break;
                        case "Ka":
                            currentMaterial.setKa(Float.valueOf(tok.nextToken()),
                                    Float.valueOf(tok.nextToken()),
                                    Float.valueOf(tok.nextToken()));
                            break;
                        case "Kd":
                            currentMaterial.setKd(Float.valueOf(tok.nextToken()),
                                    Float.valueOf(tok.nextToken()),
                                    Float.valueOf(tok.nextToken()));
                            break;
                        case "Ks":
                            currentMaterial.setKs(Float.valueOf(tok.nextToken()),
                                    Float.valueOf(tok.nextToken()),
                                    Float.valueOf(tok.nextToken()));
                            break;
                        case "Tf":
                            currentMaterial.setTf(Float.valueOf(tok.nextToken()),
                                    Float.valueOf(tok.nextToken()),
                                    Float.valueOf(tok.nextToken()));
                            break;
                        case "illum":
                            currentMaterial
                                    .setIllum(Float.valueOf(tok.nextToken()));
                            break;
                        case "d":
                            currentMaterial.setD(Float.valueOf(tok.nextToken()));
                            break;
                        case "Ns":
                            currentMaterial.setNs(Float.valueOf(tok.nextToken()));
                            break;
                        case "sharpness":
                            currentMaterial.setSharpness(Float.valueOf(tok
                                    .nextToken()));
                            break;
                        case "Ni":
                            currentMaterial.setNi(Float.valueOf(tok.nextToken()));
                            break;
                    }
                }
            }

        }
        reader.close();

    }

    public final JSONObject toJSON() {
        JSONObject jmaterials = new JSONObject();

        for (Material mat : materials) {
            jmaterials.put(mat.getName(), mat.toJSON());
        }

        return jmaterials;
    }

    public final File toFile() throws IOException {
        File file = File.createTempFile("file", "temp");
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(file));
        bw1.write(toJSON().toString());
        bw1.close();
        return file;
    }

}
