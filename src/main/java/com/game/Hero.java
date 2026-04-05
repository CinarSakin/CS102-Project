package com.game;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.effect.Light.Point;

public class Hero extends LivingEntity {
    private static Hero CurrentHero;
    

    private Talisman[] talismans = new Talisman[3];
    private Consumable[] consumables = new Consumable[3];
    private Weapon[] weapons = new Weapon[2];
    private int heldWeapon;

    public Hero(Point2D position, Sword starterSword, double diffMulti, Room currentRoom) {
        super(LivingType.HERO, position, currentRoom, diffMulti);

        weapons[0] = starterSword;
        this.heldWeapon = 0;
    }

    public void move(Point2D velocity) {
        
        double nextX = this.getDimension().getX() + velocity.getX();
        double nextY = this.getDimension().getY() + velocity.getY();

        // Only update position if the new coordinates are within bounds
        if (Game.isPositionValid(this, nextX, nextY)) {
            dimension.moveTo(nextX, nextY);
        }

        
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

        if (this.health <= 0) {
            animStates.add(LivingAnimStates.DIE);
            // Oyun bitiş ekranını tetikle
        }
    }

    @Override
    public void attack() {
        weapons[heldWeapon].use();
    }

    //incremented getHero()
    public static Hero getHero() {
        if(CurrentHero != null)return CurrentHero;
        return CurrentHero = new Hero(null, null, 0,null);
       
    }
    public void setImage(){
        //seting image iplementation...
    }
}
