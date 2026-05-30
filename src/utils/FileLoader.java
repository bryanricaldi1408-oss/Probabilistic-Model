package utils;

import indexing.Document;
import preprocessing.PorterStemmer;
import preprocessing.StopwordRemover;
import preprocessing.Tokenizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {

    private final Tokenizer tokenizer;
    private final StopwordRemover stopwordRemover;
    private final PorterStemmer stemmer;

    public FileLoader() {
        tokenizer =new Tokenizer();
        stopwordRemover =new StopwordRemover();
        stemmer =new PorterStemmer();
    }

    public List<Document>loadDocuments(String filePath) {

        List<Document> documents = new ArrayList<>();

        try (BufferedReader reader =new BufferedReader( new FileReader(filePath))) {

            String line;

            int currentDocId = -1;

            StringBuilder content =new StringBuilder();

            while ((line =reader.readLine())!= null) {

                line = line.trim();

                // awal dokumen baru
                if (line.startsWith(".I")) {

                    // simpan dokumen sebelumnya
                    if (currentDocId != -1) {
                        documents.add(createDocument(currentDocId,content.toString()));
                    }

                    currentDocId =Integer.parseInt(line.substring(2).trim());
                    content =new StringBuilder();
                }
                // skip metadata
                else if (line.startsWith(".")) {
                    continue;
                }
                else {
                    content.append(line).append(" ");
                }
            }

            // dokumen terakhir
            if (currentDocId != -1) {
                documents.add(createDocument(currentDocId,content.toString()));
            }

        } catch (IOException e) {
            System.out.println("Error reading cran.all: "+ e.getMessage());
        }

        return documents;
    }

    private Document createDocument(int docId,String text) {

        List<String> tokens =stopwordRemover.removeStopwords( tokenizer.tokenize(text));

        List<String> stemmedTokens = new ArrayList<>();

        for (String token :tokens) {
            stemmedTokens.add(stemmer.stem(token));
        }

        return new Document(docId,text,stemmedTokens);
    }
}
