package ch.mab.search.es.business;

import ch.mab.search.es.model.SearchQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.stereotype.Service;

@Service
public class ComposQueryService {

    public QueryBuilder composeQuery(SearchQuery query) {
        if (query.isFuzzy() && !query.isDocumentName() && query.getFromDate() == null && query.getToDate() == null) {
            return composeFuzzyQuery();
        }

        if (query.isFuzzy() && query.isDocumentName() && query.getFromDate() == null && query.getToDate() == null) {
            return composeFuzzyDocumentNameQuery();
        }

        if (query.isFuzzy() && query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeFuzzyDocumentNameRangeQuery();
        }

        if (query.isFuzzy() && !query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeFuzzyRangeQuery();
        }

        if (!query.isFuzzy() && query.isDocumentName() && query.getFromDate() == null && query.getToDate() == null) {
            return composeDocumentNameQuery();
        }

        if (!query.isFuzzy() && query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeDocumentNameRangeQuery();
        }

        if (!query.isFuzzy() && !query.isDocumentName() && (query.getFromDate() != null || query.getToDate() != null)) {
            return composeRangeQuery();
        }

        return composeMatchAllQuery();
    }

    private QueryBuilder composeFuzzyQuery() {
        return null;
    }

    private QueryBuilder composeFuzzyDocumentNameQuery() {
        return null;
    }

    private QueryBuilder composeFuzzyDocumentNameRangeQuery() {
        return null;
    }

    private QueryBuilder composeFuzzyRangeQuery() {
        return null;
    }

    private QueryBuilder composeDocumentNameQuery() {
        return null;
    }

    private QueryBuilder composeDocumentNameRangeQuery() {
        return null;
    }

    private QueryBuilder composeRangeQuery() {
        return null;
    }

    private QueryBuilder composeMatchAllQuery() {
        return null;
    }
}
