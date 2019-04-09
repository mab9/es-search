package ch.mab.search.es.rest;

import ch.mab.search.es.business.SearchService;
import ch.mab.search.es.model.SearchDoc;
import ch.mab.search.secasignbox.model.Archivespace;
import ch.mab.search.secasignbox.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public String saveDoc() {
        searchService.saveDoc("doc-x");
        return "doc-x\n";
    }

    @PostMapping
    public ResponseEntity saveDoc(
            @RequestBody SearchDoc doc) throws IOException {

        Archivespace space = new Archivespace("revisor");
        space.setId(doc.getArchivespaceId());
        User user = new User("mab");

        return new ResponseEntity(searchService.createDoc(doc, space, user), HttpStatus.CREATED);
    }
}
