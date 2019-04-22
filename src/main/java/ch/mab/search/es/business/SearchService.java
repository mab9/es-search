package ch.mab.search.es.business;

import ch.mab.search.es.model.ProfileDocument;
import ch.mab.search.es.model.SecasignboxDocument;
import ch.mab.search.secasignbox.model.Metadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SearchService {

    @Autowired
    private RestHighLevelClient client;

    private ObjectMapper objectMapper = new ObjectMapper();

    public SearchService() {
    }

    public Optional<SecasignboxDocument> createSecasignboxDocument(SecasignboxDocument document) {
        return null;
    }

    public Optional<SecasignboxDocument> findById(UUID id) {
        return null;
    }

    public List<SecasignboxDocument> findAll() {
        return null;
    }

    public List<SecasignboxDocument> searchByMetadata(Metadata metadata) {
        return null;
    }

    public Optional<SecasignboxDocument> deleteSecasignboxDocument(UUID id) {
        return null;
    }

    public Optional<SecasignboxDocument> updateSecasignboxDocument(SecasignboxDocument document) {
        return null;
    }
}
