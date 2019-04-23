package ch.mab.search.es.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public abstract class AbstractIndex {

    protected final Gson gson = new Gson();

    @Autowired
    public ObjectMapper objectMapper;

    public abstract XContentBuilder createMappingObject() throws IOException;

    @Autowired
    public RestHighLevelClient client;

}