package ch.mab.search.es.business;

import ch.mab.search.es.model.ProfileDocument;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class ProfileServiceTest {

    @Autowired
    private ProfileService service;

    @BeforeEach
    public void setUp() throws IOException {
        boolean indexExists = service.getIndex();
        if (indexExists) {
            service.deleteIndex();
        }

        service.createProfileIndex();
    }

    @Test
    public void createProfile_createDocument_returnCreatedDocument() throws Exception {
        ProfileDocument document =
                new ProfileDocument(UUID.randomUUID().toString(), "mabambam", "mabam", Collections.emptyList(),
                                    Collections.emptyList());
        Optional<ProfileDocument> profile = service.createProfile(document);
        Assertions.assertEquals(document, profile.get());
    }

    @Test
    public void updateMapping_updateIndex_returnOkResponse() throws Exception {
        AcknowledgedResponse acknowledgedResponse = service.updateMapping();
        Assertions.assertTrue(acknowledgedResponse.isAcknowledged());
    }

    @Test
    public void deleteIndex_index_noExistingIndex() throws Exception {
        Assertions.assertTrue(service.getIndex());
        service.deleteIndex();
        Assertions.assertFalse(service.getIndex());
    }

    @Test
    public void findeAll_profileDocuments_returnAllCreatedDocuments() throws Exception {
        List<ProfileDocument> all = service.findAll();
        Assertions.assertTrue(all.isEmpty());

        int amount = 500;

        for (int i = 0; i < amount; i++) {
            ProfileDocument document =
                    new ProfileDocument(UUID.randomUUID().toString(), "mabambam-" + UUID.randomUUID().toString(),
                                        "mabam", Collections.emptyList(), Collections.emptyList());
            service.createProfile(document);
        }

        // elastic search is indexing async
        TimeUnit.SECONDS.sleep(2);
        long totalHits = service.getTotalHits();
        Assertions.assertEquals(amount, totalHits);
    }
}
