package ch.mab.search.es.model;

import java.util.Date;

public class SearchQuery {

    private String term = null;
    private boolean fuzzy = false;
    private boolean documentName = false;
    private Date fromDate = null;
    private Date toDate = null;

    public SearchQuery() {
    }

    public SearchQuery(String term, boolean fuzzy, boolean documentName, Date fromDate, Date toDate) {
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

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
