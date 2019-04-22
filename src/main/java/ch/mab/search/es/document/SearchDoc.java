package ch.mab.search.es.document;

import ch.mab.search.secasignbox.model.DocState;
import ch.mab.search.secasignbox.model.Metadata;

import java.util.Date;
import java.util.List;
import java.util.UUID;


public class SearchDoc {

    // TODO check if ES internal doc have their own IDs.
    private UUID id;

    private UUID archivespaceId;

    private DocState docState;

    private String docName;

    private Date uploadDate;

    private Date signDate;

    private List<Metadata> metadataList;

    private String docContent;

    public SearchDoc() {
    }

    public SearchDoc(UUID id, UUID archivespaceId, DocState docState, String docName, Date uploadDate,
                     Date signDate, List<Metadata> metadataList, String docContent) {
        this.id = id;
        this.archivespaceId = archivespaceId;
        this.docState = docState;
        this.docName = docName;
        this.uploadDate = uploadDate;
        this.signDate = signDate;
        this.metadataList = metadataList;
        this.docContent = docContent;
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

    public DocState getDocState() {
        return docState;
    }

    public void setDocState(DocState docState) {
        this.docState = docState;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
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

    public String getDocContent() {
        return docContent;
    }

    public void setDocContent(String docContent) {
        this.docContent = docContent;
    }
}
