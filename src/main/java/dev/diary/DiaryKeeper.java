package dev.diary;
import dev.diary.gui.LoginView;
import dev.diary.service.UserService;
import javafx.application.Application;

import javafx.stage.Stage;

public class DiaryKeeper extends Application {

    private UserService userService;

    @Override
    public void init() {
        userService = new UserService();
    }

    @Override
    public void start(Stage primaryStage) {
        LoginView loginView = new LoginView(primaryStage, userService);
        loginView.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
