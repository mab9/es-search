package ch.mab.search.es.model;

import java.util.List;

public class Documents {
    List<SecasignboxDocument> docs;

    public Documents(List<SecasignboxDocument> docs) {
        this.docs = docs;
    }

    public List<SecasignboxDocument> getDocs() {
        return docs;
    }

    public void setDocs(List<SecasignboxDocument> docs) {
        this.docs = docs;
    }
}
