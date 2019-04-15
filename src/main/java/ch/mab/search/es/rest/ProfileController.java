package ch.mab.search.es.rest;

import ch.mab.search.es.business.ProfileService;
import ch.mab.search.es.document.ProfileDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/profiles")
public class ProfileController {

    @Autowired
    private ProfileService service;


    public ProfileController() {}

    @PostMapping
    public ResponseEntity createProfile(
            @RequestBody ProfileDocument document) throws Exception {

        return new ResponseEntity(service.createProfile(document), HttpStatus.CREATED);
    }
}