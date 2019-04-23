package ch.mab.search.es.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class IndexServiceTest {

    private final String INDEX = this.getClass().getName().toLowerCase();

    @Autowired
    private IndexService service;

    @BeforeEach
    public void setUp() throws IOException {
        if (service.isIndexExisting(INDEX)) {
            service.deleteIndex(INDEX);
        }
        service.createIndex(INDEX);
    }

    @Test
    public void updateMapping_updateIndex_returnOkResponse() throws Exception {
        //AcknowledgedResponse acknowledgedResponse = service.updateMapping();
        //Assertions.assertTrue(acknowledgedResponse.isAcknowledged());
    }

    @Test
    public void deleteIndex_indexNotExisting_noExistingIndex() throws Exception {
        Assertions.assertTrue(service.isIndexExisting(INDEX));
        service.deleteIndex(INDEX);
        Assertions.assertFalse(service.isIndexExisting(INDEX));
    }
}
