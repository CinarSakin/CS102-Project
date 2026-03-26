package com.game;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    private static double UI_SCALE;
    private static double WIDTH, HEIGHT;

    private StackPane mainPane; // used for transitioning between menus
    private StackPane mainMenu, settingsMenu, saveMenu, gamemodeMenu;

    private Image longBtnDefault, longBtnPressed, shortBtnDefault, shortBtnPressed, squareBtnDefault, squareBtnPressed;
    private Image bgImageRaw, bgTexture;
    private Font customFont, titleFont;

    @Override
    public void start(Stage primaryStage) {
        try {

            // calculating screen size
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            WIDTH = screenBounds.getWidth() * .75;
            HEIGHT = screenBounds.getHeight() * .75;
            UI_SCALE = 1.0; // will load from settings file

            // loading assets
            longBtnDefault = loadImage("/sprites/ui/longBtnDefault.png", uiScale(48));
            longBtnPressed = loadImage("/sprites/ui/longBtnPressed.png", uiScale(48));   
            shortBtnDefault = loadImage("/sprites/ui/shortBtnDefault.png", uiScale(48));
            shortBtnPressed = loadImage("/sprites/ui/shortBtnPressed.png", uiScale(48));   
            squareBtnDefault = loadImage("/sprites/ui/squareBtnDefault.png", uiScale(48));
            squareBtnPressed = loadImage("/sprites/ui/squareBtnPressed.png", uiScale(48));   
            bgImageRaw = loadImage("/sprites/ui/mainMenuBackground.png", HEIGHT);
            bgTexture  = loadImage("/sprites/ui/backgroundTexture.png", HEIGHT);
            customFont = Font.loadFont(getClass().getResourceAsStream("/ByteBounce.ttf"), uiScale(40));
            titleFont =  Font.loadFont(getClass().getResourceAsStream("/ByteBounce.ttf"), uiScale(84));

            // main menu title
            Text titleText = new Text("DUNGEONFALL");
            titleText.setFont(titleFont);
            titleText.setFill(Color.WHITE);
            titleText.setScaleX(.85);
            titleText.setBoundsType(javafx.scene.text.TextBoundsType.VISUAL);

            javafx.scene.effect.DropShadow outerStroke = new javafx.scene.effect.DropShadow();
            outerStroke.setColor(Color.rgb(103, 15, 255));
            outerStroke.setRadius(12.0);
            outerStroke.setSpread(0.5);
            titleText.setEffect(outerStroke);   

            // main menu title container
            VBox mainMenuTitleContainer = new VBox(titleText);
            mainMenuTitleContainer.setAlignment(Pos.TOP_CENTER);
            mainMenuTitleContainer.setPadding(new Insets(HEIGHT*.13, 0, 0, 0));

            // main menu buttons
            Button playBtn = createStyledButton("PLAY", 0);
            Button settingsBtn = createStyledButton("SETTINGS", 0);
            Button exitBtn = createStyledButton("EXIT", 0);
            Button leaderboardBtn = createStyledButton("LEADERBOARD", 0);
            
            VBox mainMenuButtonContainer = new VBox(uiScale(15), playBtn, settingsBtn, leaderboardBtn, exitBtn);
            mainMenuButtonContainer.setAlignment(Pos.CENTER);

            ImageView bgImage = new ImageView(bgImageRaw);
            Background bgFill = new Background(new BackgroundFill(Color.rgb(18, 14, 37), null, null));
            mainMenu = new StackPane(bgImage, mainMenuTitleContainer, mainMenuButtonContainer );
            mainMenu.setBackground(bgFill);

            mainPane = new StackPane(mainMenu);
            mainPane.setBackground(bgFill);

            // settings menu
            Button resetSettingsBtn = createStyledButton("RESET SETTINGS", 0);
            StackPane stpane = (StackPane)(resetSettingsBtn.getGraphic());
            stpane.getChildren().get(1).setScaleX(.7);

            Button exitSettingsBtn = createStyledButton("CLOSE", 0);
            Label settingsTitle = new Label("SETTINGS");
            settingsTitle.setFont(customFont);
            settingsTitle.setAlignment(Pos.TOP_CENTER);

            VBox settingsMenuContainer = new VBox(uiScale(15), settingsTitle, resetSettingsBtn, exitSettingsBtn);
            settingsMenuContainer.setAlignment(Pos.CENTER);
            
            Rectangle settingsBG = new Rectangle(WIDTH, HEIGHT);
            settingsBG.setFill(new ImagePattern(bgTexture, 0, 0, bgTexture.getWidth(), HEIGHT, false));
            settingsMenu = new StackPane(settingsBG, settingsMenuContainer);

            // gamemode menu
            Button standardBtn = createStyledButton("STANDARD", 0);
            Button infiniteBtn = createStyledButton("INFINITE", 0);
            Button exitGamemodeBtn = createStyledButton("GO BACK", 1);
            exitGamemodeBtn.setScaleX(.8);
            exitGamemodeBtn.setScaleY(.8);

            VBox gamemodeButtonContainer = new VBox(uiScale(15), standardBtn, infiniteBtn);
            gamemodeButtonContainer.setAlignment(Pos.CENTER);

            StackPane.setAlignment(exitGamemodeBtn, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(exitGamemodeBtn, new Insets(0, uiScale(20), uiScale(25), 0));

            gamemodeMenu = new StackPane(new ImageView(bgImageRaw), gamemodeButtonContainer, exitGamemodeBtn);

            // save file menu
            Button exitSavesBtn = createStyledButton("GO BACK", 1);
        //    StackPane.setAlignment(exitSavesBtn, Pos.BOTTOM_CENTER);

            Rectangle saveBG = new Rectangle(WIDTH, HEIGHT);
            saveBG.setFill(new ImagePattern(bgTexture, 0, 0, bgTexture.getWidth(), HEIGHT, false));
            saveMenu = new StackPane(saveBG, exitSavesBtn);

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
            mainMenuButtonContainer.setTranslateY(HEIGHT/5);
            TranslateTransition btnsSlide = slideInTransition(mainMenuButtonContainer, HEIGHT/40, 1, 1.5);
            FadeTransition btnsFade = fadeInTransition(mainMenuButtonContainer, 1, 1.5);
            FadeTransition titleFade = fadeInTransition(mainMenuTitleContainer, 1.3, 1.5);
            FadeTransition bgFade = fadeInTransition(bgImage, 2.2, .2);

            // skip
            btnsSlide.setOnFinished(e -> {
                mainPane.setOnMousePressed(null);
                mainPane.setOnKeyPressed(null);
            });

            mainPane.setOnMousePressed(e -> {
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
                bgImage.setOpacity(1); 
            });

            mainPane.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.SPACE){
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
                    bgImage.setOpacity(1); 
                }
            });

            mainPane.getChildren().addAll(gamemodeMenu,saveMenu,settingsMenu);
            for (javafx.scene.Node n : mainPane.getChildren()){
                if (n != mainMenu){
                    n.setVisible(false);
                    n.setOpacity(0);
                }
            }

            primaryStage.setTitle("Dungeonfall");
            primaryStage.setScene(new Scene(mainPane, WIDTH, HEIGHT));
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    private void headShake(Node node) {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(node.translateXProperty(), 0)),
            new KeyFrame(Duration.millis(30), new KeyValue(node.translateXProperty(), uiScale(-7))),
            new KeyFrame(Duration.millis(90), new KeyValue(node.translateXProperty(), uiScale(7))),
            new KeyFrame(Duration.millis(150), new KeyValue(node.translateXProperty(), uiScale(-7))),
            new KeyFrame(Duration.millis(210), new KeyValue(node.translateXProperty(), uiScale(7))),
            new KeyFrame(Duration.millis(270), new KeyValue(node.translateXProperty(), uiScale(-7))),
            new KeyFrame(Duration.millis(300), new KeyValue(node.translateXProperty(), 0))
        );
        timeline.play();
    }

    private Button createStyledButton(String label, int type)
    { // type 0: long, 1: short, 2: square
        Image def = type == 0 ? longBtnDefault : (type == 1 ? shortBtnDefault : squareBtnDefault);
        Image press = type == 0 ? longBtnPressed : (type == 1 ? shortBtnPressed : squareBtnPressed);

        Button btn = new Button();
        
        ImageView btnView = new ImageView(def);
        btnView.setSmooth(false); 

        Text btnText = new Text(label);
        btnText.setFont(customFont);
        btnText.setFill(Color.WHITE);
        btnText.setTranslateY(uiScale(-4)); 
        btnText.setScaleX(.8);

        StackPane btnContent = new StackPane(btnView, btnText);
        btn.setGraphic(btnContent);
        btn.setBackground(Background.EMPTY);
        btn.setPadding(Insets.EMPTY);

        // hover effect
        btn.setOnMouseEntered(e -> {
        //    btn.setRotate((6*Math.pow(Math.random()-0.5,2) + .8) * (Math.random()<.5 ? 1 : -1));
            btn.setScaleX(btn.getScaleX() * 1.05);
            btn.setScaleY(btn.getScaleY() * 1.05);
            btnText.setFill(Color.rgb(246, 244, 255));
            btnView.setEffect(new javafx.scene.effect.ColorAdjust(0.05, .2, 0.05, 0));
        });
        
        btn.setOnMouseExited(e -> {
        //    btn.setRotate(0);
            btn.setScaleX(btn.getScaleX() / 1.05);
            btn.setScaleY(btn.getScaleY() / 1.05);
            btnText.setFill(Color.WHITE);
            btnView.setEffect(null);
        });

        // press effect
        btn.setOnMousePressed(e -> {
            btnView.setImage(press);
            btnText.setTranslateY(uiScale(2.5)); 
        });
        
        btn.setOnMouseReleased(e -> {
            btnView.setImage(def);
            btnText.setTranslateY(uiScale(-4)); 
        });
        
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

    private TranslateTransition slideInTransition(Node box, double toY, double duration, double delay){
        TranslateTransition slideTrans = new TranslateTransition(Duration.seconds(duration), box);
        slideTrans.setInterpolator(Interpolator.EASE_OUT);
        slideTrans.setToY(toY);
        slideTrans.setDelay(Duration.seconds(delay));
        slideTrans.play();
        return slideTrans;
    }

    private void changeMenu(StackPane menu) {     
        mainPane.setMouseTransparent(true);
        for (javafx.scene.Node n : mainPane.getChildren()){
            if (n.isVisible()){
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(.5),n);
                fadeOut.setToValue(0);
                fadeOut.setInterpolator(Interpolator.EASE_OUT);
                fadeOut.setOnFinished(e -> {
                    n.setVisible(false);
                });
                fadeOut.play();
            }
        }
        menu.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(.5),menu);
        fadeIn.setInterpolator(Interpolator.EASE_IN);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setDelay(Duration.seconds(.5));
        fadeIn.setOnFinished(e -> mainPane.setMouseTransparent(false));
        fadeIn.play();
    }

    public static double uiScale(double s) {
        return Math.min(WIDTH/1280.0, HEIGHT/720.0) * UI_SCALE * s;
    }

    public static Image loadImage(String name, double height){
        return new Image(
            App.class.getResourceAsStream(name), 0,
            height, true, false
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}