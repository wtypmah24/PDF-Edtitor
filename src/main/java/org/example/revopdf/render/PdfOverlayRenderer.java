package org.example.revopdf.render;

import javafx.scene.canvas.GraphicsContext;
import org.example.revopdf.model.PdfDocumentState;
import org.example.revopdf.model.PdfElement;

public class PdfOverlayRenderer {
  public void renderPage(
          PdfDocumentState documentState, int page, GraphicsContext gc, double zoom) {
    gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

    for (PdfElement element : documentState.getElementsForPage(page)) {
      element.render(gc, zoom);
    }
  }
}
