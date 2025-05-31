module com.example.connect4_minimax {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.connect4_minimax to javafx.fxml;
    exports com.example.connect4_minimax;
}