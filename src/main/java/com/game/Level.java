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
    
    // instance variables
    private ArrayList<Room> rooms = new ArrayList<Room>();
    public ArrayList<Area> areas = new ArrayList<Area>();
    public Room startingRoom;
    private Room root;
    
    private Level(int levelCount){
        this.levelNo = levelCount;
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

        for (Area area : loadedLevel.areas) {
            area.livingEntities = new ArrayList<>();
            area.enemies = new ArrayList<>();
    
            for (Entity e : area.getEntities()) {
                e.currentArea = area; 
    
                if (e instanceof LivingEntity) area.livingEntities.add((LivingEntity) e);
                if (e instanceof Enemy) area.enemies.add((Enemy) e);
                
                if (e instanceof Hero) {
                    Game.hero = (Hero) e;
                    Hero.currentHero = (Hero) e;
                }
                
                // e.reloadImage(); 
            }
        }
    
        return currentLevel = loadedLevel;
    }

    public static Level constructNew(int levelCount) {
        if (getLevel() != null) throw new IllegalStateException("There is already a Level instance!");
        new Level(levelCount);
        return currentLevel;
    }

    public static void save(){
        try (java.io.PrintWriter out = new java.io.PrintWriter("save" + levelNo + ".txt")) {
            out.println(levelNo);
            out.println(Hero.getHero().health);  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    //CONSTRUCTING METHODS
    private void generateLevel(){
        root = new Room(144, 144, 4800, 3600);
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

        startingRoom = rooms.get((int) (Math.random() * rooms.size()));
        startingRoom.setStartingRoom();

        Game.hero = new Hero(
            startingRoom.getDimension().getCenter().subtract(new Point2D(8, 8)),
            null, 1, startingRoom
        );

        for (Room r : rooms) {
            r.spawnEntities();
        }

        this.areas.clear();
        this.areas.addAll(this.rooms);
        this.areas.addAll(Room.getHHalls());
        this.areas.addAll(Room.getVHalls());


        // for (Room r : rooms) {
        //     if (r.type == Room.RoomType.PORTAL) {
        //         if(heroSetIn)break;
        //         Dimension newDim = new Dimension((r.getX1()+r.getWidth())/2-hero.getDimension().getWidth()/2,
        //             (r.getY1()+r.getHeight())/2 - hero.getDimension().getHeight(),
        //             hero.getDimension().getWidth(),hero.getDimension().getHeight());
        //         hero.setDimension(newDim);
                
        //     }
        // }
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

    /*
    private void addHalls() {
        for (Room c : rooms) {
            for (Room n : c.hNeighbors) {
                double overlapTop = max(c.getY1(), n.getY1());
                double overlapBot = min(c.getY2(), n.getY2());
                if (overlapBot - overlapTop > gridSize*2) {
                    double y = c.snapToGrid(random(overlapTop, overlapBot - gridSize));
                    Room.hHalls.add(new Hall(c.getX2(), y, n.getX1()-c.getX2(), 2*gridSize));
                }
            }

            // Repeat similar logic for vNeighbors/vHalls
            for (Room n : c.vNeighbors) {
                double overlapTop = max(c.getY1(), n.getY1());
                double overlapBot = min(c.getY2(), n.getY2());
                if (overlapBot - overlapTop > gridSize*2) {
                    double x = random(overlapTop, overlapBot - gridSize);
                    Room.vHalls.add(new Hall(x, c.getY2(), 2*gridSize, n.getY1()-c.getY2()));
                }
            }
        }
    }
    */
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
                Room.hHalls.add(new Hall(startX, y, gapW, hallThickness));
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
                Room.vHalls.add(new Hall(x, startY, hallThickness, gapH));
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