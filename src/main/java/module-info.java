module dev.diary {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens dev.diary to javafx.fxml;
    exports dev.diary;
    exports dev.diary.dao;
    opens dev.diary.dao to javafx.fxml;
    exports dev.diary.gui;
    opens dev.diary.gui to javafx.fxml;
}