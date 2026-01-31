package org.example.revopdf.render;

import javafx.scene.canvas.GraphicsContext;
import org.example.revopdf.model.PdfDocumentState;

public interface PdfOverlayRenderer {
  void renderPage(PdfDocumentState documentState, int page, GraphicsContext gc, double zoom);
}
