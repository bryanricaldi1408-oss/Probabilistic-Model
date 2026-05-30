package retrieval;

import indexing.Document;
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

    public List<SearchResult> search(int queryId, String query) {

        List<String> queryTerms = preprocessQuery(query);

        List<SearchResult> results = new ArrayList<>();

        int N = index.getNumberOfDocuments();

        Set<Integer> relevantDocs = relevanceMap.getOrDefault(queryId, new HashSet<>());

        int R = relevantDocs.size();

        for (Document document : index.getDocuments()) {

            double rsv = 0.0;

            int docId = document.getDocId();

            for (String term : queryTerms) {

                int tf = index.getTF(term, docId);

                // BIM binary:
                // term ada / tidak
                if (tf > 0) {

                    int Nt = index.getDF(term);

                    int rt = calculateRt(term, relevantDocs);

                    double numerator = (rt + 0.5) * (N - R - Nt + rt + 0.5);
                    double denominator = (R - rt + 0.5) * (Nt - rt + 0.5);
                    double wt = Math.log(numerator / denominator);

                    rsv += wt;
                }
            }

            if (rsv > 0) {

                results.add(new SearchResult(docId, rsv));
            }
        }

        Collections.sort(results);

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
