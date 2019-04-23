package ch.mab.search.es.business;

import ch.mab.search.es.model.ProfileDocument;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class IndexServiceTest {

    private final String INDEX = this.getClass().getName().toLowerCase();

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private IndexService indexService;

    @Autowired
    private ProfileService profileService;

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

        indexService.createIndex(INDEX, profileService.createMappingObject());

        indexExists = client.indices().exists(request, RequestOptions.DEFAULT);
        Assertions.assertTrue(indexExists);
    }

    @Test
    void updateMapping_updateIndex_returnOkResponse() throws IOException {
        Assertions.assertTrue(indexService.isIndexExisting(INDEX));
        AcknowledgedResponse acknowledgedResponse = indexService.updateMapping(INDEX, profileService.createMappingObject());
        Assertions.assertTrue(acknowledgedResponse.isAcknowledged());
    }

    @Test
    void deleteIndex_indexNotExisting_noExistingIndex() throws Exception {
        Assertions.assertTrue(indexService.isIndexExisting(INDEX));
        indexService.deleteIndex(INDEX);
        Assertions.assertFalse(indexService.isIndexExisting(INDEX));
    }

    @Test
    void getTotalHits_indexedDocuments_amountOfIndexedDocuments() throws IOException, InterruptedException {
        indexService.updateMapping(INDEX, profileService.createMappingObject());
        ProfileDocument document =
                new ProfileDocument("rabar", "barbara", "papap", Collections.emptyList(), Collections.emptyList());
        profileService.createProfile(INDEX, document);
        profileService.createProfile(INDEX, document);
        profileService.createProfile(INDEX, document);

        TimeUnit.SECONDS.sleep(2);
        Assertions.assertEquals(3, indexService.getTotalHits(INDEX));
    }
}
