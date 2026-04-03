package com.game;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    private static Scene scene;
    private static StackPane menuPane, gamePane; // holders of menus/layers
    private static StackPane mainMenu, settingsMenu, saveMenu, gamemodeMenu;

    private final DoubleProperty UI_SCALE = new SimpleDoubleProperty(1.0); // load from save
    private final DoubleProperty uiSizeProp = new SimpleDoubleProperty(1.0);

    private final ObjectProperty<Image> longBtnDefaultProp = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> longBtnPressedProp = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> shortBtnDefaultProp = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> shortBtnPressedProp = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> squareBtnDefaultProp = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> squareBtnPressedProp = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> bgImageProp = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> bgTextureProp = new SimpleObjectProperty<>();

    private final ObjectProperty<Font> customFontProp = new SimpleObjectProperty<>();
    private final ObjectProperty<Font> titleFontProp = new SimpleObjectProperty<>();

    private void reloadUI() {
        if (scene == null) return;

        longBtnDefaultProp.set(loadImage("/sprites/ui/longBtnDefault.png", uiSize(48)));
        longBtnPressedProp.set(loadImage("/sprites/ui/longBtnPressed.png", uiSize(48)));
        shortBtnDefaultProp.set(loadImage("/sprites/ui/shortBtnDefault.png", uiSize(48)));
        shortBtnPressedProp.set(loadImage("/sprites/ui/shortBtnPressed.png", uiSize(48)));
        squareBtnDefaultProp.set(loadImage("/sprites/ui/squareBtnDefault.png", uiSize(48)));
        squareBtnPressedProp.set(loadImage("/sprites/ui/squareBtnPressed.png", uiSize(48)));
        bgImageProp.set(loadImage("/sprites/ui/mainMenuBackground.png", scene.getHeight()));
        bgTextureProp.set(loadImage("/sprites/ui/backgroundTexture.png", scene.getHeight()));

        customFontProp.set(Font.loadFont(getClass().getResourceAsStream("/ByteBounce.ttf"), uiSize(40)));
        titleFontProp.set(Font.loadFont(getClass().getResourceAsStream("/ByteBounce.ttf"), uiSize(84)));
    }

    @Override
    public void start(Stage primaryStage) {
        try {            
            Background bgFill = new Background(new BackgroundFill(Color.rgb(18, 14, 37), null, null));
            
            StackPane root = new StackPane();
            root.setBackground(bgFill);
 
            menuPane = new StackPane();
            menuPane.setBackground(bgFill);
            gamePane = new StackPane();
            gamePane.setVisible(false);
            gamePane.getChildren().addAll(
                new StackPane(),new StackPane(),new StackPane(),new StackPane()
            );
 
            root.getChildren().addAll(gamePane, menuPane);

            Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            scene = new Scene(root, screenBounds.getWidth() * .75, screenBounds.getHeight() * .75);

            uiSizeProp.bind(Bindings.createDoubleBinding(
                () -> Math.min(scene.getWidth() / 960.0, scene.getHeight() / 720.0) * UI_SCALE.get(),
                scene.widthProperty(), scene.heightProperty(), UI_SCALE
            ));

            reloadUI();
            scene.widthProperty().addListener((obs, oldVal, newVal) -> {reloadUI();});
            scene.heightProperty().addListener((obs, oldVal, newVal) -> {reloadUI();});

            // main menu title
            Text titleText = new Text("DUNGEONFALL");
            titleText.fontProperty().bind(titleFontProp);
            titleText.setFill(Color.WHITE);
            titleText.setScaleX(.85);
            titleText.setBoundsType(javafx.scene.text.TextBoundsType.VISUAL);

            DropShadow outerStroke = new DropShadow();
            outerStroke.setColor(Color.rgb(103, 15, 255));
            outerStroke.setRadius(12.0);
            outerStroke.setSpread(0.5);
            titleText.setEffect(outerStroke);

            StackPane.setAlignment(titleText, Pos.TOP_CENTER);
            uiSizeProp.addListener((obs, old, val) -> 
                StackPane.setMargin(titleText, new Insets(scene.getHeight() * .13, 0, 0, 0)));
            StackPane.setMargin(titleText, new Insets(scene.getHeight() * .13, 0, 0, 0));

            // main menu buttons
            Button playBtn = createStyledButton("PLAY", 0);
            Button settingsBtn = createStyledButton("SETTINGS", 0);
            Button leaderboardBtn = createStyledButton("LEADERBOARD", 0);
            Button exitBtn = createStyledButton("EXIT", 0);

            playBtn.setCursor(Cursor.HAND);
            settingsBtn.setCursor(Cursor.HAND);
            leaderboardBtn.setCursor(Cursor.HAND);
            exitBtn.setCursor(Cursor.HAND);
            
            VBox mainMenuButtonContainer = new VBox(playBtn, settingsBtn, leaderboardBtn, exitBtn);
            mainMenuButtonContainer.setAlignment(Pos.CENTER);
            mainMenuButtonContainer.spacingProperty().bind(uiSizeBinding(15));

            ObjectBinding<Background> background = Bindings.createObjectBinding(() -> {
                Image img = bgImageProp.get();
                BackgroundImage bgImg = new BackgroundImage(
                    img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER, BackgroundSize.DEFAULT
                );
                return new Background(bgImg);
            }, bgImageProp);

            Region bgRegion = new Region();
            bgRegion.backgroundProperty().bind(background);

            mainMenu = new StackPane(bgRegion, titleText, mainMenuButtonContainer);
            mainMenu.setBackground(bgFill);
            menuPane.getChildren().add(mainMenu);

            // settings menu
            Button resetSettingsBtn = createStyledButton("RESET SETTINGS", 0);
            StackPane stpane = (StackPane)(resetSettingsBtn.getGraphic());
            stpane.getChildren().get(1).setScaleX(.7);

            Button exitSettingsBtn = createStyledButton("CLOSE", 0);
            Label settingsTitle = new Label("SETTINGS");
            settingsTitle.fontProperty().bind(customFontProp);
            settingsTitle.setAlignment(Pos.TOP_CENTER);

            VBox settingsMenuContainer = new VBox(settingsTitle, resetSettingsBtn, exitSettingsBtn);
            settingsMenuContainer.setAlignment(Pos.CENTER);
            settingsMenuContainer.spacingProperty().bind(uiSizeBinding(15));

            ObjectBinding<Background> textureBackground = Bindings.createObjectBinding(() -> {
                Image img = bgTextureProp.get();
                BackgroundImage bgImg = new BackgroundImage(
                    img, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER, BackgroundSize.DEFAULT
                );
                return new Background(bgImg);
            }, bgTextureProp);

            Region settingsBgRegion = new Region();
            settingsBgRegion.backgroundProperty().bind(textureBackground);
            
            settingsMenu = new StackPane(settingsBgRegion, settingsMenuContainer);

            // gamemode menu
            Button standardBtn = createStyledButton("STANDARD", 0);
            Button infiniteBtn = createStyledButton("INFINITE", 0);
            Button exitGamemodeBtn = createStyledButton("GO BACK", 1, .8);

            VBox gamemodeButtonContainer = new VBox(standardBtn, infiniteBtn);
            gamemodeButtonContainer.setAlignment(Pos.CENTER);
            gamemodeButtonContainer.spacingProperty().bind(uiSizeBinding(15));

            StackPane.setAlignment(exitGamemodeBtn, Pos.BOTTOM_RIGHT);
            uiSizeProp.addListener((obs, old, val) -> {
                StackPane.setMargin(exitGamemodeBtn, new Insets(0, uiSize(20), uiSize(25), 0));});
            StackPane.setMargin(exitGamemodeBtn, new Insets(0, uiSize(20), uiSize(25), 0));

            Region gmBgRegion = new Region();
            gmBgRegion.backgroundProperty().bind(background);
            gamemodeMenu = new StackPane(gmBgRegion, gamemodeButtonContainer, exitGamemodeBtn);

            // save file menu
            Button exitSavesBtn = createStyledButton("GO BACK", 1);

            Label saveTitle = new Label("SELECT SAVE");
            saveTitle.fontProperty().bind(titleFontProp);
            saveTitle.setTextFill(Color.WHITE);

            GridPane slotGrid = new GridPane();
            slotGrid.setAlignment(Pos.CENTER);
            slotGrid.setHgap(50);
            slotGrid.setVgap(50);

            for(int i = 0; i < 6; i++) {
                int slotIndex = i + 1;
                boolean isEmpty = true;
                Button slotBtn = new Button();
                slotBtn.setBackground(Background.EMPTY);
                slotBtn.setPadding(Insets.EMPTY);

                Label slotLabel = new Label("SLOT " + slotIndex);
                slotLabel.fontProperty().bind(customFontProp);
                slotLabel.setTextFill(Color.rgb(187, 174, 232, 1));

                Label slotInfo = new Label(isEmpty ? "* EMPTY *" : "* CONTINUE *");
                slotInfo.fontProperty().bind(customFontProp);
                slotInfo.setTextFill(Color.rgb(219, 218, 220, 1));

                VBox slotTextBox = new VBox(slotLabel, slotInfo);
                slotTextBox.setAlignment(Pos.CENTER);

                Button deleteBtn = createStyledButton("X", 2, 0.5);
                deleteBtn.setVisible(!isEmpty);

                StackPane slotPane = new StackPane(slotTextBox);
                slotPane.setCursor(Cursor.HAND);

                slotPane.minWidthProperty().bind(uiSizeBinding(240)); 
                slotPane.minHeightProperty().bind(uiSizeBinding(180));

                if (!isEmpty) {
                    StackPane.setAlignment(deleteBtn, Pos.TOP_RIGHT);
                    slotPane.getChildren().add(deleteBtn);
                }

                slotPane.backgroundProperty().bind(Bindings.createObjectBinding(() ->
                    new Background(new BackgroundFill(
                        Color.rgb(30, 20, 60, 0.75), new CornerRadii(5), Insets.EMPTY
                    )), uiSizeProp));

                slotPane.setOnMouseEntered(e -> {
                    slotPane.setScaleX(1.02);
                    slotPane.setScaleY(1.02);
                });

                slotPane.setOnMouseExited(e -> {
                    slotPane.setScaleX(1.0);
                    slotPane.setScaleY(1.0);
                });

                slotPane.setOnMousePressed(e -> {
                    slotPane.setScaleX(0.98);
                    slotPane.setScaleY(0.98);
                });

                slotPane.setOnMouseReleased(e -> {
                    slotPane.setScaleX(1.0);
                    slotPane.setScaleY(1.0);
                    // ToDo (Start the game)
                });

                deleteBtn.setOnAction(e -> {
                    // ToDo (Delete Button)
                });
                slotGrid.add(slotPane, i % 3, i / 3);
            }
            VBox saveLayout = new VBox(40);
            saveLayout.setAlignment(Pos.CENTER);
            saveLayout.getChildren().addAll(saveTitle, slotGrid);

            Region saveBgRegion = new Region();
            saveBgRegion.backgroundProperty().bind(textureBackground);

            StackPane.setAlignment(exitSavesBtn, Pos.BOTTOM_RIGHT);
            uiSizeProp.addListener((obs, old, val) -> 
                StackPane.setMargin(exitSavesBtn, new Insets(0, uiSize(20), uiSize(25), 0)));
            StackPane.setMargin(exitSavesBtn, new Insets(0, uiSize(20), uiSize(25), 0));
            saveMenu = new StackPane(saveBgRegion, saveLayout, exitSavesBtn);

            // button functions
            playBtn.setOnAction(e -> changeMenu(gamemodeMenu));
            settingsBtn.setOnAction(e -> changeMenu(settingsMenu));
            leaderboardBtn.setOnAction(e -> headShake(leaderboardBtn));
            exitBtn.setOnAction(e -> Platform.exit());

            exitSettingsBtn.setOnAction(e -> changeMenu(mainMenu));

            standardBtn.setOnAction(e -> changeMenu(saveMenu));
            infiniteBtn.setOnAction(e -> headShake(infiniteBtn));
            exitGamemodeBtn.setOnAction(e -> changeMenu(mainMenu));

            exitSavesBtn.setOnAction(e -> changeMenu(gamemodeMenu));

            // beginning transitions 
            mainMenuButtonContainer.setTranslateY(scene.getHeight() / 5);
            TranslateTransition btnsSlide = slideInTransition(mainMenuButtonContainer, scene.getHeight()/40, 1, 1.5);
            FadeTransition btnsFade = fadeInTransition(mainMenuButtonContainer, 1, 1.5);
            FadeTransition titleFade = fadeInTransition(titleText, 1.3, 1.5);
            FadeTransition bgFade = fadeInTransition(bgRegion, 2.2, .2);

            // skip
            Runnable skipIntro = () -> {
                btnsFade.stop();
                btnsSlide.stop();
                titleFade.stop();
                bgFade.stop();
                btnsFade.setDelay(Duration.ZERO);
                btnsSlide.setDelay(Duration.ZERO);
                titleFade.setDelay(Duration.ZERO);
                btnsFade.setRate(2);
                btnsSlide.setRate(2);
                titleFade.setRate(2);
                btnsFade.play();
                btnsSlide.play();
                titleFade.play();
                bgRegion.setOpacity(1);
            };

            btnsSlide.setOnFinished(e -> {
                menuPane.setOnMousePressed(null);
                menuPane.setOnKeyPressed(null);
            });

            menuPane.setOnMousePressed(e -> skipIntro.run());
            menuPane.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.SPACE) {
                    skipIntro.run();
                }
            });

            // adding menus to the menuPane
            menuPane.getChildren().addAll(gamemodeMenu, saveMenu, settingsMenu);
            for (Node n : menuPane.getChildren()) {
                if (n != mainMenu) {
                    n.setVisible(false);
                    n.setOpacity(0);
                }
            }

            // setting up the stage
            primaryStage.setTitle("Dungeonfall");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(480);
            primaryStage.setMinHeight(360);
            primaryStage.show();

            primaryStage.setFullScreenExitHint("");
            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.F11) {
                    primaryStage.setFullScreen(!primaryStage.isFullScreen());
                }
            });

            UI_SCALE.set(1);

        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    private void headShake(Node node) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(node.translateXProperty(), 0)),
            new KeyFrame(Duration.millis(30), new KeyValue(node.translateXProperty(), uiSize(-7))),
            new KeyFrame(Duration.millis(90), new KeyValue(node.translateXProperty(), uiSize(7))),
            new KeyFrame(Duration.millis(150), new KeyValue(node.translateXProperty(), uiSize(-7))),
            new KeyFrame(Duration.millis(210), new KeyValue(node.translateXProperty(), uiSize(7))),
            new KeyFrame(Duration.millis(270), new KeyValue(node.translateXProperty(), uiSize(-7))),
            new KeyFrame(Duration.millis(300), new KeyValue(node.translateXProperty(), 0))
        );
        timeline.play();
    }

    // type 0: long, 1: short, 2: square
    private Button createStyledButton(String label, int type) {return createStyledButton(label, type, 1.0);}

    private Button createStyledButton(String label, int type, double baseScale){
        ObjectProperty<Image> defProp = type == 0 ? longBtnDefaultProp : type == 1 ? shortBtnDefaultProp : squareBtnDefaultProp;
        ObjectProperty<Image> pressProp = type == 0 ? longBtnPressedProp : type == 1 ? shortBtnPressedProp : squareBtnPressedProp;

        Button btn = new Button();
        btn.setScaleX(baseScale);
        btn.setScaleY(baseScale);

        ImageView btnView = new ImageView();
        btnView.setSmooth(false);

        // press effect
        BooleanProperty isPressed = new SimpleBooleanProperty(false);
        btnView.imageProperty().bind(Bindings.createObjectBinding(
            () -> isPressed.get() ? pressProp.get() : defProp.get(),
            isPressed, defProp, pressProp
        ));

        Text btnText = new Text(label);
        btnText.fontProperty().bind(customFontProp);
        btnText.setFill(Color.WHITE);
        btnText.setScaleX(.8);

        btnText.translateYProperty().bind(Bindings.createDoubleBinding(
            () -> isPressed.get() ? uiSize(2) : uiSize(-4),
            isPressed, uiSizeProp
        ));

        StackPane btnContent = new StackPane(btnView, btnText);
        btn.setGraphic(btnContent);
        btn.setBackground(Background.EMPTY);
        btn.setPadding(Insets.EMPTY);

        // hover effect
        btn.setOnMouseEntered(e -> {
            btn.setScaleX(baseScale * 1.05);
            btn.setScaleY(baseScale * 1.05);
            btnText.setFill(Color.rgb(246, 244, 255));
            btnView.setEffect(new ColorAdjust(0.05, .2, 0.05, 0));
        });
        
        btn.setOnMouseExited(e -> {
            btn.setScaleX(baseScale);
            btn.setScaleY(baseScale);
            btnText.setFill(Color.WHITE);
            btnView.setEffect(null);
        });
        
        btn.setOnMousePressed(e -> isPressed.set(true));
        btn.setOnMouseReleased(e -> isPressed.set(false));

        return btn;
    }

    private FadeTransition fadeInTransition(Node node, double duration, double delay) {
        node.setOpacity(0);
        FadeTransition fadeTrans = new FadeTransition(Duration.seconds(duration), node);
        fadeTrans.setToValue(1);
        fadeTrans.setDelay(Duration.seconds(delay));
        fadeTrans.play();
        return fadeTrans;
    }

    private TranslateTransition slideInTransition(Node box, double toY, double duration, double delay) {
        TranslateTransition slideTrans = new TranslateTransition(Duration.seconds(duration), box);
        slideTrans.setInterpolator(Interpolator.EASE_OUT);
        slideTrans.setToY(toY);
        slideTrans.setDelay(Duration.seconds(delay));
        slideTrans.play();
        return slideTrans;
    }

    private void changeMenu(StackPane menu) {
        menuPane.setMouseTransparent(true);
        for (Node n : menuPane.getChildren()) {
            if (n.isVisible()) {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(.5), n);
                fadeOut.setToValue(0);
                fadeOut.setInterpolator(Interpolator.EASE_OUT);
                fadeOut.setOnFinished(e -> n.setVisible(false));
                fadeOut.play();
            }
        }
        menu.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(.5), menu);
        fadeIn.setInterpolator(Interpolator.EASE_IN);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setDelay(Duration.seconds(.5));
        fadeIn.setOnFinished(e -> menuPane.setMouseTransparent(false));
        fadeIn.play();
    }

    public double uiSize(double s) {
        return uiSizeProp.get() * s;
    }

    public DoubleBinding uiSizeBinding(double s) {
        return Bindings.createDoubleBinding(() -> uiSizeProp.get() * s, uiSizeProp);
    }

    public Image loadImage(String name, double height) {
        return new Image(getClass().getResourceAsStream(name), 0, height, true, false);
    }

    public static StackPane getStage() {return menuPane;}

    public enum GameLayer {GROUND, ENTITIES, VFX, HUD}
    public static StackPane getGameLayer(GameLayer layer) {
        return (StackPane) gamePane.getChildren().get(layer.ordinal());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
