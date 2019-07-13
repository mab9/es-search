package ch.mab.search.es.business;

import ch.mab.search.es.TestHelperService;
import ch.mab.search.es.model.ContactDocument;
import ch.mab.search.es.model.Technology;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    void createIndex_customAnalyzer_shouldTokenizeUnderscore() throws IOException {
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

        indexService.createIndex(INDEX, searchService.createMappingObjectWithAnalyzer("underscore_analyzer"), settings);
        GetIndexResponse index = indexService.getIndex(INDEX);
        Assertions.assertTrue(index.getSettings().get(INDEX).hasValue("index.analysis.analyzer.underscore_analyzer.tokenizer"));

        AnalyzeRequest request = new AnalyzeRequest(INDEX);
        request.analyzer("underscore_analyzer");
        request.text("2018_dagobert_duck taler");
        AnalyzeResponse analyze = client.indices().analyze(request, RequestOptions.DEFAULT);

        List<String> expectedTerms = analyze.getTokens().stream().map(AnalyzeResponse.AnalyzeToken::getTerm).collect(Collectors.toList());
        Assertions.assertTrue(expectedTerms.containsAll(Stream.of("2018", "dagobert", "duck taler").collect(Collectors.toList())));
    }

    @Test
    void createIndex_customAnalyzerAndDefaultLanguage_shouldTokenizeUnderscoreAndFilter() throws IOException {
        XContentBuilder settings = XContentFactory.jsonBuilder()
        .startObject()
                .startObject("analysis")
                    .startObject("filter")
                        .startObject("german_stop")
                            .field("type", "stop")
                            .field("stopwords", "_german_")
                        .endObject()
                        .startObject("german_stemmer")
                            .field("type", "stemmer")
                            .field("language", "light_german")
                        .endObject()
                    .endObject()
                    .startObject("analyzer")
                        .startObject("underscore_analyzer")
                            .field("type", "custom")
                            .field("tokenizer", "underscore_tokenizer")
                            .field("filter", "german_stop, german_stemmer")
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

        indexService.createIndex(INDEX, searchService.createMappingObjectWithAnalyzer("underscore_analyzer"), settings);
        GetIndexResponse index = indexService.getIndex(INDEX);
        Assertions.assertTrue(index.getSettings().get(INDEX).hasValue("index.analysis.analyzer.underscore_analyzer.tokenizer"));

        AnalyzeRequest request = new AnalyzeRequest(INDEX);
        request.analyzer("underscore_analyzer");
        request.text("2018_dagobert_duck taler", "Ein_Mann_der_Baden_geht,_ging heute_ein_Mal_gehend nach Hause zu seiner gehenden Oma.");
        AnalyzeResponse analyze = client.indices().analyze(request, RequestOptions.DEFAULT);

        List<String> expectedTerms = analyze.getTokens().stream().map(AnalyzeResponse.AnalyzeToken::getTerm).collect(Collectors.toList());
        Assertions.assertTrue(expectedTerms.containsAll(Stream.of("2018", "dagobert", "duck tal", "Ein","Mann", "Bad", "geht,", "ging heut", "Mal", "gehend nach Hause zu seiner gehenden Oma.").collect(Collectors.toList())));
    }

    @Test
    void createIndex_customAnalyzerAndDefaultLanguage_shouldTokenizeUnderscoreWhitespaceAndFilter() throws IOException {
        XContentBuilder settings = XContentFactory.jsonBuilder()
        .startObject()
                .startObject("analysis")
                    .startObject("filter")
                        .startObject("german_stop")
                            .field("type", "stop")
                            .field("stopwords", "_german_")
                        .endObject()
                        .startObject("german_stemmer")
                            .field("type", "stemmer")
                            .field("language", "light_german")
                        .endObject()
                    .endObject()
                    .startObject("analyzer")
                        .startObject("underscore_analyzer")
                            .field("type", "custom")
                            .field("tokenizer", "underscore_tokenizer")
                            .field("filter", "lowercase, german_stop, german_stemmer")
                        .endObject()
                    .endObject()
                    .startObject("tokenizer")
                        .startObject("underscore_tokenizer")
                            .field("type", "char_group")
                            .field("tokenize_on_chars", "_,whitespace")
                        .endObject()
                    .endObject()
                .endObject()
            .endObject();

        if (indexService.isIndexExisting(INDEX)) {
            indexService.deleteIndex(INDEX);
        }

        indexService.createIndex(INDEX, searchService.createMappingObjectWithAnalyzer("underscore_analyzer"), settings);
        GetIndexResponse index = indexService.getIndex(INDEX);
        Assertions.assertTrue(index.getSettings().get(INDEX).hasValue("index.analysis.analyzer.underscore_analyzer.tokenizer"));

        AnalyzeRequest request = new AnalyzeRequest(INDEX);
        request.analyzer("underscore_analyzer");
        request.text("2018_dagobert_duck taler", "Ein Mann der Baden geht, ging heute gehend nach Hause zu seiner gehenden Oma.");
        AnalyzeResponse analyze = client.indices().analyze(request, RequestOptions.DEFAULT);

        List<String> expectedTerms = analyze.getTokens().stream().map(AnalyzeResponse.AnalyzeToken::getTerm).collect(Collectors.toList());
        Assertions.assertTrue(expectedTerms.containsAll(Stream.of("2018", "dagobert", "duck", "tal","mann", "bad", "geht,", "ging", "heut", "gehend", "haus", "gehend", "oma.").collect(Collectors.toList())));
    }

     @Test
    void createIndex_rebuildStandardAnalyzer_shouldTokenizeExtendStandardAnalyzer() throws IOException {
        XContentBuilder settings = XContentFactory.jsonBuilder()
        .startObject()
                .startObject("analysis")
                    .startObject("filter")
                        .startObject("german_stop")
                            .field("type", "stop")
                            .field("stopwords", "_german_")
                        .endObject()
                        .startObject("german_stemmer")
                            .field("type", "stemmer")
                            .field("language", "light_german")
                        .endObject()
                    .endObject()
                    .startObject("analyzer")
                        .startObject("rebuilt_standard")
                            .field("type", "custom")
                            .field("tokenizer", "standard")
                            .field("filter", "lowercase, german_stop, german_stemmer")
                        .endObject()
                    .endObject()
                .endObject()
            .endObject();

        if (indexService.isIndexExisting(INDEX)) {
            indexService.deleteIndex(INDEX);
        }

        indexService.createIndex(INDEX, searchService.createMappingObjectWithAnalyzer("rebuilt_standard"), settings);
        GetIndexResponse index = indexService.getIndex(INDEX);
        Assertions.assertTrue(index.getSettings().get(INDEX).hasValue("index.analysis.analyzer.rebuilt_standard.tokenizer"));

        AnalyzeRequest request = new AnalyzeRequest(INDEX);
        request.analyzer("rebuilt_standard");
        request.text("Ein Mann schwimmt am Abend zu einem Schwimmbad. Schwimmend trifft er auf zwei Schwimmenden. Ist das möglich?");
        AnalyzeResponse analyze = client.indices().analyze(request, RequestOptions.DEFAULT);

        List<String> expectedTerms = analyze.getTokens().stream().map(AnalyzeResponse.AnalyzeToken::getTerm).collect(Collectors.toList());
        Assertions.assertTrue(expectedTerms.containsAll(Stream.of("mann", "schwimmt", "abend", "schwimmbad", "schwimmend", "trifft", "zwei", "schwimmend", "moglich").collect(Collectors.toList())));
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
