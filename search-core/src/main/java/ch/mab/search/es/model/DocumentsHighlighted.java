package ch.mab.search.es.model;

import java.util.List;

public class DocumentsHighlighted {

    List<SearchHighlights> docs;

    public DocumentsHighlighted(List<SearchHighlights> docs) {
        this.docs = docs;
    }

    public List<SearchHighlights> getDocs() {
        return docs;
    }

    public void setDocs(List<SearchHighlights> docs) {
        this.docs = docs;
    }
}
