package com.game;

public class Effect {
    private transient AnimationManager animManager;

    private EffectType effectType;
    private long remainingDuration;
    private transient LivingEntity targetEntity;

    public enum EffectType {
        FEAR {
            @Override
            public void affectEntity(LivingEntity targetEntity) {
                if (targetEntity instanceof Enemy) ((Enemy)targetEntity).flee();
            }
        },
        TIRE { // for cooldowns and stun
            @Override
            public void affectEntity(LivingEntity targetEntity) {
            //    ((Enemy)targetEntity).setCanAttack(false);
            }
        },
        STUN {
            @Override
            public void affectEntity(LivingEntity targetEntity) {
                targetEntity.walkSpeed = 0; 
                targetEntity.attackSpeed = 0;
                TIRE.affectEntity(targetEntity);
            }
        },
        BURN {
            @Override
            public void affectEntity(LivingEntity targetEntity) {
                targetEntity.getDamaged(5);
            }
        },
        FREEZE {
            @Override
            public void affectEntity(LivingEntity targetEntity) {
                targetEntity.walkSpeed += -1;
                targetEntity.attackSpeed += -1;
            }
        },

        HEAL {
            @Override public void affectEntity(LivingEntity targetEntity) { targetEntity.health += 10; }
        },
        SPEED_UP{
            @Override public void affectEntity(LivingEntity targetEntity) { targetEntity.walkSpeed += 10; }
        },
        ATK_SPEED_UP{
            @Override public void affectEntity(LivingEntity targetEntity) { targetEntity.attackSpeed += 10; }
        },
        DMG_UP{
            @Override public void affectEntity(LivingEntity targetEntity) { targetEntity.damage += 10; }
        },
        ;

        abstract void affectEntity(LivingEntity targetEntity);
    }

    public Effect(EffectType effectType, long remainingDuration, LivingEntity targetEntity) {
        this.effectType = effectType;
        this.remainingDuration = remainingDuration ;
        this.targetEntity = targetEntity;
    }

    public void startEffect() {
        affectEntity();
        targetEntity.effects.add(this);
    }

    public void affectEntity() {
        effectType.affectEntity(targetEntity);
        remainingDuration += -100;

        if (remainingDuration < 0) { stopEffect(); }
    }

    public void stopEffect() {
        targetEntity.effects.remove(this);
    }

    public long getRemainingDuration() {
        return remainingDuration;
    }

    public Effect.EffectType getEffectType() { return effectType; }

    public void setTargetEntity(LivingEntity entity) {
        this.targetEntity = entity;
    }
}