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
            return queryByTermFuzzyPhraseOnDocNameAndContent(query.getTerm());
        }

        if (query.isFuzzy() && query.isDocumentName() && query.getFromDate() == null && query.getToDate() == null) {
            return composeQueryFuzzyPhraseOnDocName(query);
        }

        if (query.isFuzzy() && query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeQueryFuzzyPhraseRangeOnDocName(query);
        }

        if (query.isFuzzy() && !query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeQueryFuzzyPhraseRangeOnDocNameAndContent(query);
        }

        if (!query.isFuzzy() && query.isDocumentName() && query.getFromDate() == null && query.getToDate() == null) {
            return composeQueryPhraseOnDocName(query);
        }

        if (!query.isFuzzy() && query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeQueryPhraseRangeOnDocName(query);
        }

        if (!query.isFuzzy() && !query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeQueryRangeOnDocNameAndContent(query);
        }

        return queryByTermFuzzyPhraseOnDocNameAndContent(query.getTerm());
    }

    private QueryBuilder composeQueryPhraseOnDocName(SearchQuery query) {
        return QueryBuilders.matchPhraseQuery(FIELD_SECASIGN_DOC_NAME, query.getTerm()).slop(10);
    }

    private QueryBuilder composeQueryFuzzyPhraseOnDocName(SearchQuery query) {
        QueryBuilder phraseQuery = QueryBuilders.matchPhraseQuery(FIELD_SECASIGN_DOC_NAME, query.getTerm()).slop(10);
        QueryBuilder fuzzyQuery = QueryBuilders.matchQuery(FIELD_SECASIGN_DOC_NAME, query.getTerm())
                                               .fuzziness(Fuzziness.AUTO)
                                               .maxExpansions(50);

        return QueryBuilders.boolQuery().should(phraseQuery).should(fuzzyQuery);
    }

    private QueryBuilder composeQueryRangeOnDocNameAndContent(SearchQuery query) {
        RangeQueryBuilder rangeUploadDate = new RangeQueryBuilder(FIELD_SECASIGN_DOC_UPLOAD_DATE);
        RangeQueryBuilder range;

        if (query.getFromDate() != null && query.getToDate() != null) {
            range = rangeUploadDate.gte(query.getFromDate()).lte(query.getToDate());
        } else if (query.getFromDate() != null && query.getToDate() == null) {
            range = rangeUploadDate.gte(query.getFromDate()).lte(new Date());
        } else {
            range = rangeUploadDate.lte(query.getToDate());
        }

        QueryBuilder queryAll = queryByTermFuzzyPhraseOnDocNameAndContent(query.getTerm());
        return QueryBuilders.boolQuery().must(range).should(queryAll);
    }

    private QueryBuilder composeQueryFuzzyPhraseRangeOnDocName(SearchQuery query) {
        QueryBuilder matchDocName = composeQueryFuzzyPhraseOnDocName(query);
        QueryBuilder matchRange = composeQueryPhraseRangeOnDocName(query);
        return QueryBuilders.boolQuery().must(matchRange).should(matchDocName);
    }

    private QueryBuilder composeQueryFuzzyPhraseRangeOnDocNameAndContent(SearchQuery query) {
        QueryBuilder matchAll = queryByTermFuzzyPhraseOnDocNameAndContent(query.getTerm());
        QueryBuilder matchRange = composeQueryRangeOnDocNameAndContent(query);
        return QueryBuilders.boolQuery().must(matchRange).should(matchAll);
    }

    private QueryBuilder composeQueryPhraseRangeOnDocName(SearchQuery query) {
        QueryBuilder matchDocName = composeQueryPhraseOnDocName(query);
        QueryBuilder matchRange = composeQueryRangeOnDocNameAndContent(query);
        return QueryBuilders.boolQuery().must(matchDocName).must(matchRange);
    }

    public QueryBuilder queryByTermFuzzyPhraseOnDocNameAndContent(String term) {
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
