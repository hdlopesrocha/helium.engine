package com.enter4ward.lwjgl;

import java.lang.reflect.Field;
import java.util.Arrays;

public class LibraryLoader {

    public static void loadNativeLibraries() throws Exception {
        switch (System.getProperty("os.name")) {
            case "Mac OS X":
                addLibraryPath("native/macosx");

                break;
            case "Linux":
                addLibraryPath("native/linux");
                break;
            default:
                addLibraryPath("native/windows");
                if (System.getProperty("os.arch").equals("amd64")
                        || System.getProperty("os.arch").equals("x86_x64")) {
                    System.loadLibrary("OpenAL64");
                } else {
                    System.loadLibrary("OpenAL32");

                }
                break;
        }
    }

    private static void addLibraryPath(String s) throws Exception {
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);

        final String[] paths = (String[]) usrPathsField.get(null);

        for (String path : paths) {
            if (path.equals(s)) {
                return;
            }
        }

        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[paths.length - 1] = s;
        usrPathsField.set(null, newPaths);

    }

}
