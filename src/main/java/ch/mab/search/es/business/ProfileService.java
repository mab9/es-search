package ch.mab.search.es.business;

import ch.mab.search.es.document.ProfileDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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

    @Autowired
    public ProfileService() {}

    public String createProfile(ProfileDocument document) throws Exception {

        UUID uuid = UUID.randomUUID();
        document.setId(uuid.toString());

        String json = gson.toJson(document);

        IndexRequest request = new IndexRequest("posts");
        request.id(document.getId());
        request.source(json, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

        return indexResponse
                .getResult()
                .name();
    }

    public Optional<ProfileDocument> findById(UUID id) {
        GetRequest getRequest = new GetRequest("posts", id.toString());
        GetResponse getResponse = null;
        try {
            getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.of(gson.fromJson(getResponse.getSource().toString(), ProfileDocument.class));
    }

    public Optional<String> updateProfile(ProfileDocument document) {
        Optional<ProfileDocument> current = findById(UUID.fromString(document.getId()));

        if (current.isEmpty()) {
            return Optional.empty();
        }

        String json = gson.toJson(document);

        UpdateRequest request = new UpdateRequest("posts", current.get().getId());
        request.doc(json, XContentType.JSON);

        UpdateResponse updateResponse = null;
        try {
            updateResponse = client.update(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return Optional.of(updateResponse.getId());
    }

    public List<ProfileDocument> findAll() {


            SearchRequest searchRequest = new SearchRequest();
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getSearchResult(searchResponse);


    }

    private List<ProfileDocument> getSearchResult(SearchResponse response) {
        SearchHit[] searchHit = response.getHits().getHits();

        List<ProfileDocument> profileDocuments = new ArrayList<>();

        if (searchHit.length > 0) {

            Arrays.stream(searchHit)
                  .forEach(hit -> profileDocuments
                                   .add(objectMapper
                                                .convertValue(hit.getSourceAsMap(),
                                                              ProfileDocument.class))
                          );
        }

        return profileDocuments;
    }
}