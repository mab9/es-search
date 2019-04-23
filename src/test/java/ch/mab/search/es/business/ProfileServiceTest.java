package ch.mab.search.es.business;

import ch.mab.search.es.model.ProfileDocument;
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

    private final String INDEX = this.getClass().getName().toLowerCase();

    @Autowired
    private ProfileService profileService;

    @Autowired
    private IndexService indexService;

    @BeforeEach
    public void setUp() throws IOException {
        if (indexService.isIndexExisting(INDEX)) {
            indexService.deleteIndex(INDEX);
        }
        indexService.createIndex(INDEX, profileService.createMappingObject());
    }

    @Test
    public void createProfile_createDocument_returnCreatedDocument() throws Exception {
        ProfileDocument document =
                new ProfileDocument( "mabambam", "mabam", Collections.emptyList(),
                                    Collections.emptyList());
        Optional<ProfileDocument> profile = profileService.createProfile(INDEX, document);
        Assertions.assertEquals(document, profile.get());
    }

    @Test
    public void findeAll_profileDocuments_returnAllCreatedDocuments() throws Exception {
        List<ProfileDocument> all = profileService.findAll(INDEX);
        Assertions.assertTrue(all.isEmpty());

        int amount = 100;

        for (int i = 0; i < amount; i++) {
            ProfileDocument document =
                    new ProfileDocument( "mabambam-" + UUID.randomUUID().toString(),
                                        "mabam", Collections.emptyList(), Collections.emptyList());
            profileService.createProfile(INDEX, document);
        }

        // elastic search is indexing async
        TimeUnit.SECONDS.sleep(2);
        long totalHits = indexService.getTotalHits(INDEX);
        Assertions.assertEquals(amount, totalHits);
    }

    @Test
    void findById_profileDocument_findExpectedDocument() throws IOException {
        ProfileDocument document = new ProfileDocument( "mabambam-" + UUID.randomUUID().toString(),
                                    "mabam", Collections.emptyList(), Collections.emptyList());
        profileService.createProfile(INDEX, document);
        Optional<ProfileDocument> expectedDocument = profileService.findById(INDEX, document.getId());

        Assertions.assertEquals(document, expectedDocument.get());
    }
}
