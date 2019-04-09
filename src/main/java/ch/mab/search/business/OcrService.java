package ch.mab.search.business;

import ch.mab.search.api.Ocr;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class OcrService {

    private final Ocr ocr = new OcrTika();

    String extractTextFromFile(File file) throws IOException {
        return ocr.extractText(file);
    }
}
