package com.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SaveManager {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    // game directory
    public static File getSaveDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        
        String gameFolderName = ".dungeonfall"; 
        File saveDirectory;

        if (os.contains("win")) {
            saveDirectory = new File(System.getenv("APPDATA"), gameFolderName);

        } else {
            saveDirectory = new File(System.getProperty("user.home"), gameFolderName);
        }

        if (!saveDirectory.exists()) {
            saveDirectory.mkdirs();
        }

        return saveDirectory;
    }

    // settings
    public static void saveSettings() {
        File settingsFile = new File(getSaveDirectory(), "settings.json");

        try (FileWriter writer = new FileWriter(settingsFile)) {
            gson.toJson(GameSettings.instance, writer);
        } catch (IOException e) {
            System.err.println("Settings could not be saved: " + e.getMessage());
        }
    }

    public static GameSettings loadSettings() {
        File settingsFile = new File(getSaveDirectory(), "settings.json");

        if (settingsFile.exists()) {
            try (FileReader reader = new FileReader(settingsFile)) {               
                GameSettings loadedSettings = (new Gson()).fromJson(reader, GameSettings.class);
                if (loadedSettings != null) {
                    GameSettings.instance = loadedSettings;
                }
                
            } catch (Exception e) {
                System.err.println("Error when reading settings.json: " + e.getMessage());
            }
        } else {
            System.out.println("There is no settings file. Creating a new one.");
        }

        return GameSettings.instance;
    }

    // save file check
    public static boolean saveExists(char slot) {
        String fileName = "save" + slot + ".json";
        File file = new File(getSaveDirectory(), fileName);
        return file.exists() && file.length() > 0;
    }

    public static void deleteSave(char slot) {
        File file = new File(getSaveDirectory(), "save" + slot + ".json");
        if (file.exists()) {
            file.delete();
        }
    }

    // level
    public static void saveLevel(Level level, char slot) {
        String fileName = "save" + slot + ".json";
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(level, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Level loadLevel(char slot) throws IOException {
        String fileName = "save" + slot + ".json";
        File file = new File(fileName);
    
        if (!file.exists()) throw new FileNotFoundException();
    
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, Level.class);
        }
    }

}