package ch.mab.search.es.business;

import ch.mab.search.es.api.AbstractIndex;
import ch.mab.search.es.model.Metadata;
import ch.mab.search.es.model.SearchHighlights;
import ch.mab.search.es.model.SecasignboxDocument;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
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
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SecasignboxService extends AbstractIndex {

    public SecasignboxService() {
    }

    public Optional<SecasignboxDocument> indexDocument(String index, SecasignboxDocument document) throws IOException {
        IndexRequest request = createIndexRequest(index, document);
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        return findById(index, UUID.fromString(indexResponse.getId()));
    }

    public BulkResponse bulkIndexDocument(String index, Collection<SecasignboxDocument> documents) throws
            IOException {

        BulkRequest bulkRequest = new BulkRequest();
        documents.forEach(document -> {
            IndexRequest indexRequest = createIndexRequest(index, document);
            bulkRequest.add(indexRequest);
        });

        return client.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    private IndexRequest createIndexRequest(String index, SecasignboxDocument document) {
        IndexRequest request = new IndexRequest(index);
        request.id(document.getId().toString());
        String json = gson.toJson(document);
        request.source(json, XContentType.JSON);
        return request;
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

    public List<SearchHighlights> findByQueryInDocumentContentHighlighted(String index, String query) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        MatchQueryBuilder docContentQuery = new MatchQueryBuilder("documentContent", query);
        docContentQuery.fuzziness(Fuzziness.AUTO);
        docContentQuery.prefixLength(3);
        docContentQuery.maxExpansions(10);

        MatchPhrasePrefixQueryBuilder documentName = QueryBuilders.matchPhrasePrefixQuery("documentName", query);
        documentName.boost(4.0f);

        BoolQueryBuilder should = QueryBuilders.boolQuery().should(documentName).should(docContentQuery);
        sourceBuilder.query(should);
        searchRequest.source(sourceBuilder);

        // sort descending by score
        sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));

        HighlightBuilder highlightBuilder = createHighlighter("documentContent", "unified");
        sourceBuilder.highlighter(highlightBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResultHighlighted(searchResponse);
    }

    private HighlightBuilder createHighlighter(String field, String type) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field hgField = new HighlightBuilder.Field(field);
        hgField.highlighterType(type);
        highlightBuilder.field(hgField);

        HighlightBuilder.Field hgField2 = new HighlightBuilder.Field("documentName");
        hgField2.highlighterType(type);
        highlightBuilder.field(hgField2);
        return highlightBuilder;
    }

    public List<SecasignboxDocument> findByQueryInDocumentContent(String index, String query) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("documentContent",query));
        searchRequest.source(sourceBuilder);
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

    public List<SecasignboxDocument> searchByDocumentName(String index, String documentName) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        MatchQueryBuilder matchQuery = new MatchQueryBuilder("documentName", documentName);

        sourceBuilder.query(matchQuery);
        searchRequest.source(sourceBuilder);

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

    private List<SearchHighlights> getSearchResultHighlighted(SearchResponse response) {
        SearchHit[] searchHit = response.getHits().getHits();

        List<SearchHighlights> dtos = new ArrayList<>();

        for (SearchHit hit : searchHit) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            List<String> highlights = new ArrayList<>();
            highlights.addAll(getHighlights(highlightFields, "documentContent"));
            highlights.addAll(getHighlights(highlightFields, "documentName"));

            SearchHighlights dto = new SearchHighlights();
            SecasignboxDocument document =
                    objectMapper.convertValue(hit.getSourceAsMap(), SecasignboxDocument.class);
            dto.setDocumentId(document.getId());
            dto.setDocumentName(document.getDocumentName());
            dto.setHighlights(replaceHighlihtCursivByBold(highlights));
            dtos.add(dto);
        }

        return dtos.stream().distinct().collect(Collectors.toList());
    }

    private List<String> getHighlights(Map<String, HighlightField> highlightFields, String field) {
        HighlightField highlight = highlightFields.get(field);
        if (highlight == null) {
            System.err.println("you missed something");
            return Collections.emptyList();
        }
        Text[] fragments = highlight.fragments();
        return Arrays.stream(fragments).map(Text::string).collect(Collectors.toList());
    }

    private List<String> replaceHighlihtCursivByBold(List<String> highlights) {
        return highlights.stream().map(highlight -> {
            highlight = highlight.replace("<em>", "<b>");
            return highlight.replace("</em>", "</b>");
        }).collect(Collectors.toList());
    }

    private List<SecasignboxDocument> getSearchResult(SearchResponse response) {
        SearchHit[] searchHit = response.getHits().getHits();
        List<SecasignboxDocument> documents = new ArrayList<>();

        for (SearchHit hit : searchHit) {
            documents.add(objectMapper.convertValue(hit.getSourceAsMap(), SecasignboxDocument.class));
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
