package ch.mab.search.es.business;

import ch.mab.search.es.api.AbstractIndex;
import ch.mab.search.es.model.ContactDocument;
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
public class ContactService extends AbstractIndex {

    private final String INDEX = "contacts";

    public ContactService() {
    }

    public Optional<ContactDocument> createContact(String index, ContactDocument document) throws IOException {
        String json = objectMapper.writeValueAsString(document);
        IndexRequest request = new IndexRequest(index);
        request.id(document.getId().toString());
        request.source(json, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        return findById(index, UUID.fromString(indexResponse.getId()));
    }

    public Optional<ContactDocument> findById(String index, UUID id) throws IOException {
        GetRequest getRequest = new GetRequest(index, id.toString());
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        if (getResponse.getSource() != null) {
            return Optional.of(objectMapper.convertValue(getResponse.getSource().toString(), ContactDocument.class));
        } else {
            return Optional.empty();
        }
    }

    public Optional<ContactDocument> updateContact(String index, ContactDocument document) throws IOException {
        Optional<ContactDocument> current = findById(index, document.getId());

        if (current.isEmpty()) {
            return Optional.empty();
        }

        String json = objectMapper.writeValueAsString(document);
        UpdateRequest request = new UpdateRequest(INDEX, current.get().getId().toString());
        request.doc(json, XContentType.JSON);
        UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);

        return findById(index, UUID.fromString(updateResponse.getId()));
    }

    public List<ContactDocument> findAll(String index) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse);
    }

    private List<ContactDocument> getSearchResult(SearchResponse response) {
        SearchHit[] searchHit = response.getHits().getHits();

        List<ContactDocument> contactDocuments = new ArrayList<>();

        if (searchHit.length > 0) {
            Arrays.stream(searchHit)
                  .forEach(hit -> contactDocuments.add(
                          objectMapper.convertValue(hit.getSourceAsMap(), ContactDocument.class)));
        }

        return contactDocuments;
    }

    public List<ContactDocument> searchByTechnology(String technology) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        QueryBuilder queryBuilder =
                QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("technologies.name", technology));

        searchSourceBuilder.query(QueryBuilders.nestedQuery("technologies", queryBuilder, ScoreMode.Avg));
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(response);
    }

    public Optional<ContactDocument> deleteContactDocument(String index, UUID id) throws IOException {
        Optional<ContactDocument> current = findById(index, id);

        if (current.isEmpty()) {
            return current;
        }

        DeleteRequest deleteRequest = new DeleteRequest(INDEX, current.get().getId().toString());
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