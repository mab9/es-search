package ch.mab.search.es.rest;

import ch.mab.search.es.business.ContactService;
import ch.mab.search.es.model.Contact;
import ch.mab.search.es.model.ContactDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contacts")
@CrossOrigin(origins = "http://localhost:4200")
public class ContactController {

    @Autowired
    private ContactService service;

    private static final List<Contact> dummyContacts = new ArrayList<>();

    public ContactController() {
        initDummyContacts();
    }

    private void initDummyContacts() {
        dummyContacts.add(new Contact(UUID.randomUUID(), "Marco", "marco@sutter.ch", "079"));
        dummyContacts.add(new Contact(UUID.randomUUID(), "Florian", "florian@sutter.ch", "079"));
        dummyContacts.add(new Contact(UUID.randomUUID(), "Ladina", "ladina@sutter.ch", "079"));
        dummyContacts.add(new Contact(UUID.randomUUID(), "Vera", "vera@sutter.ch", "079"));
        dummyContacts.add(new Contact(UUID.randomUUID(), "Hans-Martin", "hansmartin@sutter.ch", "079"));
        dummyContacts.add(new Contact(UUID.randomUUID(), "Nele", "nele@sutter.ch", "079"));
        dummyContacts.add(new Contact(UUID.randomUUID(), "Marc-Antoine", "marcantoine@sutter.ch", "079"));
        dummyContacts.add(new Contact(UUID.randomUUID(), "Maja", "maja@sutter.ch", "079"));
    }

    @PostMapping
    public ResponseEntity createContact(@RequestBody ContactDocument document) throws Exception {
        return new ResponseEntity(service.createContact("contacts", document), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Item> findById(@PathVariable UUID id) throws IOException {
        Contact result = dummyContacts.stream()
                                      .filter(contact -> contact.getId().equals(id))
                                      .findAny()
                                      .orElse(new Contact(UUID.randomUUID(), "Random", "not@found.ch", "079"));

        return new ResponseEntity<>(new Item(result), HttpStatus.OK);

        /*

        Optional<ContactDocument> result = service.findById("contacts", id);

        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.get(), HttpStatus.OK);*/
    }

    @PutMapping
    public ResponseEntity<ContactDocument> updateContact(@RequestBody ContactDocument document) throws IOException {
        Optional<ContactDocument> result = service.updateContact("contacts", document);
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    @GetMapping
    //public ResponseEntity<List<ContactDocument>> findAll() throws Exception {
    public ResponseEntity<Items> findAll() throws Exception {
        return new ResponseEntity<>(new Items(dummyContacts), HttpStatus.OK);
//        return new ResponseEntity<>(service.findAll("contacts"), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ContactDocument> deleteContact(@PathVariable UUID id) throws Exception {
        Optional<ContactDocument> result = service.deleteContactDocument("contacts", id);
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    @GetMapping(value = "/search2")
    public ResponseEntity<List<ContactDocument>> search(@RequestParam(value = "technology") String technology) throws
            IOException {
        return new ResponseEntity<>(service.searchByTechnology(technology), HttpStatus.OK);
    }


    @GetMapping(value = "/search")
    public ResponseEntity<Items> searchDummies(@RequestParam(value="term") String term) {
        List<Contact> results = dummyContacts.stream()
                                 .filter(contact -> contact.getName().toLowerCase().contains(term.toLowerCase())).collect(Collectors.toList());
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
