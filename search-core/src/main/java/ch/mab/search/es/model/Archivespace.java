package ch.mab.search.es.model;

import java.util.UUID;

public class Archivespace {

    private UUID id;

    private String name;

    public Archivespace() {
    }

    public Archivespace(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
