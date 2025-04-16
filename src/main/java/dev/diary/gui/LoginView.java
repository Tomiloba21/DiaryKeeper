package dev.sec3.se3.gui;

import dev.sec3.se3.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginView implements View {
    private final Stage stage;
    private final UserService userService;
    private Scene scene;

    public LoginView(Stage stage, UserService userService) {
        this.stage = stage;
        this.userService = userService;
        createUI();
    }

    private void createUI() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setStyle("-fx-background-color: #f5f5f5;");

        // Header
        Text dkeeper = new Text("Welcome to DiaryKeeper");
        dkeeper.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        dkeeper.setFill(Color.DARKSLATEBLUE);
        dkeeper.setEffect(new DropShadow(5, Color.LIGHTGRAY));

        // Input fields
        TextField usernameField = createStyledTextField("Username");
        PasswordField passwordField = createStyledPasswordField("Password");

        // Buttons
        Button loginButton = createStyledButton("Login");
        Button registerButton = createStyledButton("Register");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loginButton, registerButton);

        // Error message
        Text errorMessage = new Text();
        errorMessage.setFill(Color.CRIMSON);

        // Layout
        gridPane.add(dkeeper, 0, 0, 2, 1);
        GridPane.setHalignment(dkeeper, javafx.geometry.HPos.CENTER);

        gridPane.add(new Label("Username:"), 0, 1);
        gridPane.add(usernameField, 1, 1);

        gridPane.add(new Label("Password:"), 0, 2);
        gridPane.add(passwordField, 1, 2);

        gridPane.add(buttonBox, 0, 3, 2, 1);
        gridPane.add(errorMessage, 0, 4, 2, 1);
        GridPane.setHalignment(errorMessage, javafx.geometry.HPos.CENTER);

        // Event handlers
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText(), errorMessage));
        registerButton.setOnAction(e -> showRegisterView());

        scene = new Scene(gridPane, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }

    private void handleLogin(String username, String password, Text errorMessage) {
        try {
            userService.authenticateUser(username, password)
                    .ifPresentOrElse(
                            user -> {
                                DiaryView diaryView = new DiaryView(new Stage(), user,userService);
                                diaryView.show();
                                close();
                            },
                            () -> {
                                errorMessage.setText("✘ Invalid username or password");
                                shake(errorMessage);
                            }
                    );
        } catch (Exception ex) {
            errorMessage.setText("✘ " + ex.getMessage());
            shake(errorMessage);
        }
    }

    private void showRegisterView() {
        RegisterView registerView = new RegisterView(new Stage(), userService);
        registerView.show();
        close();
    }

    // UI Helper methods
    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("styled-text-field");
        return field;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.getStyleClass().add("styled-text-field");
        return field;
    }

    public Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("styled-button");
        return button;
    }

    private void shake(Text text) {
        javafx.animation.TranslateTransition shake =
                new javafx.animation.TranslateTransition(javafx.util.Duration.millis(50), text);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    @Override
    public void show() {
        stage.setTitle("DiaryKeeper Portal");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void close() {
        stage.close();
    }
}