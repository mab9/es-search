package ch.mab.search.es.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.PathParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contacts")
@CrossOrigin(origins = "http://localhost:4200")
public class ContactController {

    @Autowired
    private ProfileController service;

    private static final List<Contact> contacts = new ArrayList<>();

    public ContactController() {
        contacts.add(new Contact(UUID.randomUUID().toString(), "Marco", "marco@sutter.ch", "079"));
        contacts.add(new Contact(UUID.randomUUID().toString(), "Florian", "florian@sutter.ch", "079"));
        contacts.add(new Contact(UUID.randomUUID().toString(), "Ladina", "ladina@sutter.ch", "079"));
        contacts.add(new Contact(UUID.randomUUID().toString(), "Vera", "vera@sutter.ch", "079"));
        contacts.add(new Contact(UUID.randomUUID().toString(), "Hans-Martin", "hansmartin@sutter.ch", "079"));
        contacts.add(new Contact(UUID.randomUUID().toString(), "Nele", "nele@sutter.ch", "079"));
        contacts.add(new Contact(UUID.randomUUID().toString(), "Marc-Antoine", "marcantoine@sutter.ch", "079"));
        contacts.add(new Contact(UUID.randomUUID().toString(), "Maja", "maja@sutter.ch", "079"));
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<Items>getContacts()  {
        return new ResponseEntity<>(new Items(contacts), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Item> getContacts(@PathVariable String id) {
        Contact result = contacts.stream()
                                 .filter(contact -> contact.id.equals(id))
                                 .findAny()
                                 .orElse(new Contact(UUID.randomUUID().toString(), "Random", "not@found.ch", "079"));

        return new ResponseEntity<>(new Item(result), HttpStatus.OK);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Items> search(@RequestParam(value="term") String term) {
        List<Contact> results = contacts.stream()
                                 .filter(contact -> contact.name.toLowerCase().contains(term.toLowerCase())).collect(Collectors.toList());
        return new ResponseEntity<>(new Items(results), HttpStatus.OK);
    }
}

class Item {

    Contact item;

    public Item(Contact item) {
        this.item = item;
    }

    public Contact getItem() {
        return item;
    }

    public void setItem(Contact item) {
        this.item = item;
    }
}

class Items {
    List<Contact> items;

    public Items(List<Contact> items) {
        this.items = items;
    }

    public List<Contact> getItems() {
        return items;
    }

    public void setItems(List<Contact> items) {
        this.items = items;
    }
}

class Contact {

    String id;
    String name;
    String email;
    String phone;

    public Contact(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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