package ch.mab.search.business;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
public class OcrServiceTest {


    @Autowired
    private final OcrService service = new OcrService();

    @Test
    public void extractTextFromFile_pdfWithoutImage_fullText() throws IOException {
        File file = new File("src/test/java/futurama.pdf");
        String txt = service.extractTextFromFile(file);
        assertTrue(!txt.isEmpty());
    }

    @Test
    public void extractTextFromFile_pdfWithImageComputerLetters_fullText() throws IOException {
        File file = new File("src/test/java/LE_Tasks.pdf");
        String txt = service.extractTextFromFile(file);
        assertTrue(!txt.isEmpty());
    }

    @Test
    public void extractTextFromFile_pdfWithImageHandWrittenLetters_fullText() throws IOException {
        File file = new File("src/test/java/04 SOLID Prinzipien 19F 4Ia.pdf");
        String txt = service.extractTextFromFile(file);
        assertTrue(!txt.isEmpty());
    }
}