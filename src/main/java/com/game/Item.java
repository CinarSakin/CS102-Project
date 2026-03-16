package com.game;

import javafx.scene.image.Image;

public abstract class Item {
    public Image image; 
    public String name;
    public String description;

    public Item(Image image, String name, String description) {
        this.image = image;
        this.name = name;
        this.description = description;
    }

    public Effect applyEffect(LivingEntity target){
        // ToDo
        return new Effect(0, 1, target); // to silence the compiler
    }

    public abstract void draw();
}
