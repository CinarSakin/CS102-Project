module com.game {
    requires javafx.controls;
    requires transitive javafx.graphics;
    opens com.game to javafx.graphics, javafx.fxml, javafx.controls;
    exports com.game;
}