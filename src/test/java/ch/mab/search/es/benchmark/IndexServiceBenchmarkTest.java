package ch.mab.search.es.benchmark;

import ch.mab.search.es.TestHelperService;
import ch.mab.search.es.business.IndexService;
import ch.mab.search.es.business.SecasignboxService;
import ch.mab.search.es.model.SecasignboxDocument;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

        System.out.println("elapsed time in milli seconds: " + timeElapsed);
        System.out.println("Amount of files used for the bulk index : " + files.size());
        System.out.println("Total file size of pdfs (not secasignbox documents) in bytes : " + totalFileSize);
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