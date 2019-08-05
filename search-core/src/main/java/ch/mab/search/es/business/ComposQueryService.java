package ch.mab.search.es.business;

import ch.mab.search.es.base.IndexMappingSetting;
import ch.mab.search.es.model.SearchQuery;
import org.elasticsearch.index.query.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ComposQueryService {

    private final float BOOST_DOCUMENT_NAME = 4.0f;

    public ComposQueryService() {
    }

    public QueryBuilder composeQuery(SearchQuery query) {
        if (query.isFuzzy() && !query.isDocumentName() && query.getFromDate() == null && query.getToDate() == null) {
            return composeQueryFuzzyOnDocNameAndDocContent(query);
        }

        if (query.isFuzzy() && query.isDocumentName() && query.getFromDate() == null && query.getToDate() == null) {
            return composeQueryFuzzyOnDocName(query);
        }

        if (query.isFuzzy() && query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeQueryFuzzyAndRangeOnDocName(query);
        }

        if (query.isFuzzy() && !query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeQueryFuzzyAndRangeOnDocNameAndDocContent(query);
        }

        if (!query.isFuzzy() && query.isDocumentName() && query.getFromDate() == null && query.getToDate() == null) {
            return composeQueryOnDocName(query);
        }

        if (!query.isFuzzy() && query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeQueryRangeOnDocName(query);
        }

        if (!query.isFuzzy() && !query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeQueryRangeOnDocUploadDate(query);
        }

        return composeQueryDefault(query.getTerm());
    }

    private QueryBuilder composeQueryFuzzyOnDocNameAndDocContent(SearchQuery query) {
        MatchQueryBuilder matchDocContent = new MatchQueryBuilder(IndexMappingSetting.FIELD_SECASIGN_DOC_CONTENT, query.getTerm());
        MatchQueryBuilder matchDocName = new MatchQueryBuilder(IndexMappingSetting.FIELD_SECASIGN_DOC_NAME, query.getTerm());

        // the add of a prefixLength could be a performance improvement but does restrict the search.
        matchDocContent.maxExpansions(7);
        matchDocName.maxExpansions(7);

        matchDocName.boost(BOOST_DOCUMENT_NAME);
        return QueryBuilders.boolQuery().should(matchDocContent).should(matchDocName);
    }

    // todo hier das vom Search Service verwenden
    private QueryBuilder composeQueryOnDocName(SearchQuery query) {
        return new MatchPhraseQueryBuilder(IndexMappingSetting.FIELD_SECASIGN_DOC_NAME, query.getTerm());
    }

    // todo hier das vom Search Service verwenden
    private QueryBuilder composeQueryFuzzyOnDocName(SearchQuery query) {
        QueryBuilder matchDocName = composeQueryOnDocName(query);
        return QueryBuilders.boolQuery().should(matchDocName);
    }

    private QueryBuilder composeQueryFuzzyAndRangeOnDocName(SearchQuery query) {
        QueryBuilder matchFuzzyAndDocumentName = composeQueryFuzzyOnDocName(query);
        QueryBuilder matchRange = composeQueryRangeOnDocUploadDate(query);
        return QueryBuilders.boolQuery().must(matchRange).should(matchFuzzyAndDocumentName);
    }

    private QueryBuilder composeQueryFuzzyAndRangeOnDocNameAndDocContent(SearchQuery query) {
        QueryBuilder matchFuzzy = composeQueryFuzzyOnDocNameAndDocContent(query);
        QueryBuilder matchRange = composeQueryRangeOnDocUploadDate(query);
        return QueryBuilders.boolQuery().must(matchRange).should(matchFuzzy);
    }

    // todo braucht es den boost?
    private QueryBuilder composeQueryRangeOnDocName(SearchQuery query) {
        QueryBuilder matchDocumentName = composeQueryOnDocName(query);
        matchDocumentName.boost(BOOST_DOCUMENT_NAME);
        QueryBuilder matchRange = composeQueryRangeOnDocUploadDate(query);
        return QueryBuilders.boolQuery().must(matchDocumentName).must(matchRange);
    }

    private QueryBuilder composeQueryRangeOnDocUploadDate(SearchQuery query) {
        RangeQueryBuilder rangeUploadDate = new RangeQueryBuilder(IndexMappingSetting.FIELD_SECASIGN_DOC_UPLOAD_DATE);
        RangeQueryBuilder range;

        if (query.getFromDate() != null && query.getToDate() != null) {
            range = rangeUploadDate.gte(query.getFromDate()).lte(query.getToDate());
        } else if (query.getFromDate() != null && query.getToDate() == null) {
            range =  rangeUploadDate.gte(query.getFromDate()).lte(new Date());
        } else {
            range = rangeUploadDate.lte(query.getToDate());
        }

        return QueryBuilders.boolQuery().must(range).should(composeQueryDefault(query.getTerm()));
    }

    // todo hier das vom searchservice verwendne
    public QueryBuilder composeQueryDefault(String term) {
        MatchPhraseQueryBuilder matchPhraseDocName = new MatchPhraseQueryBuilder(IndexMappingSetting.FIELD_SECASIGN_DOC_NAME, term);
        MatchPhraseQueryBuilder matchDocContent = new MatchPhraseQueryBuilder(IndexMappingSetting.FIELD_SECASIGN_DOC_CONTENT, term);
        MatchQueryBuilder matchDocName = new MatchQueryBuilder(IndexMappingSetting.FIELD_SECASIGN_DOC_NAME, term);

        // the add of a prefixLength could be a performance improvement but does restrict the search.
        matchDocName.maxExpansions(7);
        matchDocName.boost(BOOST_DOCUMENT_NAME + 4);
        matchPhraseDocName.boost(BOOST_DOCUMENT_NAME);

        return QueryBuilders.boolQuery().should(matchDocContent).should(matchDocName).should(matchPhraseDocName);
    }
}
