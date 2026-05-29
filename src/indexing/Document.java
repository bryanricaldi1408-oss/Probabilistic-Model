package indexing;

import java.util.List;

public class Document {

    private int docId;
    private String content;
    private List<String> tokens;
    private int documentLength;

    public Document(int docId, String content, List<String> tokens) {
        this.docId = docId;
        this.content = content;
        this.tokens = tokens;
        this.documentLength = tokens.size();
    }

    public int getDocId() {
        return docId;
    }

    public String getContent() {
        return content;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public int getDocumentLength() {
        return documentLength;
    }

    @Override
    public String toString() {
        return "Document{" +
                "docId=" + docId +
                ", documentLength=" + documentLength +
                '}';
    }
}