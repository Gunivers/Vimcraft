package net.gunivers.vimcraft;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

public final class KeyUtils {

    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final Map<Integer, Character> SCANCODE_TO_CHAR_MAP = new HashMap<>();

    private KeyUtils() {
    }

    static {
	for (char character : CHARS.toCharArray()) {
	    SCANCODE_TO_CHAR_MAP.put(GLFW.glfwGetKeyScancode(character), character);
	}
    }

    public static Character getCharacter(int scancode) {
	return SCANCODE_TO_CHAR_MAP.get(scancode);
    }

}
