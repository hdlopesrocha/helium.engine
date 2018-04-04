package com.enter4ward.wavefront;

import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class WaveFront {

    private static final int TRIANGLE = 3;

    private final Map<String, Group> groups;

    private final List<String> positions;

    private final List<String> normals;

    private final List<String> textures;

    public WaveFront(final InputStream file) throws IOException {
        groups = new TreeMap<>();
        positions = new ArrayList<>();
        normals = new ArrayList<>();
        textures = new ArrayList<>();

        positions.add("0.0");
        positions.add("0.0");
        positions.add("0.0");
        normals.add("0.0");
        normals.add("0.0");
        normals.add("0.0");
        textures.add("0.0");
        textures.add("0.0");

        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
        String line;
        String groupName = "";
        String materialName = "";

        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("\\s+", " ");
            StringTokenizer tok = new StringTokenizer(line, " ");

            if (tok.hasMoreElements()) {
                String cmd = tok.nextToken();

                switch (cmd) {
                    case "v":
                        positions.add(simplify(tok.nextToken()));
                        positions.add(simplify(tok.nextToken()));
                        positions.add(simplify(tok.nextToken()));
                        break;
                    case "vn":
                        normals.add(simplify(tok.nextToken()));
                        normals.add(simplify(tok.nextToken()));
                        normals.add(simplify(tok.nextToken()));
                        break;
                    case "vt":
                        textures.add(simplify(tok.nextToken()));
                        textures.add(simplify(tok.nextToken()));
                        break;
                    case "g":
                        groupName = tok.nextToken();
                        break;
                    case "usemtl":
                        materialName = tok.nextToken();
                        break;
                    case "f":

                        for (int i = 0; i < TRIANGLE; ++i) {
                            String vpn = tok.nextToken();

                            String[] token = vpn.split("/");

                            Group cg = groups.get(groupName);
                            if (cg == null) {
                                cg = new Group(groupName);
                                groups.put(groupName, cg);
                            }

                            MaterialGroup mg = cg.getMaterialGroup(materialName);

                            if (mg.addIndex(vpn)) {
                                mg.addPosition(positions, Integer.valueOf(token[0]));
                                if (token[1].length() > 0) {
                                    mg.addTexture(textures,
                                            Integer.valueOf(token[1]));
                                } else {
                                    mg.addTexture();
                                }
                                if (token[2].length() > 0) {
                                    mg.addNormal(normals, Integer.valueOf(token[2]));
                                } else {
                                    mg.addNormal();
                                }
                            }
                        }
                        break;
                }
            }
        }
        reader.close();
    }

    private static String simplify(final String s) {
        String temp = s;
        if (temp.contains(".")) {

            while (true) {
                if (temp.charAt(temp.length() - 1) == '0'
                        && temp.charAt(temp.length() - 2) != '.') {
                    temp = temp.substring(0, temp.length() - 1);
                } else {
                    break;
                }
            }

        }
        return temp;

    }

    public final JSONObject toJSON() {
        JSONObject jgroups = new JSONObject();
        for (Group gr : groups.values()) {
            jgroups.put(gr.getName(), gr.toJSON());
        }
        return jgroups;
    }

    public final File toFile() throws IOException {
     /*   File file = File.createTempFile("file", "temp");
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(file));
        bw1.write(toJSON().toString());
        bw1.close();
        return file;*/
     return null;
    }
}
