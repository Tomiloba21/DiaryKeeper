module dev.sec3.se3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens dev.sec3.se3 to javafx.fxml;
    exports dev.sec3.se3;
}