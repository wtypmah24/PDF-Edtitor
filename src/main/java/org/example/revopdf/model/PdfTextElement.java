package org.example.revopdf.model;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class PdfTextElement implements PdfElement {

  private final int page;
  private String text;
  private double x;
  private double y;

  private String fontFamily = "Arial";
  private double fontSize = 12;
  private Color color = Color.BLACK;

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
    gc.setFill(color);
    gc.setFont(Font.font(fontFamily, fontSize * zoom));
    gc.fillText(text, x * zoom, y * zoom);
  }

  @Override
  public boolean contains(double px, double py) {
    Bounds b = getBounds();
    return b.contains(px, py);
  }

  @Override
  public void move(double dx, double dy) {
    x += dx;
    y += dy;
  }

  @Override
  public Bounds getBounds() {
    double width = text.length() * fontSize * 0.55;
    double height = fontSize;

    return new BoundingBox(x, y - height, width, height);
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setFontSize(double fontSize) {
    this.fontSize = fontSize;
  }

  public double getFontSize() {
    return fontSize;
  }

  public void setFontFamily(String fontFamily) {
    this.fontFamily = fontFamily;
  }

  public String getFontFamily() {
    return fontFamily;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Color getColor() {
    return color;
  }
}
