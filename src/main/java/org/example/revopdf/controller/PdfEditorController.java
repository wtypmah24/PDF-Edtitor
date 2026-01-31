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
import org.example.revopdf.service.PdfBoxReader;
import org.example.revopdf.service.PdfDocumentService;

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
  private void onNoneToolSelected() {
    activeTool = ToolMode.NONE;
    updateCursor();
  }

  @FXML
  private void onDragToolSelected() {
    activeTool = ToolMode.DRAG;
    updateCursor();
  }

  @FXML
  private void onTextToolSelected() {
    activeTool = ToolMode.TEXT;
    updateCursor();
  }

  @FXML
  private void onZoomIn() {
    if (documentState == null) return;

    try {
      documentState.zoomIn();
      rerenderPdfPage();
      redrawOverlay();
    } catch (IOException e) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText(e.getMessage());
      alert.setContentText(e.getMessage());
      alert.showAndWait();
    }
  }

  @FXML
  private void onZoomOut() {
    if (documentState == null) return;

    documentState.zoomOut();
    try {
      rerenderPdfPage();
    } catch (IOException e) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText(e.getMessage());
      alert.setContentText(e.getMessage());
      alert.showAndWait();
    }
    redrawOverlay();
  }

  private final PdfBoxReader pdfBoxReader = new PdfBoxReader();
  private final PdfRendererAdapter pdfRenderer = new PdfRendererAdapter(pdfBoxReader);
  private final PdfDocumentService pdfDocumentService =
      new PdfDocumentService(pdfBoxReader, pdfRenderer);

  private final PdfOverlayRendererAdapter overlayRenderer = new PdfOverlayRendererAdapter();

  private PdfDocumentState documentState;
  private PdfDrawElement currentDrawElement;

  private PdfElement selectedElement;
  private double lastMouseX;
  private double lastMouseY;

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

  private void redrawOverlay() {
    if (documentState == null) return;

    GraphicsContext gc = overlayCanvas.getGraphicsContext2D();
    overlayRenderer.renderPage(
        documentState, documentState.getCurrentPage(), gc, documentState.getZoom());
  }

  private void rerenderPdfPage() throws IOException {
    int page = documentState.getCurrentPage();
    double zoom = documentState.getZoom();

    Image pageImage = pdfDocumentService.renderPage(page, 150 * zoom);
    pdfImageView.setImage(pageImage);
  }

  private void setupCanvasHandlers() {

    overlayCanvas.setOnMousePressed(
        e -> {
          if (documentState == null) return;

          double x = e.getX() / documentState.getZoom();
          double y = e.getY() / documentState.getZoom();

          switch (activeTool) {
            case DRAW -> startDrawing(e);
            case TEXT -> placeText(e);
            case DRAG -> startDragging(x, y);
          }
        });

    overlayCanvas.setOnMouseDragged(
        e -> {
          double x = e.getX() / documentState.getZoom();
          double y = e.getY() / documentState.getZoom();

          if (activeTool == ToolMode.DRAW && currentDrawElement != null) {
            currentDrawElement.addPoint(x, y);
            redrawOverlay();
          }

          if (activeTool == ToolMode.DRAG && selectedElement != null) {
            double dx = x - lastMouseX;
            double dy = y - lastMouseY;

            selectedElement.move(dx, dy);

            lastMouseX = x;
            lastMouseY = y;

            redrawOverlay();
          }
        });

    overlayCanvas.setOnMouseReleased(e -> currentDrawElement = null);
  }

  private void startDragging(double x, double y) {
    selectedElement = null;

    for (PdfElement element : documentState.getElementsForPage(documentState.getCurrentPage())) {
      if (element.contains(x, y)) {
        selectedElement = element;
        break;
      }
    }

    lastMouseX = x;
    lastMouseY = y;

    if (selectedElement != null) {
      overlayCanvas.setCursor(Cursor.CLOSED_HAND);
    }
  }

  private void startDrawing(MouseEvent e) {
    currentDrawElement = new PdfDrawElement(documentState.getCurrentPage());
    currentDrawElement.addPoint(
        e.getX() / documentState.getZoom(), e.getY() / documentState.getZoom());
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
                      documentState.getCurrentPage(),
                      text,
                      e.getX() / documentState.getZoom(),
                      e.getY() / documentState.getZoom());
              documentState.addElement(textElement);
              redrawOverlay();
            });
  }

  private void updateCursor() {
    switch (activeTool) {
      case DRAW -> overlayCanvas.setCursor(Cursor.CROSSHAIR);
      case TEXT -> overlayCanvas.setCursor(Cursor.TEXT);
      case DRAG -> overlayCanvas.setCursor(Cursor.OPEN_HAND);
      case NONE -> overlayCanvas.setCursor(Cursor.HAND);
      default -> overlayCanvas.setCursor(Cursor.DEFAULT);
    }
  }
}
