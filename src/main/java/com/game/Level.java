package com.game;

import java.util.ArrayList;

import javafx.geometry.Point2D;

public class Level {

    // class variables
    public static int gridSize = 48;
    private static Level currentLevel;
    public static int levelNo;
    public static Hero hero;
    public static boolean heroSetIn = false;
    
    // root room boyutları (aspect ratio hesabı için)
    public static double rootLevelWidth  = 4800* (1+levelNo/10);
    public static double rootLevelHeight = 3600 * (1+levelNo/10);

    // instance variables
    private transient ArrayList<Room> rooms = new ArrayList<Room>();
    public ArrayList<Area> areas = new ArrayList<Area>();
    public Room startingRoom;
    public Room bossRoom;
    private transient Room root;
    private int savedLevelNo; // non-static copy for GSON (levelNo is static)

    private Level(int levelCount){
        this.levelNo = levelCount;
        this.savedLevelNo = levelCount;
        currentLevel = this;
        generateLevel();

    }

    // SAVEING MECHANISMS
    public static Level constructFromSave(char saveSlot) throws Exception {
        if (getLevel() != null) throw new IllegalStateException("There is already a Level instance!");

        Level loadedLevel = SaveManager.loadLevel(saveSlot);

        if (loadedLevel == null) {
            throw new Exception("Level data could not be loaded!");
        }

        levelNo = loadedLevel.savedLevelNo;
        loadedLevel.rooms = new ArrayList<>();

        Room.hHalls.clear();
        Room.vHalls.clear();

        for (Area area : loadedLevel.areas) {
            area.getDimension().setArea(area);

            area.livingEntities = new ArrayList<>();
            area.enemies = new ArrayList<>();

            if (area instanceof Room) {
                Room r = (Room) area;
                loadedLevel.rooms.add(r);
                r.hNeighbors = new ArrayList<>();
                r.vNeighbors = new ArrayList<>();
            } else if (area instanceof Hall) {
                Hall hall = (Hall) area;
                if (hall.isHorizontal) Room.hHalls.add(hall);
                else Room.vHalls.add(hall);
            }

            for (Entity e : area.getEntities()) {
                e.currentArea = area;

                if (e instanceof LivingEntity) {
                    LivingEntity le = (LivingEntity) e;
                    area.livingEntities.add(le);
                    for (Effect effect : le.effects) {
                        effect.setTargetEntity(le);
                    }
                }
                if (e instanceof Enemy) area.enemies.add((Enemy) e);

                if (e instanceof Hero) {
                    Game.hero = (Hero) e;
                    Hero.currentHero = (Hero) e;
                }

                e.reloadImages();
            }
        }

        return currentLevel = loadedLevel;
    }

    public static void resetLevel() {
        currentLevel = null;
    }

    public static Level constructNew(int levelCount) {//TODO: fix the construction because it gives a level every time.
        if (getLevel() != null) throw new IllegalStateException("There is already a Level instance!");
        new Level(levelCount);
        return currentLevel;
    }

    //CONSTRUCTING METHODS
    private void generateLevel(){
        root = new Room(144, 144, 4800, 3600);
        rootLevelWidth  = 4800;
        rootLevelHeight = 3600;
        rooms = new ArrayList<>();
    //    rooms.add(root);
        
        root.divide();
        getLeaves(root);
        findNeighbors();
        shrink();
        addHalls();      

        int index =(int) (Math.random()*rooms.size());
        rooms.get(index).setStartingRoom();
        startingRoom = rooms.get(index);
        
        int bossIndex = (int) (Math.random()*rooms.size());
        while(!rooms.get(bossIndex).setBossRoom()){bossIndex = (int) (Math.random()*rooms.size());}
        bossRoom = rooms.get(bossIndex);

        if (Game.hero == null){
            Game.hero = new Hero(
                startingRoom.getDimension().getCenter().subtract(new Point2D(8, 8)),
                Sword.STARTER_SWORD, 1, startingRoom
            );
        } else Game.hero.getDimension().moveCenterTo(startingRoom.getDimension().getCenter());

        for (Room r : rooms) {
            r.spawnEntities();
        }

        this.areas.clear();
        this.areas.addAll(this.rooms);
        this.areas.addAll(Room.getHHalls());
        this.areas.addAll(Room.getVHalls());

    }


    private void getLeaves(Room c) {
        if (c.left == null) rooms.add(c);
        else {
        getLeaves(c.left);
        getLeaves(c.right);
        }
    }

    private void findNeighbors() {
        // getLeaves(root);
        for (Room c : rooms) {
            for (Room o : rooms) {
                if (c == o) continue;
                if (c.getX2() == o.getX1() && Math.max(c.getY1(), o.getY1()) < Math.min(c.getY2(), o.getY2())) c.hNeighbors.add(o);
                if (c.getY2() == o.getY1() && Math.max(c.getX1(), o.getX1()) < Math.min(c.getX2(), o.getX2())) c.vNeighbors.add(o);
            }
        }
    }


    private void addHalls() {
        Room.hHalls.clear();
        Room.vHalls.clear();
 
        int hallThickness = gridSize * 2;
 
        for (Room c : rooms) {
 
            // HORIZONTAL NEIGHBORS (c is left, n is right)
            for (Room n : c.hNeighbors) {
                double startX = c.getX2();
                double endX   = n.getX1();
                double gapW   = endX - startX;
                if (gapW <= 0) continue;
 
                double overlapTop = Math.max(c.getY1(), n.getY1());
                double overlapBot = Math.min(c.getY2(), n.getY2());
                double overlap    = overlapBot - overlapTop;
                if (overlap < hallThickness) continue;
 
                double midY = overlapTop + (overlap - hallThickness) / 2;
                double y = c.snapToGrid(midY);
                Room.hHalls.add(new Hall(startX, y, gapW, hallThickness, true));
            }
 
            // VERTICAL NEIGHBORS (c is top, n is bottom)
            for (Room n : c.vNeighbors) {
                double startY = c.getY2();
                double endY   = n.getY1();
                double gapH   = endY - startY;
                if (gapH <= 0) continue;
 
                double overlapLeft  = Math.max(c.getX1(), n.getX1());
                double overlapRight = Math.min(c.getX2(), n.getX2());
                double overlap      = overlapRight - overlapLeft;
                if (overlap < hallThickness) continue;
 
                double midX = overlapLeft + (overlap - hallThickness) / 2;
                double x = c.snapToGrid(midX);
                Room.vHalls.add(new Hall(x, startY, hallThickness, gapH, false));
            }
        }
    }
    public void shrink() { root.shrink(); }
    
    //UPDATEING METHODS
    public void update(double dt){
        for (Area a : getAreas()){
            a.update(dt);
        }
    }    
    public static boolean isCleared(){
        if(currentLevel == null)return true;
        return false;
    }

    public static void endLevel(){
        if(Game.getType() == 0)Game.endGame();
        else{
            currentLevel = null;
            Game.level = constructNew(levelNo++);
            
        }
    }
    //GETTER,SETTERS
    public static Level getLevel() {
        return currentLevel;
    }
    public static ArrayList<Room> getRooms(){
        return Level.getLevel().rooms;
    }
    public static ArrayList<Area> getAreas(){
        return Level.getLevel().areas;
    }
    public static void setHero(Hero aHero){hero = aHero;}
}