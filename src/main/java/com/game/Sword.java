package com.game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class Sword extends Weapon {
    private SwordType swordType;

    public enum SwordType {
        FLAMING(
            new Image("flaming_sword.png"),
            "Flaming Sword",
            "It Burns.",
            1.5,
            10
        ),
        ICY(
            new Image("icy_sword.png"),
            "Icy Sword",
            "Freezes the enemies!",
            1.5,
            10
        );
    
        public final Image image;
        public final String name;
        public final String description;
        public final double attackSpeed;
        public final double damage;
    
        SwordType(Image image, String name, String description, double attackSpeed, double damage) {
            this.image = image;
            this.name = name;
            this.description = description;
            this.attackSpeed = attackSpeed;
            this.damage = damage;
        }
    }

    public Sword(SwordType swordType, double damageMultiplier) {
        super(swordType.image, swordType.name, swordType.description, swordType.attackSpeed, swordType.damage*damageMultiplier);
        this.swordType = swordType;
    }

    @Override
    public void use() {
        Point2D heroPos = Hero.getHero().getDimension().getPos();
        Dimension hitBox = new Dimension(heroPos.getX() + 20, heroPos.getY(), 30, 30);

        for (LivingEntity target : LivingEntityManager.getLivingEntities()) {
            if (target != Hero.getHero() && target.getDimension().intersects(hitBox)) {
                target.getDamaged(this.damage);

                if (this.swordType == SwordType.FLAMING) {
                    new Effect(Effect.EffectType.BURN, 3000, target).startEffect();
                } else if (this.swordType == SwordType.ICY) {
                    new Effect(Effect.EffectType.FREEZE, 2000, target).startEffect();
                }
            }
        }
    }

    @Override
    public void draw() {
        // ToDo
    }
}
