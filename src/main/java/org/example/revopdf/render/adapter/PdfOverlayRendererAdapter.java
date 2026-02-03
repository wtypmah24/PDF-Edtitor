package org.example.revopdf.render.adapter;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.revopdf.model.PdfDocumentState;
import org.example.revopdf.model.PdfElement;
import org.example.revopdf.render.PdfOverlayRenderer;

public class PdfOverlayRendererAdapter implements PdfOverlayRenderer {
  @Override
  public void renderPage(
      PdfDocumentState documentState, int page, GraphicsContext gc, double zoom) {
    gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

    for (PdfElement element : documentState.getElementsForPage(page)) {
      element.render(gc, zoom);
      if (element == documentState.getSelectedElement()) {
        drawSelection(gc, element, zoom);
      }
    }
  }

  private void drawSelection(GraphicsContext gc, PdfElement element, double zoom) {
    Bounds b = element.getBounds();

    gc.setStroke(Color.DODGERBLUE);
    gc.setLineWidth(1);
    gc.setLineDashes(6);

    gc.strokeRect(
        b.getMinX() * zoom, b.getMinY() * zoom, b.getWidth() * zoom, b.getHeight() * zoom);

    gc.setLineDashes(null);
  }
}
