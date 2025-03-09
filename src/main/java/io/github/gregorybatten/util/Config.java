package io.github.gregorybatten.util;

import java.io.File;

public class Config {
    public static final String DB_URL = "jdbc:mysql://localhost:3306/cards";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "password";
    public static final boolean DEBUG_MODE = true;
    
    public static File getFile(String filename) {
        return new File("src/main/resources/io/github/gregorybatten/" + filename);
    }
}
