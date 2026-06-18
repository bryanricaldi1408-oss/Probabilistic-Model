package retrieval;

import java.util.Map;
import java.util.Set;

import indexing.InvertedIndex;

//tidak menemukan definisi BM10, membuat BM11
public class BM10 extends BM25{
    public BM10(InvertedIndex index, Map<Integer, Set<Integer>> relevanceMap, double k1) {
        super(index,relevanceMap, k1,1);
    }
}
