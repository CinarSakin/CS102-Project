package com.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import javafx.geometry.Point2D;

public class SaveManager {

    private static class SubtypeAdapterFactory implements TypeAdapterFactory {

        private boolean handles(Class<?> rawType) {
            return rawType == Area.class || rawType == Entity.class
                || rawType == Item.class || rawType == Weapon.class;
        }

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!handles(type.getRawType())) return null;

            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    if (value == null) { out.nullValue(); return; }

                    // get the adapter for the real class (e.g. Room, Hero, Sword...)
                    TypeAdapter realAdapter = gson.getDelegateAdapter(
                        SubtypeAdapterFactory.this, TypeToken.get(value.getClass()));

                    JsonObject json = realAdapter.toJsonTree(value).getAsJsonObject();
                    json.addProperty("__type", value.getClass().getName());
                    gson.getAdapter(JsonObject.class).write(out, json);
                }

                @Override
                public T read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) { in.nextNull(); return null; }

                    JsonObject json = gson.getAdapter(JsonObject.class).read(in);
                    JsonElement typeField = json.get("__type");
                    if (typeField == null) throw new IOException("__type field missing in save data");

                    try {
                        Class<?> realClass = Class.forName(typeField.getAsString());
                        TypeAdapter realAdapter = gson.getDelegateAdapter(
                            SubtypeAdapterFactory.this, TypeToken.get(realClass));
                        return (T) realAdapter.fromJsonTree(json);
                    } catch (ClassNotFoundException e) {
                        throw new IOException("Unknown class in save: " + typeField.getAsString(), e);
                    }
                }
            };
        }
    }

    private static final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapterFactory(new SubtypeAdapterFactory())
        .registerTypeAdapter(Point2D.class, new TypeAdapter<Point2D>() {
            @Override
            public void write(JsonWriter out, Point2D value) throws IOException {
                if (value == null) { out.nullValue(); return; }
                out.beginObject();
                out.name("x").value(value.getX());
                out.name("y").value(value.getY());
                out.endObject();
            }
            @Override
            public Point2D read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) { in.nextNull(); return null; }
                double x = 0, y = 0;
                in.beginObject();
                while (in.hasNext()) {
                    String name = in.nextName();
                    if (name.equals("x")) x = in.nextDouble();
                    else if (name.equals("y")) y = in.nextDouble();
                    else in.skipValue();
                }
                in.endObject();
                return new Point2D(x, y);
            }
        })
        .create();
    
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
            App.updateSaveSlots();
        }
    }

    // level
    public static void saveLevel(Level level, char slot) {
        File file = new File(getSaveDirectory(), "save" + slot + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(level, writer);
            System.out.println("[Save] Game saved to: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("[Save] Failed to save game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Level loadLevel(char slot) throws IOException {
        File file = new File(getSaveDirectory(), "save" + slot + ".json");

        if (!file.exists()) throw new FileNotFoundException();

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, Level.class);
        }
    }

}