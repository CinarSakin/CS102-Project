package com.game;

public class GameStats {
    public int score = 0;
    public int enemiesKilled = 0;
    public int roomsVisited = 0;//TODO
    public double timePassed = 0;
    public int bossKilled = 0;
    public int levelsCleared = 0;
    public int chestsOpened = 0;
    //instance
    public static GameStats instance = new GameStats();

    public void calculateScore(){
        score += enemiesKilled*100+ roomsVisited*50 + bossKilled*500 + levelsCleared*500 + chestsOpened*10;
    }

    public static GameStats getInstance(){return instance;}
    public int getScore(){return score;}
}
