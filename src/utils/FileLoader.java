package utils;

import indexing.Document;
import preprocessing.PorterStemmer;
import preprocessing.StopwordRemover;
import preprocessing.Tokenizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileLoader {

    private final Tokenizer tokenizer;
    private final StopwordRemover stopwordRemover;
    private final PorterStemmer stemmer;

    public FileLoader() {
        tokenizer = new Tokenizer();
        stopwordRemover = new StopwordRemover();
        stemmer = new PorterStemmer();
    }

    public List<Document> loadDocuments(String folderPath) {

        List<Document> documents = new ArrayList<>();

        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Folder tidak ditemukan: " + folderPath);
            return documents;
        }

        File[] files = folder.listFiles((dir, name)
                -> name.toLowerCase().endsWith(".txt"));

        if (files == null) {
            return documents;
        }

        // Sort file: 1.txt, 2.txt, 3.txt ...
        java.util.Arrays.sort(files, Comparator.comparingInt(file -> {
            String name = file.getName().replace(".txt", "");
            return Integer.parseInt(name);
        }));

        for (File file : files) {

            try {

                String content = Files.readString(file.toPath());

                // preprocessing
                List<String> tokens =tokenizer.tokenize(content);

                tokens =stopwordRemover.removeStopwords(tokens);

                List<String> stemmedTokens =new ArrayList<>();

                for (String token : tokens) {
                    stemmedTokens.add(stemmer.stem(token));
                }

                // ambil doc id dari nama file
                int docId = Integer.parseInt(file.getName().replace(".txt", ""));

                Document document =
                        new Document(
                                docId,
                                content,
                                stemmedTokens
                        );

                documents.add(document);

            } catch (IOException e) {
                System.out.println("Gagal membaca file: "+ file.getName());
            }
        }

        return documents;
    }
}