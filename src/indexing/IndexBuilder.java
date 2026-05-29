package indexing;

import java.util.List;

public class IndexBuilder {

    public InvertedIndex buildIndex(List<Document> documents) {

        InvertedIndex invertedIndex =new InvertedIndex();

        invertedIndex.build(documents);

        return invertedIndex;
    }
}