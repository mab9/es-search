package ch.mab.search.es.model;

public class Metadata {

    private Long index;
    private String key;
    private String value;

    public Metadata() {
    }

    public Metadata(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Metadata(Long index, String key, String value) {
        this.index = index;
        this.key = key;
        this.value = value;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
