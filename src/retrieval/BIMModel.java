package retrieval;

import indexing.Document;
import indexing.InvertedIndex;
import preprocessing.PorterStemmer;
import preprocessing.StopwordRemover;
import preprocessing.Tokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BIMModel {

    private final InvertedIndex index;
    private final Tokenizer tokenizer;
    private final StopwordRemover stopwordRemover;
    private final PorterStemmer stemmer;

    public BIMModel(InvertedIndex index) {
        this.index = index;
        this.tokenizer = new Tokenizer();
        this.stopwordRemover =new StopwordRemover();
        this.stemmer =new PorterStemmer();
    }

    public List<SearchResult> search(String query) {

        List<String> queryTokens =preprocessQuery(query);

        List<SearchResult> results = new ArrayList<>();

        int N = index.getNumberOfDocuments();

        for (Document document :index.getDocuments()) {

            double rsv = 0.0;

            for (String term : queryTokens) {

                int tf =index.getTF(term,document.getDocId());

                // BIM bersifat binary:
                // term ada / tidak ada
                if (tf > 0) {
                    int Nt =index.getDF(term);
                    if (Nt > 0) {
                        double wt = Math.log((0.5 * N) / Nt );
                        rsv += wt;
                    }
                }
            }

            if (rsv > 0) {
                results.add(new SearchResult(document.getDocId(),rsv));
            }
        }

        Collections.sort(results);

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