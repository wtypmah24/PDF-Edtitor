package org.example.revopdf.model;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class PdfDrawElement implements PdfElement {
  private final int page;
  private final List<Point2D> points = new ArrayList<>();
  private double strokeWidth = 2.0;

  public PdfDrawElement(int page) {
    this.page = page;
  }

  public void addPoint(double x, double y) {
    points.add(new Point2D(x, y));
  }

  @Override
  public int getPage() {
    return page;
  }

  @Override
  public void render(GraphicsContext gc, double zoom) {
    if (points.size() < 2) return;

    gc.setLineWidth(strokeWidth * zoom);

    Point2D prev = points.getFirst();
    for (int i = 1; i < points.size(); i++) {
      Point2D curr = points.get(i);
      gc.strokeLine(prev.getX() * zoom, prev.getY() * zoom, curr.getX() * zoom, curr.getY() * zoom);
      prev = curr;
    }
  }
}
