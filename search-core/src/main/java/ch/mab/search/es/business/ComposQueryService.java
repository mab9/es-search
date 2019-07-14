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
            return composeFuzzyQuery(query);
        }

        if (query.isFuzzy() && query.isDocumentName() && query.getFromDate() == null && query.getToDate() == null) {
            return composeFuzzyDocumentNameQuery(query);
        }

        if (query.isFuzzy() && query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeFuzzyDocumentNameRangeQuery(query);
        }

        if (query.isFuzzy() && !query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeFuzzyRangeQuery(query);
        }

        if (!query.isFuzzy() && query.isDocumentName() && query.getFromDate() == null && query.getToDate() == null) {
            return composeDocumentNameQuery(query);
        }

        if (!query.isFuzzy() && query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeDocumentNameRangeQuery(query);
        }

        if (!query.isFuzzy() && !query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeRangeQuery(query);
        }

        return composeMatchAllQuery(query.getTerm());
    }

    private QueryBuilder composeFuzzyQuery(SearchQuery query) {
        assert query.isFuzzy();

        MatchQueryBuilder matchDocContent = new MatchQueryBuilder(IndexMappingSetting.FIELD_SECASIGN_DOC_CONTENT, query.getTerm());
        MatchQueryBuilder matchDocName = new MatchQueryBuilder(IndexMappingSetting.FIELD_SECASIGN_DOC_NAME, query.getTerm());

        // the add of a prefixLength could be a performance improvement but does restrict the search.
        matchDocContent.maxExpansions(7);
        matchDocName.maxExpansions(7);

        matchDocName.boost(BOOST_DOCUMENT_NAME);
        return QueryBuilders.boolQuery().should(matchDocContent).should(matchDocName);
    }

    private QueryBuilder composeFuzzyDocumentNameQuery(SearchQuery query) {
        assert query.isFuzzy() && query.isDocumentName();

        MatchQueryBuilder matchDocContent = new MatchQueryBuilder(IndexMappingSetting.FIELD_SECASIGN_DOC_CONTENT, query.getTerm());
        MatchQueryBuilder matchDocName1 = new MatchQueryBuilder(IndexMappingSetting.FIELD_SECASIGN_DOC_NAME, query.getTerm());
        QueryBuilder matchDocName2 = composeDocumentNameQuery(query);

        matchDocName1.maxExpansions(7);
        matchDocContent.maxExpansions(7);

        matchDocName1.boost(BOOST_DOCUMENT_NAME);
        matchDocName2.boost(BOOST_DOCUMENT_NAME);
        return QueryBuilders.boolQuery().should(matchDocContent).should(matchDocName1).should(matchDocName2);
    }

    // TODO check if such queries are good.
    private QueryBuilder composeFuzzyDocumentNameRangeQuery(SearchQuery query) {
        assert query.isFuzzy() && query.isDocumentName();
        assert query.getFromDate() != null || query.getToDate() != null;
        if (query.getFromDate() != null && query.getToDate() != null) {
            assert query.getFromDate().compareTo(query.getToDate()) <= 0;
        }

        QueryBuilder matchFuzzyAndDocumentName = composeFuzzyDocumentNameQuery(query);
        QueryBuilder matchRange = composeRangeQuery(query);
        return QueryBuilders.boolQuery().must(matchRange).should(matchFuzzyAndDocumentName);

    }

    private QueryBuilder composeFuzzyRangeQuery(SearchQuery query) {
        assert query.isFuzzy();
        assert query.getFromDate() != null || query.getToDate() != null;
        if (query.getFromDate() != null && query.getToDate() != null) {
            assert query.getFromDate().compareTo(query.getToDate()) <= 0;
        }

        QueryBuilder matchFuzzy = composeFuzzyQuery(query);
        QueryBuilder matchRange = composeRangeQuery(query);
        return QueryBuilders.boolQuery().must(matchRange).should(matchFuzzy);
    }

    private QueryBuilder composeDocumentNameQuery(SearchQuery query) {
        assert query.isDocumentName();
        return new MatchPhraseQueryBuilder(IndexMappingSetting.FIELD_SECASIGN_DOC_NAME, query.getTerm());
    }

    private QueryBuilder composeDocumentNameRangeQuery(SearchQuery query) {
        assert query.isDocumentName();
        assert query.getFromDate() != null || query.getToDate() != null;
        if (query.getFromDate() != null && query.getToDate() != null) {
            assert query.getFromDate().compareTo(query.getToDate()) <= 0;
        }

        QueryBuilder matchDocumentName = composeDocumentNameQuery(query);
        matchDocumentName.boost(BOOST_DOCUMENT_NAME);
        QueryBuilder matchRange = composeRangeQuery(query);
        return QueryBuilders.boolQuery().must(matchDocumentName).must(matchRange);
    }

    private QueryBuilder composeRangeQuery(SearchQuery query) {
        assert query.getFromDate() != null || query.getToDate() != null;
        if (query.getFromDate() != null && query.getToDate() != null) {
            assert query.getFromDate().compareTo(query.getToDate()) <= 0;
        }

        RangeQueryBuilder rangeUploadDate = new RangeQueryBuilder(IndexMappingSetting.FIELD_SECASIGN_DOC_UPLOAD_DATE);
        RangeQueryBuilder range;

        if (query.getFromDate() != null && query.getToDate() != null) {
            range = rangeUploadDate.gte(query.getFromDate()).lte(query.getToDate());
        } else if (query.getFromDate() != null && query.getToDate() == null) {
            range =  rangeUploadDate.gte(query.getFromDate()).lte(new Date());
        } else {
            range = rangeUploadDate.lte(query.getToDate());
        }

        return QueryBuilders.boolQuery().must(range).should(composeMatchAllQuery(query.getTerm()));
    }

    public QueryBuilder composeMatchAllQuery(String term) {
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
