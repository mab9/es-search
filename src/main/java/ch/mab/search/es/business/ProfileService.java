package ch.mab.search.es.business;

import ch.mab.search.es.model.ProfileDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
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
public class ProfileService {

    @Autowired
    private RestHighLevelClient client;

    private final Gson gson = new Gson();

    @Autowired
    private ObjectMapper objectMapper;

    private final String INDEX = "posts";

    public ProfileService() {
    }

    public Optional<ProfileDocument> createProfile(ProfileDocument document) throws Exception {
        UUID uuid = UUID.randomUUID();
        document.setId(uuid.toString());

        String json = gson.toJson(document);

        IndexRequest request = new IndexRequest(INDEX);
        request.id(document.getId());
        request.source(json, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        return findById(UUID.fromString(indexResponse.getId()));
    }

    public Optional<ProfileDocument> findById(UUID id) throws IOException {
        GetRequest getRequest = new GetRequest(INDEX, id.toString());
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        if (getResponse.getSource() != null) {
            return Optional.of(gson.fromJson(getResponse.getSource().toString(), ProfileDocument.class));
        } else {
            return Optional.empty();
        }
    }

    public Optional<ProfileDocument> updateProfile(ProfileDocument document) throws IOException {
        Optional<ProfileDocument> current = findById(UUID.fromString(document.getId()));

        if (current.isEmpty()) {
            return Optional.empty();
        }

        String json = gson.toJson(document);
        UpdateRequest request = new UpdateRequest(INDEX, current.get().getId());
        request.doc(json, XContentType.JSON);
        UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);

        return findById(UUID.fromString(updateResponse.getId()));
    }

    public List<ProfileDocument> findAll() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse);
    }

    private List<ProfileDocument> getSearchResult(SearchResponse response) {
        SearchHit[] searchHit = response.getHits().getHits();

        List<ProfileDocument> profileDocuments = new ArrayList<>();

        if (searchHit.length > 0) {
            Arrays.stream(searchHit)
                  .forEach(hit -> profileDocuments.add(
                          objectMapper.convertValue(hit.getSourceAsMap(), ProfileDocument.class)));
        }

        return profileDocuments;
    }

    public List<ProfileDocument> searchByTechnology(String technology) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        QueryBuilder queryBuilder =
                QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("technologies.name", technology));

        searchSourceBuilder.query(QueryBuilders.nestedQuery("technologies", queryBuilder, ScoreMode.Avg));
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(response);
    }

    public Optional<ProfileDocument> deleteProfileDocument(UUID id) throws IOException {
        Optional<ProfileDocument> current = findById(id);

        if (current.isEmpty()) {
            return current;
        }

        DeleteRequest deleteRequest = new DeleteRequest(INDEX, current.get().getId());
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        return current;
    }

    public CreateIndexResponse createProfileIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(INDEX);
        appendSettings(request);
        request.mapping(createMappingObject());
        return client.indices().create(request, RequestOptions.DEFAULT);
    }

    private XContentBuilder createMappingObject() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            { builder.startObject("firstName"); { builder.field("type", "text"); } builder.endObject(); }
            { builder.startObject("lastName"); { builder.field("type", "text"); } builder.endObject(); }
            { builder.startObject("technologies"); { builder.field("type", "nested"); } builder.endObject(); }
            builder.endObject(); }
        builder.endObject();
        return builder;
    }

    private void appendSettings(CreateIndexRequest request) {
        request.settings(Settings.builder()
                                 .put("index.number_of_shards", 3)
                                 .put("index.number_of_replicas", 2));
    }

    public AcknowledgedResponse updateMapping() throws IOException {
        PutMappingRequest request = new PutMappingRequest(INDEX);
        request.source(createMappingObject());
        return client.indices().putMapping(request, RequestOptions.DEFAULT);
    }

    public AcknowledgedResponse deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(INDEX);
        return client.indices().delete(request, RequestOptions.DEFAULT);
    }

    public boolean getIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest(INDEX);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }
}