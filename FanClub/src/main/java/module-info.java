module org.example.finalproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens org.example.finalproject to javafx.fxml;
    exports org.example.finalproject;
}