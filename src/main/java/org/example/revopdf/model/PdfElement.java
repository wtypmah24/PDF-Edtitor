package org.example.revopdf.model;

import javafx.scene.canvas.GraphicsContext;

public interface PdfElement {
  int getPage();
  void render(GraphicsContext gc, double zoom);
}
