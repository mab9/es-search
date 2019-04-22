package ch.mab.search.es.business;

import ch.mab.search.es.model.ProfileDocument;
import ch.mab.search.es.model.SecasignboxDocument;
import ch.mab.search.secasignbox.model.Metadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SecasignboxService {

    @Autowired
    private RestHighLevelClient client;

    private final Gson gson = new Gson();

    @Autowired
    private ObjectMapper objectMapper;

    private final String INDEX = "secasignbox";

    public SecasignboxService() {
    }

    public Optional<SecasignboxDocument> createSecasignboxDocument(SecasignboxDocument document) throws IOException {
        UUID uuid = UUID.randomUUID();
        document.setId(uuid);

        String json = gson.toJson(document);

        IndexRequest request = new IndexRequest(INDEX);
        request.id(document.getId().toString());
        request.source(json, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        return findById(UUID.fromString(indexResponse.getId()));
    }

    public Optional<SecasignboxDocument> findById(UUID id) throws IOException {
        GetRequest getRequest = new GetRequest(INDEX, id.toString());
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        if (getResponse.getSource() != null) {
            return Optional.of(gson.fromJson(getResponse.getSource().toString(), SecasignboxDocument.class));
        } else {
            return Optional.empty();
        }
    }

    public List<SecasignboxDocument> findAll() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse);    }

    public List<SecasignboxDocument> searchByMetadata(Metadata metadata) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        QueryBuilder queryBuilder =
                QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("metadata.value", metadata));

        searchSourceBuilder.query(QueryBuilders.nestedQuery("metadata", queryBuilder, ScoreMode.Avg));
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(response);
    }

    public Optional<SecasignboxDocument> deleteSecasignboxDocument(UUID id) throws IOException {
        Optional<SecasignboxDocument> current = findById(id);

        if (current.isEmpty()) {
            return current;
        }

        DeleteRequest deleteRequest = new DeleteRequest(INDEX, current.get().getId().toString());
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        return current;
    }

    public Optional<SecasignboxDocument> updateSecasignboxDocument(SecasignboxDocument document) throws IOException {
        Optional<SecasignboxDocument> current = findById(document.getId());

        if (current.isEmpty()) {
            return Optional.empty();
        }

        String json = gson.toJson(document);
        UpdateRequest request = new UpdateRequest(INDEX, current.get().getId().toString());
        request.doc(json, XContentType.JSON);
        UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);

        return findById(UUID.fromString(updateResponse.getId()));
    }

    private List<SecasignboxDocument> getSearchResult(SearchResponse response) {
        SearchHit[] searchHit = response.getHits().getHits();

        List<SecasignboxDocument> profileDocuments = new ArrayList<>();

        if (searchHit.length > 0) {
            Arrays.stream(searchHit)
                  .forEach(hit -> profileDocuments.add(
                          objectMapper.convertValue(hit.getSourceAsMap(), SecasignboxDocument.class)));
        }

        return profileDocuments;
    }
}
