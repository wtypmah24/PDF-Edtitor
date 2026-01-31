package org.example.revopdf.controller;

import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.example.revopdf.model.*;
import org.example.revopdf.render.adapter.PdfOverlayRendererAdapter;
import org.example.revopdf.render.adapter.PdfRendererAdapter;
import org.example.revopdf.service.PdfDocumentService;
import org.example.revopdf.service.PdfBoxReader;

public class PdfEditorController {

  @FXML private ImageView pdfImageView;
  @FXML private Canvas overlayCanvas;
  private ToolMode activeTool = ToolMode.NONE;

  @FXML
  private void onDrawToolSelected() {
    activeTool = ToolMode.DRAW;
    updateCursor();
  }

  @FXML
  private void onTextToolSelected() {
    activeTool = ToolMode.TEXT;
    updateCursor();
  }

  private final PdfBoxReader pdfBoxReader = new PdfBoxReader();
  private final PdfRendererAdapter pdfRenderer = new PdfRendererAdapter(pdfBoxReader);
  private final PdfDocumentService pdfDocumentService =
      new PdfDocumentService(pdfBoxReader, pdfRenderer);

  private final PdfOverlayRendererAdapter overlayRenderer = new PdfOverlayRendererAdapter();

  private PdfDocumentState documentState;
  private PdfDrawElement currentDrawElement;
  private double zoom = 1.0;

  @FXML
  public void initialize() {
    pdfImageView
        .boundsInParentProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              overlayCanvas.setWidth(newVal.getWidth());
              overlayCanvas.setHeight(newVal.getHeight());
              redrawOverlay();
            });

    setupCanvasHandlers();
  }

  // ================= PDF OPEN =================

  @FXML
  protected void onOpenPdfClick() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose PDF file");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

    File file = fileChooser.showOpenDialog(pdfImageView.getScene().getWindow());
    if (file == null) return;

    try {
      pdfDocumentService.openDocument(file);
      Image pageImage = pdfDocumentService.renderPage(0, 150);
      pdfImageView.setImage(pageImage);

      documentState = new PdfDocumentState(file);
      redrawOverlay();

    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Couldn't open PDF: " + e.getMessage(), ButtonType.OK)
          .showAndWait();
    }
  }

  // ================= RENDER OVERLAY =================

  private void redrawOverlay() {
    if (documentState == null) return;

    GraphicsContext gc = overlayCanvas.getGraphicsContext2D();
    overlayRenderer.renderPage(documentState, documentState.getCurrentPage(), gc, zoom);
  }

  private void setupCanvasHandlers() {

    overlayCanvas.setOnMousePressed(
        e -> {
          if (documentState == null) return;

          switch (activeTool) {
            case DRAW -> startDrawing(e);
            case TEXT -> placeText(e);
          }
        });

    overlayCanvas.setOnMouseDragged(
        e -> {
          if (activeTool == ToolMode.DRAW && currentDrawElement != null) {
            currentDrawElement.addPoint(e.getX() / zoom, e.getY() / zoom);
            redrawOverlay();
          }
        });

    overlayCanvas.setOnMouseReleased(e -> currentDrawElement = null);
  }

  private void startDrawing(MouseEvent e) {
    currentDrawElement = new PdfDrawElement(documentState.getCurrentPage());
    currentDrawElement.addPoint(e.getX() / zoom, e.getY() / zoom);
    documentState.addElement(currentDrawElement);
  }

  private void placeText(MouseEvent e) {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setHeaderText("Enter text");

    dialog
        .showAndWait()
        .ifPresent(
            text -> {
              PdfTextElement textElement =
                  new PdfTextElement(
                      documentState.getCurrentPage(), text, e.getX() / zoom, e.getY() / zoom);
              documentState.addElement(textElement);
              redrawOverlay();
            });
  }

  private void updateCursor() {
    switch (activeTool) {
      case DRAW -> overlayCanvas.setCursor(Cursor.CROSSHAIR);
      case TEXT -> overlayCanvas.setCursor(Cursor.TEXT);
      default -> overlayCanvas.setCursor(Cursor.DEFAULT);
    }
  }
}
