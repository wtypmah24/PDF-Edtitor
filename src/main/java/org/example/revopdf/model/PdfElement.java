package org.example.revopdf.model;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;

public interface PdfElement {
  int getPage();

  void render(GraphicsContext gc, double zoom);

  boolean contains(double x, double y); // попали ли мышкой

  void move(double dx, double dy);

  Bounds getBounds();
}
