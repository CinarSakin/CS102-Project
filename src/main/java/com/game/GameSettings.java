package com.game;

public class GameSettings {

    // instance accessible from static class
    public static GameSettings instance = new GameSettings();

    // sound settings
    public int masterVolume = 80;
    public int musicVolume = 80;
    public int sfxVolume = 80;

    public static double getMusicVolume() {return instance.masterVolume * instance.musicVolume;}
    public static double getSfxVolume() {return instance.masterVolume * instance.sfxVolume;}

    // other settings
    public double uiScale = 1;
    
    // last window properties
    public double windowX = -1;
    public double windowY = -1;
    public double windowWidth = 960.0; 
    public double windowHeight = 720.0;
    public boolean isMaximized = false;
    public boolean isFullscreen = false;

}