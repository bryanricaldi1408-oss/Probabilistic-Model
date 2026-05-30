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

    // public List<SearchResult> search(String query){
    //     List<String> queryTokens = preprocessQuery(query);
    //     Map<String, Integer> termFrequency = new HashMap<>();


    //     for(String term : queryTokens){

    //         double wt = 0.0;
            
    //         Map<Integer, Integer> retrievePostings = index.getPostingList(term);
    //         if(!retrievePostings.isEmpty()){

    //             for(Integer key : retrievePostings.keySet()){
    //                 termFrequency.putIfAbsent(term, 0);
    //                 termFrequency.put(term, termFrequency.get(term) + retrievePostings.get(key));
    //             }
    //         }
    //     }
    // }

    private List<String> preprocessQuery(String query) {

        List<String> tokens =stopwordRemover.removeStopwords(tokenizer.tokenize(query));

        List<String> processed =new ArrayList<>();

        for (String token : tokens) {
            processed.add(stemmer.stem(token));
        }

        return processed;
    }
}