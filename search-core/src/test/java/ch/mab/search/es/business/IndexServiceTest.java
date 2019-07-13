package ch.mab.search.es.business;

import ch.mab.search.es.TestHelperService;
import ch.mab.search.es.model.ContactDocument;
import ch.mab.search.es.model.SearchHighlights;
import ch.mab.search.es.model.Technology;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootTest
class IndexServiceTest {

    private final String INDEX = this.getClass().getName().toLowerCase();

    @Autowired
    private TestHelperService testService;

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private IndexService indexService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private SearchService searchService;

    @BeforeEach
    void setUp() throws IOException {
        if (indexService.isIndexExisting(INDEX)) {
            indexService.deleteIndex(INDEX);
        }
        indexService.createIndex(INDEX);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (indexService.isIndexExisting(INDEX)) {
            indexService.deleteIndex(INDEX);
        }
    }

    @Test
    void createIndex_addAnalyzerToSettings_shouldCreateIndex() throws IOException {
        Settings settings = Settings.builder()
                                    .put("analysis.analyzer",
                                         " \"std_english\": {\n" + "\"type\":      \"standard\",\n" +
                                         "\"stopwords\": \"_english_\"\n}")
                                    .put("index.number_of_shards", 3)
                                    .put("index.number_of_replicas", 2)
                                    .build();
        if (indexService.isIndexExisting(INDEX)) {
            indexService.deleteIndex(INDEX);
        }
        indexService.createIndex(INDEX, settings);
        GetIndexResponse index = indexService.getIndex(INDEX);
        Assertions.assertTrue(index.getSetting(INDEX, "index.analysis.analyzer").contains("english"));
    }

    @Test
    void createIndex_addTokenizeOnCharsTokenizer_shouldCreateIndex() throws IOException {
        XContentBuilder settings = XContentFactory.jsonBuilder()
        .startObject()
                .startObject("analysis")
                    .startObject("analyzer")
                        .startObject("underscore_analyzer")
                            .field("tokenizer", "underscore_tokenizer")
                            .field("type", "custom")
                        .endObject()
                    .endObject()
                    .startObject("tokenizer")
                        .startObject("underscore_tokenizer")
                            .field("type", "char_group")
                            .field("tokenize_on_chars", "_")
                        .endObject()
                    .endObject()
                .endObject()
            .endObject();

        if (indexService.isIndexExisting(INDEX)) {
            indexService.deleteIndex(INDEX);
        }
        indexService.createIndex(INDEX, searchService.createMappingObject(), settings);
        GetIndexResponse index = indexService.getIndex(INDEX);
        Assertions.assertTrue(index.getSettings().get(INDEX).hasValue("index.analysis.analyzer.underscore_analyzer.tokenizer"));

        searchService.indexDocument(INDEX, testService.createSecasignDocument(Path.of("2018_dagobert_duck"), "I am passionate about Elasticsearch"));

        List<SearchHighlights> dagobert = searchService.findByTermHighlighted(INDEX, "dagobert");
        dagobert.forEach(h -> {
            System.out.println(h.getDocumentName() + ": ");
            h.getHighlights().forEach(hi -> System.out.print(hi + ", "));
        });
    }


    // remove
    @Test
    void createIndex_addTokenizeOnCharsTokenizerAndMapping_shouldCreateIndex() throws IOException {
        Settings settings = Settings.builder()
                                    .put("analysis.analyzer", "\"type\": \"custom\"," + "\"underscore_analyzer\":" +
                                                              "{\"tokenizer\":\"underscore_tokenizer\"}")
                                    .put("analysis.tokenizer",
                                         "\"underscore_tokenizer\":" + "{\"type\":\"char_group\"," +
                                         "{\"tokenize_on_chars\": [\"_\"]}")
                                    .build();
        if (indexService.isIndexExisting(INDEX)) {
            indexService.deleteIndex(INDEX);
        }
        indexService.createIndex(INDEX, searchService.createMappingObject(), settings);
        GetIndexResponse index = indexService.getIndex(INDEX);
        Assertions.assertTrue(index.getSetting(INDEX, "index.analysis.analyzer").contains("underscore_tokenizer"));
    }

    @Test
    void analyze_language_returnTokensAnalzyedByStandardTokenizer() throws IOException {
        testService.initIndexIfNotExisting(INDEX);
        AnalyzeRequest request = new AnalyzeRequest();
        request.text("Some text to analyze", "Some more text to analyze");
        request.analyzer("english");

        AnalyzeResponse response = client.indices().analyze(request, RequestOptions.DEFAULT);
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        System.out.println("English: Some text to analyze\", \"Some more text to analyze\"");
        tokens.forEach(token -> System.out.println(token.getTerm()));

        request = new AnalyzeRequest();
        request.text("Etwas Text zum analysieren", "Etwas mehr Text zum analysieren");
        request.analyzer("german");

        response = client.indices().analyze(request, RequestOptions.DEFAULT);
        tokens = response.getTokens();
        System.out.println("German: Etwas Text zum analysieren\", \"Etwas mehr Text zum analysieren");
        tokens.forEach(token -> System.out.println(token.getTerm()));
    }

