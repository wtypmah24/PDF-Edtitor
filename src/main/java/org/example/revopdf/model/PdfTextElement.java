package org.example.revopdf.model;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class PdfTextElement implements PdfElement {
  private final PdfDocumentState documentState;

  private final int page;
  private String text;
  private double x;
  private double y;

  private String fontFamily = "Arial";
  private double fontSize = 12;
  private Color color = Color.BLACK;

  public PdfTextElement(PdfDocumentState documentState, int page, String text, double x, double y) {
    this.documentState = documentState;
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
    double fontSizePx = documentState.ptToPx(fontSize);
    gc.setFont(Font.font(fontFamily, fontSizePx));

    double cx = documentState.pdfToCanvasX(x) * zoom;
    double cy = documentState.pdfToCanvasY(y) * zoom;

    gc.fillText(text, cx, cy);
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
    double width = text.length() * fontSize * 0.6;
    double ascent = fontSize * 0.8;
    double descent = fontSize * 0.2;

    return new BoundingBox(x, y - ascent, width, ascent + descent);
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

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public Color getColor() {
    return color;
  }
}
