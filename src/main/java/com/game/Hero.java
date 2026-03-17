package com.game;

public class Hero extends LivingEntity {

    private Talisman[] talismans = new Talisman[3];
    private Consumable[] consumables = new Consumable[3];
    private Weapon[] weapons = new Weapon[2];

    public Hero(int x, int y, Sword starterSword, double diffMulti) {
        super(x, y, LivingType.HERO, diffMulti);

        weapons[0] = starterSword;
    }
}
