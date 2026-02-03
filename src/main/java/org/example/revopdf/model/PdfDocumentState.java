package org.example.revopdf.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfDocumentState {

  private final File sourceFile;
  private int currentPage = 0;
  private double zoom = 1.0;

  private final List<PdfElement> elements = new ArrayList<>();
  private PdfElement selectedElement;

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

  public double getZoom() {
    return zoom;
  }

  public void setZoom(double zoom) {
    this.zoom = Math.max(0.2, Math.min(5.0, zoom));
  }

  public void zoomIn() {
    setZoom(zoom * 1.1);
  }

  public void zoomOut() {
    setZoom(zoom / 1.1);
  }

  public PdfElement getSelectedElement() {
    return selectedElement;
  }

  public void setSelectedElement(PdfElement selectedElement) {
    this.selectedElement = selectedElement;
  }

  public void clearSelection() {
    this.selectedElement = null;
  }

  public PdfElement findElementAt(double x, double y) {
    var elements = getElementsForPage(currentPage);
    for (int i = elements.size() - 1; i >= 0; i--) {
      if (elements.get(i).contains(x, y)) {
        return elements.get(i);
      }
    }
    return null;
  }
}
