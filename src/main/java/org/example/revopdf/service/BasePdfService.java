package org.example.revopdf.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class BasePdfService {
  private final PdfReader pdfReader;

  public BasePdfService(PdfReader pdfReader) {
    this.pdfReader = pdfReader;
  }

  public Image renderFirstPage(File file) throws IOException {
    BufferedImage bufferedImage = pdfReader.pdfToBufferedImage(file);
    return SwingFXUtils.toFXImage(bufferedImage, null);
  }
}
