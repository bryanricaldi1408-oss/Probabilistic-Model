package retrieval;

import indexing.Document;
import indexing.InvertedIndex;
import preprocessing.PorterStemmer;
import preprocessing.StopwordRemover;
import preprocessing.Tokenizer;

import java.util.*;

public class BIMModel {

    private final InvertedIndex index;

    private final Tokenizer tokenizer;
    private final StopwordRemover stopwordRemover;
    private final PorterStemmer stemmer;

    public BIMModel(InvertedIndex index) {

        this.index = index;

        tokenizer = new Tokenizer();

        stopwordRemover = new StopwordRemover();

        stemmer = new PorterStemmer();
    }

    public List<SearchResult> search(String query, Set<Integer> pseudoRelevantDocs) {

        List<String> queryTerms = preprocess(query);

        Map<Integer, Double> scores = new HashMap<>();

        int N = index.getDocuments().size();

        int R = pseudoRelevantDocs.size();

        for (String term : queryTerms) {
            Map<Integer, Integer> postings = index.getPostingList(term);
            if (postings == null) {
                continue;
            }

            int Nt = postings.size();

            int rt = 0;

            for (Integer docId : pseudoRelevantDocs) {
                if (postings.containsKey(docId)) {
                    rt++;
                }
            }

            double numerator = (rt + 0.5) * (N - R - Nt + rt + 0.5);

            double denominator = (R - rt + 0.5) * (Nt - rt + 0.5);

            double wt = Math.log(numerator / denominator);

            for (Integer docId : postings.keySet()) {
                scores.put(docId, scores.getOrDefault(docId, 0.0) + wt);
            }
        }

        return rank(scores);
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

    public Set<Integer> getPseudoRelevantDocs(List<SearchResult> results, int k) {

        Set<Integer> docs = new HashSet<>();

        for (int i = 0; i < Math.min(k, results.size()); i++) {
            docs.add(results.get(i).getDocId());
        }
        return docs;
    }

    private List<SearchResult> rank(Map<Integer, Double> scores) {

        List<SearchResult> results = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : scores.entrySet()) {
            results.add(new SearchResult(entry.getKey(), entry.getValue()));
        }

        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return results;
    }

    public List<SearchResult> searchInitial(String query) {

        List<String> queryTerms = preprocess(query);
        Map<Integer, Double> scores = new HashMap<>();
        int N = index.getNumberOfDocuments();

        for (String term : queryTerms) {

            Map<Integer, Integer> postings = index.getPostingList(term);

            if (postings.isEmpty()) {
                continue;
            }

            int Nt = postings.size();

            double wt = Math.log((N - Nt + 0.5)/(Nt + 0.5));
            for (Integer docId : postings.keySet()) {
                scores.put(docId,scores.getOrDefault(docId,0.0) + wt);
            }
        }

        return rank(scores);
    }
}
