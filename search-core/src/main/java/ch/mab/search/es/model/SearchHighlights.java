package ch.mab.search.es.model;

import java.util.List;
import java.util.UUID;

public class SearchHighlights {

    private List<String> highlights;
    private UUID documentId;
    private String documentName;

    public SearchHighlights() {
    }

    public SearchHighlights(List<String> highlights, UUID documentId, String documentName) {
        this.highlights = highlights;
        this.documentId = documentId;
        this.documentName = documentName;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<String> highlights) {
        this.highlights = highlights;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

}
