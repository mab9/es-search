package ch.mab.search.es.rest;

import ch.mab.search.es.model.ElasticsearchModel;
import ch.mab.search.es.business.IndexService;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/indices")
public class IndexController {

    @Autowired
    private IndexService indexService;

    public IndexController() {
    }

    @GetMapping(value = "{index}")
    public ResponseEntity isIndexExisting(@PathVariable String index) throws IOException {
        return new ResponseEntity<>(indexService.isIndexExisting(index), HttpStatus.OK);
    }

    @PostMapping(value = "{index}")
    public ResponseEntity<CreateIndexResponse> createIndex(@PathVariable String index) throws IOException {
        CreateIndexResponse response = indexService.createIndex(index);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "{index}")
    public ResponseEntity<AcknowledgedResponse> updateIndexMapping(@PathVariable String index) throws IOException {
        AcknowledgedResponse response = indexService.updateMapping(index, ElasticsearchModel.mappingDefaultContactDoc());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "{index}")
    public ResponseEntity<AcknowledgedResponse> deleteIndex(@PathVariable String index) throws IOException {
        AcknowledgedResponse response = indexService.deleteIndex(index);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}