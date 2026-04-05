package com.game;

import javafx.geometry.Point2D;

public class Hero extends LivingEntity {
    private static Hero CurrentHero;
    

    private Talisman[] talismans = new Talisman[3];
    private Consumable[] consumables = new Consumable[3];
    private Weapon[] weapons = new Weapon[2];
    private int heldWeapon;

    public Hero(Point2D position, Sword starterSword, double diffMulti, Room currentRoom) {
        super(LivingType.HERO, position, currentRoom, diffMulti);

        weapons[0] = starterSword;
        this.heldWeapon = 0;
    }

    public void move(int dir) {
<<<<<<< HEAD
        switch (dir) {
            case 1: // up
                move(new Point2D(0, -walkSpeed));
            case 2: // right
                move(new Point2D(walkSpeed, 0));
                isLookingRight = true;
            case 3: // down
                move(new Point2D(0, walkSpeed));
            case 4: // left
                move(new Point2D(-walkSpeed, 0));
                isLookingRight = false;
=======
        
        double speed = 5.0;
        double nextX = this.getDimension().getX();
        double nextY = this.getDimension().getY();

        if (dir == 1) nextY -= speed; // Up
        if (dir == 2) nextX += speed; // Right
        if (dir == 3) nextY += speed; // Down
        if (dir == 4) nextX -= speed; // Left

        // Only update position if the new coordinates are within bounds
        if (Game.isPositionValid(this, nextX, nextY)) {
            switch (dir) {
                case 1: // up
                    dimension.moveBy(new Point2D(0, -walkSpeed));
                case 2: // right
                    dimension.moveBy(new Point2D(walkSpeed, 0));
                case 3: // down
                    dimension.moveBy(new Point2D(0, walkSpeed));
                case 4: // left
                    dimension.moveBy(new Point2D(-walkSpeed, 0));
            }
        }

        
    }

    public void checkEntityCollisions() {
        for (Room room : Level.getRooms()) {
            for (Entity e : room.getEntities()) {
                if (this.getDimension().intersects(e.getDimension())) {
                    // if (e instanceof Consumable) {
                    //     // Trigger pickup logic
                    //     ((Consumable) e).onPickup(this); 
                } else if (e instanceof Projectile) {
                    this.getDamaged(10);//TODO: change the damage later.
                }
            }
>>>>>>> game-loop-and-drawing
        }
    }
    

    @Override
    public void update() {
        
        for (int i = effects.size() - 1; i >= 0; i--) {
            effects.get(i).affectEntity();
            if (effects.get(i).getRemainingDuration() <= 0) {
                effects.remove(i);
            }
        }
        checkEntityCollisions();

        if (this.health <= 0) {
            animStates.add(LivingAnimStates.DIE);
            // Oyun bitiş ekranını tetikle
        }
    }

    @Override
    public void attack() {
        weapons[heldWeapon].use();
    }

    //incremented getHero()
    public static Hero getHero() {
        if(CurrentHero != null)return CurrentHero;
<<<<<<< HEAD
        return CurrentHero = new Hero(null, null, 0, null);
=======
        return CurrentHero = new Hero(null, null, 0,null);
>>>>>>> game-loop-and-drawing
       
    }
    public void setImage(){
        //seting image iplementation...
    }
}
