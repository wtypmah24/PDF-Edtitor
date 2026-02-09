package org.example.revopdf.model;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfDocumentState {
  private static final double PT_TO_PX = 150 / 72.0;

  private final File sourceFile;
  private final int pageCount;
  private int currentPage = 0;
  private double zoom = 1.0;

  // PDF (pt)
  private final double pageWidthPt;
  private final double pageHeightPt;

  // Canvas (px)
  private double canvasWidthPx;
  private double canvasHeightPx;

  private final List<PdfElement> elements = new ArrayList<>();
  private PdfElement selectedElement;

  public PdfDocumentState(File sourceFile) {
    this.sourceFile = sourceFile;
    try (PDDocument document = Loader.loadPDF(sourceFile)) {
      this.pageCount = document.getNumberOfPages();
      PDPage page = document.getPage(currentPage);
      PDRectangle mb = page.getMediaBox();
      this.pageWidthPt = mb.getWidth();
      this.pageHeightPt = mb.getHeight();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // ---------- canvas ----------

  public void updateCanvasSize(double width, double height) {
    this.canvasWidthPx = width;
    this.canvasHeightPx = height;
  }

  // ---------- coord mapping ----------

  public double canvasToPdfX(double canvasX) {
    return (canvasX / canvasWidthPx) * pageWidthPt;
  }

  public double canvasToPdfY(double canvasY) {
    return ((canvasHeightPx - canvasY) / canvasHeightPx) * pageHeightPt;
  }

  public double ptToPx(double pt) {
    return pt * PT_TO_PX;
  }

  public double pdfToCanvasX(double pdfX) {
    return (pdfX / pageWidthPt) * canvasWidthPx;
  }

  public double pdfToCanvasY(double pdfY) {
    return canvasHeightPx - (pdfY / pageHeightPt) * canvasHeightPx;
  }

  // ---------- elements ----------

  public void addElement(PdfElement element) {
    elements.add(element);
  }

  public void removeElement(PdfElement element) {
    elements.remove(element);
  }

  public List<PdfElement> getElementsForPage(int page) {
    return elements.stream().filter(e -> e.getPage() == page).toList();
  }

  public PdfElement findElementAtPdf(double x, double y) {
    var list = getElementsForPage(currentPage);
    for (int i = list.size() - 1; i >= 0; i--) {
      if (list.get(i).contains(x, y)) {
        return list.get(i);
      }
    }
    return null;
  }

  public PdfElement findElementAtCanvas(double x, double y) {
    double pdfX = canvasToPdfX(x);
    double pdfY = canvasToPdfY(y);

    var list = getElementsForPage(currentPage);
    for (int i = list.size() - 1; i >= 0; i--) {
      if (list.get(i).contains(pdfX, pdfY)) {
        return list.get(i);
      }
    }
    return null;
  }

  // ---------- misc ----------

  public double getZoom() {
    return zoom;
  }

  public void zoomIn() {
    zoom = Math.min(zoom * 1.1, 5.0);
  }

  public void zoomOut() {
    zoom = Math.max(zoom / 1.1, 0.2);
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

  public int getCurrentPage() {
    return currentPage;
  }

  public File getSourceFile() {
    return sourceFile;
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  public int getPageCount() {
    return pageCount;
  }
}
