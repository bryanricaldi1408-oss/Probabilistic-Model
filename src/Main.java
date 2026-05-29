import indexing.Document;
import indexing.InvertedIndex;
import retrieval.BIMModel;
import retrieval.SearchResult;
import utils.FileLoader;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // path dataset
        String datasetPath = "../dataset/Cranfield";

        // load documents
        FileLoader loader = new FileLoader();

        List<Document> documents = loader.loadDocuments(datasetPath);

        System.out.println("Documents loaded: " + documents.size());

        // build index
        InvertedIndex index = new InvertedIndex();

        index.build(documents);

        System.out.println("Vocabulary size: " + index.getIndex().size());

        System.out.println(
                "Average document length: "
                        + String.format(
                                "%.2f",
                                index.getAverageDocumentLength()));

        // initialize BIM
        BIMModel bim = new BIMModel(index);

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.print("\nQuery: ");

            String query = scanner.nextLine();

            if (query.equalsIgnoreCase("exit")) {
                break;
            }

            List<SearchResult> results = bim.search(query);

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
