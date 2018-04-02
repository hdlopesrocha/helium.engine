package com.enter4ward.lwjgl;

import java.lang.reflect.Field;
import java.util.Arrays;


public class LibraryLoader {

    public static void loadNativeLibraries() throws Exception {
        if (System.getProperty("os.name").equals("Mac OS X")) {
            addLibraryPath("native/macosx");

        } else if (System.getProperty("os.name").equals("Linux")) {
            addLibraryPath("native/linux");
        } else {
            addLibraryPath("native/windows");
            if (System.getProperty("os.arch").equals("amd64")
                    || System.getProperty("os.arch").equals("x86_x64")) {
                System.loadLibrary("OpenAL64");
            } else {
                System.loadLibrary("OpenAL32");

            }
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
