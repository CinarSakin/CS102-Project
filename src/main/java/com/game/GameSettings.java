package com.game;

import java.util.Locale;

import javafx.scene.input.KeyCode;

public class GameSettings {

    public static final int DEFAULT_MASTER_VOLUME = 80;
    public static final int DEFAULT_MUSIC_VOLUME = 80;
    public static final int DEFAULT_SFX_VOLUME = 80;
    public static final double DEFAULT_UI_SCALE = 1.0;

    // instance accessible from static class
    public static GameSettings instance = new GameSettings();

    // sound settings
    public int masterVolume = DEFAULT_MASTER_VOLUME;
    public int musicVolume = DEFAULT_MUSIC_VOLUME;
    public int sfxVolume = DEFAULT_SFX_VOLUME;

    // visual settings
    public double uiScale = DEFAULT_UI_SCALE;

    // attack input mode: "KEYBOARD" or "MOUSE"
    public String attackInputMode = "KEYBOARD";

    // control bindings
    public String keyUp = "W";
    public String keyDown = "S";
    public String keyLeft = "A";
    public String keyRight = "D";
    public String keyAttack = "SPACE";
    public String keyInteract = "E";
    public String keyMenu = "ESCAPE";
    public String keyMap = "M";
    public String keySwap = "Q";

    // last window properties
    public double windowX = -1;
    public double windowY = -1;
    public double windowWidth = 1280;
    public double windowHeight = 720;
    public boolean isMaximized = false;
    public boolean isFullscreen = false;

    public static double getMasterVolume() {
        return clampVolume(instance.masterVolume) / 100.0;
    }

    public static double getMusicVolume() {
        return getMasterVolume() * clampVolume(instance.musicVolume) / 100.0;
    }

    public static double getSfxVolume() {
        return getMasterVolume() * clampVolume(instance.sfxVolume) / 100.0;
    }

    public static String getUiScaleLabel() {
        return String.format("%d%%", (int) Math.round(instance.uiScale * 100));
    }

    public static void setMasterVolume(int volume) {
        instance.masterVolume = clampVolume(volume);
    }

    public static void setMusicVolume(int volume) {
        instance.musicVolume = clampVolume(volume);
    }

    public static void setSfxVolume(int volume) {
        instance.sfxVolume = clampVolume(volume);
    }

    public static void setUiScale(double scale) {
        instance.uiScale = Math.max(0.5, Math.min(2.0, scale));
    }

    public static boolean isMouseAttackMode() {
        return "MOUSE".equals(instance.attackInputMode);
    }

    public static void setAttackInputMode(String mode) {
        instance.attackInputMode = mode;
    }

    public static void setKeyBinding(String action, String key) {
        switch (action.toLowerCase()) {
            case "up" -> instance.keyUp = key;
            case "down" -> instance.keyDown = key;
            case "left" -> instance.keyLeft = key;
            case "right" -> instance.keyRight = key;
            case "attack" -> instance.keyAttack = key;
            case "interact" -> instance.keyInteract = key;
            case "menu" -> instance.keyMenu = key;
            case "map" -> instance.keyMap = key;
            case "swap" -> instance.keySwap = key;
        }
    }

    public static String getKeyBinding(String action) {
        return switch (action.toLowerCase()) {
            case "up" -> instance.keyUp;
            case "down" -> instance.keyDown;
            case "left" -> instance.keyLeft;
            case "right" -> instance.keyRight;
            case "attack" -> instance.keyAttack;
            case "interact" -> instance.keyInteract;
            case "menu" -> instance.keyMenu;
            case "map" -> instance.keyMap;
            case "swap" -> instance.keySwap;
            default -> "";
        };
    }

    public static KeyCode getKeyCode(String action) {
        return KeyCode.valueOf((getKeyBinding(action)).toUpperCase(Locale.ENGLISH));
    }

    public static void resetAudio() {
        instance.masterVolume = DEFAULT_MASTER_VOLUME;
        instance.musicVolume = DEFAULT_MUSIC_VOLUME;
        instance.sfxVolume = DEFAULT_SFX_VOLUME;
    }

    public static void resetVisuals() {
        instance.uiScale = DEFAULT_UI_SCALE;
        instance.attackInputMode = "KEYBOARD";
    }

    public static void resetControls() {
        instance.keyUp = "W";
        instance.keyDown = "S";
        instance.keyLeft = "A";
        instance.keyRight = "D";
        instance.keyAttack = "SPACE";
        instance.keyInteract = "E";
        instance.keyMenu = "ESCAPE";
        instance.keyMap = "M";
        instance.keySwap = "Q";
    }

    public static void resetAll() {
        resetAudio();
        resetVisuals();
        resetControls();
    }

    private static int clampVolume(int value) {
        if (value < 0) return 0;
        if (value > 100) return 100;
        return value;
    }
}