package ch.mab.search.api;

import java.io.File;
import java.io.IOException;

public interface Ocr {

    String extractText(File pdf) throws IOException;
}
