package ch.mab.search.es.business;

import ch.mab.search.es.model.ElasticsearchModel;
import ch.mab.search.es.model.ContactDocument;
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
public class ContactServiceTest {

    private final String INDEX = this.getClass().getName().toLowerCase();

    @Autowired
    private ContactService contactService;

    @Autowired
    private IndexService indexService;

    @BeforeEach
    public void setUp() throws IOException {
        if (indexService.isIndexExisting(INDEX)) {
            indexService.deleteIndex(INDEX);
        }
        indexService.createIndex(INDEX, ElasticsearchModel.mappingDefaultContactDoc());
    }

    @Test
    public void createProfile_createDocument_returnCreatedDocument() throws Exception {
        ContactDocument document = createContact();
        Optional<ContactDocument> contact = contactService.createContact(INDEX, document);
        Assertions.assertEquals(document, contact.get());
    }

    private ContactDocument createContact() {
        return new ContactDocument("mabambam-" + UUID.randomUUID().toString(), "sut", Collections.emptyList(),
                                   "mab@mab.ch", "079");
    }

    @Test
    public void findeAll_profileDocuments_returnAllCreatedDocuments() throws Exception {
        List<ContactDocument> all = contactService.findAll(INDEX);
        Assertions.assertTrue(all.isEmpty());

        int amount = 100;

        for (int i = 0; i < amount; i++) {
            ContactDocument document = createContact();
            contactService.createContact(INDEX, document);
        }

        // elastic search is indexing async
        TimeUnit.SECONDS.sleep(2);
        long totalHits = indexService.getTotalDocuments(INDEX);
        Assertions.assertEquals(amount, totalHits);
    }

    @Test
    void findById_profileDocument_findExpectedDocument() throws IOException {
        ContactDocument document = createContact();
        contactService.createContact(INDEX, document);
        Optional<ContactDocument> expectedDocument = contactService.findById(INDEX, document.getId());

        Assertions.assertEquals(document, expectedDocument.get());
    }
}
