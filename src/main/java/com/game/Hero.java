package com.game;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.effect.Light.Point;
import javafx.scene.image.Image;

public class Hero extends LivingEntity {
    private static Hero currentHero;
    private static Image idle_right = new Image(Hero.class.getResourceAsStream("/sprites/entities/hero_idle.png"), Level.gridSize, 0, true, false);
    private static Image idle_left = new Image(Hero.class.getResourceAsStream("/sprites/entities/hero_idle_flipped.png"), Level.gridSize, 0, true, false);

    private Talisman[] talismans = new Talisman[3];
    private Consumable[] consumables = new Consumable[3];
    private Weapon[] weapons = new Weapon[2];
    private int heldWeapon;

    public Hero(Point2D position, Sword starterSword, double diffMulti, Area currentArea) {
        super(LivingType.HERO, position, currentArea, diffMulti);
        currentHero = this; 
        weapons[0] = starterSword;
        this.heldWeapon = 0;

        // for now to try
        imageToDraw = idle_right;
    }

    public void move(Point2D velocity) {
        
        // Only update position if the new coordinates are within bounds
        super.move(velocity.multiply(walkSpeed));

        
    }

    public void checkEntityCollisions() {
        for (Room room : Level.getRooms()) {
            for (Entity e : room.getEntities()) {
                if (this.getDimension().intersects(e.getDimension())) {
                    // if (e instanceof Consumable) {
                    //     // Trigger pickup logic
                    //     ((Consumable) e).onPickup(this); 
                } else if (e instanceof Projectile) {
                    this.getDamaged(10);//TODO: change the damage later.
                }
            }
        }
    }
    

    @Override
    public void update(double dt) {
        
        super.update();
        
        checkEntityCollisions();

        imageToDraw = isLookingRight ? idle_right : idle_left;

        if (this.health <= 0) {
            animManager.setCurrentAnim(LivingStates.DIE);
            // Oyun bitiş ekranını tetikle
        }
    }

    @Override
    public void attack() {
        weapons[heldWeapon].use();
    }

    //incremented getHero()
    public static Hero getHero() {
        return currentHero;
       
    }
    public void setImage(){
        //seting image iplementation...
    }
}
