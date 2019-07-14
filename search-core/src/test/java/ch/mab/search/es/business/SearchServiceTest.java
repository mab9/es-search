package ch.mab.search.es.business;

import ch.mab.search.es.TestHelperService;
import ch.mab.search.es.base.IndexMappingSetting;
import ch.mab.search.es.model.SearchStrike;
import ch.mab.search.es.model.SecasignboxDocument;
import org.elasticsearch.action.bulk.BulkResponse;
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
import java.util.stream.Collectors;

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

    @Test
    void findByDocumentNamenAndTerm_matchQuery_returnAnalyzedDocuments() throws IOException, InterruptedException {
        indexService.deleteIndex(INDEX);
        indexService.createIndex(INDEX, IndexMappingSetting.mappingAnalyzerSecasignDoc(), IndexMappingSetting.settingGermanRebuiltAndUnderscoreAnalyzerSecasignDoc());

        SecasignboxDocument doc1 = testService.createSecasignDocCustomeContentAndDate("2018_mandel_fx_threads");
        SecasignboxDocument doc2 = testService.createSecasignDocCustomeContentAndDate("2019 mandel fx threads");

        searchService.indexDocument(INDEX, doc1);
        searchService.indexDocument(INDEX, doc2);

        TimeUnit.SECONDS.sleep(2);
        List<SearchStrike> strikes;
        List<String> expectedStrikes;

        strikes = searchService.queryByTerm(INDEX, "Mandel");
        expectedStrikes = strikes.stream().flatMap(strike -> strike.getHighlights().stream()).collect(Collectors.toList());
        Assertions.assertTrue(expectedStrikes.contains("2018_<b>mandel</b>_fx_threads"));
        Assertions.assertTrue(expectedStrikes.contains("2019 <b>mandel</b> fx threads"));
        Assertions.assertEquals(strikes.get(0).getScore(), strikes.get(1).getScore());

        strikes = searchService.queryByTerm(INDEX, "2018_mandel");
        expectedStrikes = strikes.stream().flatMap(strike -> strike.getHighlights().stream()).collect(Collectors.toList());
        Assertions.assertTrue(expectedStrikes.contains("<b>2018</b>_<b>mandel</b>_fx_threads"));
        Assertions.assertTrue(expectedStrikes.contains("2019 <b>mandel</b> fx threads"));
        Assertions.assertNotEquals(strikes.get(0).getScore(), strikes.get(1).getScore());
    }

    // fuzzy einbauen und testen
    // phrase einbauen und testen
    // shingle mapping einbauen und testen
    // search in 2 indexes

        @Test
    void findByDocumentNamenAndTerm_phraseQuery_returnAnalyzedDocuments() throws IOException, InterruptedException {
        indexService.deleteIndex(INDEX);
        indexService.createIndex(INDEX, IndexMappingSetting.mappingAnalyzerSecasignDoc(), IndexMappingSetting.settingGermanRebuiltAndUnderscoreAnalyzerSecasignDoc());

        SecasignboxDocument doc1 = testService.createSecasignDocCustomeContentAndDate("2018_mandel_fx_threads_concurrent_pic");
        SecasignboxDocument doc2 = testService.createSecasignDocCustomeContentAndDate("2018_mandel_fx_threads_concurrent_img");
        SecasignboxDocument doc3 = testService.createSecasignDocCustomeContentAndDate("2019 mandel fx threads concurrent pic");

        searchService.indexDocument(INDEX, doc1);
        searchService.indexDocument(INDEX, doc2);
        searchService.indexDocument(INDEX, doc3);

        TimeUnit.SECONDS.sleep(2);
        List<SearchStrike> strikes;
        List<String> expectedStrikes;

        strikes = searchService.queryPhraseByTerm(INDEX, "mandel threads");
        expectedStrikes = strikes.stream().flatMap(strike -> strike.getHighlights().stream()).collect(Collectors.toList());
        Assertions.assertTrue(expectedStrikes.contains("2018_<b>mandel</b>_fx_<b>threads</b>_concurrent_pic"));
        Assertions.assertTrue(expectedStrikes.contains("2018_<b>mandel</b>_fx_<b>threads</b>_concurrent_img"));
        Assertions.assertTrue(expectedStrikes.contains("2019 <b>mandel</b> fx <b>threads</b> concurrent pic"));
        Assertions.assertEquals(strikes.get(0).getScore(), strikes.get(1).getScore());
        Assertions.assertEquals(strikes.get(0).getScore(), strikes.get(2).getScore());

        strikes = searchService.queryPhraseByTerm(INDEX, "2018 threads");
        expectedStrikes = strikes.stream().flatMap(strike -> strike.getHighlights().stream()).collect(Collectors.toList());
        Assertions.assertTrue(expectedStrikes.contains("<b>2018</b>_mandel_fx_<b>threads</b>_concurrent_pic"));
        Assertions.assertTrue(expectedStrikes.contains("<b>2018</b>_mandel_fx_<b>threads</b>_concurrent_img"));
        Assertions.assertEquals(strikes.get(0).getScore(), strikes.get(1).getScore());
        Assertions.assertEquals(2, expectedStrikes.size());

        strikes = searchService.queryPhraseByTerm(INDEX, "2018 fx pic");
        expectedStrikes = strikes.stream().flatMap(strike -> strike.getHighlights().stream()).collect(Collectors.toList());
        Assertions.assertTrue(expectedStrikes.contains("<b>2018</b>_mandel_<b>fx</b>_threads_concurrent_<b>pic</b>"));
        Assertions.assertEquals(1, expectedStrikes.size());
    }

}
