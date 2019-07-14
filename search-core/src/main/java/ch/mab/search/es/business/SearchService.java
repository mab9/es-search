package ch.mab.search.es.business;

import ch.mab.search.es.api.AbstractIndex;
import ch.mab.search.es.base.IndexMappingSetting;
import ch.mab.search.es.model.SearchStrike;
import ch.mab.search.es.model.SearchQuery;
import ch.mab.search.es.model.SecasignboxDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ch.mab.search.es.base.IndexMappingSetting.*;

@Service
public class SearchService extends AbstractIndex {

    @Autowired
    private ComposQueryService composQueryService;

    public SearchService() {
    }

    public Optional<SecasignboxDocument> indexDocument(String index, SecasignboxDocument document) throws IOException {
        IndexRequest request = createIndexRequest(index, document);
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        return findById(index, UUID.fromString(indexResponse.getId()));
    }

    public BulkResponse bulkIndexDocument(String index, Collection<SecasignboxDocument> documents) throws IOException {

        BulkRequest bulkRequest = new BulkRequest();
        documents.forEach(document -> {
            IndexRequest indexRequest = createIndexRequest(index, document);
            bulkRequest.add(indexRequest);
        });

        return client.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    private IndexRequest createIndexRequest(String index, SecasignboxDocument document) {
        IndexRequest request = new IndexRequest(index);
        request.id(document.getDocumentId().toString());
        String json = null;
        try {
            json = objectMapper.writeValueAsString(document);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        request.source(json, XContentType.JSON);
        return request;
    }

    public Optional<SecasignboxDocument> deleteDocument(String index, UUID id) throws IOException {
        Optional<SecasignboxDocument> current = findById(index, id);

        if (current.isEmpty()) {
            return current;
        }

        DeleteRequest deleteRequest = new DeleteRequest(index, current.get().getDocumentId().toString());
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        return current;
    }

    public Optional<SecasignboxDocument> findById(String index, UUID id) throws IOException {
        GetRequest getRequest = new GetRequest(index, id.toString());
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        if (getResponse.getSource() != null) {
            return Optional.of(objectMapper.readValue(getResponse.getSourceAsString(), SecasignboxDocument.class));
        } else {
            return Optional.empty();
        }
    }

    public List<SecasignboxDocument> findAll(String index) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchAllQueryBuilder matchAllQueryBuilder = new MatchAllQueryBuilder();
        searchSourceBuilder.query(matchAllQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse);
    }

    public List<SearchStrike> findByQueryHighlighted(String index, SearchQuery searchQuery) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        HighlightBuilder highlightBuilder = createHighlighter(FIELD_SECASIGN_DOC_NAME, FIELD_SECASIGN_DOC_CONTENT, FIELD_SECASIGN_DOC_UPLOAD_DATE);
        sourceBuilder.highlighter(highlightBuilder);

        QueryBuilder query = composQueryService.composeQuery(searchQuery);
        sourceBuilder.query(query);
        // sort descending by score
        sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchStrikes(searchResponse);
    }

    public List<SearchStrike> findByTermHighlighted(String index, String term) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        HighlightBuilder highlightBuilder = createHighlighter(FIELD_SECASIGN_DOC_CONTENT, FIELD_SECASIGN_DOC_NAME);
        sourceBuilder.highlighter(highlightBuilder);

        QueryBuilder query = composQueryService.composeMatchAllQuery(term);
        sourceBuilder.query(query);
        // sort descending by score
        sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchStrikes(searchResponse);
    }

    public  List<SearchStrike> queryByTerm(String index, String term) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        HighlightBuilder highlightBuilder = createHighlighter( FIELD_SECASIGN_DOC_NAME);
        sourceBuilder.highlighter(highlightBuilder);

        QueryBuilder query =  QueryBuilders.matchQuery(FIELD_SECASIGN_DOC_NAME, term);
        sourceBuilder.query(query);

        searchRequest.source(sourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchStrikes(search);
    }

    public  List<SearchStrike> queryPhraseByTerm(String index, String term) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        HighlightBuilder highlightBuilder = createHighlighter( FIELD_SECASIGN_DOC_NAME);
        sourceBuilder.highlighter(highlightBuilder);

        // slop -> amount of missing words between two word in a term
        QueryBuilder query = QueryBuilders.matchPhraseQuery(FIELD_SECASIGN_DOC_NAME, term).slop(10);
        sourceBuilder.query(query);

        searchRequest.source(sourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchStrikes(search);
    }

    public  List<SearchStrike> queryPhraseFuzzyByTerm(String index, String term) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        HighlightBuilder highlightBuilder = createHighlighter( FIELD_SECASIGN_DOC_NAME);
        sourceBuilder.highlighter(highlightBuilder);

        QueryBuilder phraseQuery = QueryBuilders.matchPhraseQuery(FIELD_SECASIGN_DOC_NAME, term).slop(10);
        QueryBuilder fuzzyQuery = QueryBuilders.matchQuery(FIELD_SECASIGN_DOC_NAME, term).fuzziness(Fuzziness.AUTO).maxExpansions(50);

        QueryBuilder query = QueryBuilders
                .boolQuery()
                    .should(phraseQuery)
                    .should(fuzzyQuery);

        sourceBuilder.query(query);
        searchRequest.source(sourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchStrikes(search);
    }

    private HighlightBuilder createHighlighter(String... fields) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();

        Arrays.stream(fields).forEach(field -> {
            HighlightBuilder.Field hgField2 = new HighlightBuilder.Field(field);
            hgField2.highlighterType("unified");
            highlightBuilder.field(hgField2);
        });
        return highlightBuilder;
    }
    private List<SearchStrike> getSearchStrikes(SearchResponse response) {
        SearchHit[] searchHit = response.getHits().getHits();

        List<SearchStrike> dtos = new ArrayList<>();

        for (SearchHit hit : searchHit) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            List<String> highlights = new ArrayList<>();
            highlights.addAll(getHighlights(highlightFields, FIELD_SECASIGN_DOC_CONTENT));
            highlights.addAll(0, getHighlights(highlightFields, FIELD_SECASIGN_DOC_NAME));
            highlights.addAll(0, getHighlights(highlightFields, FIELD_SECASIGN_DOC_UPLOAD_DATE));

            SecasignboxDocument document = objectMapper.convertValue(hit.getSourceAsMap(), SecasignboxDocument.class);
            SearchStrike dto = new SearchStrike();
            dto.setScore(hit.getScore());
            dto.setDocumentId(document.getDocumentId());
            dto.setDocumentName(document.getDocumentName());
            dto.setHighlights(replaceHighlihtCursivByBold(highlights));
            dtos.add(dto);
        }

        return dtos.stream().distinct().collect(Collectors.toList());
    }

    private List<String> getHighlights(Map<String, HighlightField> highlightFields, String field) {
        HighlightField highlight = highlightFields.get(field);
        if (highlight == null) {
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
}
