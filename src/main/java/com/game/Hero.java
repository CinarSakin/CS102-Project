package com.game;

import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class Hero extends LivingEntity {
    public static Hero currentHero;
    public static boolean isDead = false;

    public static final int TALISMAN_AMOUNT = 3;
    public static final int CONSUMABLE_AMOUNT = 3;

    public Talisman[] talismans = new Talisman[TALISMAN_AMOUNT];
    public Consumable[] consumables = new Consumable[CONSUMABLE_AMOUNT];
    public Weapon[] weapons = new Weapon[2];
    public int heldWeapon;

    public Hero(Point2D position, Sword starterSword, double diffMulti, Area currentArea) {
        super(LivingType.HERO, position, currentArea, diffMulti);
        currentHero = this; 
        weapons[0] = starterSword;
        this.heldWeapon = 0;
    }

    public void move(Point2D direction) {
        super.move(direction.multiply(walkSpeed));
    }

    /*
    public void checkEntityCollisions() {
        for (Room room : Level.getRooms()) {
            for (Entity e : room.getEntities()) {
                if (this.getDimension().intersects(e.getDimension())) {
                    // if (e instanceof Consumable) {
                    //     // Trigger pickup logic
                    //     ((Consumable) e).onPickup(this); 

            }
        }
    }
        */
    

    @Override
    public void update(double dt) {
        super.update(dt);

        if (health <= 0 && !isDead) {
            isDead = true;
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> App.showGameOver());
            delay.play();
        }
    }

    @Override
    public void attack() {
        weapons[heldWeapon].use();
    }

    public void useConsumable(int index) {
        // TODO
    }

    public void dropConsumable(int index, double x, double y) {
        dropItem(consumables[index], x, y);
        consumables[index] = null;
    }

    public void dropTalisman(int index, double x, double y) {
        dropItem(talismans[index], x, y);
        talismans[index] = null;
    }

    public void dropItem(Item item, double x, double y) {
        // TODO
    }

    //incremented getHero()
    public static Hero getHero() {
        return currentHero;
    }
    public void setImage(){
        //seting image iplementation...
    }
}
