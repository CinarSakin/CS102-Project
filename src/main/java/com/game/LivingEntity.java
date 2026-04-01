package com.game;

import java.util.ArrayList;
import javafx.geometry.Point2D;

public abstract class LivingEntity extends Entity {
    // to suppress the compiler
    public int maxHealth;
    public double health;
    public double armor;
    public double damage;
    public double walkSpeed;
    public double attackSpeed;
    public double range; // distance enemies can attack
    public double fear; // chance of enemies running away in range when they get hit.
    public LivingType livingType;
    public ArrayList<Effect> effects = new ArrayList<>();
    public ArrayList<LivingAnimStates> animStates = new ArrayList<>();
    public static LivingType[] livingTypes = new LivingType[]{LivingType.WALKER, LivingType.BOMBER, LivingType.SKELETON};

    enum LivingAnimStates {
        DIE(3),
        ATTACK(2),
        GET_BUFFED(2), // might be transparent
        WALKING_UP(1),
        WALKING_DOWN(1),
        WALKING_RIGHT(1),
        WALKING_LEFT(1),
        IDLE(1),
        TAKE_DAMAGE(0), // might be transparent
        ;
        /*
        DIE > ATTACK > WALKING and IDLE > TAKE_DAMAGE

        anims with lower priority can't play over anims above them.
        ATTACK can't play while DIE         because DIE > ATTACK
        IDLE   can play   while TAKE_DAMAGE because IDLE > TAKE_DAMAGE
        */

        private int priority;
        private LivingAnimStates(int priority) {
            this.priority = priority;
        }
    }

    enum LivingType {
        HERO(new Point2D(48, 48), 100, 0, 10, 10, 0.8, 0, 0),
        WALKER(new Point2D(24, 24), 10, 0, 2, 10, 0.9, 1, 2),
        BOMBER(new Point2D(36,36), 50, 10, 20, 7, 0.2, 1, 30),
        SKELETON(new Point2D(96, 96), 75, 5, 1, 8, 0.3, 1, 10)
        ;

        void attack(LivingEntity targetEntity) {}

        // add other enemy types
        
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

    public LivingEntity(LivingType livingType, Point2D position, double diffMulti) {
        super(new Dimension(position.getX(), position.getY(), livingType.size.getX(), livingType.size.getY()));

        this.maxHealth = (int)(livingType.maxHealth * diffMulti);
        this.health = this.maxHealth;
        this.armor = livingType.armor * diffMulti;
        this.damage = livingType.damage * diffMulti;
        this.walkSpeed = livingType.walkSpeed;
        this.attackSpeed = livingType.attackSpeed;
        this.fear = livingType.fear;
        
        animStates.add(LivingAnimStates.IDLE);
        LivingEntityManager.register(this);
    }
    public static LivingType RandomType() {
        int rand = (int) (Math.random()*livingTypes.length);
        return livingTypes[rand];
    }

    public void update() {
        for (Effect effe : effects) {
            if (effe.getRemainingDuration() < 0) {
                effects.remove(effe);
            }

            effe.affectEntity();
        }

        if (livingType.equals(LivingType.HERO)) {
            ((Hero)this).update();
        }
        else {
            ((Enemy)this).update();
        }
        draw();
    }

    public void follow(LivingEntity targetEntity) {
        Point2D direction = findTargetDirection(targetEntity);

        dimension.moveBy(direction.multiply(walkSpeed));
    }

    public void attack() {}

    @Override
    public void draw() {
        // selecting animation to play
        LivingAnimStates animToPlay = animStates.get(0); // IDLE

        // finding largest priority
        // TODO only one anim can play at a time. maybe a arraylist in anim for that
        for (LivingAnimStates anim : animStates) {
            if (animToPlay.priority < anim.priority) {
                animToPlay = anim;
            }
        }

        Animation.playAnim(animToPlay.name());
    }

    @Override
    public void despawn() {
        super.despawn(); 
        LivingEntityManager.unregister(this);
    }

    public void heal(double healAmount) {
        this.health = Math.min(this.health+healAmount, this.maxHealth);
        // + heal animation
    }

    public void getDamaged(double damage){
        this.health = Math.max(this.health+damage, 0);
        animStates.add(LivingAnimStates.TAKE_DAMAGE);

        if (this.health == 0){
            animStates.clear();
            animStates.add(LivingAnimStates.DIE);

            // if hero > lose the game
            // if enemy > despawn
        }
    }
}
