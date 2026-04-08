package com.game;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Sorts.descending;

public class LeaderboardManager {

    private static final String COLLECTION = "leaderboard";

    public record LeaderboardEntry(String playerName, int score, int enemiesKilled,
                                   int bossKilled, int levelsCleared, int chestsOpened,
                                   int timePassed) {}

    public static void submitScore(String playerName) {
        if (!MongoManager.isConnected()) return;
        GameStats s = GameStats.getInstance();
        Document doc = new Document("playerName", playerName)
                .append("score",         s.calculateScore())
                .append("enemiesKilled", s.enemiesKilled)
                .append("bossKilled",    s.bossKilled)
                .append("levelsCleared", s.levelsCleared)
                .append("chestsOpened",  s.chestsOpened)
                .append("timePassed",    (int) s.timePassed)
                .append("timestamp",     System.currentTimeMillis());
        getCollection().insertOne(doc);
    }

    public static void recomputeAllScores() {
        if (!MongoManager.isConnected()) return;
        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                int newScore = computeScore(
                        doc.getInteger("enemiesKilled", 0),
                        doc.getInteger("bossKilled",    0),
                        doc.getInteger("levelsCleared", 0),
                        doc.getInteger("chestsOpened",  0)
                );
                getCollection().updateOne(
                        new Document("_id", doc.get("_id")),
                        new Document("$set", new Document("score", newScore))
                );
            }
        }
    }

    public static List<LeaderboardEntry> getTopScores(int limit) {
        List<LeaderboardEntry> entries = new ArrayList<>();
        if (!MongoManager.isConnected()) return entries;
        try (MongoCursor<Document> cursor = getCollection()
                .find().sort(descending("score")).limit(limit).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                entries.add(new LeaderboardEntry(
                        doc.getString("playerName"),
                        doc.getInteger("score",         0),
                        doc.getInteger("enemiesKilled", 0),
                        doc.getInteger("bossKilled",    0),
                        doc.getInteger("levelsCleared", 0),
                        doc.getInteger("chestsOpened",  0),
                        doc.getInteger("timePassed", 0)
                ));
            }
        }
        return entries;
    }

    public static int computeScore(int enemies, int bosses, int levels, int chests) {
        return enemies * 30 + bosses * 500 + levels * 300 + chests * 50;
    }

    private static MongoCollection<Document> getCollection() {
        return MongoManager.getDatabase().getCollection(COLLECTION);
    }
}
