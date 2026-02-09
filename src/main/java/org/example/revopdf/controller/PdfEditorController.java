package org.example.revopdf.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.example.revopdf.manager.PdfEditorInteractionManager;
import org.example.revopdf.model.*;
import org.example.revopdf.service.PdfSaveService;

import java.io.File;
import java.io.IOException;

public class PdfEditorController {

  private final PdfEditorInteractionManager interactionManager = new PdfEditorInteractionManager();

  @FXML private ImageView pdfImageView;
  @FXML private Canvas overlayCanvas;

  @FXML private HBox textToolbar;

  @FXML private Spinner<Integer> fontSizeSpinner;
  @FXML private ColorPicker textColorPicker;
  @FXML private ComboBox<String> fontFamilyCombo;

  @FXML private HBox eraserToolbar;
  @FXML private ComboBox<Integer> eraserSizeCombo;
  @FXML private Label pageLabel;
  @FXML private Button prevPageButton;
  @FXML private Button nextPageButton;

  @FXML
  private void onPrevPage() {
    interactionManager.prevPage();
    updatePageControls();
  }

  @FXML
  private void onNextPage() {
    interactionManager.nextPage();
    updatePageControls();
  }

  @FXML
  protected void onOpenPdfClick() {
    interactionManager.openPdf();
  }

  @FXML
  private void onDrawToolSelected() {
    interactionManager.setTool(ToolMode.DRAW);
  }

  @FXML
  private void onEraseToolSelected() {
    interactionManager.setTool(ToolMode.ERASE);
  }

  @FXML
  private void onNoneToolSelected() {
    interactionManager.setTool(ToolMode.NONE);
  }

  @FXML
  private void onDragToolSelected() {
    interactionManager.setTool(ToolMode.DRAG);
  }

  @FXML
  private void onTextToolSelected() {
    interactionManager.setTool(ToolMode.TEXT);
  }

  @FXML
  private void onZoomIn() {
    interactionManager.zoomIn();
  }

  @FXML
  private void onZoomOut() {
    interactionManager.zoomOut();
  }

  @FXML
  private void onEditSelectedText() {
    if (!(interactionManager.getDocumentState().getSelectedElement()
        instanceof PdfTextElement textElement)) return;

    TextInputDialog dialog = new TextInputDialog(textElement.getText());
    dialog.setHeaderText("Edit text");

    dialog
        .showAndWait()
        .ifPresent(
            newText -> {
              textElement.setText(newText);
              redrawOverlay();
            });
  }

  @FXML
  private void onSavePdfClick() {
    FileChooser fc = new FileChooser();
    fc.setTitle("Save PDF");
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

    File target = fc.showSaveDialog(pdfImageView.getScene().getWindow());
    if (target == null) return;

    try {
      new PdfSaveService()
          .save(
              interactionManager.getDocumentState().getSourceFile(),
              target,
              interactionManager.getDocumentState());

    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Save failed: " + e.getMessage()).showAndWait();
    }
  }

  @FXML
  public void initialize() {

    interactionManager.init(
        overlayCanvas,
        pdfImageView,
        this::redrawOverlay,
        this::updateToolbars,
        this::syncTextToolbarWithSelection,
        this::updatePageControls);

    pdfImageView
        .boundsInParentProperty()
        .addListener(
            (obs, o, n) -> {
              overlayCanvas.setWidth(n.getWidth());
              overlayCanvas.setHeight(n.getHeight());
              redrawOverlay();
            });
    fontSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 200, 14));
    fontSizeSpinner
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (newVal != null) {
                interactionManager.updateSelectedTextFontSize(newVal);
              }
            });

    textColorPicker
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (newVal != null) {
                interactionManager.updateSelectedTextColor(newVal);
              }
            });

    fontFamilyCombo.getItems().addAll(Font.getFamilies());
    fontFamilyCombo.setValue("Arial");
    fontFamilyCombo
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (newVal != null) {
                interactionManager.updateSelectedTextFontFamily(newVal);
              }
            });

    eraserSizeCombo.getItems().addAll(5, 10, 15, 20, 30, 40, 60, 80);
    eraserSizeCombo.setValue(20);

    eraserSizeCombo
        .valueProperty()
        .addListener(
            (obs, o, n) -> {
              if (n != null) {
                interactionManager.setEraserRadius(n);
              }
            });

    updateToolbars();
  }

  private void updateToolbars() {
    var state = interactionManager.getDocumentState();

    boolean textSelected = state != null && state.getSelectedElement() instanceof PdfTextElement;

    boolean eraserActive = interactionManager.getActiveTool() == ToolMode.ERASE;

    textToolbar.setVisible(textSelected);
    textToolbar.setManaged(textSelected);

    eraserToolbar.setVisible(eraserActive);
    eraserToolbar.setManaged(eraserActive);
  }

  private void syncTextToolbarWithSelection() {
    var state = interactionManager.getDocumentState();
    if (state == null) return;

    var el = state.getSelectedElement();
    if (el instanceof PdfTextElement text) {
      fontSizeSpinner.getValueFactory().setValue((int) text.getFontSize());
      textColorPicker.setValue(text.getColor());
    }
  }

  private void updatePageControls() {
    var state = interactionManager.getDocumentState();
    if (state == null) {
      pageLabel.setText("");
      prevPageButton.setDisable(true);
      nextPageButton.setDisable(true);
      return;
    }

    int page = state.getCurrentPage();
    int total = state.getPageCount();

    pageLabel.setText((page + 1) + " / " + total);

    prevPageButton.setDisable(page == 0);
    nextPageButton.setDisable(page == total - 1);
  }

  private void redrawOverlay() {
    interactionManager.redraw();
  }
}
