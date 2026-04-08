package com.game;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeaderboardManager {

    private static final String COLLECTION = "leaderboard";

    public record LeaderboardEntry(String playerName, int score, int enemiesKilled,
                                   int bossKilled, int levelsCleared, int chestsOpened,
                                   int puzzlesCompleted, int timePassed) {}

    public static void submitScore(String playerName) {
        if (!MongoManager.isConnected()) return;
        GameStats s = GameStats.getInstance();
        Document doc = new Document("playerName", playerName)
                .append("score",           s.calculateScore())
                .append("enemiesKilled",   s.enemiesKilled)
                .append("bossKilled",      s.bossKilled)
                .append("levelsCleared",   s.levelsCleared)
                .append("chestsOpened",    s.chestsOpened)
                .append("puzzlesCompleted",s.puzzlesCompleted)
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
                        doc.getInteger("enemiesKilled",   0),
                        doc.getInteger("bossKilled",      0),
                        doc.getInteger("levelsCleared",   0),
                        doc.getInteger("chestsOpened",    0),
                        doc.getInteger("puzzlesCompleted",0)
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
        List<Document> pipeline = Arrays.asList(
            new Document("$sort", new Document("score", -1)),
            new Document("$group", new Document("_id", "$playerName")
                .append("score",         new Document("$first", "$score"))
                .append("enemiesKilled", new Document("$first", "$enemiesKilled"))
                .append("bossKilled",    new Document("$first", "$bossKilled"))
                .append("levelsCleared", new Document("$first", "$levelsCleared"))
                .append("chestsOpened",  new Document("$first", "$chestsOpened"))
                .append("puzzlesCompleted",  new Document("$first", "$puzzlesCompleted"))
                .append("timePassed",    new Document("$first", "$timePassed"))),
            new Document("$sort", new Document("score", -1)),
            new Document("$limit", limit)
        );
        try (MongoCursor<Document> cursor = getCollection().aggregate(pipeline).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                entries.add(new LeaderboardEntry(
                        doc.getString("_id"),
                        doc.getInteger("score",           0),
                        doc.getInteger("enemiesKilled",   0),
                        doc.getInteger("bossKilled",      0),
                        doc.getInteger("levelsCleared",   0),
                        doc.getInteger("chestsOpened",    0),
                        doc.getInteger("puzzlesCompleted",0),
                        doc.getInteger("timePassed",      0)
                ));
            }
        }
        return entries;
    }

    public static int ENEMY_SLAIN_POINTS = 30;
    public static int BOSS_SLAIN_POINTS = 500;
    public static int LEVELS_CLEARED_POINTS = 300;
    public static int CHESTS_OPENED_POINTS = 50;
    public static int PUZZLES_COMPLETED_POINTS = 100;

    public static int computeScore(int enemies, int bosses, int levels, int chests, int puzzles) {
        return enemies*ENEMY_SLAIN_POINTS + bosses*BOSS_SLAIN_POINTS + levels*LEVELS_CLEARED_POINTS +
            chests*CHESTS_OPENED_POINTS + puzzles*PUZZLES_COMPLETED_POINTS;
    }

    private static MongoCollection<Document> getCollection() {
        return MongoManager.getDatabase().getCollection(COLLECTION);
    }
}
