package com.game;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HUD {

    // minimap
    private static Canvas mapCanvas;
    private static boolean mapVisible = false;
    private static final double MAP_PAD = 20;
    private static final java.util.HashMap<String, java.util.HashMap<Double, Image>> iconCache = new java.util.HashMap<>();

    private static Rectangle hpBar;
    private static Rectangle defBar;
    private static ImageView[] consIcons = new ImageView[3];
    private static ImageView[] talismanIcons = new ImageView[3];
    private static ImageView[] weaponIcons = new ImageView[2];
    private static ImageView ghostIcon;
    private static Point2D dropPreviewPos = null;
    private static double lastMouseSceneX, lastMouseSceneY;
    private static boolean isDragging = false;

    public static void setup() {
        App.HUDlayer.getChildren().clear();

        // --- GHOST ICON TO DRAG ---
        ghostIcon = new ImageView();
        ghostIcon.setVisible(false);
        ghostIcon.setMouseTransparent(true);
        ghostIcon.fitWidthProperty().bind(App.uiSizeBinding(40));
        ghostIcon.fitHeightProperty().bind(App.uiSizeBinding(40));
        StackPane.setAlignment(ghostIcon, Pos.TOP_LEFT);

        // --- BARS and TALISMANS ---
        VBox topLeftBox = new VBox();
        topLeftBox.spacingProperty().bind(App.uiSizeBinding(10));
        topLeftBox.setPickOnBounds(false);
        StackPane.setAlignment(topLeftBox, Pos.TOP_LEFT);
        topLeftBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
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
            ImageView icon = (ImageView) slot.getChildren().get(1);
            talismanIcons[i] = icon;
            final int index = i;
            icon.setOnMousePressed(e -> { if (e.getButton() == MouseButton.PRIMARY) startDrag(icon, e); });
            setupDragHandlers(icon, () -> Hero.getHero().dropTalisman(index, dropPreviewPos.getX(), dropPreviewPos.getY()));
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
                if (e.getButton() == MouseButton.SECONDARY) Hero.getHero().useConsumable(index);
                else if (e.getButton() == MouseButton.PRIMARY) startDrag(itemIcon, e);
            });
            setupDragHandlers(itemIcon, () -> Hero.getHero().dropConsumable(index, dropPreviewPos.getX(), dropPreviewPos.getY()));
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

        StackPane secWeaponSlot = createSlot(60, 50);
        StackPane mainWeaponSlot = createSlot(90, 70);
        weaponIcons[1] = (ImageView) secWeaponSlot.getChildren().get(1);
        weaponIcons[0] = (ImageView) mainWeaponSlot.getChildren().get(1);
        for (int i = 0; i < 2; i++) {
            final int index = i;
            ImageView icon = weaponIcons[i];
            icon.setOnMousePressed(e -> { if (e.getButton() == MouseButton.PRIMARY) startDrag(icon, e); });
            setupDragHandlers(icon, () -> Hero.getHero().dropWeapon(index, dropPreviewPos.getX(), dropPreviewPos.getY()));
        }
        weaponBox.getChildren().addAll(secWeaponSlot, mainWeaponSlot);

        // minimap canvas
        mapCanvas = new Canvas(1, 1);
        mapCanvas.setVisible(false);
        StackPane.setAlignment(mapCanvas, Pos.CENTER);
        mapCanvas.setMouseTransparent(true);

        Runnable resizeMap = () -> {
            double ratio = Level.rootLevelWidth / Level.rootLevelHeight;
            double maxW  = App.getScene().getWidth()  * 0.7;
            double maxH  = App.getScene().getHeight() * 0.57;
            double w = Math.min(maxW, maxH * ratio);
            double h = w / ratio;
            if (h > maxH) {h = maxH; w = h * ratio;}
            mapCanvas.setWidth(w);
            mapCanvas.setHeight(h);
        };
        resizeMap.run();
        App.getScene().widthProperty().addListener((obs, o, n)  -> resizeMap.run());
        App.getScene().heightProperty().addListener((obs, o, n) -> resizeMap.run());

        App.HUDlayer.getChildren().addAll(topLeftBox, consBox, weaponBox, ghostIcon, mapCanvas);
    }

    public static void update() {
        if (Hero.getHero() == null || hpBar == null) return;

        if (isDragging && ghostIcon.isVisible() && dropPreviewPos != null) {
            dropPreviewPos = computeDropPos(lastMouseSceneX, lastMouseSceneY);
            moveGhostToDropPos();
        }

        double hpPercent = Math.max(0, Hero.getHero().health / Hero.getHero().maxHealth);
        hpBar.setWidth(App.uiSize(200) * hpPercent);

        double defPercent = Math.max(0, Hero.getHero().armor / 100);
        defBar.setWidth(App.uiSize(200) * defPercent);

        for (int i = 0; i < 3; i++) {
            Consumable cons = Hero.getHero().consumables[i];
            consIcons[i].setImage(cons != null ? cons.loadImageAtSize(App.uiSize(40)) : null);
        }

        for (int i = 0; i < 3; i++) {
            Talisman tal = Hero.getHero().talismans[i];
            talismanIcons[i].setImage(tal != null ? tal.loadImageAtSize(App.uiSize(40)) : null);
        }

        double[] weaponSizes = {App.uiSize(70), App.uiSize(50)};
        for (int i = 0; i < 2; i++) {
            Weapon w = Hero.getHero().weapons[i];
            weaponIcons[i].setImage(w != null ? w.loadImageAtSize(weaponSizes[i]) : null);
        }

        drawMap();
    }

    public static boolean isMapVisible() {
        return mapVisible;
    }

    public static void closeMap() {
        mapVisible = false;
        mapCanvas.setVisible(false);
    }

    public static void toggleMap() {
        mapVisible = !mapVisible;
        mapCanvas.setVisible(mapVisible);
    }

    private static Image loadIconAtSize(String path, double size) {
        double key = Math.round(size);
        iconCache.computeIfAbsent(path, p -> new java.util.HashMap<>())
                 .computeIfAbsent(key, k -> {
                     var stream = HUD.class.getResourceAsStream(path);
                     return stream != null ? new Image(stream, k, k, true, true) : null;
                 });
        return iconCache.get(path).get(key);
    }

    private static void drawMap() {
        if (!mapVisible || mapCanvas == null) return;

        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());

        // background
        gc.setFill(Color.rgb(10, 8, 25, 0.8));
        gc.fillRoundRect(1, 1, mapCanvas.getWidth()-2, mapCanvas.getHeight()-2, 16, 16);
        gc.setStroke(Color.rgb(180, 160, 230, 0.8));
        gc.setLineWidth(2);
        gc.strokeRoundRect(1, 1, mapCanvas.getWidth()-2, mapCanvas.getHeight()-2, 17, 17);

        // level bounds
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        for (Area area : Level.getAreas()) {
            Dimension d = area.getDimension();
            minX = Math.min(minX, d.getX());
            minY = Math.min(minY, d.getY());
            maxX = Math.max(maxX, d.getRightX());
            maxY = Math.max(maxY, d.getBottomY());
        }

        double levelW = maxX - minX;
        double levelH = maxY - minY;
        double drawW = mapCanvas.getWidth()  - MAP_PAD * 2;
        double drawH = mapCanvas.getHeight() - MAP_PAD * 2;
        double scale = Math.min(drawW / levelW, drawH / levelH);

        // draw areas
        for (Area area : Level.getAreas()) {
            Dimension d = area.getDimension();
            double x = MAP_PAD + (d.getX() - minX) * scale;
            double y = MAP_PAD + (d.getY() - minY) * scale;
            double w = d.getWidth() * scale;
            double h = d.getHeight() * scale;

            gc.setFill(area instanceof Room
                ? Color.rgb(60, 50, 100, 0.9)
                : Color.rgb(40, 35, 70, 0.9));
            gc.fillRect(x, y, w, h);
            gc.setStroke(Color.rgb(140, 120, 200, 0.7));
            gc.setLineWidth(1);
            gc.strokeRect(x, y, w, h);

            // oda ikonu
            if (area instanceof Room) {
                double iconSize = App.uiSize(30);
                String iconPath = switch (((Room) area).getType()) {
                    case BOSS   -> "/sprites/ui/bossRoom_icon.png";
                    case LOOT   -> "/sprites/ui/lootRoom_icon.png";
                    case PORTAL -> "/sprites/ui/starterRoom_icon.png";
                    default     -> null;
                };
                if (iconPath != null) {
                    Image icon = loadIconAtSize(iconPath, iconSize);
                    if (icon != null)
                        gc.drawImage(icon, x + (w - iconSize) / 2, y + (h - iconSize) / 2, iconSize, iconSize);
                }
            }
        }

        // draw hero position
        Hero hero = Hero.getHero();
        if (hero != null) {
            Point2D hc = hero.getDimension().getCenter();
            double hx = MAP_PAD + (hc.getX() - minX) * scale;
            double hy = MAP_PAD + (hc.getY() - minY) * scale;
            gc.setFill(Color.rgb(180, 180, 255, 0.8));
            gc.fillRect(hx - 3, hy - 3, 6, 6);
            gc.setStroke(Color.WHITE);
            gc.strokeRect(hx - 5, hy - 5, 10, 10);
        }
    }

    private static Point2D computeDropPos(double sceneX, double sceneY) {
        Hero hero = Hero.getHero();
        if (hero == null) return null;
        Point2D heroCenter = hero.getDimension().getCenter();
        double camX = -heroCenter.getX() + (App.getScene().getWidth() / 2);
        double camY = -heroCenter.getY() + (App.getScene().getHeight() / 2);
        Point2D target = new Point2D(sceneX - camX, sceneY - camY);
        Point2D delta = target.subtract(heroCenter);
        if (delta.magnitude() > 3 * Level.gridSize)
            delta = delta.normalize().multiply(3 * Level.gridSize);

        double half = DroppedItem.DROP_SIZE / 2;
        double footH = DroppedItem.DROP_SIZE * 0.3;
        Dimension area = hero.currentArea.getDimension();
        double x = Math.max(area.getX() + half,         Math.min(heroCenter.getX() + delta.getX(), area.getRightX()  - half));
        double y = Math.max(area.getY() - half + footH, Math.min(heroCenter.getY() + delta.getY(), area.getBottomY() - half));
        return new Point2D(x, y);
    }

    private static void moveGhostToDropPos() {
        if (dropPreviewPos == null) return;
        Hero hero = Hero.getHero();
        if (hero == null) return;
        Point2D heroCenter = hero.getDimension().getCenter();
        double camX = -heroCenter.getX() + (App.getScene().getWidth() / 2);
        double camY = -heroCenter.getY() + (App.getScene().getHeight() / 2);
        double screenX = dropPreviewPos.getX() + camX;
        double screenY = dropPreviewPos.getY() + camY;
        Point2D local = App.HUDlayer.sceneToLocal(screenX, screenY);
        ghostIcon.setTranslateX(local.getX() - App.uiSize(20));
        ghostIcon.setTranslateY(local.getY() - App.uiSize(20));
    }

    private static void startDrag(ImageView icon, MouseEvent e) {
        if (icon.getImage() == null) return;
        ghostIcon.setImage(icon.getImage());
        ghostIcon.setVisible(true);
        ghostIcon.setOpacity(1.0);
        icon.setOpacity(0.3);
        isDragging = true;
        lastMouseSceneX = e.getSceneX();
        lastMouseSceneY = e.getSceneY();
        Point2D local = App.HUDlayer.sceneToLocal(e.getSceneX(), e.getSceneY());
        ghostIcon.setTranslateX(local.getX() - App.uiSize(20));
        ghostIcon.setTranslateY(local.getY() - App.uiSize(20));
    }

    private static void setupDragHandlers(ImageView icon, Runnable onDrop) {
        icon.setOnMouseDragged(e -> {
            if (!ghostIcon.isVisible()) return;
            lastMouseSceneX = e.getSceneX();
            lastMouseSceneY = e.getSceneY();
            boolean overCanvas = e.getPickResult().getIntersectedNode() instanceof Canvas;
            if (overCanvas) {
                dropPreviewPos = computeDropPos(e.getSceneX(), e.getSceneY());
                moveGhostToDropPos();
            } else {
                dropPreviewPos = null;
                Point2D local = App.HUDlayer.sceneToLocal(e.getSceneX(), e.getSceneY());
                ghostIcon.setTranslateX(local.getX() - App.uiSize(20));
                ghostIcon.setTranslateY(local.getY() - App.uiSize(20));
            }
        });
        icon.setOnMouseReleased(e -> {
            if (!ghostIcon.isVisible()) return;
            isDragging = false;
            ghostIcon.setVisible(false);
            ghostIcon.setOpacity(1.0);
            icon.setOpacity(1.0);
            boolean overCanvas = e.getPickResult().getIntersectedNode() instanceof Canvas;
            if (overCanvas && dropPreviewPos != null) onDrop.run();
            dropPreviewPos = null;
        });
    }

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
}
