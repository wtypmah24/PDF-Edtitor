package org.example.revopdf.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfDocumentState {

  private final File sourceFile;
  private int currentPage = 0;

  private final List<PdfElement> elements = new ArrayList<>();

  public PdfDocumentState(File sourceFile) {
    this.sourceFile = sourceFile;
  }

  public File getSourceFile() {
    return sourceFile;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  public void addElement(PdfElement element) {
    elements.add(element);
  }

  public void removeElement(PdfElement element) {
    elements.remove(element);
  }

  public List<PdfElement> getElementsForPage(int page) {
    return elements.stream().filter(e -> e.getPage() == page).toList();
  }

  public List<PdfElement> getAllElements() {
    return List.copyOf(elements);
  }
}
