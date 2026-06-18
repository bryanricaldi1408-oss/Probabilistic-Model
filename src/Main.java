import evaluation.Evaluator;
import indexing.Document;
import indexing.InvertedIndex;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import retrieval.BIMModel;
import retrieval.BM10;
import retrieval.BM25;
import retrieval.SearchResult;
import retrieval.TwoPoisson;
import utils.FileLoader;
import utils.QueryLoader;
import utils.RelevanceLoader;

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

        // Configurable Parameters
        double k1 = 1.2;
        double b = 0.75;
        double kTwoPoisson = 1.5;

        // Initialize Evaluator
        Evaluator evaluator = new Evaluator();

        int choice = 3; // Default to BM25
        System.out.println("\nSelect Model:");
        System.out.println("1. BIM Model");
        System.out.println("2. Two-Poisson Model");
        System.out.println("3. BM25 Model");
        System.out.println("4. BM10 Model");

        System.out.print("Choice (1/2/3/4): ");
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid input, defaulting to BM25.");
        }

        if (choice == 2) {
            System.out.print("Enter k for Two-Poisson (default 1.5): ");
            try {
                String input = scanner.nextLine();
                if (!input.isEmpty())
                    kTwoPoisson = Double.parseDouble(input);
            } catch (Exception e) {
                System.out.println("Invalid k, using default.");
            }
        } else if (choice == 3) {
            System.out.print("Enter k1 (default 1.2): ");
            try {
                String input = scanner.nextLine();
                if (!input.isEmpty())
                    k1 = Double.parseDouble(input);
            } catch (Exception e) {
                System.out.println("Invalid k1, using default.");
            }

            System.out.print("Enter b (default 0.75): ");
            try {
                String input = scanner.nextLine();
                if (!input.isEmpty())
                    b = Double.parseDouble(input);
            } catch (Exception e) {
                System.out.println("Invalid b, using default.");
            }
        }else if(choice==4){
            System.out.print("Enter k1 (default 1.2): ");
            try {
                String input = scanner.nextLine();
                if (!input.isEmpty())
                    k1 = Double.parseDouble(input);
            } catch (Exception e) {
                System.out.println("Invalid k1, using default.");
            }
        } 

        // Initialize Models
        BIMModel bim = new BIMModel(index, relevanceMap);
        TwoPoisson tp = new TwoPoisson(index, relevanceMap, kTwoPoisson);
        BM25 bm25 = new BM25(index, relevanceMap, k1, b);
        BM10 bm10 = new BM10(index,relevanceMap,k1);
        while (true) {
            String modelName;
            if (choice == 1)
                modelName = "BIM";
            else if (choice == 2)
                modelName = "Two-Poisson";
            else if (choice==4)
                modelName = "BM10";
            else
                modelName = "BM25";

            System.out.print("\n[" + modelName
                    + "] Query (type 'switch' to change model, 'eval' for evaluation, 'exit' to quit): ");
            String query = scanner.nextLine();

            if (query.equalsIgnoreCase("exit")) {
                break;
            }

            if (query.equalsIgnoreCase("switch")) {
                System.out.println("\nSelect Model:");
                System.out.println("1. BIM Model");
                System.out.println("2. Two-Poisson Model");
                System.out.println("3. BM25 Model");
                System.out.println("4. BM10 Model");
                System.out.print("Choice (1/2/3/4): ");
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (Exception e) {
                    System.out.println("Invalid input.");
                    continue;
                }

                if (choice == 2) {
                    System.out.print("Enter k for Two-Poisson (default 1.5): ");
                    try {
                        String input = scanner.nextLine();
                        if (!input.isEmpty())
                            kTwoPoisson = Double.parseDouble(input);
                    } catch (Exception e) {
                        System.out.println("Invalid k, using default.");
                    }
                    tp = new TwoPoisson(index, relevanceMap, kTwoPoisson);
                } else if (choice == 3) {
                    System.out.print("Enter k1 (default 1.2): ");
                    try {
                        String input = scanner.nextLine();
                        if (!input.isEmpty())
                            k1 = Double.parseDouble(input);
                    } catch (Exception e) {
                        System.out.println("Invalid k1, using default.");
                    }

                    System.out.print("Enter b (default 0.75): ");
                    try {
                        String input = scanner.nextLine();
                        if (!input.isEmpty())
                            b = Double.parseDouble(input);
                    } catch (Exception e) {
                        System.out.println("Invalid b, using default.");
                    }

                    bm25 = new BM25(index, relevanceMap, k1, b);
                }else if (choice==4){
                    System.out.print("Enter k1 (default 1.2): ");
                    try {
                        String input = scanner.nextLine();
                        if (!input.isEmpty())
                            k1 = Double.parseDouble(input);
                    } catch (Exception e) {
                        System.out.println("Invalid k1, using default.");
                    }
                }
                continue;
            }

            // ========================
            // EVALUASI
            // ========================
            if (query.equalsIgnoreCase("eval")) {
                System.out.println("\n=== Mode Evaluasi ===");
                System.out.println("1. Evaluasi satu query (masukkan query)");
                System.out.println("2. Evaluasi seluruh query Cranfield (MAP + 11-Point Avg)");
                System.out.print("Pilihan (1/2): ");

                String evalChoice = scanner.nextLine().trim();

                if (evalChoice.equals("1")) {
                    System.out.print("Masukkan query: ");
                    String evalQuery = scanner.nextLine();

                    int matchedQueryId = queryLoader.findMostSimilarQuery(evalQuery);
                    System.out.println("Matched Cranfield Query ID: " + matchedQueryId);

                    List<SearchResult> results;
                    if (choice == 1) {
                        Set<Integer> relevantDocs = relevanceMap.getOrDefault(matchedQueryId, new HashSet<>());
                        results = bim.search(evalQuery, relevantDocs);
                    } else if (choice == 2) {
                        results = tp.search(matchedQueryId, evalQuery);
                    }else if (choice == 4) {
                        results = bm10.search(matchedQueryId, query);
                    } else {
                        results = bm25.search(matchedQueryId, evalQuery);
                    }

                    Set<Integer> relevantDocs = relevanceMap.getOrDefault(matchedQueryId, new HashSet<>());
                    evaluator.evaluateSingleQuery(results, relevantDocs, matchedQueryId);

                } else if (evalChoice.equals("2")) {
                    System.out.println("\nMenjalankan evaluasi pada seluruh query Cranfield...");
                    System.out.println("Model: " + modelName);
                    System.out.println("Mohon tunggu...\n");

                    Map<Integer, String> rawQueries = queryLoader.getRawQueries();
                    Map<Integer, List<SearchResult>> allResults = new HashMap<>();

                    List<Integer> queryIds = new ArrayList<>(rawQueries.keySet());
                    Collections.sort(queryIds);

                    for (int qId : queryIds) {
                        String qText = rawQueries.get(qId);

                        List<SearchResult> results;
                        if (choice == 1) {
                            Set<Integer> relevantDocs = relevanceMap.getOrDefault(qId, new HashSet<>());
                            results = bim.search(qText, relevantDocs);
                        } else if (choice == 2) {
                            results = tp.search(qId, qText);
                        } else {
                            results = bm25.search(qId, qText);
                        }

                        allResults.put(qId, results);
                    }

                    evaluator.evaluateAll(allResults, relevanceMap);

                } else {
                    System.out.println("Pilihan tidak valid.");
                }
                continue;
            }

            // Auto match query
            int matchedQueryId = queryLoader.findMostSimilarQuery(query);
            System.out.println("Matched Cranfield Query ID: " + matchedQueryId);

            List<SearchResult> results;
            if (choice == 1) {
                Set<Integer> relevantDocs = relevanceMap.getOrDefault(matchedQueryId,new HashSet<>());
                results = bim.search(query,relevantDocs);
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

            // Tampilkan evaluasi otomatis setelah hasil pencarian
            Set<Integer> relevantDocs = relevanceMap.getOrDefault(matchedQueryId, new HashSet<>());
            if (!relevantDocs.isEmpty()) {
                evaluator.evaluateSingleQuery(results, relevantDocs, matchedQueryId);
            }
        }

        scanner.close();
        System.out.println("Program terminated.");
    }
}
