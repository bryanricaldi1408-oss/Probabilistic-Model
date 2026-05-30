package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class RelevanceLoader {

    /*
     * queryId -> relevant doc ids
     */
    private final Map<Integer,Set<Integer>> relevanceMap;

    public RelevanceLoader() {
        relevanceMap = new HashMap<>();
    }

    public Map<Integer, Set<Integer>>
    loadRelevance(String filePath) {

        try (BufferedReader reader =new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line =reader.readLine())!= null) {

                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts =line.split("\\s+");

                if (parts.length < 2) {
                    continue;
                }

                int queryId =Integer.parseInt(parts[0]);

                int docId =Integer.parseInt(parts[1]);

                int relevanceScore =(parts.length >= 3)? Integer.parseInt(parts[2]): 1;

                // relevance > 0 dianggap relevan
                if (relevanceScore > 0) {
                    relevanceMap.putIfAbsent(queryId,new HashSet<>());
                    relevanceMap.get(queryId).add(docId);
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading cranqrel: " + e.getMessage());
        }

        return relevanceMap;
    }
}