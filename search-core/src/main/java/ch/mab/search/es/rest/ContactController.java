package ch.mab.search.es.rest;

import ch.mab.search.es.business.ContactService;
import ch.mab.search.es.model.ContactDocument;
import ch.mab.search.es.model.Item;
import ch.mab.search.es.model.Items;
import ch.mab.search.es.model.Technology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contacts")
@CrossOrigin(origins = "http://localhost:4200")
public class ContactController {

    @Autowired
    private ContactService service;

    private static final List<ContactDocument> dummyContacts = new ArrayList<>();

    public ContactController() {
        initDummyContacts();
    }

    private void initDummyContacts() {
        List<Technology> technologies = Collections.singletonList(new Technology("Angular", "10"));

        dummyContacts.add(new ContactDocument("Marco", "Sutter", technologies, "marco@sutter.ch", "079"));
        dummyContacts.add(
                new ContactDocument("Florian", "Sutter", technologies, "florian@sutter.ch", "079"));
        dummyContacts.add(new ContactDocument("Ladina", "Sutter", technologies, "ladina@sutter.ch", "079"));
        dummyContacts.add(new ContactDocument("Vera", "Sutter", technologies, "vera@sutter.ch", "079"));
        dummyContacts.add(
                new ContactDocument("Hans-Martin", "Sutter", technologies, "hansmartin@sutter.ch", "079"));
        dummyContacts.add(new ContactDocument("Nele", "Sutter", technologies, "nele@sutter.ch", "079"));
        dummyContacts.add(
                new ContactDocument("Marc-Antoine", "Sutter", technologies, "marcantoine@sutter.ch", "079"));
        dummyContacts.add(new ContactDocument("Maja", "Sutter", technologies, "maja@sutter.ch", "079"));
    }

    @PostMapping
    public ResponseEntity createContact(@RequestBody ContactDocument document) throws Exception {
        return new ResponseEntity(service.createContact("contacts", document), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Item> findById(@PathVariable String id) {
        ContactDocument dummy =
                new ContactDocument("Random", "Rando", Collections.emptyList(), "not@found.ch", "079");

        if (id.equals("0")) {
            return new ResponseEntity<>(new Item(dummy), HttpStatus.OK);
        }

        ContactDocument result = dummyContacts.stream()
                                              .filter(contact -> contact.getId().equals(UUID.fromString(id)))
                                              .findAny()
                                              .orElse(dummy);

        return new ResponseEntity<>(new Item(result), HttpStatus.OK);
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
    public ResponseEntity<Items> findAll() throws Exception {
        return new ResponseEntity<>(new Items(dummyContacts), HttpStatus.OK);
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
    public ResponseEntity<Items> searchDummies(@RequestParam(value = "term") String term) {
        List<ContactDocument> results = dummyContacts.stream()
                                                     .filter(contact -> contact.getFirstName()
                                                                               .toLowerCase()
                                                                               .contains(term.toLowerCase()))
                                                     .collect(Collectors.toList());
        return new ResponseEntity<>(new Items(results), HttpStatus.OK);
    }
}
