package org.example.revopdf.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PdfBoxReader implements PdfReader {
  public BufferedImage pdfToBufferedImage(File pdfFile) throws IOException {
    try (PDDocument document = Loader.loadPDF(pdfFile)) {
      PDFRenderer pdfRenderer = new PDFRenderer(document);
      return pdfRenderer.renderImageWithDPI(0, 150);
    }
  }
}
