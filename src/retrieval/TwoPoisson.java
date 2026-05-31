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

            if(retrievePostings != null && !retrievePostings.isEmpty()){
                int cf = 0; // Collection Frequency
                int df = retrievePostings.size(); // banyak-nya dokumen yang mengandung term tersebut
                
                for(int tf : retrievePostings.values()){
                    // Menjumlahkan semua tf di semua dokumen
                    cf += tf;
                }

                // 1. Estimasi Parameter
                // Mehitung rata-rata TF global dari dokumen yang mengandung term ini
                // Menjadi threshold 
                double meanTf = (double) cf / df;
                int eliteCount = 0;
                int sumTfElite = 0;
                int sumTfNonElite = 0;

                for(int tf : retrievePostings.values()){
                    if(tf >= meanTf){
                        eliteCount++;
                        sumTfElite += tf;
                    }else{
                        sumTfNonElite += tf;
                    }
                }

                int nonEliteCount = N - eliteCount;
                double pi = (double) eliteCount/N;
                double lambda1 = (eliteCount > 0) ? (double) sumTfElite / eliteCount : 0.01;
                double lambda2 = (nonEliteCount > 0) ? (double) sumTfNonElite / nonEliteCount : 0.01;
                
                if (lambda1 <= 0) lambda1 = 0.01;
                if (lambda2 <= 0) lambda2 = 0.01;

                double idf = Math.log((N - df + 0.5) / (df + 0.5) + 1.0);

                for(Map.Entry<Integer, Integer> entry : retrievePostings.entrySet()){
                    int docId = entry.getKey();
                    int tf = entry.getValue();

                    double probElite = Math.exp(-lambda1) * Math.pow(lambda1, tf);
                    double probNonElite = Math.exp(-lambda2) * Math.pow(lambda2, tf);

                    double numerator = probElite * pi;
                    double denominator = numerator + (probNonElite * (1.0 - pi));
                    
                    double probIsElite = 0.0;
                    if (denominator > 0) {
                        probIsElite = numerator / denominator;
                    }

                    double termScore = probIsElite * idf;
                    documentScore.put(docId, documentScore.getOrDefault(docId, 0.0) + termScore);
                }
            }
        }

        List<SearchResult> results = new ArrayList<>();
        for(Map.Entry<Integer, Double> entry : documentScore.entrySet()){
            results.add(new SearchResult(entry.getKey(), entry.getValue()));
        }

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