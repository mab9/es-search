package ch.mab.search.es.model;

import java.util.List;
import java.util.UUID;

public class SearchStrike {

    private List<String> highlights;
    private UUID documentId;
    private String documentName;
    private String documentContent;
    private long uploadDate;
    private float score;

    public SearchStrike() {
    }

    public SearchStrike(List<String> highlights, UUID documentId, String documentName, String documentContent,
                        long uploadDate, float score) {
        this.highlights = highlights;
        this.documentId = documentId;
        this.documentName = documentName;
        this.documentContent = documentContent;
        this.uploadDate = uploadDate;
        this.score = score;
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

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public long getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(long uploadDate) {
        this.uploadDate = uploadDate;
    }
}
