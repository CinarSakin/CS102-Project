package com.game;

public class Hero extends LivingEntity {
    private Talisman[] talismans;
    private Consumable[] consumables;
    private int inventorySpace;
    private Weapon primaryWeapon;
    private Weapon secondaryWeapon;
    // there might be a weapons = new Weapon[]{new Weapon(SWORD), new Weapon(BOW)}
    // do talismans and consumables share the same inventory space, or are they separate.

    public Hero(int x, int y, int maxHp, int armor, int baseDmg, int atkSpeed, int inventorySpace, Weapon primaryWeapon, Weapon secondaryWeapon) {
        super(x, y, maxHp, armor, baseDmg, atkSpeed);

        talismans = new int[inventorySpace];
        consumables = new int[inventorySpace];
        this.primaryWeapon = primaryWeapon;
        this.secondaryWeapon = secondaryWeapon;
    }

    public Hero(int x, int y, LivingType LT) {
        super(x, y, LT);
    }
}
