package io.github.gregorybatten.util;

public class Logger {

    public static void log(String... message) {
        if (Config.DEBUG_MODE) {
            for (String msg : message) {
                System.out.println("[DEBUG] " + msg);
            }
        }
    }
}
