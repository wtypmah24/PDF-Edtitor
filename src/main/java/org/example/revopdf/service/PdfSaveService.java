package org.example.revopdf.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.geometry.Point2D;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.example.revopdf.model.*;

public class PdfSaveService {

  public void save(File sourceFile, File targetFile, PdfDocumentState state) throws IOException {
    try (PDDocument document = Loader.loadPDF(sourceFile)) {

      for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
        PDPage page = document.getPage(pageIndex);

        try (PDPageContentStream contentStream =
            new PDPageContentStream(
                document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

          List<PdfElement> elements = state.getElementsForPage(pageIndex);

          for (PdfElement element : elements) {
            writeElement(contentStream, element);
          }
        }
      }

      document.save(targetFile);
    }
  }

  private void writeElement(PDPageContentStream cs, PdfElement element) throws IOException {
    if (element instanceof PdfTextElement text) {
      writeText(cs, text);
    } else if (element instanceof PdfDrawElement draw) {
      writeDraw(cs, draw);
    } else if (element instanceof PdfWhiteoutBrushElement erase) {
      writeWhiteout(cs, erase);
    }
  }

  private void writeText(PDPageContentStream cs, PdfTextElement text) throws IOException {

    PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    float fontSize = (float) text.getFontSize();

    float x = (float) text.getX();
    float y = (float) text.getY();

    cs.beginText();
    cs.setFont(font, fontSize);
    cs.setNonStrokingColor(convertColor(text.getColor()));
    cs.newLineAtOffset(x, y);
    cs.showText(text.getText());
    cs.endText();
  }

  private void writeDraw(PDPageContentStream cs, PdfDrawElement draw) throws IOException {
    List<Point2D> pts = draw.getPoints();
    if (pts.size() < 2) return;

    cs.setStrokingColor(0, 0, 0);
    cs.setLineWidth((float) draw.getStrokeWidth());

    Point2D first = pts.getFirst();
    cs.moveTo((float) first.getX(), (float) first.getY());

    for (int i = 1; i < pts.size(); i++) {
      Point2D p = pts.get(i);
      cs.lineTo((float) p.getX(), (float) p.getY());
    }

    cs.stroke();
  }

  private void writeWhiteout(PDPageContentStream cs, PdfWhiteoutBrushElement erase)
      throws IOException {

    cs.setNonStrokingColor(java.awt.Color.WHITE);

    for (Point2D p : erase.getPoints()) {
      float r = (float) erase.getRadius();
      cs.addRect((float) p.getX() - r, (float) p.getY() - r, r * 2, r * 2);
      cs.fill();
    }
  }

  private java.awt.Color convertColor(javafx.scene.paint.Color fx) {
    return new java.awt.Color(
        (float) fx.getRed(), (float) fx.getGreen(), (float) fx.getBlue(), (float) fx.getOpacity());
  }
}
