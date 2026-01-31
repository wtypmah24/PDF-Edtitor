package org.example.revopdf.render.adapter;

import javafx.scene.canvas.GraphicsContext;
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
    }
  }
}
