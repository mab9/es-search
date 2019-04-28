package ch.mab.search.es.model;

public class Item {


    ContactDocument item;

    public Item(ContactDocument item) {
        this.item = item;
    }

    public ContactDocument getItem() {
        return item;
    }

    public void setItem(ContactDocument item) {
        this.item = item;
    }
}
