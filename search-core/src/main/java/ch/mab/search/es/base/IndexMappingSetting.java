package ch.mab.search.es.base;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

public class IndexMappingSetting {

    public static XContentBuilder mappingDefault() throws IOException {
        return XContentFactory.jsonBuilder()
        .startObject()
            .startObject("properties")
                .startObject("documentId")
                    .field("type", "text")
                .endObject()
                .startObject("documentName")
                    .field("type", "text")
                .endObject()
                .startObject("uploadDate")
                    .field("type", "date")
                .endObject()
                .startObject("documentContent")
                    .field("type", "text")
                .endObject()
            .endObject()
        .endObject();
    }

    public static XContentBuilder mappingAnalyzer(String analyzer) throws IOException {
        return XContentFactory.jsonBuilder()
        .startObject()
            .startObject("properties")
                .startObject("documentId")
                    .field("type", "text")
                .endObject()
                .startObject("documentName")
                    .field("type", "text")
                    .field("analyzer", analyzer)
                .endObject()
                .startObject("uploadDate")
                    .field("type", "date")
                .endObject()
                .startObject("documentContent")
                    .field("type", "text")
                    .field("analyzer", "german")
                .endObject()
            .endObject()
        .endObject();
    }

    public XContentBuilder shingleMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();{
            builder.startObject("properties");{
                builder.startObject("documentId");{
                    builder.field("type", "text");
                }builder.endObject();
            }
            {
                builder.startObject("documentName");{
                builder.field("search_analyzer", "analyzer_shingle");
                builder.field("index_analyzer", "analyzer_shingle");
                builder.field("type", "text");
            }builder.endObject();
            }
            {
                builder.startObject("uploadDate");{
                builder.field("type", "date");
            }builder.endObject();
            }
            {
                builder.startObject("documentContent");{
                builder.field("type", "text");
            }builder.endObject();
            }builder.endObject();
        }
        builder.endObject();
        return builder;
    }
}
