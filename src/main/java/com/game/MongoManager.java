package com.game;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.InputStream;
import java.util.Properties;

public class MongoManager {

    private static MongoClient client;
    private static MongoDatabase database;
    private static boolean connected = false;

    public static void connect() {
        if (connected) return;
        try {
            Properties props = new Properties();
            InputStream stream = MongoManager.class.getResourceAsStream("/mongo.properties");
            if (stream == null) {
                System.err.println("mongo.properties not found, leaderboard disabled.");
                return;
            }
            props.load(stream);
            String uri = props.getProperty("uri");
            String dbName = props.getProperty("database", "dungeonfall");

            client = MongoClients.create(uri);
            database = client.getDatabase(dbName);
            connected = true;
            System.out.println("MongoDB connected.");
        } catch (Exception e) {
            System.err.println("MongoDB connection failed: " + e.getMessage());
        }
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    public static boolean isConnected() {
        return connected;
    }

    public static void close() {
        if (client != null) client.close();
        connected = false;
    }
}
