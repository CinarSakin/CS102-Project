package com.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    private double UI_SCALE = 1.0; 
    private final double WIDTH = 1280; 
    private final double HEIGHT = 720;

    private Image btnDefault, btnPressed, bgImageRaw;
    private Font customFont, titleFont;

    @Override
    public void start(Stage primaryStage) {
        try {

            // loading assets
            btnDefault = new Image(getClass().getResourceAsStream("/sprites/ui/buttonDefault.png"), 192 * UI_SCALE, 0, true, false);
            btnPressed = new Image(getClass().getResourceAsStream("/sprites/ui/buttonPressed.png"), 192 * UI_SCALE, 0, true, false);   
            bgImageRaw = new Image(getClass().getResourceAsStream("/sprites/ui/mainMenuBackground.png"), 0, HEIGHT, true, false);
            customFont = Font.loadFont(getClass().getResourceAsStream("/ByteBounce.ttf"), 36 * UI_SCALE);
            titleFont =  Font.loadFont(getClass().getResourceAsStream("/ByteBounce.ttf"), 80 * UI_SCALE);

            // title
            Text titleText = new Text("DUNGEONFALL");
            titleText.setFont(titleFont);
            titleText.setFill(Color.WHITE);

            javafx.scene.effect.DropShadow outerStroke = new javafx.scene.effect.DropShadow();
            outerStroke.setColor(Color.rgb(124, 14, 250));
            outerStroke.setRadius(5.0);
            outerStroke.setSpread(0.8);
            titleText.setEffect(outerStroke);

            // title container
            VBox titleContainer = new VBox();
            titleContainer.setAlignment(Pos.TOP_CENTER);
            titleContainer.setPadding(new Insets(85 * UI_SCALE, 0, 0, 0));
            titleContainer.getChildren().add(titleText);

            // buttons
            Button playBtn = createStyledButton("PLAY");
            Button settingsBtn = createStyledButton("SETTINGS");
            Button exitBtn = createStyledButton("EXIT");

            // button functions
            exitBtn.setOnAction(e -> Platform.exit());

            // button container
            VBox buttonContainer = new VBox(15 * UI_SCALE);
            buttonContainer.setAlignment(Pos.CENTER);
            buttonContainer.getChildren().addAll(playBtn, settingsBtn, exitBtn);
            buttonContainer.setOpacity(0);

            // entrance animation (title)
            titleContainer.setOpacity(0);
            FadeTransition titleFade = new FadeTransition(Duration.seconds(2), titleContainer);
            titleFade.setToValue(1);
            titleFade.play();

            // entrance animation (buttons)
            FadeTransition btnFade = new FadeTransition(Duration.seconds(2), buttonContainer);
            btnFade.setToValue(1);
            btnFade.setDelay(Duration.seconds(1.5));
            btnFade.play();

            TranslateTransition btnSlide = new TranslateTransition(Duration.seconds(1.8), buttonContainer);
            btnSlide.setFromY(150 * UI_SCALE);
            btnSlide.setToY(30 * UI_SCALE);
            btnSlide.setDelay(Duration.seconds(1.5));
            btnSlide.play();

            // setup
            StackPane root = new StackPane();
            root.setBackground(new Background(new BackgroundFill(Color.rgb(18, 14, 37), null, null)));
            root.getChildren().addAll(new ImageView(bgImageRaw), titleContainer, buttonContainer);

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            primaryStage.setTitle("Dungeonfall");
            primaryStage.setScene(scene);
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
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
        });
        
        btn.setOnMouseExited(e -> {
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

    public static void main(String[] args) {
        launch(args);
    }
}