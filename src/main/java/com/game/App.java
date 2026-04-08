package com.game;

import java.util.Locale;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class App extends Application {

    private static Game activeGame;
    private static Scene scene;
    private static Stage stage;
    private static StackPane menuPane, gamePane; // holders of menus/layers
    private static StackPane mainMenu, settingsMenu, saveMenu, gamemodeMenu;
    private static GridPane slotGrid;
    public static StackPane HUDlayer = new StackPane();
    private static Canvas[] layers = new Canvas[GameLayer.values().length];
    private static Background bgFill;
    private static boolean qualifiedUser = true;
    private static boolean settingsFromGame =false;

    public static final DoubleProperty UI_SCALE = new SimpleDoubleProperty(1.0); // load from save
    public static final DoubleProperty uiSizeProp = new SimpleDoubleProperty(1.0);

    public static final ObjectProperty<Image> longBtnDefaultProp = new SimpleObjectProperty<>();
    public static final ObjectProperty<Image> longBtnPressedProp = new SimpleObjectProperty<>();
    public static final ObjectProperty<Image> shortBtnDefaultProp = new SimpleObjectProperty<>();
    public static final ObjectProperty<Image> shortBtnPressedProp = new SimpleObjectProperty<>();
    public static final ObjectProperty<Image> squareBtnDefaultProp = new SimpleObjectProperty<>();
    public static final ObjectProperty<Image> squareBtnPressedProp = new SimpleObjectProperty<>();
    public static final ObjectProperty<Image> bgImageProp = new SimpleObjectProperty<>();
    public static final ObjectProperty<Image> bgTextureProp = new SimpleObjectProperty<>();

    public static final ObjectProperty<Font> fontPropSmall = new SimpleObjectProperty<>();
    public static final ObjectProperty<Font> fontPropMedium = new SimpleObjectProperty<>();
    public static final ObjectProperty<Font> fontPropBig = new SimpleObjectProperty<>();
    private String bytebounceFontFamily;

    public static Runnable closePauseMenu;

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

        fontPropSmall.set(Font.font(bytebounceFontFamily, uiSize(40)));
        fontPropMedium.set(Font.font(bytebounceFontFamily, uiSize(72)));
        fontPropBig.set(Font.font(bytebounceFontFamily, uiSize(84)));

    }

    @Override
    public void start(Stage primaryStage) {
        try {            
            stage = primaryStage;

            GameSettings settings = SaveManager.loadSettings();
            UI_SCALE.set(settings.uiScale);

            bgFill = new Background(new BackgroundFill(Color.rgb(18, 14, 37), null, null));
            
            StackPane root = new StackPane();
            root.setBackground(bgFill);
 
            menuPane = new StackPane();
            menuPane.setBackground(bgFill);
            gamePane = new StackPane();
            gamePane.setVisible(false);

            root.getChildren().addAll(gamePane, menuPane);

            Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            scene = new Scene(root, screenBounds.getWidth() * .75, screenBounds.getHeight() * .75);

            bytebounceFontFamily = Font.loadFont(getClass().getResourceAsStream("/ByteBounce.ttf"), 1).getFamily();

            uiSizeProp.bind(Bindings.createDoubleBinding(
                () -> Math.min(scene.getWidth() / 720.0, scene.getHeight() / 720.0) * UI_SCALE.get(),
                scene.widthProperty(), scene.heightProperty(), UI_SCALE
            ));

            reloadUI();
            PauseTransition resizeDelay = new PauseTransition(Duration.millis(50));
            resizeDelay.setOnFinished(e -> reloadUI());
            scene.widthProperty().addListener((obs, oldVal, newVal) -> {resizeDelay.playFromStart();});
            scene.heightProperty().addListener((obs, oldVal, newVal) -> {resizeDelay.playFromStart();});
            UI_SCALE.addListener((obs, oldVal, newVal) -> {resizeDelay.playFromStart();});

            // main menu title
            Text titleText = new Text("DUNGEONFALL");
            titleText.fontProperty().bind(fontPropBig);
            titleText.setFill(Color.WHITE);
            titleText.setScaleX(.85);
            titleText.setBoundsType(TextBoundsType.VISUAL);

            DropShadow outerStroke = new DropShadow();
            outerStroke.setColor(Color.rgb(103, 15, 255));
            outerStroke.setRadius(12.0);
            outerStroke.setSpread(0.5);
            titleText.setEffect(outerStroke);

            StackPane.setAlignment(titleText, Pos.TOP_CENTER);
            scene.heightProperty().addListener((obs, old, val) -> 
                StackPane.setMargin(titleText, new Insets(scene.getHeight() * .13, 0, 0, 0)));
            StackPane.setMargin(titleText, new Insets(scene.getHeight() * .13, 0, 0, 0));

            // main menu buttons
            Button playBtn = createStyledButton("PLAY", 0);
            Button settingsBtn = createStyledButton("SETTINGS", 0);
            Button leaderboardBtn = createStyledButton("LEADERBOARD", 0);
            Button exitBtn = createStyledButton("EXIT", 0);
            
            VBox mainMenuButtonContainer = new VBox(playBtn, settingsBtn, leaderboardBtn, exitBtn);
            mainMenuButtonContainer.setAlignment(Pos.CENTER);
            mainMenuButtonContainer.spacingProperty().bind(uiSizeBinding(12));

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
            Button resetControlsBtn = createStyledButton("RESET CONTROLS", 1);
            Button resetSettingsBtn = createStyledButton("RESET SETTINGS", 0);
            StackPane resetSettingsGraphic = (StackPane)(resetSettingsBtn.getGraphic());
            resetSettingsGraphic.getChildren().get(1).setScaleX(.7);

            Button exitSettingsBtn = createStyledButton("CLOSE", 0);
            Label settingsTitle = new Label("SETTINGS");
            settingsTitle.fontProperty().bind(fontPropSmall);
            settingsTitle.setAlignment(Pos.TOP_CENTER);
            settingsTitle.setTextFill(Color.WHITE);

            Label controlsLabel = new Label("Controls");
            controlsLabel.fontProperty().bind(fontPropSmall);
            controlsLabel.setTextFill(Color.WHITE);

            GridPane controlsGrid = new GridPane();
            controlsGrid.setHgap(8);
            controlsGrid.setVgap(8);
            controlsGrid.setAlignment(Pos.CENTER_LEFT);

            String[] controlActions = {"up", "down", "left", "right", "attack", "interact", "map"};
            Button[] keyButtons = new Button[controlActions.length];
            javafx.beans.property.ObjectProperty<String> waitingForKey = new SimpleObjectProperty<>(null);

            for (int i = 0; i < controlActions.length; i++) {
                String action = controlActions[i];
                String label = action.substring(0, 1).toUpperCase(Locale.ENGLISH) + action.substring(1);
                Button actionBtn = createStyledButton(label, 1);
                actionBtn.setDisable(true);
                actionBtn.setOpacity(0.9);

                String binding = GameSettings.getKeyBinding(action);
                Button keyBtn = createStyledButton(binding, 1);
                int index = i;
                keyBtn.setOnAction(e -> {
                    keyBtn.setText("Press a key..");
                    waitingForKey.set(controlActions[index]);
                });
                keyBtn.prefWidthProperty().bind(uiSizeBinding(120));
                actionBtn.prefWidthProperty().bind(uiSizeBinding(120));
                controlsGrid.add(actionBtn, 0, i);
                controlsGrid.add(keyBtn, 1, i);
                keyButtons[i] = keyBtn;
            }

            Label visualsLabel = new Label("Visuals");
            visualsLabel.fontProperty().bind(fontPropSmall);
            visualsLabel.setTextFill(Color.WHITE);

            GridPane visualsGrid = new GridPane();
            visualsGrid.setHgap(8);
            visualsGrid.setVgap(8);
            visualsGrid.setAlignment(Pos.CENTER_LEFT);

            Button uiScaleBtn = createStyledButton("UI Scale", 1);
            Button uiScaleValue = createStyledButton(GameSettings.getUiScaleLabel(), 1);
            uiScaleValue.setDisable(true);
            uiScaleValue.setOpacity(0.9);
            Slider uiScaleSlider = new Slider(0.75, 1.25, settings.uiScale);
            uiScaleSlider.setShowTickLabels(false);
            uiScaleSlider.setShowTickMarks(false);
            uiScaleSlider.setPrefWidth(200);
            uiScaleSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                GameSettings.setUiScale(newVal.doubleValue());
                UI_SCALE.set(newVal.doubleValue());
                uiScaleValue.setText(GameSettings.getUiScaleLabel());
            });
            visualsGrid.add(uiScaleBtn, 0, 0);
            visualsGrid.add(uiScaleValue, 1, 0);
            visualsGrid.add(uiScaleSlider, 0, 1, 2, 1);

            Button attackInputBtn = createStyledButton("Attack Input", 1);
            attackInputBtn.setDisable(true);
            attackInputBtn.setOpacity(0.9);
            Button attackInputValue = createStyledButton(GameSettings.instance.attackInputMode, 1);
            attackInputValue.setOnAction(e -> {
                String next = GameSettings.isMouseAttackMode() ? "KEYBOARD" : "MOUSE";
                GameSettings.setAttackInputMode(next);
                attackInputValue.setText(next);
            });
            visualsGrid.add(attackInputBtn, 0, 2);
            visualsGrid.add(attackInputValue, 1, 2);

            Label audioLabel = new Label("Audio");
            audioLabel.fontProperty().bind(fontPropSmall);
            audioLabel.setTextFill(Color.WHITE);

            GridPane audioGrid = new GridPane();
            audioGrid.setHgap(8);
            audioGrid.setVgap(8);
            audioGrid.setAlignment(Pos.CENTER_LEFT);

            Button allBtn = createStyledButton("All", 1);
            Button allValue = createStyledButton(GameSettings.getMasterVolume()+"%", 1);
            allValue.setDisable(true);
            allValue.setOpacity(0.9);
            Slider allSlider = new Slider(0, 100, settings.masterVolume);
            allSlider.setShowTickLabels(false);
            allSlider.setShowTickMarks(false);
            allSlider.setPrefWidth(200);
            allSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                GameSettings.setMasterVolume(newVal.intValue());
                allValue.setText(GameSettings.getMasterVolume()+"%");
            });
            audioGrid.add(allBtn, 0, 0);
            audioGrid.add(allValue, 1, 0);
            audioGrid.add(allSlider, 0, 1, 2, 1);

            Button musicBtn = createStyledButton("Music", 1);
            Button musicValue = createStyledButton(GameSettings.getMusicVolume()+"%", 1);
            musicValue.setDisable(true);
            musicValue.setOpacity(0.9);
            Slider musicSlider = new Slider(0, 100, settings.musicVolume);
            musicSlider.setShowTickLabels(false);
            musicSlider.setShowTickMarks(false);
            musicSlider.setPrefWidth(200);
            musicSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                GameSettings.setMusicVolume(newVal.intValue());
                musicValue.setText(GameSettings.instance.musicVolume+"%");
            });
            audioGrid.add(musicBtn, 0, 2);
            audioGrid.add(musicValue, 1, 2);
            audioGrid.add(musicSlider, 0, 3, 2, 1);

            Button sfxBtn = createStyledButton("SFX", 1);
            Button sfxValue = createStyledButton(GameSettings.instance.sfxVolume+"%", 1);
            sfxValue.setDisable(true);
            sfxValue.setOpacity(0.9);
            Slider sfxSlider = new Slider(0, 100, settings.sfxVolume);
            sfxSlider.setShowTickLabels(false);
            sfxSlider.setShowTickMarks(false);
            sfxSlider.setPrefWidth(200);
            sfxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                GameSettings.setSfxVolume(newVal.intValue());
                sfxValue.setText(GameSettings.instance.sfxVolume+"%");
            });
            audioGrid.add(sfxBtn, 0, 4);
            audioGrid.add(sfxValue, 1, 4);
            audioGrid.add(sfxSlider, 0, 5, 2, 1);

            VBox leftColumn = new VBox(controlsLabel, controlsGrid);
            leftColumn.setSpacing(12);
            VBox rightColumn = new VBox(visualsLabel, visualsGrid, audioLabel, audioGrid);
            rightColumn.setSpacing(12);

            HBox settingsContent = new HBox(leftColumn, rightColumn);
            settingsContent.setAlignment(Pos.CENTER);
            settingsContent.setSpacing(28);

            HBox resetButtons = new HBox(resetControlsBtn, resetSettingsBtn);
            resetButtons.setAlignment(Pos.CENTER);
            resetButtons.setSpacing(10);

            VBox settingsMenuContainer = new VBox(settingsTitle, settingsContent, resetButtons, exitSettingsBtn);
            settingsMenuContainer.setAlignment(Pos.TOP_CENTER);
            settingsMenuContainer.setSpacing(16);
            settingsMenuContainer.setPadding(new Insets(18));

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

            resetControlsBtn.setOnAction(e -> {
                GameSettings.resetControls();
                for (int i = 0; i < controlActions.length; i++) {
                    keyButtons[i].setText(GameSettings.getKeyBinding(controlActions[i]));
                }
            });

            resetSettingsBtn.setOnAction(e -> {
                GameSettings.resetVisuals();
                GameSettings.resetAudio();
                uiScaleSlider.setValue(settings.uiScale);
                allSlider.setValue(settings.masterVolume);
                musicSlider.setValue(settings.musicVolume);
                sfxSlider.setValue(settings.sfxVolume);
                uiScaleValue.setText(GameSettings.getUiScaleLabel());
                attackInputValue.setText(GameSettings.instance.attackInputMode);
                allValue.setText(GameSettings.getMasterVolume()+"%");
                musicValue.setText(GameSettings.instance.musicVolume+"%");
                sfxValue.setText(GameSettings.instance.sfxVolume+"%");
                UI_SCALE.set(GameSettings.instance.uiScale);
            });

            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (waitingForKey.get() != null) {
                    String action = waitingForKey.get();
                    String keyName = event.getCode().getName();
                    GameSettings.setKeyBinding(action, keyName);
                    for (int i = 0; i < controlActions.length; i++) {
                        if (controlActions[i].equals(action)) {
                            keyButtons[i].setText(keyName);
                            break;
                        }
                    }
                    waitingForKey.set(null);
                    event.consume();
                    return;
                }
                if (event.getCode() == KeyCode.F11) {
                    primaryStage.setFullScreen(!primaryStage.isFullScreen());
                }
            });
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
            saveTitle.fontProperty().bind(fontPropMedium);
            saveTitle.setTextFill(Color.WHITE);

            slotGrid = new GridPane();
            slotGrid.setAlignment(Pos.CENTER);
            slotGrid.vgapProperty().bind(uiSizeBinding(30));
            slotGrid.hgapProperty().bind(uiSizeBinding(30));

            updateSaveSlots();
            
            VBox saveLayout = new VBox();
            saveLayout.spacingProperty().bind(uiSizeBinding(35));
            saveLayout.setAlignment(Pos.CENTER);
            saveLayout.getChildren().addAll(saveTitle, slotGrid);

            uiSizeProp.addListener((obs, old, val) -> {
                StackPane.setMargin(saveLayout, new Insets(0, 0, uiSize(30), 0));});
            StackPane.setMargin(saveLayout, new Insets(0, 0, uiSize(30), 0));

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
            exitBtn.setOnAction(e ->
                primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST))
            );

            exitSettingsBtn.setOnAction(e -> {
                if(!settingsFromGame)changeMenu(mainMenu);
                else {
                    settingsFromGame = false;
                    menuPane.setVisible(false);
                    settingsMenu.setVisible(false);
                    settingsMenu.setOpacity(0);
                    gamePane.setVisible(true);
                }
            });

            standardBtn.setOnAction(e -> changeMenu(saveMenu));
            infiniteBtn.setOnAction(e -> {
                if(qualifiedUser){startInfinite();}
                else headShake(infiniteBtn);
                
            });
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

            // load window
            if (settings.windowX != -1) {
                primaryStage.setX(settings.windowX);
                primaryStage.setY(settings.windowY);
                primaryStage.setWidth(settings.windowWidth);
                primaryStage.setHeight(settings.windowHeight);
                primaryStage.setMaximized(settings.isMaximized);
                primaryStage.setFullScreen(settings.isFullscreen);
            }

            primaryStage.setFullScreenExitHint("");
            primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

            // canvas
            for (GameLayer layer : GameLayer.values()) {
                Canvas canvas = new Canvas(800, 600);
                canvas.widthProperty().bind(scene.widthProperty());
                canvas.heightProperty().bind(scene.heightProperty());
                layers[layer.ordinal()] = canvas;
                gamePane.getChildren().add(canvas);
            }

            HUDlayer.setPickOnBounds(false);
            gamePane.getChildren().add(HUDlayer);

            primaryStage.show();

            // saver
            primaryStage.setOnCloseRequest(e -> {
                settings.isFullscreen = primaryStage.isFullScreen();
                settings.isMaximized = primaryStage.isMaximized();

                if (primaryStage.isFullScreen()) primaryStage.setFullScreen(false);
                if (primaryStage.isMaximized()) primaryStage.setMaximized(false);

                settings.windowX = primaryStage.getX();
                settings.windowY = primaryStage.getY();
                settings.windowWidth = primaryStage.getWidth();
                settings.windowHeight = primaryStage.getHeight();

                settings.uiScale = UI_SCALE.get();

                if (activeGame != null) {
                    activeGame.saveCurrentGame();
                }

                SaveManager.saveSettings();
            });

        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
    }


    // ---- MAIN METHODS ---- //

    static boolean gameOverShown = false;

    public static VBox scorePane(){
        GameStats a = GameStats.getInstance();
        a.calculateScore();
        Text time = new Text("Time Passed: " + a.getTimePassed());
        time.fontProperty().bind(fontPropSmall);
        time.setFill(Color.WHITE);
        Text score = new Text("Score: " + a.getScore());
        score.fontProperty().bind(fontPropSmall);
        score.setFill(Color.WHITE);
        Text dash = new Text("--------------------------------------------------------");
        dash.fontProperty().bind(fontPropSmall);
        dash.setFill(Color.WHITE);
        Text enemies = new Text("Enemies Killed: " + a.getEnemiesKilled() + " X100");
        enemies.fontProperty().bind(fontPropSmall);
        enemies.setFill(Color.WHITE);
        Text chests = new Text("Chest: " + a.getChestsOpened() + " X10");
        chests.fontProperty().bind(fontPropSmall);
        chests.setFill(Color.WHITE);
        Text boss = new Text("Boss' Killed: " + a.getBossKilled() + " X500");
        boss.fontProperty().bind(fontPropSmall);
        boss.setFill(Color.WHITE);
        Text level = new Text("Levels Cleared: " + a.getLevelsCleared() + " X500");
        level.fontProperty().bind(fontPropSmall);
        level.setFill(Color.WHITE);
        VBox scorePane = new VBox(time,score,dash,enemies,chests,boss,level);
        return scorePane;
    }

    public static void showGameOver() {
        if (gameOverShown) return;
        gameOverShown = true;
        activeGame.stopGame(1);

        

        StackPane overlay = new StackPane();
        overlay.setBackground(bgFill);
        overlay.setOpacity(0);

        Text text = new Text("GAME OVER");
        text.fontProperty().bind(fontPropBig);
        text.setFill(Color.WHITE);

        
        Button menuBtn = createStyledButton("MAIN MENU", 0);
        menuBtn.setOnAction(e -> {
            gameOverShown = false;
            gamePane.getChildren().remove(overlay);
            gamePane.setVisible(false);
            menuPane.setVisible(true);
        });
        VBox box;
        if(Game.getType() == 0){    
            box = new VBox(uiSize(30), text, menuBtn);
        }else{
            box = new VBox(uiSize(30), text, scorePane(), menuBtn);
        }
        box.setAlignment(Pos.CENTER);
        overlay.getChildren().add(box);

        gamePane.getChildren().add(overlay);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), overlay);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    public static void drawGameEnding() {
        if (gameOverShown) return;
        gameOverShown = true;

        activeGame.stopGame(1);

        StackPane overlay = new StackPane();
        overlay.setBackground(bgFill);
        overlay.setOpacity(0);

        Text text = new Text("YOU WON");
        text.fontProperty().bind(fontPropBig);
        text.setFill(Color.WHITE);

        Button menuBtn = createStyledButton("MAIN MENU", 0);
        menuBtn.setOnAction(e -> {
            gameOverShown = false;
            gamePane.getChildren().remove(overlay);
            gamePane.setVisible(false);
            menuPane.setVisible(true);
        });

        VBox box = new VBox(uiSize(30), text, menuBtn);
        box.setAlignment(Pos.CENTER);
        overlay.getChildren().add(box);

        gamePane.getChildren().add(overlay);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), overlay);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private static void onSlotClicked(char saveSlot) {
        menuPane.setVisible(false);

        activeGame = new Game(saveSlot);
        activeGame.setType(0);
        activeGame.startGame();

        gamePane.setVisible(true);
    }

    private static void startInfinite(){
        menuPane.setVisible(false);
        activeGame = new Game('z');
        activeGame.setType(1);
        activeGame.startInfinite();
        gamePane.setVisible(true);
    }

    public static void main(String[] args) {
        launch(args);
    }


    // ---- METHODS FOR UI ---- //

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
    private static Button createStyledButton(String label, int type) {return createStyledButton(label, type, 1.0);}

    private static Button createStyledButton(String label, int type, double baseScale){
        ObjectProperty<Image> defProp = type == 0 ? longBtnDefaultProp : type == 1 ? shortBtnDefaultProp : squareBtnDefaultProp;
        ObjectProperty<Image> pressProp = type == 0 ? longBtnPressedProp : type == 1 ? shortBtnPressedProp : squareBtnPressedProp;

        Button btn = new Button();
        btn.setScaleX(baseScale);
        btn.setScaleY(baseScale);

        btn.setCursor(Cursor.HAND);

        ImageView btnView = new ImageView();
        btnView.setSmooth(false);

        // press effect
        BooleanProperty isPressed = new SimpleBooleanProperty(false);
        btnView.imageProperty().bind(Bindings.createObjectBinding(
            () -> isPressed.get() ? pressProp.get() : defProp.get(),
            isPressed, defProp, pressProp
        ));

        Text btnText = new Text();
        btnText.textProperty().bind(btn.textProperty());
        btnText.fontProperty().bind(fontPropSmall);
        btnText.setFill(Color.WHITE);
        btnText.setScaleX(.8);
        btnText.setBoundsType(TextBoundsType.VISUAL);

        btnText.translateYProperty().bind(Bindings.createDoubleBinding(
            () -> isPressed.get() ? uiSize(2) : uiSize(-4),
            isPressed, uiSizeProp
        ));

        StackPane btnContent = new StackPane(btnView, btnText);
        btn.setGraphic(btnContent);
        btn.setBackground(Background.EMPTY);
        btn.setPadding(Insets.EMPTY);
        btn.setText(label);
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

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
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(.4), n);
                fadeOut.setToValue(0);
                fadeOut.setInterpolator(Interpolator.EASE_OUT);
                fadeOut.setOnFinished(e -> n.setVisible(false));
                fadeOut.play();
            }
        }
        menu.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(.4), menu);
        fadeIn.setInterpolator(Interpolator.EASE_IN);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setDelay(Duration.seconds(.4));
        fadeIn.setOnFinished(e -> menuPane.setMouseTransparent(false));
        fadeIn.play();
    }

    public static void updateSaveSlots() {
        slotGrid.getChildren().clear();
        for (int i = 0; i < 6; i++) {
            char slotChar = (char) (i + 65);
            boolean saveExists = SaveManager.saveExists(slotChar);
            
            Label slotLabel = new Label(("SLOT " + slotChar).toUpperCase());
            slotLabel.fontProperty().bind(fontPropSmall);
            slotLabel.setTextFill(Color.rgb(187, 174, 232, 1));
    
            Label slotInfo = new Label(saveExists ? "* CONTINUE *" : "* EMPTY *");
            slotInfo.fontProperty().bind(fontPropSmall);
            slotInfo.setTextFill(Color.rgb(219, 218, 220, 1));
    
            VBox slotTextBox = new VBox(slotLabel, slotInfo);
            slotTextBox.setAlignment(Pos.CENTER);
    
            Button deleteBtn = createStyledButton("X", 2, 0.5);
            deleteBtn.setVisible(saveExists);
            deleteBtn.setOnAction(e -> {
                SaveManager.deleteSave(slotChar);
                updateSaveSlots();
            });
    
            StackPane slotPane = new StackPane(slotTextBox);
            slotPane.setCursor(Cursor.HAND);
            slotPane.prefWidthProperty().bind(uiSizeBinding(220));
            slotPane.prefHeightProperty().bind(uiSizeBinding(160));
            slotPane.setBackground(new Background(new BackgroundFill(
                Color.rgb(30, 20, 60, 0.95), new CornerRadii(15), Insets.EMPTY
            )));
    
            if (saveExists) {
                StackPane.setAlignment(deleteBtn, Pos.TOP_RIGHT);
                slotPane.getChildren().add(deleteBtn);
            }
    
            slotPane.setOnMouseEntered(e -> { slotPane.setScaleX(1.03); slotPane.setScaleY(1.03); });
            slotPane.setOnMouseExited(e -> { slotPane.setScaleX(1.0); slotPane.setScaleY(1.0); });
            slotPane.setOnMousePressed(e -> { slotPane.setScaleX(0.97); slotPane.setScaleY(0.97); });
            slotPane.setOnMouseReleased(e -> {
                slotPane.setScaleX(1.0);
                slotPane.setScaleY(1.0);
                onSlotClicked(slotChar);
            });
    
            slotGrid.add(slotPane, i % 3, i / 3);
        }
    }

    public static double uiSize(double s) {
        return uiSizeProp.get() * s;
    }

    public static DoubleBinding uiSizeBinding(double s) {
        return Bindings.createDoubleBinding(() -> uiSizeProp.get() * s, uiSizeProp);
    }

    public Image loadImage(String name, double height) {
        return new Image(getClass().getResourceAsStream(name), 0, height, true, false);
    }

    // ---- GETTER METHODS FOR OUTSIDE APP.JAVA ---- //

     public static void drawPauseMenu(){
        Button cont = createStyledButton("Continue", 0);
        Button settings = createStyledButton("Settings", 0);
        Button SaveNExt = createStyledButton("Save & Exit", 0);
        VBox menuBox = new VBox(uiSize(30),cont,settings,SaveNExt);//make the size smaler
        
        menuBox.setAlignment(Pos.CENTER);
        menuBox.spacingProperty().bind(uiSizeBinding(10));
        StackPane pauseMenuPane = new StackPane(menuBox);
        StackPane.setAlignment(pauseMenuPane, Pos.CENTER);
        pauseMenuPane.setBackground(new Background(new BackgroundFill(
            Color.rgb(30, 20, 60, 0.3), new CornerRadii(15), Insets.EMPTY
        )));
        pauseMenuPane.prefWidthProperty().bind(uiSizeBinding(150));
        pauseMenuPane.prefHeightProperty().bind(uiSizeBinding(220));
        
        gamePane.getChildren().add(pauseMenuPane);

        closePauseMenu = () -> {
            gamePane.getChildren().remove(pauseMenuPane);
            activeGame.unPauseTimer();
        };
        cont.setOnAction(e -> {
            closePauseMenu.run();
        });
        gamePane.setOnKeyPressed(e -> {
            if (activeGame.activeKeys.contains(GameSettings.getKeyCode("menu"))){
                Game.isPaused = true;
                gamePane.getChildren().remove(pauseMenuPane);
                activeGame.unPauseTimer();
            }
        });
        settings.setOnAction(e -> {
            gamePane.setVisible(false);
            settingsMenu.setVisible(true);
            settingsMenu.setOpacity(1);
            menuPane.setVisible(true);
            settingsFromGame = true;
        });
        SaveNExt.setOnAction(event -> {
            if(activeGame != null)
                activeGame.saveCurrentGame();
            updateSaveSlots();
            gamePane.getChildren().remove(pauseMenuPane);
            gamePane.setVisible(false);
            menuPane.setVisible(true);
        });
    }

    public static Scene getScene() {return scene;}
    public static Stage getStage() {return stage;}

    public enum GameLayer {GROUND, ENTITIES, VFX}
    public static GraphicsContext getLayerGC(GameLayer layer) {
        return layers[layer.ordinal()].getGraphicsContext2D();
    }
    public static Canvas getLayerCanvas(GameLayer layer) {
        return layers[layer.ordinal()];
    }
}
