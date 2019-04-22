package ch.mab.search.es.rest;

import ch.mab.search.es.business.ProfileService;
import ch.mab.search.es.document.ProfileDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    @Autowired
    private ProfileService service;

    public ProfileController() {
    }

    @PostMapping
    public ResponseEntity createProfile(@RequestBody ProfileDocument document) throws Exception {
        return new ResponseEntity(service.createProfile(document), HttpStatus.CREATED);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity findById(@PathVariable UUID id) throws IOException {
        Optional<ProfileDocument> result;
        result = service.findById(id);

        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<ProfileDocument> updateProfile(@RequestBody ProfileDocument document) throws IOException {
        Optional<ProfileDocument> result;
            result = service.updateProfile(document);
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProfileDocument>> findAll() throws Exception {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<ProfileDocument>> search(@RequestParam(value = "technology") String technology) throws
            IOException {
        return new ResponseEntity<>(service.searchByTechnology(technology), HttpStatus.OK);
    }
}