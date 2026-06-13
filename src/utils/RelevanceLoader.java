package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RelevanceLoader {

    private final Map<Integer, Set<Integer>> relevanceMap;

    public RelevanceLoader() {
        this.relevanceMap = new HashMap<>();
    }

    /**
     * Loads relevance judgments from the Cranfield qrel file.
     * Format: QueryID DocID RelevanceScore
     *
     * @param filePath Path to the cranqrel file
     * @return A map where key is Query ID and value is a set of Relevant Document IDs
     */
    public Map<Integer, Set<Integer>> loadRelevance(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Cranfield qrel format is usually: query_id doc_id relevance_score
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    try {
                        int queryId = Integer.parseInt(parts[0]);
                        int docId = Integer.parseInt(parts[1]);
                        
                        // Treat any entry in the qrel as relevant (Binary Relevance)
                        // This treats scores 1, 2, 3, 4, etc. as equally relevant.
                        relevanceMap.computeIfAbsent(queryId, k -> new HashSet<>()).add(docId);
                    } catch (NumberFormatException e) {
                        // Skip lines that don't have valid integers
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading relevance file: " + e.getMessage());
        }

        return relevanceMap;
    }
}
