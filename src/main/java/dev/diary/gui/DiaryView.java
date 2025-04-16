package dev.sec3.se3.gui;

import dev.sec3.se3.model.DiaryEntry;
import dev.sec3.se3.model.EntryMood;
import dev.sec3.se3.model.User;
import dev.sec3.se3.service.DiaryService;
import dev.sec3.se3.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DiaryView implements View {
    private static final String EXPORT_PATH = "memories/";
    private ListView<DiaryEntry> entriesList;
    public TextField searchField;
    private final Stage stage;
    private final User user;
    private final UserService userService;
    private final DiaryService diaryService;
    private Scene scene;
    private TextArea diaryContent;
    private ComboBox<EntryMood> moodSelector;

    public DiaryView(Stage stage, User user, UserService userService) {
        this.stage = stage;
        this.user = user;
        this.userService = userService;
        this.diaryService = new DiaryService();
        this.entriesList = new ListView<>();

        createUI();
    }

    private void createUI() {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(15));
        borderPane.setStyle("-fx-background-color: #f5f5f5;");

        // Top Section
        VBox topBox = createTopSection();
        borderPane.setTop(topBox);

        // Center Section
        VBox centerBox = createCenterSection();
        borderPane.setCenter(centerBox);

        // Bottom Section
        HBox bottomBox = createBottomSection();
        borderPane.setBottom(bottomBox);

        // Left Section (Diary List)
        VBox leftBox = createLeftSection();
        borderPane.setLeft(leftBox);

        scene = new Scene(borderPane, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }

    private VBox createTopSection() {
        Text headerText = new Text("Diary Entry - " + LocalDate.now());
        headerText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        headerText.setFill(javafx.scene.paint.Color.NAVY);

        Label welcomeLabel = new Label("Welcome, " + user.getUsername());
        welcomeLabel.setStyle("-fx-font-size: 14px;");

        moodSelector = new ComboBox<>();
        moodSelector.getItems().addAll(EntryMood.values());
        moodSelector.setPromptText("Select Mood");

        moodSelector.setOnAction(event -> {
            try {
                EntryMood selectedMood = moodSelector.getValue();
                if (selectedMood != null) {
                    List<DiaryEntry> moodEntries = diaryService.getEntriesByMood(user.getId(), selectedMood);
                    entriesList.getItems().clear();
                    entriesList.getItems().addAll(moodEntries);
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Filter Error", ex.getMessage());
            }
        });


        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);
        topBox.getChildren().addAll(headerText, welcomeLabel, moodSelector);
        return topBox;
    }

    private VBox createCenterSection() {
        diaryContent = new TextArea();
        diaryContent.setWrapText(true);
        diaryContent.setPromptText("Write your thoughts here...");
        diaryContent.setStyle("-fx-font-size: 14px;");

        VBox centerBox = new VBox(10);
        centerBox.setPadding(new Insets(10));
        centerBox.getChildren().add(diaryContent);
        return centerBox;
    }

    private HBox createBottomSection() {
        Button saveButton = createStyledButton("Save Entry");
        Button clearButton = createStyledButton("Clear");
        Button logoutButton = createStyledButton("Logout");

        saveButton.setOnAction(e -> handleSave());
        clearButton.setOnAction(e -> diaryContent.clear());
        logoutButton.setOnAction(e -> handleLogout());

        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        bottomBox.getChildren().addAll(saveButton, clearButton, logoutButton);
        return bottomBox;
    }

    private VBox createLeftSection() {
        entriesList = new ListView<>();
        entriesList.setPrefWidth(200);
        entriesList.setCellFactory(param -> new DiaryEntryCell());

        // Add selection listener for entry details
        entriesList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        enableEntryControls(true);
                    } else {
                        enableEntryControls(false);
                    }
                });

        searchField = new TextField();
        searchField.setPromptText("Search entries...");

        // Entry control buttons
        Button viewButton = new Button("View Entry");
        viewButton.setDisable(true);
        viewButton.setOnAction(e -> viewSelectedEntry());

        Button exportButton = new Button("Export");
        exportButton.setDisable(true);
        exportButton.setOnAction(e -> exportSelectedEntry());

        Button deleteButton = new Button("Delete");
        deleteButton.setDisable(true);
        deleteButton.setOnAction(e -> deleteSelectedEntry());

        // Organize buttons in a horizontal box
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(viewButton, exportButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox leftBox = new VBox(10);
        leftBox.setPadding(new Insets(10));
        leftBox.getChildren().addAll(searchField, entriesList, buttonBox);
        leftBox.getStyleClass().add("left-box");

        return leftBox;
    }

    private void enableEntryControls(boolean enable) {
        // Find and enable/disable all entry control buttons
        VBox leftBox = (VBox) scene.lookup(".left-box");
        HBox buttonBox = (HBox) leftBox.getChildren().get(2);
        buttonBox.getChildren().forEach(node -> node.setDisable(!enable));
    }
    private void viewSelectedEntry() {
        DiaryEntry selectedEntry = entriesList.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            Stage viewStage = new Stage();
            viewStage.initModality(Modality.APPLICATION_MODAL);
            viewStage.setTitle("View Entry - " + selectedEntry.getTitle());

            VBox content = new VBox(10);
            content.setPadding(new Insets(15));

            Text titleText = new Text(selectedEntry.getTitle());
            titleText.setFont(Font.font("Arial", FontWeight.BOLD, 16));

            Text dateText = new Text("Created: " +
                    selectedEntry.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
            Text moodText = new Text("Mood: " + selectedEntry.getMood().toString());

            TextArea contentArea = new TextArea(selectedEntry.getContent());
            contentArea.setWrapText(true);
            contentArea.setEditable(false);
            contentArea.setPrefRowCount(10);

            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> viewStage.close());

            content.getChildren().addAll(titleText, dateText, moodText, contentArea, closeButton);
            content.setAlignment(Pos.CENTER);

            Scene scene = new Scene(content, 400, 500);
            viewStage.setScene(scene);
            viewStage.showAndWait();
        }
    }
    private void exportSelectedEntry() {
        DiaryEntry selectedEntry = entriesList.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            try {
                // Create memories directory if it doesn't exist
                File directory = new File(EXPORT_PATH);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Create filename based on entry date and title
                String filename = String.format("%s_%s.txt",
                        selectedEntry.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                        selectedEntry.getTitle().replaceAll("[^a-zA-Z0-9]", "_"));

                Path filePath = Paths.get(EXPORT_PATH, filename);

                // Write entry content to file
                String content = String.format("""
                    Title: %s
                    Date: %s
                    Mood: %s
                    
                    %s
                    """,
                        selectedEntry.getTitle(),
                        selectedEntry.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")),
                        selectedEntry.getMood(),
                        selectedEntry.getContent());

                Files.write(filePath, content.getBytes());

                showAlert(Alert.AlertType.INFORMATION, "Export Successful",
                        "Entry exported to: " + filePath.toString());

            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Export Error",
                        "Failed to export entry: " + ex.getMessage());
            }
        }
    }

    private void searchEntries(String searchTerm) {
        try {
            entriesList.getItems().clear();
            List<DiaryEntry> searchResults = diaryService.searchEntries(user.getId(), searchTerm);
            entriesList.getItems().addAll(searchResults);
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Search Error", ex.getMessage());
        }
    }

    private void handleSave() {
        if (moodSelector.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Select Mood",
                    "Please select your mood for this entry.");
            return;
        }

        try {
            DiaryEntry entry = new DiaryEntry(
                    "Entry - " + LocalDate.now(),
                    diaryContent.getText(),
                    user.getId(),
                    moodSelector.getValue()
            );
            diaryService.saveEntry(entry);

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Entry saved successfully!");
            diaryContent.clear();
            moodSelector.setValue(null);

            // Refresh the entries list after saving
            refreshEntriesList();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to save entry: " + ex.getMessage());
        }
    }
    private void deleteSelectedEntry() {
        DiaryEntry selectedEntry = entriesList.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this entry?",
                    ButtonType.YES, ButtonType.NO);
            confirmDialog.setTitle("Confirm Delete");

            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        diaryService.deleteEntry(selectedEntry.getId());
                        refreshEntriesList();
                        showAlert(Alert.AlertType.INFORMATION, "Success",
                                "Entry deleted successfully");
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, "Delete Error",
                                "Failed to delete entry: " + ex.getMessage());
                    }
                }
            });
        }
    }

    private void refreshEntriesList() {
        try {
            entriesList.getItems().clear();
            List<DiaryEntry> userEntries = diaryService.getUserEntries(user.getId());
            entriesList.getItems().addAll(userEntries);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to refresh entries: " + ex.getMessage());
        }
    }

    private void handleLogout() {
        LoginView loginView = new LoginView(new Stage(), userService);
        loginView.show();
        close();
    }

    @Override
    public void show() {
        stage.setTitle("DiaryKeeper - " + user.getUsername());
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void close() {
        stage.close();
    }

    private static class DiaryEntryCell extends ListCell<DiaryEntry> {
        @Override
        protected void updateItem(DiaryEntry entry, boolean empty) {
            super.updateItem(entry, empty);

            if (empty || entry == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox container = new VBox(5);

                Label titleLabel = new Label(entry.getTitle());
                titleLabel.setStyle("-fx-font-weight: bold;");

                Label dateLabel = new Label(entry.getCreatedAt().format(
                        DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
                dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

                Label moodLabel = new Label("Mood: " + entry.getMood().toString());
                moodLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");

                container.getChildren().addAll(titleLabel, dateLabel, moodLabel);

                // Add hover effect
                container.setOnMouseEntered(e ->
                        container.setStyle("-fx-background-color: #f0f0f0;"));
                container.setOnMouseExited(e ->
                        container.setStyle("-fx-background-color: transparent;"));

                setGraphic(container);
            }
        }
    }
}