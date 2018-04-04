package com.enter4ward.lwjgl;

public class KeyboardManager {
    private final boolean[] copyKeys = new boolean[65536];
    private final int[] statusKeys = new int[65536];

    private final boolean[] pressedKeys = new boolean[65536];

    void setKeyPressed(int key, boolean value) {
        pressedKeys[key] = value;
    }

    public boolean isKeyDown(int key) {
        return pressedKeys[key];
    }

    public boolean hasKeyReleased(int key) {
        return statusKeys[key] == 1;
    }

    public boolean hasKeyPressed(int key) {
        return statusKeys[key] == 2;
    }

    public void update() {

        for (int i = 0; i < 65536; ++i) {
            if (copyKeys[i] != pressedKeys[i]) {
                statusKeys[i] = pressedKeys[i] ? 2 : 1;
            } else {
                statusKeys[i] = 0;
            }

            copyKeys[i] = pressedKeys[i];
        }
    }

}
