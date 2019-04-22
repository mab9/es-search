package ch.mab.search.es.business;

import ch.mab.search.es.document.SearchDoc;
import ch.mab.search.es.document.SearchQuery;
import ch.mab.search.secasignbox.model.Archivespace;
import ch.mab.search.secasignbox.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.elasticsearch.action.update.UpdateHelper.ContextFields.INDEX;
import static org.elasticsearch.cluster.SnapshotsInProgress.TYPE;

@Service
public class SearchService {

    @Autowired
    private RestHighLevelClient client;

    private ObjectMapper objectMapper = new ObjectMapper();

    public SearchService() {
    }

    public boolean saveDoc(String doc) {
        System.out.println(doc);
        return true;
    }

    public String createDoc(SearchDoc doc, Archivespace archivespace, User user) throws IOException {
        UUID uuid = UUID.randomUUID();
        doc.setId(uuid);

        Map<String, Object> documentMapper = objectMapper.convertValue(doc, Map.class);

        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, doc.getId().toString())
                .source(documentMapper);

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);

        return indexResponse
                .getResult()
                .name();
    }

    public String updateDoc(SearchDoc searchDoc, User user) {
        return null;
    }

    public String deleteDoc(Long docId, User user) {
        return null;
    }

    public List<SearchDoc> searchByQuery(SearchQuery searchQuery, User user) {
        return new ArrayList<>();
    }
}
