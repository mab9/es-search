package ch.mab.search.es.rest;

import ch.mab.search.es.business.SecasignboxService;
import ch.mab.search.es.model.Metadata;
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
@RequestMapping("/api/v1/secasignbox")
public class SearchController {

    @Autowired
    private SecasignboxService service;

    @PostMapping
    public ResponseEntity createSecasignboxDocument(@RequestBody SecasignboxDocument document) throws Exception {
        return new ResponseEntity(service.indexSecasignboxDocument("secasignbox", document), HttpStatus.CREATED);
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
        Optional<SecasignboxDocument> result = service.updateSecasignboxDocument("secasignbox", document);
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SecasignboxDocument>> findAll() throws Exception {
        return new ResponseEntity<>(service.findAll("secasignbox"), HttpStatus.OK);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<SecasignboxDocument>> search(@RequestParam(value = "metadata") Metadata metadata) throws
            IOException {
        return new ResponseEntity<>(service.searchByMetadata("secasignbox", metadata), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SecasignboxDocument> deleteSecasignboxDocument(@PathVariable UUID id) throws Exception {

        Optional<SecasignboxDocument> result = service.deleteSecasignboxDocument("secasignbox", id);
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }
}
