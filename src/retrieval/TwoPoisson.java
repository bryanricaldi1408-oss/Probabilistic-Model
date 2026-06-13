package retrieval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import indexing.InvertedIndex;
import preprocessing.PorterStemmer;
import preprocessing.StopwordRemover;
import preprocessing.Tokenizer;

public class TwoPoisson {
    
    private final InvertedIndex index;
    private final Map<Integer, Set<Integer>> relevanceMap;
    private final Tokenizer tokenizer;
    private final StopwordRemover stopwordRemover;
    private final PorterStemmer stemmer;
    
    public TwoPoisson(InvertedIndex index, Map<Integer, Set<Integer>> relevanceMap){
        this.index = index;
        this.relevanceMap = relevanceMap;
        this.tokenizer = new Tokenizer();
        this.stopwordRemover = new StopwordRemover();
        this.stemmer = new PorterStemmer();
    }
    
    public List<SearchResult> search(int queryId, String query){
        List<String> queryTokens = preprocessQuery(query);
        Map<Integer, Double> documentScore = new HashMap<>();
        
        int N = index.getNumberOfDocuments();
        Set<Integer> relevantDocs = relevanceMap.getOrDefault(queryId, new HashSet<>());
        int R = relevantDocs.size();
        
        for(String term : queryTokens){
            Map<Integer, Integer> retrievePostings = index.getPostingList(term);
            int Nt = index.getDF(term);
            int rt = calculateRt(term, relevantDocs);
            
            double k = 1.5;
            
            // Formula wt based on RSJ weight
            double wt;
            if (R > 0) {
                double num = (rt + 0.5) / (R - rt + 0.5);
                double den = (Nt - rt + 0.5) / (N - Nt - R + rt + 0.5);
                wt = Math.log10(num / den);
                if (wt < 0) wt = 0; // Avoid negative weights
            } else {
                // Skenario 1: Tanpa relevance judgment (IDF-like)
                wt = Math.log10((double)(N - Nt + 0.5) / (Nt + 0.5));
                if (wt < 0) wt = 0;
            }

            if(retrievePostings != null && !retrievePostings.isEmpty()){
                for (Map.Entry<Integer, Integer> entry : retrievePostings.entrySet()) {
                    int docId = entry.getKey();
                    double ftd = entry.getValue(); 
                    
                    double scoreTerm = (ftd * (k + 1) * wt) / (ftd + k);
                    documentScore.put(docId, documentScore.getOrDefault(docId, 0.0) + scoreTerm);
                }
            }
        }
        List<SearchResult> results = new ArrayList<>();
        
        for (Map.Entry<Integer, Double> entry : documentScore.entrySet()) {
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
        
        List<String> tokens =stopwordRemover.removeStopwords(tokenizer.tokenize(query));
        
        List<String> processed =new ArrayList<>();
        
        for (String token : tokens) {
            processed.add(stemmer.stem(token));
        }
        
        return processed;
    }
}