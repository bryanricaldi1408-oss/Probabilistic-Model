import indexing.Document;
import indexing.InvertedIndex;
import retrieval.BIMModel;
import retrieval.SearchResult;
import utils.FileLoader;
import utils.QueryLoader;
import utils.RelevanceLoader;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) {


        // Load documents
        FileLoader loader = new FileLoader();

        List<Document> documents = loader.loadDocuments("../cran/cran.all.1400");

        System.out.println("Documents loaded: " + documents.size());

        // Build inverted index
        InvertedIndex index = new InvertedIndex();
        index.build(documents);

        System.out.println("Vocabulary size: " +index.getIndex().size());

        System.out.println("Average document length: " + String.format("%.2f", index.getAverageDocumentLength()));

        // Load queries
        QueryLoader queryLoader = new QueryLoader();
        queryLoader.loadQueries("../cran/cran.qry");

        System.out.println("Queries loaded.");

        // Load relevance judgement
        RelevanceLoader relevanceLoader = new RelevanceLoader();

        Map<Integer, Set<Integer>> relevanceMap = relevanceLoader.loadRelevance("../cran/cranqrel");

        System.out.println("Relevance data loaded.");

        // Initialize BIM
        BIMModel bim = new BIMModel(index, relevanceMap);

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.print("\nQuery: ");
            String query = scanner.nextLine();
            if (query.equalsIgnoreCase("exit")) {
                break;
            }

            // Auto match query
            int matchedQueryId = queryLoader.findMostSimilarQuery(query);

            System.out.println("Matched Cranfield Query ID: " + matchedQueryId);

            List<SearchResult> results = bim.search(matchedQueryId, query);

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
