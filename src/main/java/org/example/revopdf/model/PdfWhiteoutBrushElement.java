package org.example.revopdf.model;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class PdfWhiteoutBrushElement implements PdfElement {

  private final List<Point2D> points = new ArrayList<>();
  private double radius = 20;
  private final int page;

  public PdfWhiteoutBrushElement(int page) {
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

    gc.setStroke(Color.WHITE);
    gc.setLineWidth(radius * 2 * zoom);
    gc.setLineCap(StrokeLineCap.ROUND);
    gc.setLineJoin(StrokeLineJoin.ROUND);

    for (int i = 1; i < points.size(); i++) {
      Point2D p1 = points.get(i - 1);
      Point2D p2 = points.get(i);

      gc.strokeLine(
          p1.getX() * zoom, p1.getY() * zoom,
          p2.getX() * zoom, p2.getY() * zoom);
    }
  }

  @Override
  public boolean contains(double x, double y) {
    return false;
  }

  @Override
  public void move(double dx, double dy) {}

  @Override
  public Bounds getBounds() {
    return null;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  public List<Point2D> getPoints() {
    return points;
  }

  public double getRadius() {
    return radius;
  }
}
