package ch.mab.search.ocr.business;

import ch.mab.search.ocr.api.Ocr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class OcrService {

    @Autowired
    private final Ocr ocr = new OcrTika();

    public String extractTextFromFile(File file) throws IOException {
        return ocr.extractText(file);
    }
}
