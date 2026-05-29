package indexing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedIndex {

    /*
     * term -> (docId -> term frequency)
     *
     * contoh:
     * aircraft -> {1=3, 5=2}
     * engine -> {1=2, 2=1}
     */
    private Map<String, Map<Integer, Integer>> index;

    // docId -> document length
    private Map<Integer, Integer> documentLengths;

    // semua document
    private List<Document> documents;

    // average document length
    private double averageDocumentLength;

    public InvertedIndex() {
        index = new HashMap<>();
        documentLengths = new HashMap<>();
    }

    public void build(List<Document> documents) {

        this.documents = documents;

        int totalLength = 0;

        for (Document document : documents) {

            int docId = document.getDocId();
            List<String> tokens = document.getTokens();

            // document length
            int docLength = tokens.size();

            documentLengths.put(
                    docId,
                    docLength);

            totalLength += docLength;

            /*
             * hitung tf dalam dokumen
             */
            Map<String, Integer> termFrequency = new HashMap<>();

            for (String token : tokens) {
                termFrequency.put(token, termFrequency.getOrDefault(token, 0) + 1);
            }

            /*
             * masukkan ke inverted index
             */
            for (Map.Entry<String, Integer> entry : termFrequency.entrySet()) {

                String term = entry.getKey();
                int tf = entry.getValue();

                index.putIfAbsent(term,new HashMap<>());

                index.get(term).put(docId,tf);
            }
        }

        // avgdl
        if (!documents.isEmpty()) {
            averageDocumentLength = (double) totalLength/documents.size();
        }
    }

    /*
     * term frequency
     */
    public int getTF(String term, int docId) {

        if (!index.containsKey(term)) {
            return 0;
        }

        return index.get(term).getOrDefault(docId, 0);
    }

    /*
     * document frequency
     */
    public int getDF(String term) {

        if (!index.containsKey(term)) {
            return 0;
        }

        return index.get(term).size();
    }

    /*
     * posting list
     */
    public Map<Integer, Integer> getPostingList(String term) {
        return index.getOrDefault(term,new HashMap<>());
    }

    public int getDocumentLength(int docId) {
        return documentLengths.getOrDefault(docId, 0);
    }

    public double getAverageDocumentLength() {
        return averageDocumentLength;
    }

    public int getNumberOfDocuments() {
        return documents.size();
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public Map<String, Map<Integer, Integer>> getIndex() {
        return index;
    }
}