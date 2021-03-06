package ch.mab.search.es.business;

import ch.mab.search.es.api.AbstractIndex;
import ch.mab.search.es.model.SearchQuery;
import ch.mab.search.es.model.SearchStrike;
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
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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

import static ch.mab.search.es.base.SecasignBoxConstants.*;

@Service
public class SearchService extends AbstractIndex {

    @Autowired
    private SearchQueryService searchQueryService;

    public SearchService() {
    }

    public Optional<SecasignboxDocument> indexDocument(String index, SecasignboxDocument document) throws IOException {
        Objects.requireNonNull(index);
        Objects.requireNonNull(document);
        IndexRequest request = createIndexRequest(index, document);
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        return findById(index, UUID.fromString(indexResponse.getId()));
    }

    public BulkResponse bulkIndexDocument(String index, Collection<SecasignboxDocument> documents) throws IOException {
        Objects.requireNonNull(index);
        Objects.requireNonNull(documents);
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
        Objects.requireNonNull(index);
        Objects.requireNonNull(id);
        Optional<SecasignboxDocument> current = findById(index, id);

        if (current.isEmpty()) {
            return current;
        }

        DeleteRequest deleteRequest = new DeleteRequest(index, current.get().getDocumentId().toString());
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        return current;
    }

    public Optional<SecasignboxDocument> findById(String index, UUID id) throws IOException {
        Objects.requireNonNull(index);
        Objects.requireNonNull(id);
        GetRequest getRequest = new GetRequest(index, id.toString());
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        if (getResponse.getSource() != null) {
            return Optional.of(objectMapper.readValue(getResponse.getSourceAsString(), SecasignboxDocument.class));
        } else {
            return Optional.empty();
        }
    }

    public List<SearchStrike> queryBySearchQuery(String index, SearchQuery searchQuery) throws IOException {
        Objects.requireNonNull(index);
        Objects.requireNonNull(searchQuery);
        QueryBuilder query = searchQueryService.composeQuery(searchQuery);
        return query(new String[] { index }, query, FIELD_SECASIGN_DOC_NAME, FIELD_SECASIGN_DOC_CONTENT);
    }

    private List<SearchStrike> query(String[] indices, QueryBuilder query, String... highlightings) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indices);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        HighlightBuilder highlightBuilder = createHighlighter(highlightings);
        sourceBuilder.highlighter(highlightBuilder);
        sourceBuilder.query(query);
        // sort descending by score
        sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchStrikes(searchResponse);
    }

    public List<SearchStrike> queryByTermFuzzyPhraseOnDocNameAndContent(String index, String term) throws
            IOException {
        Objects.requireNonNull(index);
        Objects.requireNonNull(term);
        QueryBuilder phraseQuery =
                QueryBuilders.matchPhraseQuery(FIELD_SECASIGN_DOC_NAME, term).slop(10).boost(BOOST_DOCUMENT_NAME + BOOST_PHRASE_SEARCH);
        QueryBuilder fuzzyQuery = QueryBuilders.matchQuery(FIELD_SECASIGN_DOC_NAME, term)
                                               .fuzziness(Fuzziness.AUTO)
                                               .boost(BOOST_DOCUMENT_NAME)
                                               .maxExpansions(50);

        QueryBuilder phraseQuery2 = QueryBuilders.matchPhraseQuery(FIELD_SECASIGN_DOC_CONTENT, term).slop(30).boost(BOOST_PHRASE_SEARCH);
        QueryBuilder fuzzyQuery2 =
                QueryBuilders.matchQuery(FIELD_SECASIGN_DOC_CONTENT, term).fuzziness(Fuzziness.AUTO).maxExpansions(50);

        QueryBuilder query = QueryBuilders.boolQuery()
                                          .should(phraseQuery)
                                          .should(fuzzyQuery)
                                          .should(phraseQuery2)
                                          .should(fuzzyQuery2);

        return query(new String[] { index }, query, FIELD_SECASIGN_DOC_NAME, FIELD_SECASIGN_DOC_CONTENT);
    }

    public List<SearchStrike> queryByTermOnDocName(String index, String term) throws IOException {
        Objects.requireNonNull(index);
        Objects.requireNonNull(term);
        QueryBuilder query = QueryBuilders.matchQuery(FIELD_SECASIGN_DOC_NAME, term);
        return query(new String[] { index }, query, FIELD_SECASIGN_DOC_NAME);
    }

    public List<SearchStrike> queryByTermFuzzyOnDocName(String[] indices, String term) throws IOException {
        Objects.requireNonNull(indices);
        Objects.requireNonNull(term);
        QueryBuilder query =
                QueryBuilders.matchQuery(FIELD_SECASIGN_DOC_NAME, term).fuzziness(Fuzziness.AUTO).maxExpansions(50);
        return query(indices, query, FIELD_SECASIGN_DOC_NAME);
    }

    public List<SearchStrike> queryByTermPhraseOnDocName(String index, String term) throws IOException {
        Objects.requireNonNull(index);
        Objects.requireNonNull(term);
        QueryBuilder query = QueryBuilders.matchPhraseQuery(FIELD_SECASIGN_DOC_NAME, term).slop(10);
        return query(new String[] { index }, query, FIELD_SECASIGN_DOC_NAME);
    }

    public List<SearchStrike> queryByTermFuzzyPhraseOnDocName(String index, String term) throws IOException {
        Objects.requireNonNull(index);
        Objects.requireNonNull(term);
        QueryBuilder phraseQuery = QueryBuilders.matchPhraseQuery(FIELD_SECASIGN_DOC_NAME, term).slop(10);
        QueryBuilder fuzzyQuery =
                QueryBuilders.matchQuery(FIELD_SECASIGN_DOC_NAME, term).fuzziness(Fuzziness.AUTO).maxExpansions(50);

        QueryBuilder query = QueryBuilders.boolQuery().should(phraseQuery).should(fuzzyQuery);
        return query(new String[] { index }, query, FIELD_SECASIGN_DOC_NAME);
    }

    private HighlightBuilder createHighlighter(String... fields) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.order("sort");
        highlightBuilder.preTags("<b>");
        highlightBuilder.postTags("</b>");
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

            SecasignboxDocument document = objectMapper.convertValue(hit.getSourceAsMap(), SecasignboxDocument.class);
            SearchStrike dto = new SearchStrike();
            dto.setScore(hit.getScore());
            dto.setDocumentId(document.getDocumentId());
            dto.setDocumentName(document.getDocumentName());
            dto.setDocumentContent(document.getDocumentContent());
            dto.setHighlights(highlights);
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
}
