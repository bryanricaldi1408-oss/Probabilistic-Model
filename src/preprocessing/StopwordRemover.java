package preprocessing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StopwordRemover {

    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
            "a", "an", "and", "are", "as", "at",
            "be", "by", "for", "from",
            "has", "he", "in", "is", "it",
            "its", "of", "on", "that", "the",
            "to", "was", "were", "will", "with",
            "this", "these", "those", "or",
            "but", "about", "into", "through",
            "during", "before", "after", "above",
            "below", "up", "down", "out", "off",
            "over", "under", "again", "further",
            "then", "once", "here", "there",
            "when", "where", "why", "how",
            "all", "any", "both", "each",
            "few", "more", "most", "other",
            "some", "such", "no", "nor",
            "not", "only", "own", "same",
            "so", "than", "too", "very",
            "can", "just", "should", "now"
    ));

    public List<String> removeStopwords(List<String> tokens) {

        return tokens.stream()
                .filter(word -> !STOPWORDS.contains(word))
                .collect(Collectors.toList());
    }
}