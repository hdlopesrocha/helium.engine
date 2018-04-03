package com.enter4ward.wavefront;

import org.json.JSONObject;

import java.io.*;
import java.util.*;

// TODO: Auto-generated Javadoc

/**
 * The Class WaveFront.
 */
public class WaveFront {

    /**
     * The Constant TRIANGLE.
     */
    private static final int TRIANGLE = 3;

    /**
     * The groups.
     */
    private Map<String, Group> groups;

    /**
     * The positions.
     */
    private List<String> positions;

    /**
     * The normals.
     */
    private List<String> normals;

    /**
     * The textures.
     */
    private List<String> textures;

    /**
     * Instantiates a new wave front.
     *
     * @param file the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public WaveFront(final InputStream file) throws IOException {
        groups = new TreeMap<String, Group>();
        positions = new ArrayList<String>();
        normals = new ArrayList<String>();
        textures = new ArrayList<String>();

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

                if (cmd.equals("v")) {
                    positions.add(simplify(tok.nextToken()));
                    positions.add(simplify(tok.nextToken()));
                    positions.add(simplify(tok.nextToken()));
                } else if (cmd.equals("vn")) {
                    normals.add(simplify(tok.nextToken()));
                    normals.add(simplify(tok.nextToken()));
                    normals.add(simplify(tok.nextToken()));
                } else if (cmd.equals("vt")) {
                    textures.add(simplify(tok.nextToken()));
                    textures.add(simplify(tok.nextToken()));
                } else if (cmd.equals("g")) {
                    groupName = tok.nextToken();
                } else if (cmd.equals("usemtl")) {
                    materialName = tok.nextToken();
                } else if (cmd.equals("f")) {

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
                }
            }
        }
        reader.close();
    }

    /**
     * Simplify.
     *
     * @param s the s
     * @return the string
     */
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

    /**
     * Groups to json.
     *
     * @return the string
     */
    public final JSONObject toJSON() {
        JSONObject jgroups = new JSONObject();
        for (Group gr : groups.values()) {
            jgroups.put(gr.getName(), gr.toJSON());
        }
        return jgroups;
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
