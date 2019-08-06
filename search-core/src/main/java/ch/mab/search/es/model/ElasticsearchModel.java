package ch.mab.search.es.model;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

import static ch.mab.search.es.base.SecasignBoxConstants.*;

public class ElasticsearchModel {

    public static XContentBuilder mappingDefaultContactDoc() {
        try {
            return XContentFactory.jsonBuilder()
            .startObject()
                .startObject("properties")
                    .startObject("firstName")
                        .field("type", "text")
                    .endObject()
                    .startObject("lastName")
                        .field("type", "text")
                    .endObject()
                    .startObject("technologies")
                         .field("type", "nested")
                    .endObject()
                .endObject()
            .endObject();
        } catch (IOException e) {
            throw new RuntimeException("Could not build the object mapping.", e);
        }
    }

    public static XContentBuilder mappingDefaultSecasignDoc() {
        try {
            return XContentFactory.jsonBuilder()
            .startObject()
                .startObject("properties")
                    .startObject("documentId")
                        .field("type", "text")
                    .endObject()
                    .startObject(FIELD_SECASIGN_DOC_NAME)
                        .field("type", "text")
                    .endObject()
                    .startObject(FIELD_SECASIGN_DOC_UPLOAD_DATE)
                        .field("type", "date")
                    .endObject()
                    .startObject(FIELD_SECASIGN_DOC_CONTENT)
                        .field("type", "text")
                    .endObject()
                .endObject()
            .endObject();
        } catch (IOException e) {
            throw new RuntimeException("Could not build the object mapping.", e);
        }
    }

    public static XContentBuilder mappingAnalyzerSecasignDoc() {
        try {
            return XContentFactory.jsonBuilder()
            .startObject()
                .startObject("properties")
                    .startObject("documentId")
                        .field("type", "text")
                    .endObject()
                    .startObject(FIELD_SECASIGN_DOC_NAME)
                        .field("type", "text")
                        .field("analyzer", "underscore_analyzer")
                    .endObject()
                    .startObject(FIELD_SECASIGN_DOC_UPLOAD_DATE)
                        .field("type", "date")
                    .endObject()
                    .startObject(FIELD_SECASIGN_DOC_CONTENT)
                        .field("type", "text")
                        .field("analyzer", "rebuilt_standard_analyzer")
                    .endObject()
                .endObject()
            .endObject();
        } catch (IOException e) {
            throw new RuntimeException("Could not build the object mapping.", e);
        }
    }

    public static XContentBuilder mappingAnalyzerSecasignDoc(String docNameAnalyzer) {
        return mappingAnalyzerSecasignDoc(docNameAnalyzer, "german");
    }


    public static XContentBuilder mappingAnalyzerSecasignDoc(String docNameAnalyzer, String docContentAnalyzer) {
        try {
            return XContentFactory.jsonBuilder()
            .startObject()
                .startObject("properties")
                    .startObject("documentId")
                        .field("type", "text")
                    .endObject()
                    .startObject(FIELD_SECASIGN_DOC_NAME)
                        .field("type", "text")
                        .field("analyzer", docNameAnalyzer)
                    .endObject()
                    .startObject(FIELD_SECASIGN_DOC_UPLOAD_DATE)
                        .field("type", "date")
                    .endObject()
                    .startObject(FIELD_SECASIGN_DOC_CONTENT)
                        .field("type", "text")
                        .field("analyzer", docContentAnalyzer)
                    .endObject()
                .endObject()
            .endObject();
        } catch (IOException e) {
            throw new RuntimeException("Could not build the object mapping.", e);
        }
    }

    public static XContentBuilder mappingShingle() {
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder();
             builder.startObject();{
            builder.startObject("properties");{
                builder.startObject("documentId");{
                    builder.field("type", "text");
                }builder.endObject();
            }
            {
                builder.startObject(FIELD_SECASIGN_DOC_NAME);{
                builder.field("search_analyzer", "analyzer_shingle");
                builder.field("index_analyzer", "analyzer_shingle");
                builder.field("type", "text");
            }builder.endObject();
            }
            {
                builder.startObject(FIELD_SECASIGN_DOC_UPLOAD_DATE);{
                builder.field("type", "date");
            }builder.endObject();
            }
            {
                builder.startObject(FIELD_SECASIGN_DOC_CONTENT);{
                builder.field("type", "text");
            }builder.endObject();
            }builder.endObject();
        }
        builder.endObject();
        return builder;
        } catch (IOException e) {
                        throw new RuntimeException("Could not build the object mapping.", e);

        }
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
    public static XContentBuilder settingGermanRebuiltAndUnderscoreAnalyzerSecasignDoc() {
        try {
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
        } catch (IOException e) {
            throw new RuntimeException("Could not build the object mapping.", e);
        }
    }
}
