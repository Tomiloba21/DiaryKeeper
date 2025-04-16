package dev.sec3.se3.gui;

import dev.sec3.se3.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RegisterView implements View {
    private final Stage stage;
    private final UserService userService;
    private Scene scene;

    public RegisterView(Stage stage, UserService userService) {
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

        TextField usernameField = createStyledTextField("Username");
        TextField emailField = createStyledTextField("Email");
        PasswordField passwordField = createStyledPasswordField("Password");
        PasswordField confirmPasswordField = createStyledPasswordField("Confirm Password");

        Button registerButton = createStyledButton("Register");
        Button backButton = createStyledButton("Back to Login");

        Text errorMessage = new Text();
        errorMessage.setFill(javafx.scene.paint.Color.CRIMSON);

        gridPane.add(new Label("Username:"), 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(new Label("Email:"), 0, 1);
        gridPane.add(emailField, 1, 1);
        gridPane.add(new Label("Password:"), 0, 2);
        gridPane.add(passwordField, 1, 2);
        gridPane.add(new Label("Confirm Password:"), 0, 3);
        gridPane.add(confirmPasswordField, 1, 3);
        gridPane.add(registerButton, 0, 4, 2, 1);
        gridPane.add(backButton, 0, 5, 2, 1);
        gridPane.add(errorMessage, 0, 6, 2, 1);

        registerButton.setOnAction(e -> handleRegistration(
                usernameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText(),
                errorMessage
        ));

        backButton.setOnAction(e -> {
            LoginView loginView = new LoginView(new Stage(), userService);
            loginView.show();
            close();
        });

        scene = new Scene(gridPane, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }

    private TextField createStyledTextField(String username) {
        TextField textField = new TextField();
        textField.setPromptText(username);
        applyCommonStyles(textField);
        return textField;

    };

    public static PasswordField createStyledPasswordField(String promptText) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        applyCommonStyles(passwordField);
        return passwordField;
    }

    private static void applyCommonStyles(Region inputField) {
        inputField.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-padding: 10px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-background-color: #ffffff;"
        );

        // Set the preferred size
        inputField.setPrefWidth(250);
    }

    private void handleRegistration(String username, String email, String password,
                                    String confirmPassword, Text errorMessage) {
        if (!password.equals(confirmPassword)) {
            errorMessage.setText("✘ Passwords do not match");
            return;
        }

        try {
            userService.registerUser(username, password, email);
            showAlert("Registration Successful", "Please login with your new account");
            LoginView loginView = new LoginView(new Stage(), userService);
            loginView.show();
            close();
        } catch (Exception ex) {
            errorMessage.setText("✘ " + ex.getMessage());
        }
    }

    // ... (same helper methods as LoginView)

    @Override
    public void show() {
        stage.setTitle("Register - DiaryKeeper");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void close() {
        stage.close();
    }
}
