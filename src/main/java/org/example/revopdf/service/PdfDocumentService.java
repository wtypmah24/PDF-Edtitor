package org.example.revopdf.service;

import java.io.File;
import java.io.IOException;
import javafx.scene.image.Image;
import org.example.revopdf.render.PdfDocumentRenderer;

public class PdfDocumentService {
  private final PdfReader pdfReader;
  private final PdfDocumentRenderer pdfRenderer;

  private File currentFile;

  public PdfDocumentService(PdfReader pdfReader, PdfDocumentRenderer pdfRenderer) {
    this.pdfReader = pdfReader;
    this.pdfRenderer = pdfRenderer;
  }

  public void openDocument(File file) throws IOException {
    pdfReader.open(file);
    currentFile = file;
  }

  public void closeDocument() throws IOException {
    pdfReader.close();
  }

  public int getNumberOfPages() {
    return pdfReader.getNumberOfPages();
  }

  public Image renderPage(int page, double dpi) throws IOException {
    return pdfRenderer.renderPage(page, dpi);
  }

  public File getCurrentFile() {
    return currentFile;
  }

  public void setCurrentFile(File currentFile) {
    this.currentFile = currentFile;
  }
}
