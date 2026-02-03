package org.example.revopdf.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import org.example.revopdf.manager.PdfEditorInteractionManager;
import org.example.revopdf.model.*;

public class PdfEditorController {

  private final PdfEditorInteractionManager interactionManager = new PdfEditorInteractionManager();

  @FXML private ImageView pdfImageView;
  @FXML private Canvas overlayCanvas;

  @FXML private HBox textToolbar;

  @FXML private Spinner<Integer> fontSizeSpinner;
  @FXML private ColorPicker textColorPicker;
  @FXML private ComboBox<String> fontFamilyCombo;

  @FXML
  protected void onOpenPdfClick() {
    interactionManager.openPdf();
  }

  @FXML
  private void onDrawToolSelected() {
    interactionManager.setTool(ToolMode.DRAW);
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
  public void initialize() {

    interactionManager.init(
        overlayCanvas,
        pdfImageView,
        this::redrawOverlay,
        this::updateToolbars,
        this::syncTextToolbarWithSelection);

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

    updateToolbars();
  }

  private void updateToolbars() {
    boolean textSelected =
        interactionManager.getDocumentState() != null
            && interactionManager.getDocumentState().getSelectedElement() instanceof PdfTextElement;

    textToolbar.setVisible(textSelected);
    textToolbar.setManaged(textSelected);
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

  private void redrawOverlay() {
    interactionManager.redraw();
  }
}
