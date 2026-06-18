package retrieval;

import indexing.InvertedIndex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import preprocessing.PorterStemmer;
import preprocessing.StopwordRemover;
import preprocessing.Tokenizer;

public class BM25 {

    private final InvertedIndex index;
    private final Map<Integer, Set<Integer>> relevanceMap;
    private final Tokenizer tokenizer;
    private final StopwordRemover stopwordRemover;
    private final PorterStemmer stemmer;

    // Configurable BM25 parameters
    private final double k1;
    private final double b;

    public BM25(InvertedIndex index, Map<Integer, Set<Integer>> relevanceMap, double k1, double b) {
        this.index = index;
        this.relevanceMap = relevanceMap;
        this.tokenizer = new Tokenizer();
        this.stopwordRemover = new StopwordRemover();
        this.stemmer = new PorterStemmer();
        this.k1 = k1;
        this.b = b;
    }

    public List<SearchResult> search(int queryId, String query) {
        List<String> queryTokens = preprocessQuery(query);
        Map<Integer, Double> documentScores = new HashMap<>();

        int N = index.getNumberOfDocuments();
        double avgdl = index.getAverageDocumentLength();
        Set<Integer> relevantDocs = relevanceMap.getOrDefault(queryId, new HashSet<>());
        int R = relevantDocs.size();

        for (String term : queryTokens) {
            Map<Integer, Integer> postings = index.getPostingList(term);
            int Nt = index.getDF(term);
            int rt = calculateRt(term, relevantDocs);

            // RSJ Weight (IDF component in BM25)
            // Using log10 for consistency with other classes in this project
            double num = (rt + 0.5) / (R - rt + 0.5);
            double den = (Nt - rt + 0.5) / (N - Nt - R + rt + 0.5);
            double wt = Math.log10(num / den);
            if (wt < 0) wt = 0;

            if (postings != null && !postings.isEmpty()) {
                for (Map.Entry<Integer, Integer> entry : postings.entrySet()) {
                    int docId = entry.getKey();
                    int ftd = entry.getValue();
                    int dl = index.getDocumentLength(docId);

                    // BM25 TF component with document length normalization
                    double K = k1 * ((1 - b) + b * (dl / avgdl));
                    double tfComponent = (ftd * (k1 + 1)) / (ftd + K);

                    double score = wt * tfComponent;
                    documentScores.put(docId, documentScores.getOrDefault(docId, 0.0) + score);
                }
            }
        }

        List<SearchResult> results = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : documentScores.entrySet()) {
            results.add(new SearchResult(entry.getKey(), entry.getValue()));
        }

        java.util.Collections.sort(results);
        return results;
    }

    private int calculateRt(String term, Set<Integer> relevantDocs) {
        int rt = 0;
        for (Integer docId : relevantDocs) {
            if (index.getTF(term, docId) > 0) {
                rt++;
            }
        }
        return rt;
    }

    private List<String> preprocessQuery(String query) {
        List<String> tokens = stopwordRemover.removeStopwords(tokenizer.tokenize(query));
        List<String> processed = new ArrayList<>();
        for (String token : tokens) {
            processed.add(stemmer.stem(token));
        }
        return processed;
    }
}
