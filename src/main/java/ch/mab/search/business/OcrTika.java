package ch.mab.search.business;


import ch.mab.search.api.Ocr;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class OcrTika implements Ocr {

    @Override
    public String extractText(File pdf) throws IOException {
        Objects.requireNonNull(pdf);

        if(!pdf.canRead()) {
            throw new IllegalArgumentException("Can not read from the file: " + pdf.getAbsolutePath() +  " where the text should be extracted");
        }

        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(pdf);
        ParseContext pcontext = new ParseContext();

        PDFParser pdfparser = new PDFParser();
        try {
            pdfparser.parse(inputstream, handler, metadata,pcontext);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        }

        //getting metadata of the document
        System.out.println("Metadata of the PDF:");
        String[] metadataNames = metadata.names();

        for(String name : metadataNames) {
            System.out.println(name+ " : " + metadata.get(name));
        }

        return handler.toString();
    }
}
