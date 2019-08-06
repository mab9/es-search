package ch.mab.search.es.business;

import ch.mab.search.es.model.SearchQuery;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

import static ch.mab.search.es.base.SecasignBoxConstants.*;

@Service
public class SearchQueryService {

    public SearchQueryService() {
    }

    public QueryBuilder composeQuery(SearchQuery query) {
        Objects.requireNonNull(query);

        if (query.isFuzzy() && !query.isDocumentName() && query.getFromDate() == null && query.getToDate() == null) {
            return queryFuzzyAndPhraseByTermOnDocNameAndDocContent(query.getTerm());
        }

        if (query.isFuzzy() && query.isDocumentName() && query.getFromDate() == null && query.getToDate() == null) {
            return composeQueryFuzzyAndPhraseByDocName(query);
        }

        if (query.isFuzzy() && query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeQueryFuzzyAndPhraseAndRangeByDocName(query);
        }

        if (query.isFuzzy() && !query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeQueryFuzzyAndPhraseAndRangeByDocNameAndDocContent(query);
        }

        if (!query.isFuzzy() && query.isDocumentName() && query.getFromDate() == null && query.getToDate() == null) {
            return composeQueryPhraseByDocName(query);
        }

        if (!query.isFuzzy() && query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeQueryPhraseAndRangeByDocName(query);
        }

        if (!query.isFuzzy() && !query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeQueryRangeByDocUploadDate(query);
        }

        return queryFuzzyAndPhraseByTermOnDocNameAndDocContent(query.getTerm());
    }

    private QueryBuilder composeQueryPhraseByDocName(SearchQuery query) {
        return QueryBuilders.matchPhraseQuery(FIELD_SECASIGN_DOC_NAME, query.getTerm()).slop(10);
    }

    private QueryBuilder composeQueryFuzzyAndPhraseByDocName(SearchQuery query) {
        QueryBuilder phraseQuery = QueryBuilders.matchPhraseQuery(FIELD_SECASIGN_DOC_NAME, query.getTerm()).slop(10);
        QueryBuilder fuzzyQuery = QueryBuilders.matchQuery(FIELD_SECASIGN_DOC_NAME, query.getTerm())
                                               .fuzziness(Fuzziness.AUTO)
                                               .maxExpansions(50);

        return QueryBuilders.boolQuery().should(phraseQuery).should(fuzzyQuery);
    }

    private QueryBuilder composeQueryFuzzyAndPhraseAndRangeByDocName(SearchQuery query) {
        QueryBuilder matchFuzzyAndDocumentName = composeQueryFuzzyAndPhraseByDocName(query);
        QueryBuilder matchRange = composeQueryRangeByDocUploadDate(query);
        return QueryBuilders.boolQuery().must(matchRange).should(matchFuzzyAndDocumentName);
    }

    private QueryBuilder composeQueryFuzzyAndPhraseAndRangeByDocNameAndDocContent(SearchQuery query) {
        QueryBuilder matchFuzzy = queryFuzzyAndPhraseByTermOnDocNameAndDocContent(query.getTerm());
        QueryBuilder matchRange = composeQueryRangeByDocUploadDate(query);
        return QueryBuilders.boolQuery().must(matchRange).should(matchFuzzy);
    }

    private QueryBuilder composeQueryPhraseAndRangeByDocName(SearchQuery query) {
        QueryBuilder matchDocName = composeQueryPhraseByDocName(query);
        QueryBuilder matchRange = composeQueryRangeByDocUploadDate(query);
        return QueryBuilders.boolQuery().must(matchDocName).must(matchRange);
    }

    private QueryBuilder composeQueryRangeByDocUploadDate(SearchQuery query) {
        RangeQueryBuilder rangeUploadDate = new RangeQueryBuilder(FIELD_SECASIGN_DOC_UPLOAD_DATE);
        RangeQueryBuilder range;

        if (query.getFromDate() != null && query.getToDate() != null) {
            range = rangeUploadDate.gte(query.getFromDate()).lte(query.getToDate());
        } else if (query.getFromDate() != null && query.getToDate() == null) {
            range = rangeUploadDate.gte(query.getFromDate()).lte(new Date());
        } else {
            range = rangeUploadDate.lte(query.getToDate());
        }

        QueryBuilder queryAll = queryFuzzyAndPhraseByTermOnDocNameAndDocContent(query.getTerm());
        return QueryBuilders.boolQuery().must(range).should(queryAll);
    }

    public QueryBuilder queryFuzzyAndPhraseByTermOnDocNameAndDocContent(String term) {
        Objects.requireNonNull(term);
        QueryBuilder phraseQuerDocName =
                QueryBuilders.matchPhraseQuery(FIELD_SECASIGN_DOC_NAME, term).slop(10).boost(BOOST_DOCUMENT_NAME);
        QueryBuilder fuzzyQueryDocName = QueryBuilders.matchQuery(FIELD_SECASIGN_DOC_NAME, term)
                                                      .fuzziness(Fuzziness.AUTO)
                                                      .boost(BOOST_DOCUMENT_NAME)
                                                      .maxExpansions(50);

        QueryBuilder phraseQueryDocContent = QueryBuilders.matchPhraseQuery(FIELD_SECASIGN_DOC_CONTENT, term).slop(30);
        QueryBuilder fuzzyQueryDocContent =
                QueryBuilders.matchQuery(FIELD_SECASIGN_DOC_CONTENT, term).fuzziness(Fuzziness.AUTO).maxExpansions(50);

        return QueryBuilders.boolQuery()
                            .should(phraseQuerDocName)
                            .should(fuzzyQueryDocName)
                            .should(phraseQueryDocContent)
                            .should(fuzzyQueryDocContent);
    }
}
