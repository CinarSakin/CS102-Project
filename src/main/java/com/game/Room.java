package com.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.game.LivingEntity.LivingType;

import javafx.geometry.Point2D;


public class Room extends Area {
    //class variables
    public static int minSize = Level.gridSize*16;
    public static ArrayList<Hall> hHalls = new ArrayList<Hall>();
    public static ArrayList<Hall> vHalls = new ArrayList<Hall>();


    //Instance variables
    public double dif;
    private final int MAXIMUM_ENEMIES = (int)(20 * dif);
    public transient Room right;
    public transient Room left;
    public RoomType type ;
    public transient ArrayList<Room> hNeighbors = new ArrayList<Room>();
    public transient ArrayList<Room> vNeighbors = new ArrayList<Room>();
    

    public Room(double x1, double y1, double width, double height){
        this(x1, y1, width, height, Math.random());
    }
    public Room(double x1, double y1, double width, double height, double newType){
        super(new Dimension(x1, y1, width, height));

        if (newType < .15) // %15 -> puzzle room
            type = RoomType.PUZZLE;
        else if (newType < .5) // %35 -> loot room
            type = RoomType.LOOT;
        else
            type = RoomType.NORMAL;
 
        dif = 1+ Level.levelNo/10;

    }

    @Override
    public void update(double dt){
        super.update(dt);
        type.update(dt, this);
    }

    public void divide(){
        if (left != null) return;

        double w = dim.getWidth();
        double h = dim.getHeight();

        boolean canSplitW = w >= minSize * 2;
        boolean canSplitH = h >= minSize * 2;

        if (!canSplitW && !canSplitH) return;

        boolean splitW = Math.random() > 0.5;
        if (canSplitW && !canSplitH) splitW = true;
        else if (!canSplitW && canSplitH) splitW = false;

        if (w > h && w / h >= 1.25) splitW = true;
        else if (h > w && h / w >= 1.25) splitW = false;

        double splitPoint;
        if (splitW) { // Split vertically
            splitPoint = snapToGrid(random(minSize, w - minSize));
            left = new Room(dim.getX(), dim.getY(), splitPoint, h);
            right = new Room(dim.getX() + splitPoint, dim.getY(), w - splitPoint, h);
        } else { // Split horizontally
            splitPoint = snapToGrid(random(minSize, h - minSize));
            left = new Room(dim.getX(), dim.getY(), w, splitPoint);
            right = new Room(dim.getX(), dim.getY() + splitPoint, w, h - splitPoint);
        }

        left.divide();
        right.divide();

    }

    public void shrink() {
        if (left != null) {
            left.shrink();
            right.shrink();
        } else {
            double padding = Level.gridSize * 3; 
            
            double maxW = dim.getWidth() - (padding * 2);
            double maxH = dim.getHeight() - (padding * 2);
            
            double actualMinSize = Level.gridSize * 6; 

            double nw = snapToGrid(random(Math.min(actualMinSize, maxW), maxW));
            double nh = snapToGrid(random(Math.min(actualMinSize, maxH), maxH));

            double maxOffsetX = dim.getWidth() - nw - padding;
            double maxOffsetY = dim.getHeight() - nh - padding;

            double offsetX = snapToGrid(random(padding, Math.max(padding, maxOffsetX)));
            double offsetY = snapToGrid(random(padding, Math.max(padding, maxOffsetY)));
            dim.setX(dim.getX() + offsetX);
            dim.setY(dim.getY() + offsetY);
            dim.setWidth(nw);
            dim.setHeight(nh);
        }
    }

    public double snapToGrid(double value) {
        return Math.round(value/Level.gridSize) * Level.gridSize;
    }

    public void setupRoom() {
        type.setupRoom(this);
    }

