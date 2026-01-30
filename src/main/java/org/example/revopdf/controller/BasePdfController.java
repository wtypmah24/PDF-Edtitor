package org.example.revopdf.controller;

import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.revopdf.model.PdfBoxReader;
import org.example.revopdf.service.BasePdfService;

public class BasePdfController {

  @FXML private ImageView pdfImageView;
  private final BasePdfService basePdfService = new BasePdfService(new PdfBoxReader());

  @FXML
  protected void onOpenPdfClick() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose PDF file");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

    File file = fileChooser.showOpenDialog(pdfImageView.getScene().getWindow());
    if (file == null) return;

    try {
      Image image = basePdfService.renderFirstPage(file);
      pdfImageView.setImage(image);
    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Couldn't open PDF: " + e.getMessage(), ButtonType.OK)
          .showAndWait();
    }
  }
}
