import indexing.Document;
import indexing.InvertedIndex;
import retrieval.BIMModel;
import retrieval.SearchResult;
import utils.FileLoader;


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


        System.out.println("Relevance data loaded.");

        // Initialize BIM
        BIMModel bim = new BIMModel(index);

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.print("\nQuery: ");
            String query = scanner.nextLine();
            if (query.equalsIgnoreCase("exit")) {
                break;
            }

            /*
             * Tahap 1
             * Initial Retrieval
             */
            List<SearchResult> initialResults =bim.searchInitial(query);

            /*
             * Top 5 dianggap relevan
             */
            Set<Integer> pseudoRelevantDocs =bim.getPseudoRelevantDocs(initialResults, 5);

            //System.out.println("\nPseudo Relevant Docs:");
            /*
            for (Integer docId :pseudoRelevantDocs) {
                System.out.println("Doc " + docId);
            }*/

            List<SearchResult> finalResults =bim.search(query,pseudoRelevantDocs);

            System.out.println("\n=== Top Results ===");

            if (finalResults.isEmpty()) {
                System.out.println("No matching documents found.");
                continue;
            }

            int limit = Math.min(10, finalResults.size());

            for (int i = 0; i < limit; i++) {
                System.out.println((i + 1) + ". " + finalResults.get(i));
            }
        }

        scanner.close();

        System.out.println("Program terminated.");
    }
}
