package com.game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class Hero extends LivingEntity {
    //class variables
    private static Hero CurrentHero;
    private static 

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
                dimension.moveBy(new Point2D(0, -walkSpeed));
            case 2: // right
                dimension.moveBy(new Point2D(walkSpeed, 0));
            case 3: // down
                dimension.moveBy(new Point2D(0, walkSpeed));
            case 4: // left
                dimension.moveBy(new Point2D(-walkSpeed, 0));
        }
    }

    @Override
    public void update() {
        // Efektleri güncelle (zehir, yanma vs.)
        for (int i = effects.size() - 1; i >= 0; i--) {
            effects.get(i).affectEntity();
            if (effects.get(i).getRemainingDuration() <= 0) {
                effects.remove(i);
            }
        }
        // Ölüp ölmediğini kontrol et
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
