package org.example.revopdf.manager;

import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.example.revopdf.model.*;
import org.example.revopdf.render.adapter.PdfOverlayRendererAdapter;
import org.example.revopdf.render.adapter.PdfRendererAdapter;
import org.example.revopdf.service.PdfDocumentService;
import org.example.revopdf.service.adapter.PdfBoxReader;

import java.io.File;
import java.io.IOException;

public class PdfEditorInteractionManager {
  private final PdfOverlayRendererAdapter overlayRenderer = new PdfOverlayRendererAdapter();
  private final PdfBoxReader pdfBoxReader = new PdfBoxReader();
  private final PdfRendererAdapter pdfRenderer = new PdfRendererAdapter(pdfBoxReader);
  private final PdfDocumentService pdfDocumentService =
      new PdfDocumentService(pdfBoxReader, pdfRenderer);

  private Canvas canvas;
  private ImageView pdfImageView;
  private Runnable redrawCallback;

  private ToolMode activeTool = ToolMode.NONE;
  private PdfDocumentState documentState;

  private PdfDrawElement currentDrawElement;
  private PdfElement draggedElement;
  private double lastMouseX, lastMouseY;

  private Runnable selectionChangedCallback = () -> {};
  private Runnable textSelectionChangedCallback;

  private PdfWhiteoutBrushElement currentEraserStroke;
  private double eraserRadius = 20;

  public void init(
      Canvas canvas,
      ImageView pdfImageView,
      Runnable redrawCallback,
      Runnable selectionChangedCallback,
      Runnable textSelectionChangedCallback) {
    this.canvas = canvas;
    this.pdfImageView = pdfImageView;
    this.redrawCallback = redrawCallback;
    this.selectionChangedCallback = selectionChangedCallback;
    this.textSelectionChangedCallback = textSelectionChangedCallback;
    setupHandlers();
  }

  public void setDocumentState(PdfDocumentState state) {
    this.documentState = state;
  }

  public PdfDocumentState getDocumentState() {
    return documentState;
  }

  public void setTool(ToolMode tool) {
    if (tool == ToolMode.ERASE) {
      currentEraserStroke = new PdfWhiteoutBrushElement(documentState.getCurrentPage());
    }
    this.activeTool = tool;
    updateCursor();
    selectionChangedCallback.run();
  }

  private void setupHandlers() {
    canvas
        .sceneProperty()
        .addListener(
            (obs, oldScene, newScene) -> {
              if (newScene != null) {
                newScene.setOnKeyPressed(
                    event -> {
                      if (documentState == null) return;
                      if (event.getCode() == KeyCode.DELETE
                          || event.getCode() == KeyCode.BACK_SPACE) {
                        PdfElement selected = documentState.getSelectedElement();
                        if (selected != null) {
                          documentState.removeElement(selected);
                          documentState.clearSelection();
                          selectionChangedCallback.run();
                          redrawCallback.run();
                        }
                      }
                    });
              }
            });

    canvas.setOnMousePressed(
        e -> {
          if (documentState == null) return;

          double x = e.getX() / documentState.getZoom();
          double y = e.getY() / documentState.getZoom();

          switch (activeTool) {
            case DRAW -> startDrawing(x, y);
            case TEXT -> placeText(x, y);
            case ERASE -> startErasing(x, y);
            case DRAG -> startDragging(x, y);
            case NONE -> selectElement(x, y);
          }
        });

    canvas.setOnMouseDragged(
        e -> {
          e.consume();

          if (documentState == null) return;

          double x = e.getX() / documentState.getZoom();
          double y = e.getY() / documentState.getZoom();

          if (activeTool == ToolMode.DRAW && currentDrawElement != null) {
            currentDrawElement.addPoint(x, y);
            redrawCallback.run();
          }

          if (activeTool == ToolMode.ERASE && currentEraserStroke != null) {
            currentEraserStroke.addPoint(x, y);
            redrawCallback.run();
          }

          if (activeTool == ToolMode.DRAG && draggedElement != null) {
            draggedElement.move(x - lastMouseX, y - lastMouseY);
            lastMouseX = x;
            lastMouseY = y;
            redrawCallback.run();
          }
        });

    canvas.setOnMouseReleased(
        e -> {
          currentDrawElement = null;
          currentEraserStroke = null;
          draggedElement = null;
          updateCursor();
        });
  }

