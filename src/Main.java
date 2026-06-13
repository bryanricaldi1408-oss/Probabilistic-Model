import indexing.Document;
import indexing.InvertedIndex;
import retrieval.BIMModel;
import retrieval.TwoPoisson;
import retrieval.BM25;
import retrieval.SearchResult;
import utils.FileLoader;
import utils.QueryLoader;
import utils.RelevanceLoader;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

public class Main {

    public static void main(String[] args) {

        // Load documents
        FileLoader loader = new FileLoader();
        List<Document> documents = loader.loadDocuments("../cran/cran.all.1400");
        System.out.println("Documents loaded: " + documents.size());

        // Build inverted index
        InvertedIndex index = new InvertedIndex();
        index.build(documents);
        System.out.println("Vocabulary size: " + index.getIndex().size());
        System.out.println("Average document length: " + String.format("%.2f", index.getAverageDocumentLength()));

        // Load queries
        QueryLoader queryLoader = new QueryLoader();
        queryLoader.loadQueries("../cran/cran.qry");
        System.out.println("Queries loaded.");

        // Load relevance judgement
        RelevanceLoader relevanceLoader = new RelevanceLoader();
        Map<Integer, Set<Integer>> relevanceMap = relevanceLoader.loadRelevance("../cran/cranqrel");
        System.out.println("Relevance data loaded.");

        Scanner scanner = new Scanner(System.in);
        
        // Configurable Parameters for BM25
        double k1 = 1.2;
        double b = 0.75;

        // Initialize Models
        BIMModel bim = new BIMModel(index);
        TwoPoisson tp = new TwoPoisson(index, relevanceMap);
        
        int choice = 3; // Default to BM25
        System.out.println("\nSelect Model:");
        System.out.println("1. BIM Model");
        System.out.println("2. Two-Poisson Model");
        System.out.println("3. BM25 Model");
        System.out.print("Choice (1/2/3): ");
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid input, defaulting to BM25.");
        }

        if (choice == 3) {
            System.out.print("Enter k1 (default 1.2): ");
            try {
                String input = scanner.nextLine();
                if (!input.isEmpty()) k1 = Double.parseDouble(input);
            } catch (Exception e) { System.out.println("Invalid k1, using default."); }
            
            System.out.print("Enter b (default 0.75): ");
            try {
                String input = scanner.nextLine();
                if (!input.isEmpty()) b = Double.parseDouble(input);
            } catch (Exception e) { System.out.println("Invalid b, using default."); }
        }

        BM25 bm25 = new BM25(index, relevanceMap, k1, b);

        while (true) {
            String modelName;
            if (choice == 1) modelName = "BIM";
            else if (choice == 2) modelName = "Two-Poisson";
            else modelName = "BM25";

            System.out.print("\n[" + modelName + "] Query (type 'switch' to change model, 'exit' to quit): ");
            String query = scanner.nextLine();
            
            if (query.equalsIgnoreCase("exit")) {
                break;
            }
            
            if (query.equalsIgnoreCase("switch")) {
                System.out.println("\nSelect Model:");
                System.out.println("1. BIM Model");
                System.out.println("2. Two-Poisson Model");
                System.out.println("3. BM25 Model");
                System.out.print("Choice (1/2/3): ");
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (Exception e) {
                    System.out.println("Invalid input.");
                    continue;
                }
                
                if (choice == 3) {
                    System.out.print("Enter k1 (default 1.2): ");
                    try {
                        String input = scanner.nextLine();
                        if (!input.isEmpty()) k1 = Double.parseDouble(input);
                    } catch (Exception e) { System.out.println("Invalid k1, using default."); }
                    
                    System.out.print("Enter b (default 0.75): ");
                    try {
                        String input = scanner.nextLine();
                        if (!input.isEmpty()) b = Double.parseDouble(input);
                    } catch (Exception e) { System.out.println("Invalid b, using default."); }
                    
                    bm25 = new BM25(index, relevanceMap, k1, b);
                }
                continue;
            }

            // Auto match query
            int matchedQueryId = queryLoader.findMostSimilarQuery(query);
            System.out.println("Matched Cranfield Query ID: " + matchedQueryId);

            List<SearchResult> results;
            if (choice == 1) {
                Set<Integer> relevantDocs = relevanceMap.getOrDefault(matchedQueryId, new HashSet<>());
                results = bim.search(query, relevantDocs);
            } else if (choice == 2) {
                results = tp.search(matchedQueryId, query);
            } else {
                results = bm25.search(matchedQueryId, query);
            }

            System.out.println("\n=== Top Results ===");
            if (results.isEmpty()) {
                System.out.println("No matching documents found.");
                continue;
            }

            int limit = Math.min(10, results.size());
            for (int i = 0; i < limit; i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }
        }

        scanner.close();
        System.out.println("Program terminated.");
    }
}
