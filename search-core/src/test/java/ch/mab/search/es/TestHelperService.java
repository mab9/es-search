package ch.mab.search.es;

import ch.mab.search.es.business.IndexService;
import ch.mab.search.es.model.SecasignboxDocument;
import ch.mab.search.ocr.business.OcrService;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

@Component
public class TestHelperService {

    @Autowired
    private OcrService ocrService;

    @Autowired
    private IndexService indexService;

    private final String PATH_TO_PDF_RESOURCES = "src/test/resources/pdf";



    public List<SecasignboxDocument> readSecasignDocumentFromPdfs(List<Path> pdfs) {
        return pdfs.stream().map(pdf -> {
            try {
                String text = ocrService.extractTextFromFile(pdf.toFile());
                return createSecasignDocCustomNameAndContent(pdf.getFileName(), text);
            } catch (IOException e) {
                return null;
            }
        }).collect(Collectors.toList());
    }

    public SecasignboxDocument createSecasignDocCustomeName(String docName) {
        return new SecasignboxDocument(docName, new Date(),
                                       "Ein Thread");
    }

    public SecasignboxDocument createSecasignDocCustomNameAndContent(String docName, String docContent) {
        return new SecasignboxDocument(docName, new Date(),
                                       docContent);
    }

    public SecasignboxDocument createSecasignDocCustomNameAndContent(Path fileName, String documentContent) {
        return new SecasignboxDocument(fileName.toString(), new Date(),
                                       documentContent);
    }

    public List<Path> collectPathsOfPdfTestFiles() {
        Path roote = Paths.get(PATH_TO_PDF_RESOURCES);
        return getPaths(roote);
    }


    public List<Path> collectPathsOfPdfTestFiles(String path) {
        Path roote = Paths.get(path);
        return getPaths(roote);
    }

    private List<Path> getPaths(Path roote) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.{pdf}");
        try {
            return Files.walk(roote)
                        .filter(Files::isRegularFile)
                        .filter(f -> matcher.matches(f.getFileName()))
                        .collect(Collectors.toList());
        } catch (IOException e) {
            fail("Could not gather paths recursively from root: " + roote.toString(), e);
            throw new RuntimeException();
        }
    }

    public void initIndexIfNotExisting(String index) {
        try {
            if (indexService.isIndexExisting(index)) {
                indexService.deleteIndex(index);
            }

            indexService.createIndex(index);
        } catch (IOException e) {
            fail("Could not init index: " + index, e);
            throw new RuntimeException();
        }

    }

    public void initIndexIfNotExisting(String index, XContentBuilder mappingObject) {
        try {
            if (indexService.isIndexExisting(index)) {
                indexService.deleteIndex(index);
            }

            indexService.createIndex(index,  mappingObject);
        } catch (IOException e) {
            fail("Could not init index: " + index, e);
            throw new RuntimeException();
        }
    }

    public List<SecasignboxDocument> gatherSecasignBoxDocuments(int amount) {
        List<Path> files = collectPathsOfPdfTestFiles();
        files = files.subList(0, amount);
        return readSecasignDocumentFromPdfs(files);
    }
}
