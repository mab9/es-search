package ch.mab.search.es.business;

import ch.mab.search.es.model.DocumentState;
import ch.mab.search.es.model.ProfileDocument;
import ch.mab.search.es.model.SecasignboxDocument;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        indexService.createIndex(INDEX);
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
    public void createSecasignboxDocument_createDocument_returnCreatedDocument() throws Exception {
        indexService.updateMapping(INDEX, secasignboxService.createMappingObject());
        SecasignboxDocument document = createDocument();
        Optional<SecasignboxDocument> result = secasignboxService.createSecasignboxDocument(INDEX, document);
        Assertions.assertEquals(document, result.get());
    }

    private SecasignboxDocument createDocument() {
        return new SecasignboxDocument(UUID.randomUUID(), "Donald Duck und seine Taler", new Date(), new Date(),
                                       Collections.emptyList(),
                                       "Der Glückstaler ist Onkel Dagoberts erste selbstverdiente Münze",
                                       DocumentState.SIGNED);
    }
}
