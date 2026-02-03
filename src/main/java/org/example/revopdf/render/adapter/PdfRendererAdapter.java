package org.example.revopdf.render.adapter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.example.revopdf.render.PdfDocumentRenderer;
import org.example.revopdf.service.adapter.PdfBoxReader;

public class PdfRendererAdapter implements PdfDocumentRenderer {

  private final PdfBoxReader pdfBoxReader;

  public PdfRendererAdapter(PdfBoxReader pdfBoxReader) {
    this.pdfBoxReader = pdfBoxReader;
  }

  @Override
  public Image renderPage(int page, double dpi) throws IOException {
    BufferedImage bufferedImage = renderBufferedImage(page, dpi);
    return SwingFXUtils.toFXImage(bufferedImage, null);
  }

  private BufferedImage renderBufferedImage(int page, double dpi) throws IOException {
    PDFRenderer renderer = new PDFRenderer(pdfBoxReader.getDocument());
    return renderer.renderImageWithDPI(page, (float) dpi);
  }
}