    @Disabled(
            "Open bug from Elastic Search, patch follows in 7.3.0: https://github.com/elastic/elasticsearch/issues/39670")
    @Test
    void analyze_charGroupAnalyze_returnTokensSplittedByChar() throws IOException {
        testService.initIndexIfNotExisting(INDEX);
        /*AnalyzeRequest request = new AnalyzeRequest();

        Map<String, Object> charGroup = new HashMap<>();
        charGroup.put("type", "simple_pattern_split");
        charGroup.put("pattern", "_");
        request.tokenizer(charGroup);


                charGroup.put("type", "char_group");
        charGroup.put("tokenize_on_chars", "_");
         */

        /*
         "type": "simple_pattern_split",
          "pattern": "_"


        request.text("20191206_mobi_guthaben_wohnung", "Etwas text um mehr bessere Resultate zu erzielen.");
        request.analyzer("german");
*/

        AnalyzeRequest request = new AnalyzeRequest();
        request.text("<b>Some text to analyze</b>");
        request.addCharFilter("html_strip");
        request.tokenizer("standard");
        request.addTokenFilter("lowercase");

        Map<String, Object> stopFilter = new HashMap<>();
        stopFilter.put("type", "stop");
        stopFilter.put("stopwords", new String[] { "to" });
        request.addTokenFilter(stopFilter);

        AnalyzeResponse response = client.indices().analyze(request, RequestOptions.DEFAULT);
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        tokens.forEach(token -> System.out.println(token.getTerm()));
    }

    @Test
    void createIndex_createNewIndexWithoutMapping_returnOkResponse() throws Exception {
        indexService.deleteIndex(INDEX);
        GetIndexRequest request = new GetIndexRequest(INDEX);
        boolean indexExists = client.indices().exists(request, RequestOptions.DEFAULT);
        Assertions.assertFalse(indexExists);

        indexService.createIndex(INDEX);

        indexExists = client.indices().exists(request, RequestOptions.DEFAULT);
        Assertions.assertTrue(indexExists);
    }

    @Test
    void createIndex_createNewIndexWithtMapping_returnOkResponse() throws IOException {
        indexService.deleteIndex(INDEX);
        GetIndexRequest request = new GetIndexRequest(INDEX);
        boolean indexExists = client.indices().exists(request, RequestOptions.DEFAULT);
        Assertions.assertFalse(indexExists);

        indexService.createIndex(INDEX, contactService.createMappingObject());

        indexExists = client.indices().exists(request, RequestOptions.DEFAULT);
        Assertions.assertTrue(indexExists);
    }

    @Test
    void updateMapping_updateIndex_returnOkResponse() throws IOException {
        Assertions.assertTrue(indexService.isIndexExisting(INDEX));
        AcknowledgedResponse acknowledgedResponse =
                indexService.updateMapping(INDEX, contactService.createMappingObject());
        Assertions.assertTrue(acknowledgedResponse.isAcknowledged());
    }

    @Test
    void deleteIndex_indexNotExisting_noExistingIndex() throws Exception {
        Assertions.assertTrue(indexService.isIndexExisting(INDEX));
        indexService.deleteIndex(INDEX);
        Assertions.assertFalse(indexService.isIndexExisting(INDEX));
    }

    @Test
    void getTotalHits_indexedDocuments_returnAmountOfIndexedDocuments() throws IOException, InterruptedException {
        indexService.updateMapping(INDEX, contactService.createMappingObject());
        contactService.createContact(INDEX, createContact());
        contactService.createContact(INDEX, createContact());
        contactService.createContact(INDEX, createContact());

        TimeUnit.SECONDS.sleep(2);
        Assertions.assertEquals(3, indexService.getTotalHits(INDEX));
    }

    private ContactDocument createContact() {
        return new ContactDocument("mabambam-" + UUID.randomUUID().toString(), "sut",
                                   Collections.singletonList(new Technology("angular", "2019")), "mab@mab.ch", "079");
    }

    @Disabled
    @Test
    void getIndex_indexDetails_returnAllDetailsAboutTheIndex() throws IOException, InterruptedException {
        indexService.updateMapping(INDEX, contactService.createMappingObject());
        GetIndexResponse index = indexService.getIndex(INDEX);

        Assertions.assertEquals(index.getIndices().length, 1);
        Assertions.assertEquals(index.getIndices()[0], INDEX);

        TimeUnit.SECONDS.sleep(2);
        index = indexService.getIndex(INDEX);

        Collection<String> values = index.getMappings()
                                         .get(INDEX)
                                         .getSourceAsMap()
                                         .values()
                                         .stream()
                                         .map(Object::toString)
                                         .collect(Collectors.toList());
        Collection<String> expectedValues = Arrays.asList("technologies", "firstName", "lastName");
        Assertions.assertTrue(values.containsAll(expectedValues));
    }
}
