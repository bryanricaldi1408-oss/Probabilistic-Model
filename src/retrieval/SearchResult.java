package retrieval;

public class SearchResult implements Comparable<SearchResult> {

    private int docId;
    private double score;

    public SearchResult(int docId,double score) {
        this.docId = docId;
        this.score = score;
    }

    public int getDocId() {
        return docId;
    }

    public double getScore() {
        return score;
    }

    @Override
    public int compareTo(SearchResult other) {
        return Double.compare(other.score,this.score);
    }

    @Override
    public String toString() {
        return "Document "+ docId+ " | Score = "+ String.format("%.4f",score);
    }
}