package org.example.revopdf.service;

import java.io.File;
import java.io.IOException;

public interface PdfReader {
  void open(File file) throws IOException;

  void close() throws IOException;

  int getNumberOfPages();
}
