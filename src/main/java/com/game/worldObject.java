package com.game;

import javafx.geometry.Point2D;

public class worldObject extends Entity {
    
    public enum WorldObjectType {
        GATE,
        DROPPED_ITEM,
        CHEST
    }
    
    public WorldObjectType type;
    public Item item;
    public boolean interacted;
    public boolean open;
    public boolean unlocked;
    public double interactRadius;
    
    public worldObject(Point2D position, WorldObjectType type, Item item, Area currentArea) {
        super(new Dimension(position.getX(), position.getY(), 32, 32), currentArea);
        this.type = type;
        this.item = item;
        this.interacted = false;
        this.open = false;
        this.unlocked = false;
        
        // Circular interaction radius
        this.interactRadius = 64;
    }
    
    public boolean isHeroInRange(Hero hero) {
        Point2D objectCenter = new Point2D(
            dimension.getX() + dimension.getWidth() / 2,
            dimension.getY() + dimension.getHeight() / 2
        );
        
        Point2D heroCenter = new Point2D(
            hero.getDimension().getX() + hero.getDimension().getWidth() / 2,
            hero.getDimension().getY() + hero.getDimension().getHeight() / 2
        );
        
        double distance = objectCenter.distance(heroCenter);
        return distance <= interactRadius;
    }
    public void interact(Hero hero) {
        if (interacted) return;
        
        switch (type) {
            case DROPPED_ITEM:
                pickupItem(hero);
                break;
            case CHEST:
                openChest(hero);
                break;
            case GATE:
                unlockGate(hero);
                break;
        }
    }
    
    private void pickupItem(Hero hero) {
        if (item == null) return;
        
        if (item instanceof Weapon) {
            // Add weapon to hero inventory
        } else if (item instanceof Consumable) {
            // Add consumable to hero inventory
        } else if (item instanceof Talisman) {
            // Add talisman to hero inventory
        }
        
        interacted = true;
        currentArea.unregister(this);
    }
    
    private void openChest(Hero hero) {
        if (open) return;
        
        open = true;
        interacted = true;
        
        if (item == null) return;
        
        if (item instanceof Weapon) {
            // Add weapon to hero inventory
        } else if (item instanceof Consumable) {
            // Add consumable to hero inventory
        } else if (item instanceof Talisman) {
            // Add talisman to hero inventory
        }
    }
    
    private void unlockGate(Hero hero) {
        if (unlocked) return;
        
        unlocked = true;
        interacted = true;
    }

    @Override
    public void update(double dt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
