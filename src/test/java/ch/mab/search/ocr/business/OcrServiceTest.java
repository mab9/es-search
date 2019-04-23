package ch.mab.search.ocr.business;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class OcrServiceTest {

    private final String pathToPdfResources = "src/test/resources/pdf";

    @Autowired
    private final OcrService service = new OcrService();

    @Test
    public void extractTextFromFile_pdfWithoutImage_fullText() throws IOException {
        File file = new File(pathToPdfResources + "/futurama.pdf");
        String txt = service.extractTextFromFile(file);
        assertTrue(txt.contains("Are you, by any chance, interested in becoming my new spaceship crew?"));
    }

    @Test
    public void extractTextFromFile_pdfWithImageComputerLetters_fullText() throws IOException {
        File file = new File(pathToPdfResources + "/LE_Tasks.pdf");
        String txt = service.extractTextFromFile(file);
        assertTrue(!txt.isEmpty());
    }

    @Test
    public void extractTextFromFile_pdfWithImageHandWrittenLetters_fullText() throws IOException {
        File file = new File(pathToPdfResources + "/04 SOLID Prinzipien 19F 4Ia.pdf");
        String txt = service.extractTextFromFile(file);
        assertTrue(!txt.isEmpty());
    }
}