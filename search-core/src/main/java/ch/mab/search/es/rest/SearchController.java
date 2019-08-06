package ch.mab.search.es.rest;

import ch.mab.search.es.business.SearchService;
import ch.mab.search.es.model.SearchStrike;
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

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable UUID id) throws IOException {
        Optional<SecasignboxDocument> result = service.findById("secasignbox", id);

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

    @GetMapping(value = "search/{term}")
    public ResponseEntity<List<SearchStrike>> findDocumentsByTerm(@PathVariable String term) throws Exception {
        return new ResponseEntity<>(service.queryFuzzyAndPhraseByTermOnDocNameAndDocContent("secasignbox", term), HttpStatus.OK);
    }

    @PostMapping(value = "search/query")
    public ResponseEntity<List<SearchStrike>> findDocumentsBySearchQuery(@RequestBody SearchQuery query) throws Exception {
        return new ResponseEntity<>(service.queryBySearchQuery("secasignbox", query), HttpStatus.OK);
    }
}
