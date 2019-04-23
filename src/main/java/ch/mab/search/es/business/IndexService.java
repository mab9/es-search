package ch.mab.search.es.business;

import com.google.gson.Gson;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class IndexService {

    @Autowired
    private RestHighLevelClient client;

    public long getTotalHits(String index) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return searchResponse.getHits().getTotalHits().value;
    }

    public CreateIndexResponse createIndex(String index) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        appendSettings(request);
        return client.indices().create(request, RequestOptions.DEFAULT);
    }

    public CreateIndexResponse createIndex(String index, XContentBuilder builder) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        appendSettings(request);
        request.mapping(builder);
        return client.indices().create(request, RequestOptions.DEFAULT);
    }

    private void appendSettings(CreateIndexRequest request) {
        request.settings(Settings.builder()
                                 .put("index.number_of_shards", 3)
                                 .put("index.number_of_replicas", 2));
    }

    public AcknowledgedResponse updateMapping(String index, XContentBuilder builder) throws IOException {
        PutMappingRequest request = new PutMappingRequest(index);
        request.source(builder);
        return client.indices().putMapping(request, RequestOptions.DEFAULT);
    }

    public AcknowledgedResponse deleteIndex(String index) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        return client.indices().delete(request, RequestOptions.DEFAULT);
    }

    public boolean isIndexExisting(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }
}
