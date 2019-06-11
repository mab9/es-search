package ch.mab.search.es.rest;

import ch.mab.search.es.business.SearchService;
import ch.mab.search.es.model.SearchHighlights;
import ch.mab.search.es.model.SearchQuery;
import ch.mab.search.es.model.SecasignboxDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/documents")
public class SearchController {

    @Autowired
    private SearchService service;

    @PostMapping
    public ResponseEntity createDocument(@RequestBody SecasignboxDocument document) throws Exception {
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
    public ResponseEntity<SecasignboxDocument> updateDocument(@RequestBody SecasignboxDocument document) throws IOException {
        Optional<SecasignboxDocument> result = service.updateDocument("secasignbox", document);
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SecasignboxDocument> deleteDocument(@PathVariable UUID id) throws Exception {
        Optional<SecasignboxDocument> result = service.deleteDocument("secasignbox", id);
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SecasignboxDocument>> findAll() throws Exception {
        return new ResponseEntity<>(service.findAll("secasignbox"), HttpStatus.OK);
    }

    @GetMapping(value = "search/{term}")
    public ResponseEntity<List<SearchHighlights>> findDocumentsByTerm(@PathVariable String term) throws Exception {
        return new ResponseEntity<>(service.findByTermHighlighted("secasignbox", term), HttpStatus.OK);
    }

    @GetMapping(value = "search/highlighted/{term}")
    public ResponseEntity<List<SearchHighlights>> findDocumentsByTermHighlighted(@PathVariable String term) throws Exception {
        return new ResponseEntity<>(service.findByTermHighlighted("secasignbox", term), HttpStatus.OK);
    }

    // todo check get with payload (frontend)
    @PostMapping(value = "search/highlighted/query")
    public ResponseEntity<List<SearchHighlights>> findDocumentsByQueryHighlighted(@RequestBody SearchQuery query) throws Exception {
        return new ResponseEntity<>(service.findByQueryHighlighted("secasignbox", query), HttpStatus.OK);
    }
}
