package ch.mab.search.es;

import ch.mab.search.es.model.SecasignboxDocument;
import ch.mab.search.ocr.business.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
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
        return new SecasignboxDocument(fileName.toString(), new Date(),
                                       documentContent);
    }

    public List<Path> collectPathsOfPdfTestFiles() throws IOException {
        Path roote = Paths.get(PATH_TO_PDF_RESOURCES);
        return getPaths(roote);
    }


    public List<Path> collectPathsOfPdfTestFiles(String path) throws IOException {
        Path roote = Paths.get(path);
        return getPaths(roote);
    }

    private List<Path> getPaths(Path roote) throws IOException {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.{pdf}");
        return Files.walk(roote)
                    .filter(Files::isRegularFile)
                    .filter(f -> matcher.matches(f.getFileName()))
                    .collect(Collectors.toList());
    }
}
