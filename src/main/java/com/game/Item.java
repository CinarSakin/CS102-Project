package com.game;

import javafx.scene.effect.Effect;

public abstract class Item {
    private String name;
    private String description;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract Effect applyEffect(LivingEntity target);

    public abstract void draw();
}
