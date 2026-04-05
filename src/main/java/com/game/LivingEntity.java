package com.game;

import java.util.ArrayList;

import javafx.geometry.Point2D;

public abstract class LivingEntity extends Entity {
    public int maxHealth;
    public double health;
    public double armor;
    public double damage;
    public double walkSpeed;
    public boolean isLookingRight = true;
    public double attackSpeed;
    public double range;
    public double fear;
    public LivingType lType;
    public ArrayList<Effect> effects = new ArrayList<>();

    public static LivingType[] livingTypes = new LivingType[]{LivingType.WALKER, LivingType.BOMBER, LivingType.SKELETON};

    enum LivingStates {
        ATTACK, HEAL, TAKE_DAMAGE, DIE; 
    }

    public enum LivingType {
        HERO(new Point2D(48, 48), 100, 0, 10, 10, 0.8, 0, 0),
        WALKER(new Point2D(24, 24), 10, 0, 2, 10, 0.9, 1, 2),
        BOMBER(new Point2D(36,36), 50, 10, 20, 7, 0.2, 1, 30),
        SKELETON(new Point2D(96, 96), 75, 5, 1, 8, 0.3, 1, 10)
        ;

        void attack(LivingEntity targetEntity) {}
        // TODO boolean isLookingRight in Drawer for knowing if walking animation will be reversed or not.

        private Point2D size;
        private int maxHealth;
        private double health;
        private double armor;
        private double damage;
        private double walkSpeed;
        private double attackSpeed;
        private double fear;
        private double range;

        private LivingType(Point2D size, int maxHealth, double armor, double damage, double walkSpeed, double attackSpeed, double fear, double range) {
            this.size = size;
            this.maxHealth = maxHealth;
            this.health = maxHealth;
            this.armor = armor;
            this.damage = damage;
            this.walkSpeed = walkSpeed;
            this.attackSpeed = attackSpeed;
            this.fear = fear;
            this.range = range;
        }
    }

    public LivingEntity(LivingType lType, Point2D position, Area currentArea, double diffMulti) {
        super(new Dimension(position.getX(), position.getY(), lType.size.getX(), lType.size.getY()), currentArea);

        this.maxHealth = (int)(lType.maxHealth * diffMulti);
        this.health = this.maxHealth;
        this.armor = lType.armor * diffMulti;
        this.damage = lType.damage * diffMulti;
        this.walkSpeed = lType.walkSpeed;
        this.attackSpeed = lType.attackSpeed;
        this.fear = lType.fear;
        this.lType = lType;

    //    this.animManager = new AnimationManager(this);
    }
    public static LivingType RandomType() {
        int rand = (int) (Math.random()*livingTypes.length);
        return livingTypes[rand];
    }

    public void update() {
        for (Effect effe : effects) {
            effe.affectEntity();
            animManager.playAnim(effe.getEffectType());
        }
    }

    public void updateLookDirection(double dx) {
        isLookingRight = Math.signum(dx) >= 0;
    }

    public void move(double dx, double dy) {
        updateLookDirection(dx);

        dimension.moveByWithCollision(currentArea, dx, dy);
    }
    
    public void move(Point2D velocity) {
        updateLookDirection(velocity.getX());

        dimension.moveByWithCollision(currentArea, velocity);
    }

    public void follow(LivingEntity targetEntity) {
        Point2D direction = findTargetDirection(targetEntity);

        move(direction.multiply(walkSpeed));
    }

    public void attack() {}

    @Override
    public void despawn() {
        super.despawn(); 
    }

    public void heal(double healAmount) {
        this.health = Math.min(this.health+healAmount, this.maxHealth);
        // + heal animation
    }

    public void getDamaged(double damage){
        this.health = Math.max(this.health+damage, 0);
        animManager.playAnim(LivingStates.TAKE_DAMAGE);

        if (this.health == 0){
            animManager.playAnim(LivingStates.DIE);
            // if hero > lose the game
            // if enemy > despawn
            if (this.lType != LivingType.HERO) {
                despawn();
            }
        }
    }

    public LivingType getLivingType() {
        return this.lType;
    }
}