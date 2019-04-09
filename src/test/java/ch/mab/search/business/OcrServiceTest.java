package ch.mab.search.business;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

@SpringBootTest
public class OcrServiceTest {


    @Autowired
    private final OcrService service = new OcrService();

    @Test
    public void extractTextFromFile_pdfWithoutImage_fullText() throws IOException {
        File file = new File("src/test/resources/futurama.pdf");
        String txt = service.extractTextFromFile(file);
        assertTrue(!txt.isEmpty());
    }

    @Test
    public void extractTextFromFile_pdfWithImageComputerLetters_fullText() throws IOException {
        File file = new File("src/test/resources/LE_Tasks.pdf");
        String txt = service.extractTextFromFile(file);
        assertTrue(!txt.isEmpty());
    }

    @Test
    public void extractTextFromFile_pdfWithImageHandWrittenLetters_fullText() throws IOException {
        File file = new File("src/test/resources/04 SOLID Prinzipien 19F 4Ia.pdf");
        String txt = service.extractTextFromFile(file);
        assertTrue(!txt.isEmpty());
    }
}