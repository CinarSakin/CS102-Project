package com.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    private double UI_SCALE = 1.0; 
    private final double WIDTH = 1280; 
    private final double HEIGHT = 720;

    private StackPane mainPane; // used for transitioning between menus
    private StackPane mainMenu;
    private StackPane settingsMenu;
    private StackPane saveMenu;
    private StackPane gamemodeMenu;

    private Image longBtnDefault, longBtnPressed, shortBtnDefault, shortBtnPressed, squareBtnDefault, squareBtnPressed;
    private Image bgImageRaw, bgTexture;
    private Font customFont, titleFont;
    private Background bgFill = new Background(new BackgroundFill(Color.rgb(18, 14, 37), null, null));

    @Override
    public void start(Stage primaryStage) {
        try {

            // loading assets
            longBtnDefault = new Image(getClass().getResourceAsStream("/sprites/ui/longBtnDefault.png"), 0, 48*UI_SCALE, true, false);
            longBtnPressed = new Image(getClass().getResourceAsStream("/sprites/ui/longBtnPressed.png"), 0, 48*UI_SCALE, true, false);   
            shortBtnDefault = new Image(getClass().getResourceAsStream("/sprites/ui/shortBtnDefault.png"), 0, 48*UI_SCALE, true, false);
            shortBtnPressed = new Image(getClass().getResourceAsStream("/sprites/ui/shortBtnPressed.png"), 0, 48*UI_SCALE, true, false);   
            squareBtnDefault = new Image(getClass().getResourceAsStream("/sprites/ui/squareBtnDefault.png"), 0, 48*UI_SCALE, true, false);
            squareBtnPressed = new Image(getClass().getResourceAsStream("/sprites/ui/squareBtnPressed.png"), 0, 48*UI_SCALE, true, false);   
            bgImageRaw = new Image(getClass().getResourceAsStream("/sprites/ui/mainMenuBackground.png"), 0, HEIGHT, true, false);
            bgTexture = new Image(getClass().getResourceAsStream("/sprites/ui/backgroundTexture.png"), 0, HEIGHT, true, false);
            customFont = Font.loadFont(getClass().getResourceAsStream("/ByteBounce.ttf"), 40 * UI_SCALE);
            titleFont =  Font.loadFont(getClass().getResourceAsStream("/ByteBounce.ttf"), 84 * UI_SCALE);

            // main menu title
            Text titleText = new Text("DUNGEONFALL");
            titleText.setFont(titleFont);
            titleText.setFill(Color.WHITE);
            titleText.setScaleX(.9);

            javafx.scene.effect.DropShadow outerStroke = new javafx.scene.effect.DropShadow();
            outerStroke.setColor(Color.rgb(100, 14, 250));
            outerStroke.setRadius(12.0);
            outerStroke.setSpread(0.5);
            titleText.setEffect(outerStroke);   

            // main menu title container
            VBox mainMenuTitleContainer = new VBox(titleText);
            mainMenuTitleContainer.setAlignment(Pos.TOP_CENTER);
            mainMenuTitleContainer.setPadding(new Insets(85 * UI_SCALE, 0, 0, 0));

            // main menu buttons
            Button playBtn = createStyledButton("PLAY", 0);
            Button settingsBtn = createStyledButton("SETTINGS", 0);
            Button exitBtn = createStyledButton("EXIT", 0);
            Button leaderboardBtn = createStyledButton("LEADERBOARD", 0);
            
            VBox mainMenuButtonContainer = new VBox(15 * UI_SCALE, playBtn, settingsBtn, leaderboardBtn, exitBtn);
            mainMenuButtonContainer.setAlignment(Pos.CENTER);

            ImageView bgImage = new ImageView(bgImageRaw);
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

            VBox settingsMenuContainer = new VBox(15*UI_SCALE, settingsTitle, resetSettingsBtn, exitSettingsBtn);
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

            VBox gamemodeButtonContainer = new VBox(15 * UI_SCALE, standardBtn, infiniteBtn);
            gamemodeButtonContainer.setAlignment(Pos.CENTER);

            StackPane.setAlignment(exitGamemodeBtn, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(exitGamemodeBtn, new Insets(0, 20 * UI_SCALE, 25 * UI_SCALE, 0));

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
            exitBtn.setOnAction(e -> Platform.exit());

            exitSettingsBtn.setOnAction(e -> changeMenu(mainMenu));

            standardBtn.setOnAction(e -> changeMenu(saveMenu));
        //    infiniteBtn.setOnAction(e -> ); // will pass to the game directly 
            exitGamemodeBtn.setOnAction(e -> changeMenu(mainMenu));

            exitSavesBtn.setOnAction(e -> changeMenu(gamemodeMenu));
            
            // setup
            slideInTransition(mainMenuButtonContainer, 100, 30, 1, 1.5);
            fadeInTransition(mainMenuButtonContainer, 1, 1.5);
            fadeInTransition(mainMenuTitleContainer, 1.5, 1.5);
            fadeInTransition(bgImage, 2, .2);

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
            e.printStackTrace();
        }
    }

    private Button createStyledButton(String label, int type) { // type 0: long, 1: short, 2: square

        Image def = type == 0 ? longBtnDefault : (type == 1 ? shortBtnDefault : squareBtnDefault);
        Image press = type == 0 ? longBtnPressed : (type == 1 ? shortBtnPressed : squareBtnPressed);

        Button btn = new Button();
        
        ImageView btnView = new ImageView(def);
        btnView.setSmooth(false); 

        Text btnText = new Text(label);
        btnText.setFont(customFont);
        btnText.setFill(Color.WHITE);
        btnText.setTranslateY(-4 * UI_SCALE); 
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
        //    btn.setEffect(new javafx.scene.effect.ColorAdjust(0, 0, 0.2, 0));
            btnText.setFill(Color.rgb(246, 244, 255));
            btnView.setEffect(new javafx.scene.effect.ColorAdjust(0.05, .2, 0.05, 0));
        });
        
        btn.setOnMouseExited(e -> {
        //    btn.setRotate(0);
            btn.setScaleX(btn.getScaleX() / 1.05);
            btn.setScaleY(btn.getScaleY() / 1.05);
        //    btn.setEffect(null);
            btnText.setFill(Color.WHITE);
            btnView.setEffect(null);
        });

        // press effect
        btn.setOnMousePressed(e -> {
            btnView.setImage(press);
            btnText.setTranslateY(2.5 * UI_SCALE); 
        });
        
        btn.setOnMouseReleased(e -> {
            btnView.setImage(def);
            btnText.setTranslateY(-4 * UI_SCALE); 
        });
        
        return btn;
    }

    private void fadeInTransition(Node node, double duration, double delay) {
        node.setOpacity(0);
        FadeTransition vFade = new FadeTransition(Duration.seconds(duration), node);
        vFade.setToValue(1);
        vFade.setDelay(Duration.seconds(delay));
        vFade.play();
    }

    private void slideInTransition(Node box, double fromY, double toY, double duration, double delay){
        TranslateTransition slideTrans = new TranslateTransition(Duration.seconds(duration), box);
        slideTrans.setInterpolator(Interpolator.EASE_OUT);
        slideTrans.setFromY(fromY);
        slideTrans.setToY(toY);
        slideTrans.setDelay(Duration.seconds(delay));
        slideTrans.play();
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

    public static void main(String[] args) {
        launch(args);
    }
}