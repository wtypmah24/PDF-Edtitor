package org.example.revopdf.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

public class PdfTextElement implements PdfElement {

  private final int page;
  private String text;
  private double x;
  private double y;

  private double width = 0;
  private double height = 0;

  public PdfTextElement(int page, String text, double x, double y) {
    this.page = page;
    this.text = text;
    this.x = x;
    this.y = y;
  }

  @Override
  public int getPage() {
    return page;
  }

  @Override
  public void render(GraphicsContext gc, double zoom) {
    gc.fillText(text, x * zoom, y * zoom);

    width = text.length() * 7; // Aprx. TODO: FIX IT
    height = 12;
  }

  @Override
  public boolean contains(double px, double py) {
    return px >= x && px <= x + width && py <= y && py >= y - height;
  }

  @Override
  public void move(double dx, double dy) {
    x += dx;
    y += dy;
  }

  public void setText(String text) {
    this.text = text;
  }
}
