package retrieval;

import indexing.InvertedIndex;
import preprocessing.PorterStemmer;
import preprocessing.StopwordRemover;
import preprocessing.Tokenizer;

import java.util.*;

public class BIMModel {

    private final InvertedIndex index;
    private final Map<Integer, Set<Integer>> relevanceMap;

    private final Tokenizer tokenizer;
    private final StopwordRemover stopwordRemover;
    private final PorterStemmer stemmer;

    public BIMModel(InvertedIndex index, Map<Integer, Set<Integer>> relevanceMap) {
        this.index = index;
        this.relevanceMap = relevanceMap;
        tokenizer = new Tokenizer();
        stopwordRemover = new StopwordRemover();
        stemmer = new PorterStemmer();
    }

    public List<SearchResult> search(String query,Set<Integer> relevantDocs) {

        List<String> queryTerms = preprocess(query);
        Map<Integer, Double> scores = new HashMap<>();

        int N = index.getNumberOfDocuments();
        int R = relevantDocs.size();

        for (String term : queryTerms) {
            Map<Integer, Integer> postings = index.getPostingList(term);

            if (postings.isEmpty()) {
                continue;
            }

            int Nt = postings.size();

            int rt = 0;

            for (Integer docId : relevantDocs) {

                if (postings.containsKey(docId)) {
                    rt++;
                }
            }

            double numerator = (rt + 0.5)*(N - R - Nt + rt + 0.5);

            double denominator = (R - rt + 0.5)*(Nt - rt + 0.5);

            if (denominator <= 0) {
                continue;
            }

            double wt = Math.log(numerator /denominator);

            for (Integer docId : postings.keySet()) {
                scores.put(docId,scores.getOrDefault(docId,0.0) + wt);
            }
        }

        return rank(scores);
    }

    public double calculatePrecision(List<SearchResult> results, int queryId, int k) {

        Set<Integer> relevantDocs = relevanceMap.getOrDefault(queryId, new HashSet<>());

        int retrievedRelevant = 0;

        int limit = Math.min(k, results.size());

        for (int i = 0; i < limit; i++) {
            int docId = results.get(i).getDocId();
            if (relevantDocs.contains(docId)) {
                retrievedRelevant++;
            }
        }

        return limit == 0 ? 0 : (double) retrievedRelevant / limit;
    }

    public double calculateRecall(List<SearchResult> results, int queryId, int k) {

        Set<Integer> relevantDocs = relevanceMap.getOrDefault(queryId, new HashSet<>());

        if (relevantDocs.isEmpty()) {
            return 0;
        }

        int retrievedRelevant = 0;

        int limit = Math.min(k, results.size());

        for (int i = 0; i < limit; i++) {
            int docId = results.get(i).getDocId();

            if (relevantDocs.contains(docId)) {
                retrievedRelevant++;
            }
        }

        return (double) retrievedRelevant / relevantDocs.size();
    }

    private List<SearchResult> rank(Map<Integer, Double> scores) {
        List<SearchResult> results = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : scores.entrySet()) {
            results.add(new SearchResult(entry.getKey(), entry.getValue()));
        }
        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return results;
    }

    private List<String> preprocess(String text) {

        List<String> tokens = tokenizer.tokenize(text);
        tokens = stopwordRemover.removeStopwords(tokens);

        List<String> result = new ArrayList<>();

        for (String token : tokens) {
            result.add(stemmer.stem(token));
        }

        return result;
    }
}
