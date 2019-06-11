package ch.mab.search.es.model;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class SecasignboxDocument {

    private final UUID documentId = UUID.randomUUID();
    private String documentName;
    private long uploadDate;
    private String documentContent;

    public SecasignboxDocument(String documentName, Date uploadDate, String documentContent) {
        this.documentName = documentName;
        this.uploadDate = uploadDate.getTime();
        this.documentContent = documentContent;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public Date getUploadDate() {
        return new Date(uploadDate);
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate.getTime();
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SecasignboxDocument that = (SecasignboxDocument) o;
        return documentId.equals(that.documentId) &&
               documentName.equals(that.documentName) && Objects.equals(uploadDate, that.uploadDate) &&
               documentContent.equals(that.documentContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, documentName, uploadDate, documentContent);
    }
}
