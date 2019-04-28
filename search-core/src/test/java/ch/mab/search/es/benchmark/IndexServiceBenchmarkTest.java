package ch.mab.search.es.benchmark;

import ch.mab.search.es.TestHelperService;
import ch.mab.search.es.business.IndexService;
import ch.mab.search.es.business.SecasignboxService;
import ch.mab.search.es.model.SecasignboxDocument;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.List;

@SpringBootTest
public class IndexServiceBenchmarkTest {

    private final String INDEX = this.getClass().getName().toLowerCase();

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private IndexService indexService;

    @Autowired
    private SecasignboxService secasignboxService;

    @Autowired
    private TestHelperService testService;

    @BeforeEach
    void setUp() throws IOException {
        if (indexService.isIndexExisting(INDEX)) {
            indexService.deleteIndex(INDEX);
        }
        indexService.createIndex(INDEX, secasignboxService.createMappingObject());
    }

    @Test
    void frontend_tests_gen_20_docs() throws InterruptedException, IOException {
        List<Path> files = testService.collectPathsOfPdfTestFiles();
        files = files.subList(0, 20);
        long totalFileSize = calculateTotalFileSize(files);
        List<SecasignboxDocument> docs = testService.getSecasignboxDocumentsOfPdfs(files);

        long start = System.currentTimeMillis();
        secasignboxService.bulkIndexDocument("secasignbox", docs);
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        printSystemInfos();
        printResults(files, totalFileSize, timeElapsed);
    }

    // TODO WRITE ALL SYSTEM INFOS AND TEST RESULTS INTO RESULT FILE

    @Test
    void benchmark_bulkIndex_100_documents() throws InterruptedException, IOException {
        List<Path> files = testService.collectPathsOfPdfTestFiles();
        files = files.subList(0, 100);
        long totalFileSize = calculateTotalFileSize(files);
        List<SecasignboxDocument> docs = testService.getSecasignboxDocumentsOfPdfs(files);

        long start = System.currentTimeMillis();
        secasignboxService.bulkIndexDocument(INDEX, docs);
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        printSystemInfos();
        printResults(files, totalFileSize, timeElapsed);
    }

    @Test
    void benchmark_bulkIndex_200_documents() throws InterruptedException, IOException {
        List<Path> files = testService.collectPathsOfPdfTestFiles();
        files = files.subList(0, 200);
        long totalFileSize = calculateTotalFileSize(files);
        List<SecasignboxDocument> docs = testService.getSecasignboxDocumentsOfPdfs(files);

        long start = System.currentTimeMillis();
        secasignboxService.bulkIndexDocument(INDEX, docs);
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        printSystemInfos();
        printResults(files, totalFileSize, timeElapsed);
    }

    @Test
    void benchmark_bulkIndex_400_documents() throws InterruptedException, IOException {
        List<Path> files = testService.collectPathsOfPdfTestFiles();
        files = files.subList(0, 400);
        long totalFileSize = calculateTotalFileSize(files);
        List<SecasignboxDocument> docs = testService.getSecasignboxDocumentsOfPdfs(files);

        long start = System.currentTimeMillis();
        secasignboxService.bulkIndexDocument(INDEX, docs);
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        printSystemInfos();
        printResults(files, totalFileSize, timeElapsed);
    }

    private void printResults(List<Path> files, long totalFileSize, long timeElapsed) {
        System.out.println("elapsed time in milli seconds: " + timeElapsed);
        System.out.println("Amount of files used for the bulk index : " + files.size());
        System.out.println("Total file size of pdfs (not secasignbox documents) in bytes : " + totalFileSize);
    }

    private void printSystemInfos() {
        System.out.println("java.version: " + System.getProperties().get("java.version"));
        System.out.println("java.vm.name: " + System.getProperties().get("java.vm.name"));
        System.out.println("java.runtime.version: " + System.getProperties().get("java.runtime.version"));
        System.out.println("os.name: " + System.getProperties().get("os.name"));
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
    }

    private long calculateTotalFileSize(List<Path> files) throws IOException {
        return files.stream().mapToLong(file -> {
            FileChannel imageFileChannel = null;
            try {
                imageFileChannel = FileChannel.open(file);
                return imageFileChannel.size();
            } catch (IOException e) {
                System.out.println("could not determine file size of the file : " + file.getFileName());
                return 0;
            }
        }).sum();
    }
}