package com.game;

public class Effect {
    private EffectType effectType;
    private long remainingDuration;
    private LivingEntity targetEntity;

    enum EffectType {
        FEAR {
            @Override
            public void affectEntity(LivingEntity targetEntity) {
                ((Enemy)targetEntity).flee(); // could be nice if it was applicable to hero too
            }
        },
        TIRE {
            @Override
            public void affectEntity(LivingEntity targetEntity) {
                ((Enemy)targetEntity).setCanAttack(false);
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
            @Override
            public void affectEntity(LivingEntity targetEntity) {
                targetEntity.health += 1;
            }
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
    }

    public long getRemainingDuration() {
        return remainingDuration;
    }
}