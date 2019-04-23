package ch.mab.search.es.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SecasignboxDocument {

    private final UUID id = UUID.randomUUID();
    private UUID archivespaceId;
    private String documentName;
    private long uploadDate;
    private long signDate;
    private List<Metadata> metadatas;
    private String documentContent;
    private DocumentState documentState;

    public SecasignboxDocument(UUID archivespaceId, String documentName, Date uploadDate, Date signDate,
                               List<Metadata> metadatas, String documentContent, DocumentState documentState) {
        this.archivespaceId = archivespaceId;
        this.documentName = documentName;
        this.uploadDate = uploadDate.getTime();
        this.signDate = signDate.getTime();
        this.metadatas = metadatas;
        this.documentContent = documentContent;
        this.documentState = documentState;
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

    public Date getSignDate() {
        return new Date(signDate);
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate.getTime();
    }

    public List<Metadata> getMetadatas() {
        return metadatas;
    }

    public void setMetadatas(List<Metadata> metadatas) {
        this.metadatas = metadatas;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public DocumentState getDocumentState() {
        return documentState;
    }

    public void setDocumentState(DocumentState documentState) {
        this.documentState = documentState;
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
               Objects.equals(signDate, that.signDate) && Objects.equals(metadatas, that.metadatas) &&
               documentContent.equals(that.documentContent) && documentState == that.documentState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, archivespaceId, documentName, uploadDate, signDate, metadatas, documentContent,
                            documentState);
    }
}
