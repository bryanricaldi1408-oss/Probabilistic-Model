package utils;

import preprocessing.PorterStemmer;
import preprocessing.StopwordRemover;
import preprocessing.Tokenizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class QueryLoader {

    private final Map<Integer, List<String>> queryMap;

    private final Tokenizer tokenizer;
    private final StopwordRemover stopwordRemover;
    private final PorterStemmer stemmer;

    public QueryLoader() {

        queryMap = new HashMap<>();

        tokenizer = new Tokenizer();

        stopwordRemover = new StopwordRemover();

        stemmer = new PorterStemmer();
    }

    public Map<Integer, List<String>> loadQueries(String filePath) {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;

            int currentQueryId = -1;

            StringBuilder content = new StringBuilder();

            while ((line = reader.readLine()) != null) {

                line = line.trim();

                if (line.startsWith(".I")) {

                    // simpan query sebelumnya
                    if (currentQueryId != -1) {
                        queryMap.put(currentQueryId,preprocess(content.toString()));
                    }

                    currentQueryId = Integer.parseInt(line.substring(2).trim());

                    content = new StringBuilder();
                }

                else if (line.startsWith(".")) {
                    continue;
                }

                else {
                    content.append(line).append(" ");
                }
            }

            // query terakhir
            if (currentQueryId != -1) {
                queryMap.put(currentQueryId,preprocess(content.toString()));
            }

        } catch (IOException e) {

            System.out.println("Error reading cran.qry: "+ e.getMessage());
        }

        return queryMap;
    }

    public int findMostSimilarQuery(String userQuery) {

        List<String> userTokens = preprocess(userQuery);

        int bestQueryId = -1;
        int maxOverlap = -1;

        for (Map.Entry<Integer, List<String>> entry : queryMap.entrySet()) {

            int overlap = 0;

            List<String> queryTokens = entry.getValue();

            for (String token : userTokens) {

                if (queryTokens.contains(token)) {
                    overlap++;
                }
            }

            if (overlap > maxOverlap) {

                maxOverlap = overlap;

                bestQueryId = entry.getKey();
            }
        }

        return bestQueryId;
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