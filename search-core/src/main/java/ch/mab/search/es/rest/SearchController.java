package ch.mab.search.es.rest;

import ch.mab.search.es.business.SecasignboxService;
import ch.mab.search.es.model.Documents;
import ch.mab.search.es.model.DocumentsHighlighted;
import ch.mab.search.es.model.SearchQuery;
import ch.mab.search.es.model.SecasignboxDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/documents")
public class SearchController {

    @Autowired
    private SecasignboxService service;

    @PostMapping
    public ResponseEntity createSecasignboxDocument(@RequestBody SecasignboxDocument document) throws Exception {
        return new ResponseEntity(service.indexDocument("secasignbox", document), HttpStatus.CREATED);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity findById(@PathVariable UUID id) throws IOException {
        Optional<SecasignboxDocument> result = service.findById("secasignbox", id);

        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<SecasignboxDocument> updateSecasignboxDocument(@RequestBody SecasignboxDocument document) throws IOException {
        Optional<SecasignboxDocument> result = service.updateDocument("secasignbox", document);
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    @GetMapping
//    public ResponseEntity<List<SecasignboxDocument>> findAll() throws Exception {
    public ResponseEntity<Documents> findAll() throws Exception {
        return new ResponseEntity<>(new Documents(service.findAll("secasignbox")), HttpStatus.OK);
    }

    @GetMapping(value = "search/{term}")
    public ResponseEntity<Documents> findDocumentsByTerm(@PathVariable String term) throws Exception {
        return new ResponseEntity<>(new Documents(service.findByQueryInDocumentContent("secasignbox", term)), HttpStatus.OK);
    }

    @GetMapping(value = "search/highlighted/{term}")
    public ResponseEntity<DocumentsHighlighted> findDocumentsByTermHighlighted(@PathVariable String term) throws Exception {
        return new ResponseEntity<>(new DocumentsHighlighted(service.findByTermHighlighted("secasignbox", term)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SecasignboxDocument> deleteSecasignboxDocument(@PathVariable UUID id) throws Exception {

        Optional<SecasignboxDocument> result = service.deleteDocument("secasignbox", id);
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    @PostMapping(value = "search/highlighted/query")
    public ResponseEntity<DocumentsHighlighted> findDocumentsByQueryHighlighted(@RequestBody SearchQuery query) throws Exception {
        return new ResponseEntity<>(new DocumentsHighlighted(service.findByQueryHighlighted("secasignbox", query)), HttpStatus.OK);
    }
}
