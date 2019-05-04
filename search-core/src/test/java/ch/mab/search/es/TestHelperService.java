package ch.mab.search.es;

import ch.mab.search.es.model.DocumentState;
import ch.mab.search.es.model.SecasignboxDocument;
import ch.mab.search.ocr.business.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TestHelperService {

    @Autowired
    private OcrService ocrService;

    private final String PATH_TO_PDF_RESOURCES = "src/test/resources/pdf";

    public List<SecasignboxDocument> getSecasignboxDocumentsOfPdfs(List<Path> pdfs) {
        return pdfs.stream().map(pdf -> {
            try {
                String text = ocrService.extractTextFromFile(pdf.toFile());
                return createDocument(pdf.getFileName(), text);
            } catch (IOException e) {
                return null;
            }
        }).collect(Collectors.toList());
    }

    private SecasignboxDocument createDocument(Path fileName, String documentContent) {
        return new SecasignboxDocument(UUID.randomUUID(), fileName.toString(), new Date(), new Date(),
                                       documentContent);
    }

    public List<Path> collectPathsOfPdfTestFiles() throws IOException {
        Path roote = Paths.get(PATH_TO_PDF_RESOURCES);
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.{pdf}");
        return Files.walk(roote)
                    .filter(Files::isRegularFile)
                    .filter(f -> matcher.matches(f.getFileName()))
                    .collect(Collectors.toList());

    }
}
