package com.game;

public class Hero extends LivingEntity {
    private Talisman[] talismans;
    private Consumable[] consumables;
    private int inventorySpace;
    private Weapon[] weapons; // weapons[0]= SWORD, weapons[1] = BOW
    // do talismans and consumables share the same inventory space, or are they separate?

    public Hero(int x, int y, int inventorySpace, Weapon primaryWeapon, Weapon secondaryWeapon) {
        super(x, y, LivingType.HERO);

        talismans = new Talisman[inventorySpace];
        consumables = new Consumable[inventorySpace];
        weapons[0] = primaryWeapon;
        weapons[1] = secondaryWeapon;
    }
}
