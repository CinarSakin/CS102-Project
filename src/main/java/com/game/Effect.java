package com.game;

public class Effect {
    private AnimationManager animManager;

    private EffectType effectType;
    private long remainingDuration;
    private LivingEntity targetEntity;

    public enum EffectType {
        FEAR {
            @Override
            public void affectEntity(LivingEntity targetEntity) {
                ((Enemy)targetEntity).flee(); // could be nice if it was applicable to hero too
            }
        },
        TIRE { // for cooldowns and stun
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

        this.animManager = new AnimationManager(effectType);
    }

    public static void startEffect(Effect effe) {
        effe.affectEntity();
        effe.targetEntity.effects.add(effe);

        effe.animManager.playAnim(effe.effectType);
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
}