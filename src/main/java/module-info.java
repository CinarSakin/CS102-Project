module com.game {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    requires com.almasb.fxgl.all;

    opens com.game to javafx.graphics, javafx.fxgl.all, javafx.controls;
    exports com.game;
}