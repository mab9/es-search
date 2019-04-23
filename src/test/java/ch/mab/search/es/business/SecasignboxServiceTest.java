package ch.mab.search.es.business;

import ch.mab.search.es.model.DocumentState;
import ch.mab.search.es.model.SecasignboxDocument;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SecasignboxServiceTest {

    private final String INDEX = this.getClass().getName().toLowerCase();

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private IndexService indexService;

    @Autowired
    private SecasignboxService secasignboxService;

    @BeforeEach
    void setUp() throws IOException {
        if (indexService.isIndexExisting(INDEX)) {
            indexService.deleteIndex(INDEX);
        }
        indexService.createIndex(INDEX, secasignboxService.createMappingObject());
    }

    /*
        TODO bessere Lösung finden zum löschen der Daten nach den Tests.
     */
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

        indexService.createIndex(INDEX, secasignboxService.createMappingObject());

        indexExists = client.indices().exists(request, RequestOptions.DEFAULT);
        Assertions.assertTrue(indexExists);
    }

    @Test
    void indexDocument_createDocument_returnCreatedDocument() throws Exception {
        SecasignboxDocument document = createDocument("Donald Duck und seine Taler");
        Optional<SecasignboxDocument> result = secasignboxService.indexDocument(INDEX, document);
        Assertions.assertEquals(document, result.get());
    }

    private SecasignboxDocument createDocument(String name) {
        return new SecasignboxDocument(UUID.randomUUID(), name, new Date(), new Date(),
                                       Collections.emptyList(),
                                       "Der Glückstaler ist Onkel Dagoberts erste selbstverdiente Münze",
                                       DocumentState.SIGNED);
    }

    @Test
    void findAll_indexedDocuments_expectingCreatedDocuments () throws IOException, InterruptedException {
        List<SecasignboxDocument> all = secasignboxService.findAll(INDEX);
        Assertions.assertTrue(all.isEmpty());

        SecasignboxDocument document1 = createDocument("Donald Duck und seinen Glückstaler");
        SecasignboxDocument document2 = createDocument("Donald Duck und seine 3 Neffen");
        secasignboxService.indexDocument(INDEX, document1);
        secasignboxService.indexDocument(INDEX, document2);

        // elastic search is indexing async
        TimeUnit.SECONDS.sleep(2);
        all = secasignboxService.findAll(INDEX);
        Assertions.assertEquals(2, all.size());
        Assertions.assertTrue(all.contains(document1));
        Assertions.assertTrue(all.contains(document2));
    }

    @Test
    void findById_indexedDocuments_expectingCreatedDocuments () throws IOException, InterruptedException {
        SecasignboxDocument document1 = createDocument("Donald Duck und seinen Glückstaler");
        SecasignboxDocument document2 = createDocument("Donald Duck und seine 3 Neffen");
        secasignboxService.indexDocument(INDEX, document1);
        secasignboxService.indexDocument(INDEX, document2);

        // elastic search is indexing async
        TimeUnit.SECONDS.sleep(2);
        Optional<SecasignboxDocument> expected = secasignboxService.findById(INDEX, document1.getId());
        Assertions.assertEquals(document1, expected.get());
    }

    @Test
    void deleteDocument_indexedDocuments_expectingCreatedDocuments () throws IOException, InterruptedException {
        SecasignboxDocument document1 = createDocument("Donald Duck und seinen Glückstaler");
        SecasignboxDocument document2 = createDocument("Donald Duck und seine 3 Neffen");
        secasignboxService.indexDocument(INDEX, document1);
        secasignboxService.indexDocument(INDEX, document2);
        secasignboxService.deleteDocument(INDEX, document1.getId());

        // elastic search is indexing async
        TimeUnit.SECONDS.sleep(2);
        Optional<SecasignboxDocument> expected = secasignboxService.findById(INDEX, document1.getId());
        Assertions.assertTrue(expected.isEmpty());
    }

    @Test
    void updateDocument_indexedDocuments_expectingCreatedDocuments () throws IOException, InterruptedException {
        SecasignboxDocument document1 = createDocument("Donald Duck und sein Glückstaler");
        SecasignboxDocument document2 = createDocument("Donald Duck und seine 3 Neffen");
        secasignboxService.indexDocument(INDEX, document1);
        secasignboxService.indexDocument(INDEX, document2);

        document1.setDocumentName("Donald Duck und seinen Glückstaler");
        secasignboxService.updateDocument(INDEX, document1);

        // elastic search is indexing async
        TimeUnit.SECONDS.sleep(2);
        Optional<SecasignboxDocument> expected = secasignboxService.findById(INDEX, document1.getId());
        Assertions.assertEquals(document1, expected.get());
    }

    @Test
    void findByFuzzy_returnDocumentsContainingX() {

    }

}
