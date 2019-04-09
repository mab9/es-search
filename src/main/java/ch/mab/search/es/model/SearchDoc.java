package ch.mab.search.es.model;

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


}
