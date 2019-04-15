package ch.mab.search.es.business;

import ch.mab.search.es.document.ProfileDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String INDEX = "lead";
    private static final String TYPE = "lead";

    @Autowired
    public ProfileService() {}

    public String createProfile(ProfileDocument document) throws Exception {

        UUID uuid = UUID.randomUUID();
        document.setId(uuid.toString());

        //Map<String, Object> documentMapper = objectMapper.convertValue(document, Map.class);

        //IndexRequest request = new IndexRequest(INDEX, TYPE, document.getId())
                //.source(documentMapper);

        IndexRequest request = new IndexRequest("posts");
        request.id("1");
        String jsonString = "{" +
                            "\"user\":\"kimchy\"," +
                            "\"postDate\":\"2013-01-30\"," +
                            "\"message\":\"trying out Elasticsearch\"" +
                            "}";
        request.source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

        return indexResponse
                .getResult()
                .name();
    }
}