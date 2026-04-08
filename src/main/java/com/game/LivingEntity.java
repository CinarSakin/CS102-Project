package com.game;

import java.util.ArrayList;

import com.game.Effect.EffectType;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public abstract class LivingEntity extends Entity {
    private double timeSinceLastAttack = 0;
    private boolean isMovingThisFrame = false;

    public int maxHealth;
    public double health;
    public double armor;
    public double damage;
    public double walkSpeed;
    public double attackSpeed;
    public double attackRange;
    public double range = 11*Level.gridSize;
    public double fear;
    public LivingType lType;
    public ArrayList<Effect> effects = new ArrayList<>();
    public transient ArrayList<LivingStateObject> currentStates = new ArrayList<LivingStateObject>();

    public static LivingType[] livingTypes = new LivingType[]{LivingType.WALKER, LivingType.BOMBER, LivingType.SKELETON};

    public class LivingStateObject {
        public enum LivingState { // sort priority ascending
            TAKE_DAMAGE(.25, false),
            HEAL(.3, false),
            IDLE(.5, true),
            WALKING(.4, true),
            ATTACK(.3, false),
            DIE(2, true);

            public double animDuration;
            public boolean isLooping;

            private LivingState(double animDuration, boolean isLooping) {
                this.animDuration = animDuration;
                this.isLooping = isLooping;
            }
        }

        public LivingState state;
        private double elapsedTime = 0;

        public LivingStateObject(LivingState state) {
            this.state = state;
            if (state.isLooping) {
                if (currentStates.stream().anyMatch(s -> s.state == state)) return;
            } else {
                currentStates.removeIf(s -> s.state == state);
            }
            currentStates.add(this);
        }

        public void update(double dt) {
            elapsedTime += dt;
            if (state.isLooping) {
                elapsedTime %= state.animDuration;
            } else if (elapsedTime >= state.animDuration) {
                currentStates.remove(this);
            }
        }

        public double getElapsedTime() {
            return elapsedTime;
        }
    }

    public enum LivingType {
        HERO(new Point2D(48, 48), 100, 0, 10, 7, 0.8, 0, 0),
        WALKER(new Point2D(48, 48), 21, 1, 2, 5, 0.75, 1, 1*Level.gridSize),
        BOMBER(new Point2D(48,84), 42, 3, 15, 3.5, 0.15, 1, 3*Level.gridSize),
        SKELETON(new Point2D(48, 48), 21, 2, 1, 3.5, 0.3, 1, 5*Level.gridSize),

        BOSS(new Point2D(80, 80), 100, 10, 25, 1.25, 0.2, 0, 7*Level.gridSize);

        void attack(LivingEntity targetEntity) {}

        public Point2D size;
        private int maxHealth;
        private double health;
        private double armor;
        private double damage;
        private double walkSpeed;
        private double attackSpeed;
        private double fear;
        private double attackRange;
        private Image imageToDraw;

        private LivingType(Point2D size, int maxHealth, double armor, double damage, double walkSpeed, double attackSpeed, double fear, double attackRange) {
            this.size = size;
            this.maxHealth = maxHealth;
            this.health = maxHealth;
            this.armor = armor;
            this.damage = damage;
            this.walkSpeed = walkSpeed;
            this.attackSpeed = attackSpeed;
            this.fear = fear;
            this.attackRange = attackRange;
        }
    }

    public LivingEntity(LivingType lType, Point2D position, Area currentArea, double diffMulti) {
        super(new Dimension(position.getX(), position.getY(), lType.size.getX(), lType.size.getY()), currentArea);
        this.maxHealth = (int)(lType.maxHealth * diffMulti);
        this.health = this.maxHealth* (1+(diffMulti/10));
        this.armor = lType.armor * (1+(diffMulti/10));
        this.damage = lType.damage * (1+(diffMulti/10));
        this.walkSpeed = lType.walkSpeed;
        this.attackSpeed = lType.attackSpeed;
        this.attackRange = lType.attackRange;
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
        if (currentStates == null) currentStates = new ArrayList<>();
        timeSinceLastAttack += dt;

        // animation
        if (isMovingThisFrame) {
            setLocomotionState(LivingStateObject.LivingState.WALKING);
        } else {
            setLocomotionState(LivingStateObject.LivingState.IDLE);
        }
        isMovingThisFrame = false;

        for (LivingStateObject s : new ArrayList<>(currentStates)) {
            s.update(dt);
        }

        AnimationManager.updateImage(this);

        // effects
        for (Effect effect : new ArrayList<>(effects)) {
            effect.update(dt);
        }

        // reset and then apply effects
        walkSpeed = getLivingType().walkSpeed*dt*Level.gridSize;
        damage = getLivingType().damage;

        for (Effect e : new ArrayList<Effect>(effects)) {
            switch (e.getEffectType()) {
                case STUN:
                    walkSpeed = 0;
                    attackSpeed *= 0.1;
                    break;
                case FREEZE:
                    walkSpeed *= 0.15;
                    attackSpeed *= 0.1;
                    break;
                case FEAR:
                    walkSpeed *= -1;
                    break;
                case SPEED_UP:
                    walkSpeed *= 1.5;
                    break;
                case ATK_SPEED_UP:
                    attackSpeed *= 1.5;
                    break;
                case DMG_UP:
                    damage *= 1.5;
                    break;
            }
        }

    }

    public void addEffect(EffectType effectType, double duration) {
        for (Effect e : effects) {
            if (e.getEffectType() == effectType) {
                e.remainingDuration += duration;
                return;
            }
        }
        effects.add(new Effect(this, effectType, duration));
    }

    public void setEffect(EffectType effectType, double duration) {
        for (Effect e : effects) {
            if (e.getEffectType() == effectType) {
                e.remainingDuration = duration;
                return;
            }
        }
        effects.add(new Effect(this, effectType, duration));
    }

    private boolean hasEffect(EffectType type) {
        for (Effect e : effects) {
            if (e.getEffectType() == type) return true;
        }
        return false;
    }

    public boolean canAttack() {
        return timeSinceLastAttack > 1/lType.attackSpeed;
    }

    public void resetAttack() {
        timeSinceLastAttack = 0;
    }

    public void updateLookDirection(double dx) {
        if (dx > 0) isFlipped = false;
        else if (dx < 0) isFlipped = true;
    }

    public void moveBy(double dx, double dy) {
        if (dx != 0 || dy != 0) isMovingThisFrame = true;
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

    public void moveBy(Point2D velocity) {
        moveBy(velocity.getX(), velocity.getY());
    }

    public void moveByDirection(double dx, double dy) {
        moveBy(dx*walkSpeed, dy*walkSpeed);
    }

    public void moveByDirection(Point2D direction) {
        moveByDirection(direction.getX(), direction.getY());
    }
    

    public boolean isValidPosition() {
        double footTopY = dimension.getBottomY() - (dimension.getHeight() * 0.3);

        return Dimension.findAreaAt(new Point2D(dimension.getX(), footTopY)) != null &&
               Dimension.findAreaAt(new Point2D(dimension.getRightX(), footTopY)) != null &&
               Dimension.findAreaAt(new Point2D(dimension.getX(), dimension.getBottomY())) != null &&
               Dimension.findAreaAt(new Point2D(dimension.getRightX(), dimension.getBottomY())) != null;
    }

    public void follow(LivingEntity targetEntity) {
        Point2D direction = findTargetDirection(targetEntity);

        moveByDirection(direction);
    }

    public void setLocomotionState(LivingStateObject.LivingState state) {
        if (state == LivingStateObject.LivingState.WALKING) {
            currentStates.removeIf(s -> s.state == LivingStateObject.LivingState.IDLE);
        } else if (state == LivingStateObject.LivingState.IDLE) {
            currentStates.removeIf(s -> s.state == LivingStateObject.LivingState.WALKING);
        }
        new LivingStateObject(state);
    }

    public void attack() {}

    @Override
    public void despawn() {
        super.despawn(); 
    }

    public void heal(double healAmount) {
        this.health = Math.min(this.health+healAmount, this.maxHealth);
        new LivingStateObject(LivingStateObject.LivingState.HEAL);
    }

    public void getDamaged(double damage){
        this.health = Math.max(this.health-(damage-armor), 0);
        this.armor = Math.max(armor-2, 0);

        new LivingStateObject(LivingStateObject.LivingState.TAKE_DAMAGE);

        if (this.health == 0){
            GameStats.getInstance().enemiesKilled++;
            new LivingStateObject(LivingStateObject.LivingState.DIE);
            AnimationManager.updateImage(this);
            // if hero > lose the game
            // if enemy > despawn
            if (this.lType != LivingType.HERO) {
                if(this instanceof Boss){
                    GameStats.getInstance().bossKilled++;
                    new Portal(Level.getLevel().bossRoom.getDimension().getCenter(),Level.getLevel().bossRoom);
                }
                despawn();
            }
        }
    }

    public LivingType getLivingType() {
        return this.lType;
    }
}