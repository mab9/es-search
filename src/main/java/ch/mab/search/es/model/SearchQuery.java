package ch.mab.search.es.model;

import java.util.Date;
import java.util.UUID;

public class SearchQuery {

    UUID archivespaceId;

    Date fromDate;

    Date toDate;

    DocumentState documentState;

    String documentContent;

    String documentMetadata;

    public SearchQuery() {
    }

    public SearchQuery(UUID archivespaceId, Date fromDate, Date toDate, DocumentState documentState, String documentContent, String documentMetadata) {
        this.archivespaceId = archivespaceId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.documentState = documentState;
        this.documentContent = documentContent;
        this.documentMetadata = documentMetadata;
    }

    public UUID getArchivespaceId() {
        return archivespaceId;
    }

    public void setArchivespaceId(UUID archivespaceId) {
        this.archivespaceId = archivespaceId;
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

    public DocumentState getDocumentState() {
        return documentState;
    }

    public void setDocumentState(DocumentState documentState) {
        this.documentState = documentState;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public String getDocumentMetadata() {
        return documentMetadata;
    }

    public void setDocumentMetadata(String documentMetadata) {
        this.documentMetadata = documentMetadata;
    }
}
