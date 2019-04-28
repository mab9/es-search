package ch.mab.search.es.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ContactDocument {

    private final UUID id = UUID.randomUUID();
    private String firstName;
    private String lastName;
    private List<Technology> technologies;
    private String email;
    private String phone;

    public ContactDocument(String firstName, String lastName, List<Technology> technologies, String email,
                           String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.technologies = technologies;
        this.email = email;
        this.phone = phone;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}