package org.example.revopdf.service;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PdfBoxReader implements PdfReader {
  private PDDocument document;

  @Override
  public void open(File file) throws IOException {
    close();
    document = Loader.loadPDF(file);
  }

  @Override
  public void close() throws IOException {
    if (document != null) {
      document.close();
      document = null;
    }
  }

  @Override
  public int getNumberOfPages() {
    if (document == null) {
      throw new IllegalStateException("Document not opened");
    }
    return document.getNumberOfPages();
  }

  public PDDocument getDocument() {
    if (document == null) {
      throw new IllegalStateException("Document not opened");
    }
    return document;
  }
}
