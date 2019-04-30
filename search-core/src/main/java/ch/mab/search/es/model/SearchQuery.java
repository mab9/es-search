package ch.mab.search.es.model;

public class SearchQuery {

    private String term;
    private boolean fuzzy;
    private boolean documentName;
    private long fromDate;
    private long toDate;

    public SearchQuery() {
    }

    public SearchQuery(String term, boolean fuzzy, boolean documentName, long fromDate, long toDate) {
        this.term = term;
        this.fuzzy = fuzzy;
        this.documentName = documentName;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public boolean isFuzzy() {
        return fuzzy;
    }

    public void setFuzzy(boolean fuzzy) {
        this.fuzzy = fuzzy;
    }

    public boolean isDocumentName() {
        return documentName;
    }

    public void setDocumentName(boolean documentName) {
        this.documentName = documentName;
    }

    public long getFromDate() {
        return fromDate;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public long getToDate() {
        return toDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }
}
