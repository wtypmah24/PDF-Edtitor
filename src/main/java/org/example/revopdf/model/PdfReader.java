package org.example.revopdf.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface PdfReader {
    BufferedImage pdfToBufferedImage(File pdfFile) throws IOException;
}
