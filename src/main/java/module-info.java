module com.game {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    requires com.almasb.fxgl.all;

    requires com.google.gson;

    opens com.game to javafx.graphics, javafx.fxgl.all, javafx.controls, com.google.gson;
    exports com.game;
}