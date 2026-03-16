package com.game;

public class Hero extends LivingEntity {

    // to silence the compiler
    public class Talisman{}
    public class Consumable{}

    private Talisman[] talismans = new Talisman[3];
    private Consumable[] consumables = new Consumable[3];
    private Weapon[] weapons = new Weapon[2];

    public Hero(int x, int y, Sword starterSword, double diffMulti) {
        super(x, y, LivingType.HERO, diffMulti);

        weapons[0] = starterSword;
    }
}
