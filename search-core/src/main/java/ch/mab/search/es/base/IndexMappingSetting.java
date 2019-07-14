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

    public static XContentBuilder mappingAnalyzer() throws IOException {
        return XContentFactory.jsonBuilder()
        .startObject()
            .startObject("properties")
                .startObject("documentId")
                    .field("type", "text")
                .endObject()
                .startObject("documentName")
                    .field("type", "text")
                    .field("analyzer", "underscore_analyzer")
                .endObject()
                .startObject("uploadDate")
                    .field("type", "date")
                .endObject()
                .startObject("documentContent")
                    .field("type", "text")
                    .field("analyzer", "rebuilt_standard_analyzer")
                .endObject()
            .endObject()
        .endObject();
    }

    public static XContentBuilder mappingAnalyzer(String docNameAnalyzer) throws IOException {
        return mappingAnalyzer(docNameAnalyzer, "german");
    }


    public static XContentBuilder mappingAnalyzer(String docNameAnalyzer, String docContentAnalyzer) throws IOException {
        return XContentFactory.jsonBuilder()
        .startObject()
            .startObject("properties")
                .startObject("documentId")
                    .field("type", "text")
                .endObject()
                .startObject("documentName")
                    .field("type", "text")
                    .field("analyzer", docNameAnalyzer)
                .endObject()
                .startObject("uploadDate")
                    .field("type", "date")
                .endObject()
                .startObject("documentContent")
                    .field("type", "text")
                    .field("analyzer", docContentAnalyzer)
                .endObject()
            .endObject()
        .endObject();
    }

    public static XContentBuilder mappingShingle() throws IOException {
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

    /*
        Use the "rebuilt_standard_analyzer" analyzer for the field documentContent field. It consists of
        the standard tokenizer, a lowercase and some german filters. The standard tokenizer divides text
        into terms on word boundaries, as defined by the Unicode Text Segmentation algorithm. It removes
        most punctuation symbols. The lowercase filter enforce that every token will be filtered by the
        german filters. The german filters stemms tokens and removes stop words.

        Use the "underscore_analyzer" for the field documentName. It splits the documentName at the
        character underscore or whitespace.
     */
    public static XContentBuilder settingGermanRebuiltAndUnderscoreAnalyzer() throws IOException {
        return XContentFactory.jsonBuilder()
        .startObject()
                .startObject("analysis")
                    .startObject("filter")
                        .startObject("german_stop")
                            .field("type", "stop")
                            .field("stopwords", "_german_")
                        .endObject()
                        .startObject("german_stemmer")
                            .field("type", "stemmer")
                            .field("language", "light_german")
                        .endObject()
                    .endObject()
                    .startObject("analyzer")
                        .startObject("rebuilt_standard_analyzer")
                            .field("type", "custom")
                            .field("tokenizer", "standard")
                            .field("filter", "lowercase, german_stop, german_stemmer")
                        .endObject()
                        .startObject("underscore_analyzer")
                            .field("type", "custom")
                            .field("tokenizer", "underscore_tokenizer")
                            .field("filter", "lowercase, german_stop, german_stemmer")
                        .endObject()
                    .endObject()
                    .startObject("tokenizer")
                        .startObject("underscore_tokenizer")
                            .field("type", "char_group")
                            .field("tokenize_on_chars", "_,whitespace")
                        .endObject()
                    .endObject()
                .endObject()
            .endObject();
    }
}
