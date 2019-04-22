package ch.mab.search.es.business;

import ch.mab.search.es.model.ProfileDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class ProfileServiceTest {

    @Autowired
    private ProfileService service;

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
}
