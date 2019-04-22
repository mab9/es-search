package ch.mab.search.es.model;

import ch.mab.search.secasignbox.model.DocState;
import ch.mab.search.secasignbox.model.Metadata;

import java.util.Date;
import java.util.List;
import java.util.UUID;


public class SearchDocument {

    // TODO check if ES internal doc have their own IDs.
    private UUID id;

    private UUID archivespaceId;

    private String documentName;

    private Date uploadDate;

    private Date signDate;

    private List<Metadata> metadataList;

    private String documentContent;

    public SearchDocument() {
    }

    public SearchDocument(UUID id, UUID archivespaceId, DocState docState, String documentName, Date uploadDate,
                          Date signDate, List<Metadata> metadataList, String documentContent) {
        this.id = id;
        this.archivespaceId = archivespaceId;
        this.documentName = documentName;
        this.uploadDate = uploadDate;
        this.signDate = signDate;
        this.metadataList = metadataList;
        this.documentContent = documentContent;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public List<Metadata> getMetadataList() {
        return metadataList;
    }

    public void setMetadataList(List<Metadata> metadataList) {
        this.metadataList = metadataList;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }
}
