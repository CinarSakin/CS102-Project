package com.game;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public abstract class LivingEntity extends Entity {
    public int maxHealth;
    public double health;
    public double armor;
    public double damage;
    public double walkSpeed;
    public boolean isLookingRight = true;
    public double attackSpeed;
    public double attackRange;
    public double range;
    public double fear;
    public LivingType lType;
    public ArrayList<Effect> effects = new ArrayList<>();

    public static LivingType[] livingTypes = new LivingType[]{LivingType.WALKER, LivingType.BOMBER, LivingType.SKELETON};

    enum LivingStates {
        ATTACK, HEAL, TAKE_DAMAGE, DIE;
    }

    public enum LivingType {
        HERO(new Point2D(48, 48), 100, 0, 10, 10, 0.8, 0, 0, 5),
        WALKER(new Point2D(24, 24), 10, 0, 2, 10, 0.9, 1, 2, 5),
        BOMBER(new Point2D(36,36), 50, 10, 20, 7, 0.2, 1, 3, 5),
        SKELETON(new Point2D(96, 96), 75, 5, 1, 8, 0.3, 1, 1, 5),
        ;

        void attack(LivingEntity targetEntity) {}

        private Point2D size;
        private int maxHealth;
        private double health;
        private double armor;
        private double damage;
        private double walkSpeed;
        private double attackSpeed;
        private double fear;
        private double attackRange;
        private double range;

        private Image imageToDraw;

        private LivingType(Point2D size, int maxHealth, double armor, double damage, double walkSpeed, double attackSpeed, double fear, double attackRange, double range) {
            this.size = size;
            this.maxHealth = maxHealth;
            this.health = maxHealth;
            this.armor = armor;
            this.damage = damage;
            this.walkSpeed = walkSpeed;
            this.attackSpeed = attackSpeed;
            this.fear = fear;
            this.attackRange = attackRange;
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
        this.attackRange = lType.attackRange;
        this.range= lType.range;
        this.fear = lType.fear;
        this.lType = lType;

        this.imageToDraw = lType.imageToDraw;
    }
    public static LivingType RandomType() {
        int rand = (int) (Math.random()*livingTypes.length);
        return livingTypes[rand];
    }

    @Override
    public void update(double dt) {
        for (Effect effe : effects) {
            effe.affectEntity();
            //animManager.setCurrentAnim(effe.getEffectType());
        }
        //animManager.update(dt);
    }

    public void updateLookDirection(double dx) {
        if (dx > 0) isLookingRight = true;
        else if (dx < 0) isLookingRight = false;
    }

    public void move(double dx, double dy) {
        updateLookDirection(dx);

        if (dx != 0) {
            dimension.moveBy(dx, 0);
            
            if (!isValidPosition()) { 
                dimension.moveBy(-dx, 0);
            }
        }

        if (dy != 0) {
            dimension.moveBy(0, dy);
            
            if (!isValidPosition()) { 
                dimension.moveBy(0, -dy);
            }
        }

        Point2D center = dimension.getCenter();
        Area areaAtCenter = Dimension.findAreaAt(center);
    
        if (areaAtCenter != null && areaAtCenter != currentArea) {
            currentArea.unregister(this);
            currentArea = areaAtCenter;
            currentArea.register(this);
        }
    }
    
    public void move(Point2D velocity) {
        move(velocity.getX(), velocity.getY());
    }

    public boolean isValidPosition() {
        double footTopY = dimension.getBottomY() - (dimension.getHeight() * 0.3);

        return Dimension.findAreaAt(new Point2D(dimension.getX(), footTopY)) != null &&
               Dimension.findAreaAt(new Point2D(dimension.getRightX(), footTopY)) != null &&
               Dimension.findAreaAt(new Point2D(dimension.getX(), dimension.getBottomY())) != null &&
               Dimension.findAreaAt(new Point2D(dimension.getRightX(), dimension.getBottomY())) != null;
    }

    private Point2D getLeadingPoint(double dx, double dy) {
        double lx = (dx >= 0) ? dimension.getRightX() : dimension.getX();
        double ly = (dy >= 0) ? dimension.getBottomY() : dimension.getY();
        return new Point2D(lx, ly);
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
        // + take_damage animation

        if (this.health == 0){
            AnimationManager.updateImage(this, LivingStates.DIE);
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