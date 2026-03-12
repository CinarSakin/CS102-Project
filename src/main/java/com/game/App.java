package com.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
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

    private Image btnDefault, btnPressed, bgImageRaw, bgTexture;
    private Font customFont, titleFont;
    private Background bgFill = new Background(new BackgroundFill(Color.rgb(18, 14, 37), null, null));

    @Override
    public void start(Stage primaryStage) {
        try {

            // loading assets
            btnDefault = new Image(getClass().getResourceAsStream("/sprites/ui/buttonDefault.png"), 192 * UI_SCALE, 0, true, false);
            btnPressed = new Image(getClass().getResourceAsStream("/sprites/ui/buttonPressed.png"), 192 * UI_SCALE, 0, true, false);   
            bgImageRaw = new Image(getClass().getResourceAsStream("/sprites/ui/mainMenuBackground.png"), 0, HEIGHT, true, false);
            bgTexture = new Image(getClass().getResourceAsStream("/sprites/ui/backgroundTexture.png"), 0, HEIGHT, true, false);
            customFont = Font.loadFont(getClass().getResourceAsStream("/ByteBounce.ttf"), 36 * UI_SCALE);
            titleFont =  Font.loadFont(getClass().getResourceAsStream("/ByteBounce.ttf"), 80 * UI_SCALE);

            // main menu title
            Text titleText = new Text("DUNGEONFALL");
            titleText.setFont(titleFont);
            titleText.setFill(Color.WHITE);

            javafx.scene.effect.DropShadow outerStroke = new javafx.scene.effect.DropShadow();
            outerStroke.setColor(Color.rgb(124, 14, 250));
            outerStroke.setRadius(5.0);
            outerStroke.setSpread(0.8);
            titleText.setEffect(outerStroke);   

            // main menu title container
            VBox mainMenuTitleContainer = new VBox(titleText);
            mainMenuTitleContainer.setAlignment(Pos.TOP_CENTER);
            mainMenuTitleContainer.setPadding(new Insets(85 * UI_SCALE, 0, 0, 0));

            // main menu buttons
            Button playBtn = createStyledButton("PLAY");
            Button settingsBtn = createStyledButton("SETTINGS");
            Button exitBtn = createStyledButton("EXIT");
            Button leaderboardBtn = createStyledButton("LEADERBOARD");
            
            VBox mainMenuButtonContainer = new VBox(15 * UI_SCALE, playBtn, settingsBtn, leaderboardBtn, exitBtn);
            mainMenuButtonContainer.setAlignment(Pos.CENTER);

            mainMenu = new StackPane(new ImageView(bgImageRaw), mainMenuTitleContainer, mainMenuButtonContainer );
            mainMenu.setBackground(bgFill);

            mainPane = new StackPane(mainMenu);

            // settings menu
            Button resetSettingsBtn = createStyledButton("RESET SETTINGS");
            Button exitSettingsBtn = createStyledButton("CLOSE");
            Label settingsTitle = new Label("SETTINGS");
            settingsTitle.setFont(customFont);
            settingsTitle.setAlignment(Pos.TOP_CENTER);

            VBox settingsMenuContainer = new VBox(15*UI_SCALE, settingsTitle, resetSettingsBtn, exitSettingsBtn);
            settingsMenuContainer.setAlignment(Pos.CENTER);
            
            Rectangle settingsBG = new Rectangle(WIDTH, HEIGHT);
            settingsBG.setFill(new ImagePattern(bgTexture, 0, 0, bgTexture.getWidth(), HEIGHT, false));
            settingsMenu = new StackPane(settingsBG, settingsMenuContainer);

            // gamemode menu
            Button standardBtn = createStyledButton("STANDARD");
            Button infiniteBtn = createStyledButton("INFINITE");
            Button exitGamemodeBtn = createStyledButton("GO BACK");

            VBox gamemodeButtonContainer = new VBox(30 * UI_SCALE, standardBtn, infiniteBtn, exitGamemodeBtn);
            gamemodeButtonContainer.setAlignment(Pos.CENTER);

            gamemodeMenu = new StackPane(new ImageView(bgImageRaw), gamemodeButtonContainer);
            gamemodeMenu.setBackground(bgFill);

            // save file menu
            Button exitSavesBtn = createStyledButton("GO BACK");
            exitSavesBtn.setAlignment(Pos.BOTTOM_CENTER);

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
            playVBoxFade(mainMenuTitleContainer, 0);
            playVBoxFade(mainMenuButtonContainer, 1.5);
            playVBoxSlide(mainMenuButtonContainer, 1.2);

            primaryStage.setTitle("Dungeonfall");
            primaryStage.setScene(new Scene(mainPane, WIDTH, HEIGHT));
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Button createStyledButton(String label) {
        Button btn = new Button();
        
        ImageView btnView = new ImageView(btnDefault);
        btnView.setSmooth(false); 

        Text btnText = new Text(label);
        btnText.setFont(customFont);
        btnText.setFill(Color.WHITE);
        btnText.setTranslateY(-3 * UI_SCALE); 

        StackPane btnContent = new StackPane(btnView, btnText);
        btn.setGraphic(btnContent);
        btn.setBackground(Background.EMPTY);
        btn.setPadding(Insets.EMPTY);

        // hover effect
        btn.setOnMouseEntered(e -> {
            btn.setRotate((6*Math.pow(Math.random()-0.5,2) + .8) * (Math.random()<.5 ? 1 : -1));
            btn.setScaleX(1.07);
            btn.setScaleY(1.07);
        });
        
        btn.setOnMouseExited(e -> {
            btn.setRotate(0);
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });

        // press effect
        btn.setOnMousePressed(e -> {
            btnView.setImage(btnPressed);
            btnText.setTranslateY(3 * UI_SCALE); 
        });
        
        btn.setOnMouseReleased(e -> {
            btnView.setImage(btnDefault);
            btnText.setTranslateY(-3 * UI_SCALE); 
        });
        
        return btn;
    }

    private void playVBoxFade(VBox vbox, double fadeDelayDuration) {
        vbox.setOpacity(0);
        FadeTransition vFade = new FadeTransition(Duration.seconds(2), vbox);
        vFade.setToValue(1);
        vFade.setDelay(Duration.seconds(fadeDelayDuration));
        vFade.play();
    }

    private void playVBoxSlide(VBox box, double fadeDelayDuration) {
        TranslateTransition boxSlide = new TranslateTransition(Duration.seconds(1.8), box);
        box.setOpacity(0);
        boxSlide.setFromY(150 * UI_SCALE);
        boxSlide.setToY(30 * UI_SCALE);
        boxSlide.setDelay(Duration.seconds(fadeDelayDuration));
        boxSlide.play();
    }

    private void changeMenu(StackPane menu) {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(menu);
    }

    public static void main(String[] args) {
        launch(args);
    }
}