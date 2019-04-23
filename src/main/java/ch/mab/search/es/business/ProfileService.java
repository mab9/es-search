package ch.mab.search.es.business;

import ch.mab.search.es.api.AbstractIndex;
import ch.mab.search.es.model.ProfileDocument;
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
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ProfileService extends AbstractIndex {

    private final String INDEX = "posts";

    public ProfileService() {
    }

    public Optional<ProfileDocument> createProfile(String index, ProfileDocument document) throws Exception {
        UUID uuid = UUID.randomUUID();
        document.setId(uuid.toString());

        String json = gson.toJson(document);

        IndexRequest request = new IndexRequest(index);
        request.id(document.getId());
        request.source(json, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        return findById(index, UUID.fromString(indexResponse.getId()));
    }

    public Optional<ProfileDocument> findById(String index, UUID id) throws IOException {
        GetRequest getRequest = new GetRequest(index, id.toString());
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        if (getResponse.getSource() != null) {
            return Optional.of(gson.fromJson(getResponse.getSource().toString(), ProfileDocument.class));
        } else {
            return Optional.empty();
        }
    }

    public Optional<ProfileDocument> updateProfile(String index, ProfileDocument document) throws IOException {
        Optional<ProfileDocument> current = findById(index, UUID.fromString(document.getId()));

        if (current.isEmpty()) {
            return Optional.empty();
        }

        String json = gson.toJson(document);
        UpdateRequest request = new UpdateRequest(INDEX, current.get().getId());
        request.doc(json, XContentType.JSON);
        UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);

        return findById(index, UUID.fromString(updateResponse.getId()));
    }

    public List<ProfileDocument> findAll(String index) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
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

    public Optional<ProfileDocument> deleteProfileDocument(String index, UUID id) throws IOException {
        Optional<ProfileDocument> current = findById(index, id);

        if (current.isEmpty()) {
            return current;
        }

        DeleteRequest deleteRequest = new DeleteRequest(INDEX, current.get().getId());
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        return current;
    }

    public XContentBuilder createMappingObject() throws IOException {
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
}