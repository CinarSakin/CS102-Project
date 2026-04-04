package com.game;

import javafx.geometry.Point2D;

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

    public void move(int dir) {
        switch (dir) {
            case 1: // up
                move(new Point2D(0, -walkSpeed));
            case 2: // right
                move(new Point2D(walkSpeed, 0));
                isLookingRight = true;
            case 3: // down
                move(new Point2D(0, walkSpeed));
            case 4: // left
                move(new Point2D(-walkSpeed, 0));
                isLookingRight = false;
        }
    }

    @Override
    public void update() {
        // todo
    }

    @Override
    public void attack() {
        weapons[heldWeapon].use();
    }

    //incremented getHero()
    public static Hero getHero() {
        if(CurrentHero != null)return CurrentHero;
        return CurrentHero = new Hero(null, null, 0, null);
       
    }
}
