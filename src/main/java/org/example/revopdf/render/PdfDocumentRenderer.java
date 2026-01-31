package org.example.revopdf.render;

import javafx.scene.image.Image;

import java.io.IOException;

public interface PdfDocumentRenderer {
  Image renderPage(int page, double dpi) throws IOException;
}
