package io.github.gregorybatten.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.gregorybatten.util.Config;
import io.github.gregorybatten.util.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SettingService {

    private static final ObjectMapper mapper = new ObjectMapper();

    // Get setting with fallback logic (try-read, restore defaults, retry-read)
    public static String getSetting(String key) {
        String value = getSettingHelper(key);
        if (value != null) {
            return value;  // Found it
        }

        // Fallback if read fails (corrupt/missing file)
        restoreDefaultSettings();
        return getSettingHelper(key);
    }

    // Get setting directly from file (no fallback handling)
    private static String getSettingHelper(String key) {
        File settingsFile = Config.getFile("settings.json");

        if (!settingsFile.exists()) {
            Logger.log("settings.json not found at " + settingsFile.getAbsolutePath());
            return null;
        }

        try {
            JsonNode root = mapper.readTree(settingsFile);
            if (root != null && root.has(key)) {
                return root.get(key).asText();
            } else {
                Logger.log("Key '" + key + "' not found in settings.json.");
                return null;
            }
        } catch (IOException e) {
            Logger.log("Error reading settings.json: " + e.getMessage());
            return null;  // Triggers fallback in getSetting()
        }
    }

    // Set setting with fallback logic (try-write, restore defaults, retry-write)
    public static boolean setSetting(String key, String value) {
        if (setSettingHelper(key, value)) {
            return true;
        }

        // Fallback if write fails (corrupt/missing file)
        restoreDefaultSettings();
        return setSettingHelper(key, value);
    }

    // Set setting directly to file (no fallback handling)
    private static boolean setSettingHelper(String key, String value) {
        File settingsFile = Config.getFile("settings.json");

        try {
            // Ensure the parent directory exists
            settingsFile.getParentFile().mkdirs();

            // Read existing settings or create new object if file is empty/corrupt
            ObjectNode root = settingsFile.exists()
                ? (ObjectNode) mapper.readTree(settingsFile)
                : mapper.createObjectNode();

            root.put(key, value);  // Ensure correct type handling

            // Write updated settings to file
            mapper.writeValue(settingsFile, root);
            return true;

        } catch (IOException e) {
            Logger.log("Error writing to settings.json: " + e.getMessage());
            return false;  // Triggers fallback in setSetting()
        }
    }

    // Restores default-settings.json to settings.json (replaces existing settings.json)
    private static void restoreDefaultSettings() {
        File defaultFile = Config.getFile("default-settings.json");
        File settingsFile = Config.getFile("settings.json");

        if (!defaultFile.exists()) {
            Logger.log("Default settings file missing! Cannot restore settings.");
            return;
        }

        try {
            Files.copy(defaultFile.toPath(), settingsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Logger.log("Restored settings.json from default-settings.json");

        } catch (IOException e) {
            Logger.log("Failed to restore default settings: " + e.getMessage());
            throw new RuntimeException("Critical failure restoring settings file.", e);
        }
    }
}
