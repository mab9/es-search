package ch.mab.search.es.model;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProfileDocument document = (ProfileDocument) o;
        return id.equals(document.id) && firstName.equals(document.firstName) && lastName.equals(document.lastName) &&
               Objects.equals(technologies, document.technologies) && Objects.equals(emails, document.emails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, technologies, emails);
    }
}