    public void spawnEntities(){
        //ToDo
        switch (this.type) {
            case PORTAL:
                break;
            case NORMAL:
                double enemyCount = max(5,(int)(Math.random()*MAXIMUM_ENEMIES));
                for(int i = 0; i < enemyCount; i++ ){
                    LivingType type = LivingEntity.RandomType() ;
                    new Enemy(type, randomPos(type.size.getX(), type.size.getY()*.3), this, dif);
                    //add enemies in random points inside of the room
                    //If needed an enemys array can be added for less confision while updateing
                }
                break;
            case LOOT:
                new Chest(this.getDimension().getCenter(), this, Item.randomItem(1));
                break;
            case PUZZLE:
                //EREN KOZAN TODO:MAKE A PUZZLE GENERATION
                for(int i = 1; i <= 4; i++)
                new Plate(randomPos(48, 48), this, Item.randomItem(2), i);
                break;
            case BOSS:
                //Create a boss in the middle of the room which can or cannot move according to the move patern of its
                //new Boss(new Point2D(this.getDimension().getWidth()/2, this.getDimension().getHeight()/2), this, dif);
                new Boss(getDimension().getCenter(), this, dif);
                
                break;
                
            default:
                throw new AssertionError();
        }
        

    }


    //EXTRA METHODS
    private Point2D randomPos(double hitboxX, double hitboxY){
        int x = (int)(dim.getX() + Math.random()*(dim.getWidth()-hitboxX-Level.gridSize) + Level.gridSize);
        int y = (int)(dim.getY() + Math.random()*(dim.getHeight()-hitboxY-Level.gridSize*2) + hitboxY + Level.gridSize);
        return new Point2D(x, y);
    }

    private static double random(double a, double b){
        return Math.random()*(b-a)+a;
    }

    private static double max(double x, double a){
        if(a<x){return x;}
        return a;
    }

    private static double min(double x, double a){
        if(x>a){return x;}
        return a;
    }

    //GETTER SETTERS
    public RoomType getType(){return this.type;}
    public static ArrayList<Hall> getHHalls(){return hHalls;}
    public static ArrayList<Hall> getVHalls(){return vHalls;}
    public void setStartingRoom(){this.type = RoomType.PORTAL;}
    public boolean setBossRoom(){
        if(type != RoomType.PORTAL){
            this.type = RoomType.BOSS;
            return true;
        }
        return false;
    }
            


    //enumeration
    public enum RoomType{
        PORTAL,
        NORMAL,
        LOOT,
        PUZZLE{
            Map<Area, ArrayList<Plate>> pressMap = new HashMap<>();

            public void update(double dt, Area a){
                ArrayList<Plate> presses = pressMap.get(a);
                if (presses == null || presses.isEmpty()) return;

                boolean allPressed = true;
                for (Plate p : presses) {
                    if (!p.getPressed()) { allPressed = false; break; }
                }

                if (allPressed) {
                    boolean valid =
                        presses.get(0).pressTime.isBefore(presses.get(1).pressTime) &&
                        presses.get(1).pressTime.isBefore(presses.get(2).pressTime) &&
                        presses.get(2).pressTime.isBefore(presses.get(3).pressTime);

                    if (valid) {
                        for (int i = 0; i < 2; i++) {
                            for (int j = 0; j < 2; j++) {
                                new DroppedItem(
                                    a.dim.getCenter().add((i-.5)*Level.gridSize, (j-.5)*Level.gridSize),
                                    a, Item.randomItem(1.5)
                                );
                            }
                        }
                        pressMap.remove(a);
                        GameStats.getInstance().puzzlesCompleted++;
                    } else {
                        for (Plate p : presses) {
                            p.interactable = true;
                            p.pressed = false;
                            p.reloadImages();
                        }
                    }
                }
            }

            public void setupRoom(Area a) {
                ArrayList<Plate> presses = new ArrayList<>();
                for (Entity e : a.getEntities()) {
                    if (e instanceof Plate && e.getDimension().insideOf(a)) {
                        presses.add((Plate) e);
                    }
                }
                pressMap.put(a, presses);
            }
        },
        BOSS;
        private RoomType(){}
        public void update(double dt, Area a){}
        public void setupRoom(Area a){}
    }

}