  private void startDrawing(double x, double y) {
    currentDrawElement = new PdfDrawElement(documentState.getCurrentPage());
    currentDrawElement.addPoint(x, y);
    documentState.addElement(currentDrawElement);
  }

  private void placeText(double x, double y) {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setHeaderText("Enter text");

    dialog
        .showAndWait()
        .ifPresent(
            text -> {
              documentState.addElement(
                  new PdfTextElement(documentState.getCurrentPage(), text, x, y));
              redrawCallback.run();
            });
  }

  private void startDragging(double x, double y) {
    draggedElement = documentState.findElementAt(x, y);
    lastMouseX = x;
    lastMouseY = y;

    if (draggedElement != null) {
      canvas.setCursor(Cursor.CLOSED_HAND);
    }
  }

  private void selectElement(double x, double y) {
    documentState.setSelectedElement(documentState.findElementAt(x, y));
    documentState.setSelectedElement(documentState.findElementAt(x, y));
    redrawCallback.run();
    selectionChangedCallback.run();
    textSelectionChangedCallback.run();
    redrawCallback.run();
  }

  public void redraw() {
    if (documentState == null) return;

    GraphicsContext gc = canvas.getGraphicsContext2D();

    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

    overlayRenderer.renderPage(
        documentState, documentState.getCurrentPage(), gc, documentState.getZoom());

    drawSelection(gc);
  }

  private void drawSelection(GraphicsContext gc) {
    PdfElement selected = documentState.getSelectedElement();
    if (selected == null) return;

    Bounds b = selected.getBounds();

    double zoom = documentState.getZoom();

    gc.setLineDashes(6);
    gc.setStroke(Color.DODGERBLUE);
    gc.setLineWidth(1.5);

    gc.strokeRect(
        b.getMinX() * zoom, b.getMinY() * zoom, b.getWidth() * zoom, b.getHeight() * zoom);

    gc.setLineDashes(0.0);
  }

  public void zoomIn() {
    if (documentState == null) return;

    documentState.zoomIn();
    rerenderPdfPage();
    redrawCallback.run();
  }

  public void zoomOut() {
    if (documentState == null) return;

    documentState.zoomOut();
    rerenderPdfPage();
    redrawCallback.run();
  }

  private void rerenderPdfPage() {
    try {
      int page = documentState.getCurrentPage();
      double zoom = documentState.getZoom();

      Image pageImage = pdfDocumentService.renderPage(page, 150 * zoom);
      pdfImageView.setImage(pageImage);

    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Render error: " + e.getMessage(), ButtonType.OK)
          .showAndWait();
    }
  }

  public void openPdf() {
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
      redrawCallback.run();

    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Couldn't open PDF: " + e.getMessage(), ButtonType.OK)
          .showAndWait();
    }
  }

  public void updateSelectedTextFontSize(double size) {
    if (documentState == null) return;

    PdfElement el = documentState.getSelectedElement();
    if (el instanceof PdfTextElement text) {
      text.setFontSize(size);
      redrawCallback.run();
    }
  }

  public void updateSelectedTextFontFamily(String fontFamily) {
    if (documentState == null) return;

    PdfElement el = documentState.getSelectedElement();
    if (el instanceof PdfTextElement text) {
      text.setFontFamily(fontFamily);
      redrawCallback.run();
    }
  }

  public void updateSelectedTextColor(Color color) {
    if (documentState == null) return;

    PdfElement el = documentState.getSelectedElement();
    if (el instanceof PdfTextElement text) {
      text.setColor(color);
      redrawCallback.run();
    }
  }

  private void startErasing(double x, double y) {
    currentEraserStroke = new PdfWhiteoutBrushElement(documentState.getCurrentPage());
    currentEraserStroke.setRadius(eraserRadius);
    currentEraserStroke.addPoint(x, y);

    documentState.addElement(currentEraserStroke);
  }

  public void setEraserRadius(double radius) {
    this.eraserRadius = radius;
  }

  public ToolMode getActiveTool() {
    return activeTool;
  }

  private void updateCursor() {
    switch (activeTool) {
      case DRAW -> canvas.setCursor(Cursor.CROSSHAIR);
      case TEXT -> canvas.setCursor(Cursor.TEXT);
      case DRAG -> canvas.setCursor(Cursor.OPEN_HAND);
      case NONE -> canvas.setCursor(Cursor.HAND);
      case ERASE -> canvas.setCursor(Cursor.DISAPPEAR);
    }
  }
}
