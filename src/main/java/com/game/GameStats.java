package com.game;

public class GameStats {

    public double timePassed = 0;
    public int enemiesKilled = 0;
    public int roomsVisited = 0;//TODO
    public int bossKilled = 0;
    public int levelsCleared = 0;
    public int chestsOpened = 0;
    public int puzzlesCompleted = 0;

    //instance
    public static GameStats instance = new GameStats();

    public int calculateScore(){
        return LeaderboardManager.computeScore(enemiesKilled, bossKilled, levelsCleared, chestsOpened, puzzlesCompleted);
    }

    public static GameStats getInstance(){return instance;}
    public int getEnemiesKilled(){return enemiesKilled;}
    public double getTimePassed(){return timePassed;}
    public int getBossKilled(){return bossKilled;}
    public int getLevelsCleared(){return levelsCleared;}
    public int getChestsOpened(){return chestsOpened;}
    public int getPuzzlesCompleted(){return puzzlesCompleted;}
}
