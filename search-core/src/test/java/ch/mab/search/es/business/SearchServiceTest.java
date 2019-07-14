package ch.mab.search.es.business;

import ch.mab.search.es.TestHelperService;
import ch.mab.search.es.base.IndexMappingSetting;
import ch.mab.search.es.model.SecasignboxDocument;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SearchServiceTest {

    private final String INDEX = this.getClass().getName().toLowerCase();

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private IndexService indexService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private TestHelperService testService;

    @BeforeEach
    void setUp() throws IOException {
        if (indexService.isIndexExisting(INDEX)) {
            indexService.deleteIndex(INDEX);
        }
        indexService.createIndex(INDEX, IndexMappingSetting.mappingDefaultSecasignDoc());
    }

    @AfterEach
    void tearDown() throws IOException {
        if (indexService.isIndexExisting(INDEX)) {
            indexService.deleteIndex(INDEX);
        }
    }


    @Test
    void createIndex_createSecasignboxDocumentwIndexWithtMapping_returnOkResponse() throws IOException {
        indexService.deleteIndex(INDEX);
        GetIndexRequest request = new GetIndexRequest(INDEX);
        boolean indexExists = client.indices().exists(request, RequestOptions.DEFAULT);
        Assertions.assertFalse(indexExists);

        indexService.createIndex(INDEX, IndexMappingSetting.mappingDefaultSecasignDoc());

        indexExists = client.indices().exists(request, RequestOptions.DEFAULT);
        Assertions.assertTrue(indexExists);
    }

    @Test
    void indexDocument_createDocument_returnCreatedDocument() throws Exception {
        SecasignboxDocument document = createDocument("Donald Duck und seine Taler");
        Optional<SecasignboxDocument> result = searchService.indexDocument(INDEX, document);
        Assertions.assertEquals(document, result.get());
    }

    private SecasignboxDocument createDocument(String name) {
        return new SecasignboxDocument(name, new Date(),
                                       "Der Glückstaler ist Onkel Dagoberts erste selbstverdiente Münze");
    }

    @Test
    void findAll_indexedDocuments_expectingCreatedDocuments() throws IOException, InterruptedException {
        List<SecasignboxDocument> all = searchService.findAll(INDEX);
        Assertions.assertTrue(all.isEmpty());

        SecasignboxDocument document1 = createDocument("Donald Duck und seinen Glückstaler");
        SecasignboxDocument document2 = createDocument("Donald Duck und seine 3 Neffen");
        searchService.indexDocument(INDEX, document1);
        searchService.indexDocument(INDEX, document2);

        // elastic search is indexing async
        TimeUnit.SECONDS.sleep(2);
        all = searchService.findAll(INDEX);
        Assertions.assertEquals(2, all.size());
        Assertions.assertTrue(all.contains(document1));
        Assertions.assertTrue(all.contains(document2));
    }

    @Test
    void findById_indexedDocuments_expectingCreatedDocuments() throws IOException, InterruptedException {
        SecasignboxDocument document1 = createDocument("Donald Duck und seinen Glückstaler");
        SecasignboxDocument document2 = createDocument("Donald Duck und seine 3 Neffen");
        searchService.indexDocument(INDEX, document1);
        searchService.indexDocument(INDEX, document2);

        // elastic search is indexing async
        TimeUnit.SECONDS.sleep(2);
        Optional<SecasignboxDocument> expected = searchService.findById(INDEX, document1.getDocumentId());
        Assertions.assertEquals(document1, expected.get());
    }

    @Test
    void deleteDocument_indexedDocuments_expectingCreatedDocuments() throws IOException, InterruptedException {
        SecasignboxDocument document1 = createDocument("Donald Duck und seinen Glückstaler");
        SecasignboxDocument document2 = createDocument("Donald Duck und seine 3 Neffen");
        searchService.indexDocument(INDEX, document1);
        searchService.indexDocument(INDEX, document2);
        searchService.deleteDocument(INDEX, document1.getDocumentId());

        // elastic search is indexing async
        TimeUnit.SECONDS.sleep(2);
        Optional<SecasignboxDocument> expected = searchService.findById(INDEX, document1.getDocumentId());
        Assertions.assertTrue(expected.isEmpty());
    }

    // findAll returns per default 10 documents
    @Disabled
    @Test
    void bulkIndexDocument_createBulkOfDocuments_returnOk() throws IOException, InterruptedException {
        List<Path> files = testService.collectPathsOfPdfTestFiles();
        List<SecasignboxDocument> docs = testService.readSecasignDocumentFromPdfs(files);
        BulkResponse bulkItemResponses = searchService.bulkIndexDocument(INDEX, docs);
        TimeUnit.SECONDS.sleep(4);

        List<SecasignboxDocument> all = searchService.findAll(INDEX);
        Assertions.assertEquals(docs.size(), all.size());
        Assertions.assertTrue(docs.containsAll(all));
    }

    @Disabled("muss an die neue Logik angepasst werden")
    @Test
    void searchByDocumentName_indexedDocuments_returnDocumentWithSameName() throws IOException, InterruptedException {
        List<Path> files = testService.collectPathsOfPdfTestFiles();
        List<SecasignboxDocument> docs = testService.readSecasignDocumentFromPdfs(files);
        searchService.bulkIndexDocument(INDEX, docs);
        TimeUnit.SECONDS.sleep(2);

        List<SecasignboxDocument> search = null; // secasignboxService.searchByDocumentName(INDEX, "AS_MandelFx.pdf");
        Assertions.assertEquals(1, search.size());
        Assertions.assertTrue(docs.contains(search.get(0)));
    }

    @Test
    void searchByDocumentName_mappingAndSetting_returnAnalyzedDocuments() throws IOException, InterruptedException {
        indexService.deleteIndex(INDEX);
        indexService.createIndex(INDEX, IndexMappingSetting.mappingAnalyzerSecasignDoc(), IndexMappingSetting.settingGermanRebuiltAndUnderscoreAnalyzerSecasignDoc());

      //  indexService.createIndex(INDEX, IndexMappingSetting.mappingDefaultSecasignDoc(), IndexMappingSetting.settingGermanRebuiltAndUnderscoreAnalyzerSecasignDoc());
        SecasignboxDocument doc1 = testService.createSecasignDocCustomeContentAndDate("2018_mandel_fx_threads");
        SecasignboxDocument doc2 = testService.createSecasignDocCustomeContentAndDate("2019 mandel fx threads");

        searchService.indexDocument(INDEX, doc1);
        searchService.indexDocument(INDEX, doc2);

        TimeUnit.SECONDS.sleep(2);

        SearchResponse mandel = searchService.findByDocumentNamenAndTerm(INDEX, "mandel");
        System.out.println(mandel.getHits().getTotalHits().value);
    }
}
