module org.example.revopdf {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    opens org.example.revopdf to javafx.fxml;
    exports org.example.revopdf;
    exports org.example.revopdf.controller;
    opens org.example.revopdf.controller to javafx.fxml;

    requires java.desktop;

    requires org.apache.pdfbox;
    requires javafx.graphics;
    requires org.example.revopdf;
}