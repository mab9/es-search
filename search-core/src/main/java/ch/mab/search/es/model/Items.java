package ch.mab.search.es.model;

import java.util.List;

public class Items {
    List<ContactDocument> items;

    public Items(List<ContactDocument> items) {
        this.items = items;
    }

    public List<ContactDocument> getItems() {
        return items;
    }

    public void setItems(List<ContactDocument> items) {
        this.items = items;
    }
}
