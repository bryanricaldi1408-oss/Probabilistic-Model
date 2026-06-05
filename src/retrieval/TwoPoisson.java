package retrieval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indexing.InvertedIndex;
import preprocessing.PorterStemmer;
import preprocessing.StopwordRemover;
import preprocessing.Tokenizer;

public class TwoPoisson {
    
    private final InvertedIndex index;
    private final Tokenizer tokenizer;
    private final StopwordRemover stopwordRemover;
    private final PorterStemmer stemmer;
    
    public TwoPoisson(InvertedIndex index){
        this.index = index;
        this.tokenizer = new Tokenizer();
        this.stopwordRemover = new StopwordRemover();
        this.stemmer = new PorterStemmer();
    }
    
    public List<SearchResult> search(String query){
        List<String> queryTokens = preprocessQuery(query);
        Map<Integer, Double> documentScore = new HashMap<>();
        
        int N = index.getNumberOfDocuments();
        
        for(String term : queryTokens){
            Map<Integer, Integer> retrievePostings = index.getPostingList(term);
            // Jumlah term query muncul di semua dokumen
            int Nt = 0;
            double k = 1.5;
            if(retrievePostings != null && !retrievePostings.isEmpty()){
                Nt = retrievePostings.size();
                double wt = Math.log((0.5 *(N +1))/(Nt+0.5));
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
        
        // Mengurutkan hasil dari skor tertinggi ke terendah
        java.util.Collections.sort(results);
        
        return results;
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