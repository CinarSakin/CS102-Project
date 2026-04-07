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
    

    @Override
    public void update(double dt) {
        super.update(dt);
        if(weapons[heldWeapon] != null){
            weapons[heldWeapon].updateTimer(dt);
        }
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
        if (consumables[index] == null) return;
        dropItem(consumables[index], x, y);
        consumables[index] = null;
    }

    public void dropTalisman(int index, double x, double y) {
        if (talismans[index] == null) return;
        dropItem(talismans[index], x, y);
        talismans[index] = null;
    }

    public void dropWeapon(int index, double x, double y) {
        if (weapons[0] == null || weapons[1] == null) return;
        dropItem(weapons[index], x, y);
        weapons[index] = null;
        if (heldWeapon == index) heldWeapon = 1 - index;
    }

    public void dropItem(Item item, double x, double y) {
        // TODO
        new DroppedItem(new Point2D(x, y), currentArea, item);
    }
    
    public boolean addItem(Item item) {
        if (item instanceof Talisman) {
            for (int i = 0; i < TALISMAN_AMOUNT; i++) {
                if (talismans[i] == null) {
                    talismans[i] = (Talisman) item;
                    return true;
                }
            }
        } else if (item instanceof Consumable) {
            for (int i = 0; i < CONSUMABLE_AMOUNT; i++) {
                if (consumables[i] == null) {
                    consumables[i] = (Consumable) item;
                    return true;
                }
            }
        } else if (item instanceof Weapon) {
            if (weapons[0] == null){
                weapons[0] = (Weapon)item;
                return true;  
            }
            if (weapons[1] == null){
                weapons[1] = (Weapon)item;
                return true;
            }
        } 
        return false;
    }

    //incremented getHero()
    public static Hero getHero() {
        return currentHero;
    }
    public void setImage(){
        //seting image iplementation...
    }
}
