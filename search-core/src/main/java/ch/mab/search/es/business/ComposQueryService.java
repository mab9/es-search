package ch.mab.search.es.business;

import ch.mab.search.es.model.SearchQuery;
import org.elasticsearch.index.query.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ComposQueryService {

    private final String FIELD_DOCUMENT_NAME = "documentName";
    private final String FIELD_DOCUMENT_CONTENT = "documentContent";
    private final String FIELD_UPLOAD_DATE = "uploadDate";
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

        MatchQueryBuilder matchDocContent = new MatchQueryBuilder(FIELD_DOCUMENT_CONTENT, query.getTerm());
        MatchQueryBuilder matchDocName = new MatchQueryBuilder(FIELD_DOCUMENT_NAME, query.getTerm());

        // the add of a prefixLength could be a performance improvement but does restrict the search.
        matchDocContent.maxExpansions(7);
        matchDocName.maxExpansions(7);

        matchDocName.boost(BOOST_DOCUMENT_NAME);
        return QueryBuilders.boolQuery().should(matchDocContent).should(matchDocName);
    }

    private QueryBuilder composeFuzzyDocumentNameQuery(SearchQuery query) {
        assert query.isFuzzy() && query.isDocumentName();

        MatchQueryBuilder matchDocContent = new MatchQueryBuilder(FIELD_DOCUMENT_CONTENT, query.getTerm());
        QueryBuilder matchDocName = composeDocumentNameQuery(query);

        matchDocContent.maxExpansions(7);

        matchDocName.boost(BOOST_DOCUMENT_NAME);
        return QueryBuilders.boolQuery().should(matchDocContent).must(matchDocName);
    }

    // TODO check if such queries are good.
    private QueryBuilder composeFuzzyDocumentNameRangeQuery(SearchQuery query) {
        assert query.isFuzzy() && query.isDocumentName();
        assert query.getFromDate() != null || query.getToDate() != null;
        if (query.getFromDate() != null && query.getToDate() != null) {
            assert query.getFromDate().compareTo(query.getToDate()) <= 0;
        }

        QueryBuilder matchFuzzy = composeFuzzyQuery(query);
        QueryBuilder matchDocumentName = composeDocumentNameQuery(query);
        matchDocumentName.boost(BOOST_DOCUMENT_NAME);
        QueryBuilder matchRange = composeRangeQuery(query);
        return QueryBuilders.boolQuery().must(matchFuzzy).must(matchRange).should(matchDocumentName);

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
        return new MatchPhraseQueryBuilder(FIELD_DOCUMENT_NAME, query.getTerm());
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

        RangeQueryBuilder rangeUploadDate = new RangeQueryBuilder(FIELD_UPLOAD_DATE);
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
        MatchPhraseQueryBuilder matchPhraseDocName = new MatchPhraseQueryBuilder(FIELD_DOCUMENT_NAME, term);
        MatchPhraseQueryBuilder matchDocContent = new MatchPhraseQueryBuilder(FIELD_DOCUMENT_CONTENT, term);
        MatchQueryBuilder matchDocName = new MatchQueryBuilder(FIELD_DOCUMENT_NAME, term);

        // the add of a prefixLength could be a performance improvement but does restrict the search.
        matchDocName.maxExpansions(7);
        matchDocName.boost(BOOST_DOCUMENT_NAME);
        matchPhraseDocName.boost(BOOST_DOCUMENT_NAME);

        return QueryBuilders.boolQuery().should(matchDocContent).should(matchDocName).should(matchPhraseDocName);
    }
}
