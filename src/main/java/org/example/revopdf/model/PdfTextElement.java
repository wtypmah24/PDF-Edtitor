package org.example.revopdf.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

public class PdfTextElement implements PdfElement {
  private final int page;
  private String text;
  private double x;
  private double y;
  private int fontSize;

  public PdfTextElement(int page, String text, double x, double y) {
    this.page = page;
    this.text = text;
    this.x = x;
    this.y = y;
    this.fontSize = fontSize;
  }

  @Override
  public int getPage() {
    return page;
  }

  @Override
  public void render(GraphicsContext gc, double zoom) {
    gc.setFont(new Font(fontSize * zoom));
    gc.fillText(text, x * zoom, y * zoom);
  }
}
