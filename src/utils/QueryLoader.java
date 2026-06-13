package utils;

import preprocessing.PorterStemmer;
import preprocessing.StopwordRemover;
import preprocessing.Tokenizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryLoader {

    private final Map<Integer, List<String>> queryMap;
    private final Tokenizer tokenizer;
    private final StopwordRemover stopwordRemover;
    private final PorterStemmer stemmer;

    public QueryLoader() {
        this.queryMap = new HashMap<>();
        this.tokenizer = new Tokenizer();
        this.stopwordRemover = new StopwordRemover();
        this.stemmer = new PorterStemmer();
    }

    public Map<Integer, List<String>> loadQueries(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentId = -1;
            StringBuilder content = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(".I")) {
                    if (currentId != -1) {
                        queryMap.put(currentId, preprocess(content.toString()));
                    }
                    currentId = Integer.parseInt(line.substring(2).trim());
                    content = new StringBuilder();
                } else if (line.startsWith(".")) {
                    continue;
                } else {
                    content.append(line).append(" ");
                }
            }

            if (currentId != -1) {
                queryMap.put(currentId, preprocess(content.toString()));
            }
        } catch (IOException e) {
            System.out.println("Error loading queries: " + e.getMessage());
        }
        return queryMap;
    }

    public int findMostSimilarQuery(String inputQuery) {
        List<String> inputTokens = preprocess(inputQuery);
        int bestId = -1;
        int maxOverlap = -1;

        for (Map.Entry<Integer, List<String>> entry : queryMap.entrySet()) {
            int overlap = 0;
            List<String> queryTokens = entry.getValue();
            for (String token : inputTokens) {
                if (queryTokens.contains(token)) {
                    overlap++;
                }
            }

            if (overlap > maxOverlap) {
                maxOverlap = overlap;
                bestId = entry.getKey();
            }
        }
        return bestId;
    }

    private List<String> preprocess(String text) {
        List<String> tokens = stopwordRemover.removeStopwords(tokenizer.tokenize(text));
        List<String> processed = new ArrayList<>();
        for (String token : tokens) {
            processed.add(stemmer.stem(token));
        }
        return processed;
    }
}
