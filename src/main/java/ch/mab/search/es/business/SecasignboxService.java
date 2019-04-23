package ch.mab.search.es.business;

import ch.mab.search.es.api.AbstractIndex;
import ch.mab.search.es.model.Metadata;
import ch.mab.search.es.model.SecasignboxDocument;
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
public class SecasignboxService extends AbstractIndex {

    public SecasignboxService() {
    }

    public Optional<SecasignboxDocument> indexDocument(String index, SecasignboxDocument document) throws IOException {
        String json = gson.toJson(document);

        IndexRequest request = new IndexRequest(index);
        request.id(document.getId().toString());
        request.source(json, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        return findById(index, UUID.fromString(indexResponse.getId()));
    }

    public Optional<SecasignboxDocument> findById(String index, UUID id) throws IOException {
        GetRequest getRequest = new GetRequest(index, id.toString());
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        if (getResponse.getSource() != null) {
            return Optional.of(gson.fromJson(getResponse.getSourceAsString(), SecasignboxDocument.class));
        } else {
            return Optional.empty();
        }
    }

    public List<SecasignboxDocument> findAll(String index) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse);
    }

    public List<SecasignboxDocument> searchByMetadata(String index, Metadata metadata) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        QueryBuilder queryBuilder =
                QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("metadata.value", metadata));

        searchSourceBuilder.query(QueryBuilders.nestedQuery("metadata", queryBuilder, ScoreMode.Avg));
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(response);
    }

    public Optional<SecasignboxDocument> deleteDocument(String index, UUID id) throws IOException {
        Optional<SecasignboxDocument> current = findById(index, id);

        if (current.isEmpty()) {
            return current;
        }

        DeleteRequest deleteRequest = new DeleteRequest(index, current.get().getId().toString());
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        return current;
    }

    public Optional<SecasignboxDocument> updateDocument(String index, SecasignboxDocument document) throws IOException {
        Optional<SecasignboxDocument> current = findById(index, document.getId());

        if (current.isEmpty()) {
            return Optional.empty();
        }

        String json = gson.toJson(document);
        UpdateRequest request = new UpdateRequest(index, current.get().getId().toString());
        request.doc(json, XContentType.JSON);
        UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);

        return findById(index, UUID.fromString(updateResponse.getId()));
    }

    private List<SecasignboxDocument> getSearchResult(SearchResponse response) {
        SearchHit[] searchHit = response.getHits().getHits();

        List<SecasignboxDocument> documents = new ArrayList<>();

        if (searchHit.length > 0) {
            Arrays.stream(searchHit)
                  .forEach(hit -> documents.add(
                          objectMapper.convertValue(hit.getSourceAsMap(), SecasignboxDocument.class)));
        }

        return documents;
    }

    @Override
    public XContentBuilder createMappingObject() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
            { builder.startObject("properties");
            { builder.startObject("id"); { builder.field("type", "text"); } builder.endObject(); }
            { builder.startObject("archivespaceId"); { builder.field("type", "text"); } builder.endObject(); }
            { builder.startObject("documentName"); { builder.field("type", "text"); } builder.endObject(); }
            { builder.startObject("uploadDate"); { builder.field("type", "date"); } builder.endObject(); }
            { builder.startObject("signDate"); { builder.field("type", "date"); } builder.endObject(); }
            { builder.startObject("documentContent"); { builder.field("type", "text"); } builder.endObject(); }
            { builder.startObject("metadatas"); {
                builder.startObject("properties");
                    { builder.startObject("value"); { builder.field("type", "text"); } builder.endObject(); }
                builder.endObject(); }
                builder.endObject(); }
            builder.endObject(); }
        builder.endObject();
        return builder;
    }
}
