package com.game;

import javafx.geometry.Point2D;

public class Hero extends LivingEntity {
    //class variables
    private static Hero CurrentHero;

    private Talisman[] talismans = new Talisman[3];
    private Consumable[] consumables = new Consumable[3];
    private Weapon[] weapons = new Weapon[2];
    private int heldWeapon;

    public Hero(Point2D position, Sword starterSword, double diffMulti) {
        super(LivingType.HERO, position, diffMulti);

        weapons[0] = starterSword;
        this.heldWeapon = 0;
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
        return CurrentHero;
    }
}
