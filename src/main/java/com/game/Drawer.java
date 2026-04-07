package com.game;

import com.game.App.GameLayer;
import com.game.Projectile.ProjectileType;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Drawer {
    public static int gridSize = Level.gridSize;
    private static double wallOffset = Area.getImage(1).getHeight() - Area.getImage(0).getHeight();

    private static Rectangle hpBar;
    private static Rectangle defBar;
    private static ImageView[] consIcons = new ImageView[3];
    private static ImageView ghostIcon;

    public static void setupHUD() {
        App.HUDlayer.getChildren().clear();

        // --- GHOST ICON TO DRAG ---
        ghostIcon = new ImageView();
        ghostIcon.setVisible(false);
        ghostIcon.setMouseTransparent(true);
        ghostIcon.fitWidthProperty().bind(App.uiSizeBinding(40));
        ghostIcon.fitHeightProperty().bind(App.uiSizeBinding(40));

        // --- BARS and TALISMANS ---
        VBox topLeftBox = new VBox();
        topLeftBox.spacingProperty().bind(App.uiSizeBinding(10));
        topLeftBox.setPickOnBounds(false);

        StackPane.setAlignment(topLeftBox, Pos.TOP_LEFT);
        topLeftBox.setMaxSize(javafx.scene.layout.Region.USE_PREF_SIZE, javafx.scene.layout.Region.USE_PREF_SIZE);
        StackPane.setMargin(topLeftBox, new Insets(App.uiSize(20), 0, 0, App.uiSize(20)));

        // Health bar
        StackPane hpPane = new StackPane();
        hpPane.setAlignment(Pos.CENTER_LEFT);
        Rectangle hpBg = new Rectangle();
        hpBg.setFill(Color.rgb(50, 50, 50, 0.8));
        hpBg.widthProperty().bind(App.uiSizeBinding(200));
        hpBg.heightProperty().bind(App.uiSizeBinding(20));
        
        hpBar = new Rectangle();
        hpBar.setFill(Color.RED);
        hpBar.heightProperty().bind(App.uiSizeBinding(20));
        hpPane.getChildren().addAll(hpBg, hpBar);

        // Armor bar
        StackPane defPane = new StackPane();
        defPane.setAlignment(Pos.CENTER_LEFT);
        Rectangle defBg = new Rectangle();
        defBg.setFill(Color.rgb(50, 50, 50, 0.8));
        defBg.widthProperty().bind(App.uiSizeBinding(200));
        defBg.heightProperty().bind(App.uiSizeBinding(15));
        
        defBar = new Rectangle();
        defBar.setFill(Color.DODGERBLUE);
        defBar.heightProperty().bind(App.uiSizeBinding(15));
        defPane.getChildren().addAll(defBg, defBar);

        // Talismans
        HBox talismanBox = new HBox();
        talismanBox.spacingProperty().bind(App.uiSizeBinding(5));
        for (int i = 0; i < 3; i++) {
            StackPane slot = createSlot(50, 40);
            talismanBox.getChildren().add(slot);
        }
        topLeftBox.getChildren().addAll(hpPane, defPane, talismanBox);

        // --- CONSUMABLES ---
        VBox consBox = new VBox();
        consBox.spacingProperty().bind(App.uiSizeBinding(10));
        consBox.setPickOnBounds(false);

        StackPane.setAlignment(consBox, Pos.CENTER_LEFT);
        consBox.setMaxSize(javafx.scene.layout.Region.USE_PREF_SIZE, javafx.scene.layout.Region.USE_PREF_SIZE);
        consBox.translateXProperty().bind(App.uiSizeBinding(20));

        for (int i = 0; i < 3; i++) {
            StackPane slot = createSlot(50, 40);
            ImageView itemIcon = (ImageView) slot.getChildren().get(1);
            consIcons[i] = itemIcon;
            
            final int index = i;
            
            itemIcon.setOnMousePressed(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    Hero.getHero().useConsumable(index); // right click to use
                } else if (e.getButton() == MouseButton.PRIMARY && itemIcon.getImage() != null) {
                    // left click drag
                    ghostIcon.setImage(itemIcon.getImage());
                    ghostIcon.setVisible(true);
                    itemIcon.setOpacity(0.3);
                }
            });

            itemIcon.setOnMouseDragged(e -> {
                if (ghostIcon.isVisible()) {
                    Point2D localCoord = App.HUDlayer.sceneToLocal(e.getSceneX(), e.getSceneY());
                    ghostIcon.setTranslateX(localCoord.getX() - App.uiSize(20));
                    ghostIcon.setTranslateY(localCoord.getY() - App.uiSize(20));
                }
            });

            itemIcon.setOnMouseReleased(e -> {
                if (ghostIcon.isVisible()) {
                    ghostIcon.setVisible(false);
                    itemIcon.setOpacity(1.0);

                    if (e.getSceneX() > App.uiSize(100)) { 
                        Point2D heroCenter = Hero.getHero().getDimension().getCenter();
                        double camX = -heroCenter.getX() + (App.getScene().getWidth() / 2);
                        double camY = -heroCenter.getY() + (App.getScene().getHeight() / 2);
                        Hero.getHero().dropConsumable(index, e.getSceneX() - camX, e.getSceneY() - camY);
                    }
                }
            });
            consBox.getChildren().add(slot);
        }

        // --- WEAPONS ---
        HBox weaponBox = new HBox();
        weaponBox.setAlignment(Pos.BOTTOM_RIGHT);
        weaponBox.spacingProperty().bind(App.uiSizeBinding(10));
        weaponBox.setPickOnBounds(false);

        StackPane.setAlignment(weaponBox, Pos.BOTTOM_RIGHT);
        weaponBox.setMaxSize(javafx.scene.layout.Region.USE_PREF_SIZE, javafx.scene.layout.Region.USE_PREF_SIZE);
        StackPane.setMargin(weaponBox, new Insets(0, App.uiSize(20), App.uiSize(20), 0));

        StackPane secWeaponSlot = createSlot(60, 50); // other weapon
        StackPane mainWeaponSlot = createSlot(90, 70); // held weapon
        weaponBox.getChildren().addAll(secWeaponSlot, mainWeaponSlot);

        // add all to HUDlayer
        App.HUDlayer.getChildren().addAll(topLeftBox, consBox, weaponBox, ghostIcon);
    }

    public static void updateHUD() {
        if (Hero.getHero() == null || hpBar == null) return;

        // update bars
        double hpPercent = Math.max(0, Hero.getHero().health / Hero.getHero().maxHealth);
        hpBar.setWidth(App.uiSize(200) * hpPercent);

        double defPercent = Math.max(0, Hero.getHero().armor / 100);
        defBar.setWidth(App.uiSize(200) * defPercent);

        // update consumable icons
        for (int i = 0; i < 3; i++) {
            if (Hero.getHero().consumables[i] != null) {
                consIcons[i].setImage(Hero.getHero().consumables[i].image);
            } else {
                consIcons[i].setImage(null);
            }
        }

        // TODO: other
        
    }
    
    // helper method for slots
    private static StackPane createSlot(double bgSize, double iconSize) {
        StackPane pane = new StackPane();
        ImageView bg = new ImageView();
        bg.imageProperty().bind(App.squareBtnDefaultProp);
        bg.fitWidthProperty().bind(App.uiSizeBinding(bgSize));
        bg.fitHeightProperty().bind(App.uiSizeBinding(bgSize));
        
        ImageView icon = new ImageView();
        icon.fitWidthProperty().bind(App.uiSizeBinding(iconSize));
        icon.fitHeightProperty().bind(App.uiSizeBinding(iconSize));
        
        pane.getChildren().addAll(bg, icon);
        return pane;
    }

    public static void draw(Entity e){
        GraphicsContext gc;
        
        if (e instanceof Projectile && ((Projectile)e).getType() == ProjectileType.BOMB){
            
            gc = App.getLayerGC(App.GameLayer.VFX);
        }else{
            gc = App.getLayerGC(App.GameLayer.ENTITIES);
        }
        Image imgToDraw = e.getImage();
        double rescale = gridSize / imgToDraw.getWidth();
        gc.drawImage(imgToDraw, e.getDimension().getX(), e.getDimension().getY(),
         imgToDraw.getWidth(), imgToDraw.getHeight());
    }

    public static void draw(Room r){
        GraphicsContext gc;
        int f = 0;
        int cellAmountX = (int) (r.getDimension().getWidth() / gridSize);
        int cellAmountY = (int) (r.getDimension().getHeight() / gridSize);
 
        for (int i = -1; i <= cellAmountX; i++){
            for (int j = -1; j <= cellAmountY; j++){
 
                double drawX = r.getDimension().getX() + (i * gridSize);
                double drawY = r.getDimension().getY() + (j * gridSize);
 
                boolean isDoor = false;
                double cx = drawX + (gridSize / 2.0);
                double cy = drawY + (gridSize / 2.0);
                for (Hall h : Room.getHHalls()) {
                    if (cx >= h.getDimension().getX() && cx <= h.getDimension().getX() + h.getDimension().getWidth() &&
                        cy >= h.getDimension().getY() && cy <= h.getDimension().getY() + h.getDimension().getHeight()) {
                        isDoor = true; break;
                    }
                }
                for (Hall h : Room.getVHalls()) {
                    if (cx >= h.getDimension().getX() && cx <= h.getDimension().getX() + h.getDimension().getWidth() &&
                        cy >= h.getDimension().getY() && cy <= h.getDimension().getY() + h.getDimension().getHeight()) {
                        isDoor = true; break;
                    }
                }
 
                if (isDoor) continue;
 
                gc = App.getLayerGC(GameLayer.VFX);
 
                if (j == cellAmountY) f = 0;
                else if (i == -1) f = 1;
                else if (i == cellAmountX) f = 1;
                else if (j == -1) {
                    f = 0;
                    gc = App.getLayerGC(GameLayer.GROUND);
                }
                else {
                    f = 2;
                    gc = App.getLayerGC(GameLayer.GROUND);
                };
 
                Image imgToDraw = Room.getImage(f);
                double rescale = gridSize / imgToDraw.getWidth();
 
                double height = imgToDraw.getHeight()*rescale;
                if (f != 2) {
                    drawY += wallOffset;
                }
 
                gc.drawImage(imgToDraw, drawX, drawY, gridSize, height);
 
            }
        }
        
    }

    public static void draw(Hall h, int type){
        GraphicsContext ground = App.getLayerGC(GameLayer.GROUND);
        GraphicsContext vfx = App.getLayerGC(GameLayer.VFX);
        int cellAmountX = (int) (h.getDimension().getWidth() / gridSize);
        int cellAmountY = (int) (h.getDimension().getHeight() / gridSize);

        for (int i = 0; i < cellAmountX; i++){

            double drawX = h.getDimension().getX() + (i * gridSize);

            for (int j = 0; j < cellAmountY; j++){

                double drawY = h.getDimension().getY() + (j * gridSize);
                ground.drawImage(Area.getImage(2), drawX, drawY, gridSize, gridSize);

                if (j == 0 && type == 0) { //horizontal case
                    Image imgToDraw = Area.getImage(0);
                    double rescale = gridSize / imgToDraw.getWidth();
                    double height = imgToDraw.getHeight()*rescale;
                    ground.drawImage(imgToDraw, drawX, h.getDimension().getY() + wallOffset - gridSize, gridSize, height);
                    if (i != 0 && i != cellAmountX-1)
                        vfx.drawImage(imgToDraw, drawX, h.getDimension().getY() +cellAmountY*gridSize+wallOffset, gridSize, height);
                }
                
                if (i == 0 && type == 1) { //vertical case
                    Image imgToDraw = Area.getImage(1);
                    double rescale = gridSize / imgToDraw.getWidth();
                    double height = imgToDraw.getHeight()*rescale;
                    vfx.drawImage(imgToDraw, h.getDimension().getX()-gridSize, drawY+wallOffset, gridSize, height);
                    vfx.drawImage(imgToDraw, h.getDimension().getRightX(), drawY+wallOffset, gridSize, height);
                }

            }
        }
    }

}
