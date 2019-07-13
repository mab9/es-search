package ch.mab.search.es.business;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.*;
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

    public GetIndexResponse getIndex(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);
        return client.indices().get(request, RequestOptions.DEFAULT);
    }

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

    public CreateIndexResponse createIndex(String index, Settings settings) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(settings);
        return client.indices().create(request, RequestOptions.DEFAULT);
    }

    public CreateIndexResponse createIndex(String index, XContentBuilder builder) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.mapping(builder);
        return client.indices().create(request, RequestOptions.DEFAULT);
    }

    public CreateIndexResponse createIndex(String index, XContentBuilder mapping, XContentBuilder settings) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(settings);
        request.mapping(mapping);
        return client.indices().create(request, RequestOptions.DEFAULT);
    }

    public CreateIndexResponse createIndex(String index, XContentBuilder builder, Settings settings) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(settings);
        request.mapping(builder);
        return client.indices().create(request, RequestOptions.DEFAULT);
    }

    // TODO beurteilen ob es Shards und Replicas auf einer Kiste benötigt
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
