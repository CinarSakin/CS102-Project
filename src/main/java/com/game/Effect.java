package com.game;

/*
maybe we can make Effect an interface, because we need a different startEffect() each time.
We can make Entities implement "Effectable" and set a bool isEffected and call startEffect()
if true when something needs to burn messelaaa yağni

same with attack() for enemies and interact() for WorldObjects.
*/
public class Effect {
    private int effectType; // enumeration
    private long remaningDuration;
    private LivingEntity targetEntity;

    public Effect(int effectType, long remaningDuration, LivingEntity targetEntity) { // effectType yerine enum da olabilir yine int gerçi
        this.effectType = effectType;
        this.remaningDuration =remaningDuration ;
        this.targetEntity = targetEntity ;
    }

    public void startEffect() {

    }
}
