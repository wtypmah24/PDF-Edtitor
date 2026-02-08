package org.example.revopdf.model;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class PdfDrawElement implements PdfElement {

  private final int page;
  private final List<Point2D> points = new ArrayList<>();
  private double strokeWidth = 2.0;

  private static final double HIT_BOX = 5.0;

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

    Point2D prev = points.get(0);
    for (int i = 1; i < points.size(); i++) {
      Point2D curr = points.get(i);
      gc.strokeLine(prev.getX() * zoom, prev.getY() * zoom, curr.getX() * zoom, curr.getY() * zoom);
      prev = curr;
    }
  }

  @Override
  public boolean contains(double px, double py) {
    if (points.size() < 2) return false;

    for (int i = 1; i < points.size(); i++) {
      Point2D a = points.get(i - 1);
      Point2D b = points.get(i);

      if (distancePointToSegment(px, py, a, b) <= HIT_BOX) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void move(double dx, double dy) {
    for (int i = 0; i < points.size(); i++) {
      Point2D p = points.get(i);
      points.set(i, new Point2D(p.getX() + dx, p.getY() + dy));
    }
  }

  @Override
  public Bounds getBounds() {
    double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

    for (Point2D p : points) {
      minX = Math.min(minX, p.getX());
      minY = Math.min(minY, p.getY());
      maxX = Math.max(maxX, p.getX());
      maxY = Math.max(maxY, p.getY());
    }

    return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
  }

  private double distancePointToSegment(double px, double py, Point2D a, Point2D b) {
    double dx = b.getX() - a.getX();
    double dy = b.getY() - a.getY();

    if (dx == 0 && dy == 0) {
      dx = px - a.getX();
      dy = py - a.getY();
      return Math.sqrt(dx * dx + dy * dy);
    }

    double t = ((px - a.getX()) * dx + (py - a.getY()) * dy) / (dx * dx + dy * dy);
    t = Math.max(0, Math.min(1, t));

    double projX = a.getX() + t * dx;
    double projY = a.getY() + t * dy;

    double distX = px - projX;
    double distY = py - projY;
    return Math.sqrt(distX * distX + distY * distY);
  }

  public List<Point2D> getPoints() {
    return points;
  }

  public double getStrokeWidth() {
    return strokeWidth;
  }

  public void setStrokeWidth(double strokeWidth) {
    this.strokeWidth = strokeWidth;
  }
}
