package ch.mab.search.ocr.business;

import ch.mab.search.ocr.api.Ocr;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

@Component
public class OcrTika implements Ocr {

    private final PDFParser pdfparser;

    public OcrTika() {
        pdfparser = new PDFParser();
    }

    @Override
    public String extractText(File pdf) throws IOException {
        Objects.requireNonNull(pdf);

        if(!pdf.canRead()) {
            throw new IllegalArgumentException("Not able to read from file: " + pdf.getAbsolutePath() +  " , text can not be extracted.");
        }

        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(pdf);
        ParseContext pcontext = new ParseContext();

        try {
            pdfparser.parse(inputstream, handler, metadata,pcontext);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        }

        /*
        //getting metadata of the model
        System.out.println("Metadata of the PDF:");
        String[] metadataNames = metadata.names();

        for(String name : metadataNames) {
            System.out.println(name+ " : " + metadata.get(name));
        }
*/
        return sanitizeExtractedOutput(handler.toString());
    }


    private String sanitizeExtractedOutput(String rawText) {
        StringBuilder stringBuilder = new StringBuilder(10000);
        for (String line : rawText.split("\\r?\\n")) {
            // Only add non blank lines
            if (line != null && !line.isBlank()) {
                stringBuilder.append(" ").append(line);
            }
        }
        return stringBuilder.toString();
    }
}
