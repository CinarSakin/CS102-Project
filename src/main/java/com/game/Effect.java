package com.game;

import com.game.LivingEntity.LivingType;

public class Effect {
    private EffectType effectType;
    private long remainingDuration;
    private LivingEntity targetEntity;

    enum EffectType {
        BURN {
            @Override
            public void startEffect(LivingEntity targetEntity) {
                targetEntity.health += -1;
            }
        },
        FREEZE {
            @Override
            public void startEffect(LivingEntity targetEntity) {
                targetEntity.walkSpeed += -1; 
                targetEntity.attackSpeed += -1;
            }
        },
        STUN {
            @Override
            public void startEffect(LivingEntity targetEntity) {
                targetEntity.walkSpeed = 0; 
                targetEntity.attackSpeed = 0;
            }
        },
        HEAL {
            @Override
            public void startEffect(LivingEntity targetEntity) {
                targetEntity.health += 1;
            }
        };

        abstract void startEffect(LivingEntity targetEntity);
    }

    public Effect(EffectType effectType, long remainingDuration, LivingEntity targetEntity) {
        this.effectType = effectType;
        this.remainingDuration = remainingDuration ;
        this.targetEntity = targetEntity;
    }

    public void startEffect() {
        effectType.startEffect(targetEntity);
    }
}