package dev.sec3.se3.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public interface View {
    void show();
    void close();

    default Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("styled-button");
        return button;
    }

    default void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    default void showAlert(String message, String s) {
        showAlert(Alert.AlertType.INFORMATION, "DiaryKeeper", message);
    }
}
