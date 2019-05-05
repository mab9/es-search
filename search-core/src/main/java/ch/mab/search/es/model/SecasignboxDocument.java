package ch.mab.search.es.model;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class SecasignboxDocument {

    private final UUID id = UUID.randomUUID();
    private UUID archivespaceId;
    private String documentName;
    private long uploadDate;
    private String documentContent;

    public SecasignboxDocument(UUID archivespaceId, String documentName, Date uploadDate, String documentContent) {
        this.archivespaceId = archivespaceId;
        this.documentName = documentName;
        this.uploadDate = uploadDate.getTime();
        this.documentContent = documentContent;
    }

    public UUID getId() {
        return id;
    }

    public UUID getArchivespaceId() {
        return archivespaceId;
    }

    public void setArchivespaceId(UUID archivespaceId) {
        this.archivespaceId = archivespaceId;
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
        return id.equals(that.id) && archivespaceId.equals(that.archivespaceId) &&
               documentName.equals(that.documentName) && Objects.equals(uploadDate, that.uploadDate) &&
               documentContent.equals(that.documentContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, archivespaceId, documentName, uploadDate, documentContent);
    }
}
