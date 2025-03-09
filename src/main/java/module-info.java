module io.github.gregorybatten {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.databind;

    opens io.github.gregorybatten.controller to javafx.fxml;
    exports io.github.gregorybatten;
}
