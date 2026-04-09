package com.game;

public class Effect {

    private EffectType effectType;
    public double remainingDuration;
    public double timeSinceLastEffect = 0;
    private transient LivingEntity targetEntity;

    public enum EffectType {
        FEAR(-1) {
            @Override
            public void affectEntity(LivingEntity targetEntity) {
                if (targetEntity instanceof Enemy) ((Enemy)targetEntity).flee();
            }
        },
        KNOCKBACK(-1),
        STUN(-1),
        BURN(1) {
            @Override
            public void affectEntity(LivingEntity targetEntity) {
                targetEntity.getDamaged(3);
            }
        },
        FREEZE(-1) {},

        HEAL(2) {
            @Override public void affectEntity(LivingEntity targetEntity) { targetEntity.heal(10);}
        },
        SPEED_UP(-1){
            @Override public void affectEntity(LivingEntity targetEntity) { targetEntity.walkSpeed += 10; }
        },
        ATK_SPEED_UP(-1){
            @Override public void affectEntity(LivingEntity targetEntity) { targetEntity.attackSpeed += 10; }
        },
        DMG_UP(-1){
            @Override public void affectEntity(LivingEntity targetEntity) { targetEntity.damage += 10; }
        };

        /** Seconds between each affectEntity() call. -1 means affectEntity is never called. */
        public double affectInterval;

        private EffectType(double affectInterval) {
            this.affectInterval = affectInterval;
        }

        void affectEntity(LivingEntity targetEntity){};
    }

    public Effect(LivingEntity targetEntity, EffectType effectType, double duration) {
        this.targetEntity = targetEntity;
        this.effectType = effectType;
        this.remainingDuration = duration;
        this.timeSinceLastEffect = effectType.affectInterval;
    }

    public void update(double dt) {
        remainingDuration -= dt;
        if (remainingDuration < 0) {
            this.targetEntity.effects.remove(this);
            return;
        }

        timeSinceLastEffect += dt;
        
        if (effectType.affectInterval != -1 && timeSinceLastEffect >= effectType.affectInterval) {
            effectType.affectEntity(targetEntity);
            timeSinceLastEffect = 0;
        }

    }

    public double getRemainingDuration() {
        return remainingDuration;
    }

    public Effect.EffectType getEffectType() { return effectType; }

    public void setTargetEntity(LivingEntity entity) {
        this.targetEntity = entity;
    }
}