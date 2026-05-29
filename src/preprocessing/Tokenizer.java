package preprocessing;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public List<String> tokenize(String text) {

        // Ubah ke lowercase
        text = text.toLowerCase();

        // Hapus punctuation dan karakter selain huruf/spasi
        text = text.replaceAll("[^a-z\\s]", " ");

        // Split berdasarkan whitespace
        String[] splitWords = text.split("\\s+");

        List<String> tokens = new ArrayList<>();

        for (String word : splitWords) {

            // Hindari token kosong
            if (!word.trim().isEmpty()) {
                tokens.add(word.trim());
            }
        }

        return tokens;
    }
}