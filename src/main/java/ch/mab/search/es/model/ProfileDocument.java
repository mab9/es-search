package ch.mab.search.es.model;

import java.util.List;

public class ProfileDocument {

    private String id;
    private String firstName;
    private String lastName;
    private List<Technology> technologies;
    private List<String> emails;

    public ProfileDocument(String id, String firstName, String lastName, List<Technology> technologies,
                           List<String> emails) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.technologies = technologies;
        this.emails = emails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Technology> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<Technology> technologies) {
        this.technologies = technologies;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}