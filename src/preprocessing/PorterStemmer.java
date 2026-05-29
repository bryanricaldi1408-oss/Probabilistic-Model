package preprocessing;

/**
 * Class ini merupakan implementasi dari Porter Stemmer
 * 
 * Semua aturan porter stemmer didapat dari:
 * https://vijinimallawaarachchi.com/2017/05/09/porter-stemming-algorithm/
 */
public class PorterStemmer {
    /**
     * Stem kata yang diberikan jika kata tersebut lebih kecil dari 2 maka tidak di
     * stem
     *
     * @param word the word to be stemmed
     * @return the stemmed version of the word
     */
    public String stem(String word) {
        // kata yang lebih kecil dari 2, maka tidak di stem
        if (word.length() <= 2)
            return word.toLowerCase();
        word = word.toLowerCase();

        word = step1a(word);
        word = step1b(word);
        word = step1c(word);
        word = step2(word);
        word = step3(word);
        word = step4(word);
        word = step5(word);

        return word;
    }

    public String step1a(String word) {
        if (word.endsWith("sses"))
            return word.substring(0, word.length() - 2);
        if (word.endsWith("ies"))
            return word.substring(0, word.length() - 2);
        if (word.endsWith("s") && !word.endsWith("ss"))
            return word.substring(0, word.length() - 1);
        return word;
    }

    public String step1b(String word) {
        boolean secondPreq = false;
        boolean thirdPreq = false;
        if (word.endsWith("eed")) {
            if (measure(word.substring(0, word.length() - 3)) > 0) {
                word = word.substring(0, word.length() - 1);
            }
        } else if (word.endsWith("ed") && containVowel(word.substring(0, word.length() - 2))) {
            secondPreq = true;
            word = word.substring(0, word.length() - 2);
        } else if (word.endsWith("ing") && containVowel(word.substring(0, word.length() - 3))) {
            thirdPreq = true;
            word = word.substring(0, word.length() - 3);
        }

        // Jika syarat kedua atau syarat ketiga terpenuhi maka
        if(secondPreq || thirdPreq){
            if (word.endsWith("at") || word.endsWith("bl") || word.endsWith("iz")) {
                word = word + "e";
            } else if (doubleConsonant(word) && !(word.endsWith("l") || word.endsWith("s") || word.endsWith("z"))) {
                word = word.substring(0, word.length() - 1);
            } else if (measure(word) == 1 && endsWithCVC(word)) {
                word = word + "e";
            }
        }
        return word;
    }

    public String step1c(String word) {
        int len = word.length() - 1;
        if (len < 2)
            return word;
        if (word.charAt(len) == 'y' && isVowel(word.charAt(len - 1))) {
            word = word.substring(0, word.length() - 1) + "i";
        }
        return word;
    }

    public String step2(String word) {
        if (measure(word) > 0) {
            if (word.endsWith("ational"))
                return word.substring(0, word.length() - 7) + "ate";
            if (word.endsWith("tional"))
                return word.substring(0, word.length() - 6) + "tion";
            if (word.endsWith("enci"))
                return word.substring(0, word.length() - 4) + "ence";
            if (word.endsWith("anci"))
                return word.substring(0, word.length() - 4) + "ance";
            if (word.endsWith("izer"))
                return word.substring(0, word.length() - 4) + "ize";
            if (word.endsWith("abli"))
                return word.substring(0, word.length() - 4) + "able";
            if (word.endsWith("alli"))
                return word.substring(0, word.length() - 4) + "al";
            if (word.endsWith("entli"))
                return word.substring(0, word.length() - 5) + "ent";
            if (word.endsWith("eli"))
                return word.substring(0, word.length() - 3) + "e";
            if (word.endsWith("ousli"))
                return word.substring(0, word.length() - 5) + "ous";
            if (word.endsWith("ization"))
                return word.substring(0, word.length() - 7) + "ize";
            if (word.endsWith("ation"))
                return word.substring(0, word.length() - 5) + "ate";
            if (word.endsWith("ator"))
                return word.substring(0, word.length() - 4) + "ate";
            if (word.endsWith("alism"))
                return word.substring(0, word.length() - 5) + "al";
            if (word.endsWith("iveness"))
                return word.substring(0, word.length() - 7) + "ive";
            if (word.endsWith("fulness"))
                return word.substring(0, word.length() - 7) + "ful";
            if (word.endsWith("ousness"))
                return word.substring(0, word.length() - 7) + "ous";
            if (word.endsWith("aliti"))
                return word.substring(0, word.length() - 5) + "al";
            if (word.endsWith("iviti"))
                return word.substring(0, word.length() - 5) + "ive";
            if (word.endsWith("biliti"))
                return word.substring(0, word.length() - 6) + "ble";
        }
        return word;
    }

    public String step3(String word) {
        if (measure(word) > 0) {
            if (word.endsWith("icate"))
                return word.substring(0, word.length() - 5) + "ic";
            if (word.endsWith("ative"))
                return word.substring(0, word.length() - 5);
            if (word.endsWith("alize"))
                return word.substring(0, word.length() - 5) + "al";
            if (word.endsWith("iciti"))
                return word.substring(0, word.length() - 5) + "ic";
            if (word.endsWith("ical"))
                return word.substring(0, word.length() - 4) + "ic";
            if (word.endsWith("ful"))
                return word.substring(0, word.length() - 3);
            if (word.endsWith("ness"))
                return word.substring(0, word.length() - 4);
        }
        return word;
    }

    public String step4(String word) {
        if (measure(word) > 1) {
            if (word.endsWith("al"))
                return word.substring(0, word.length() - 2);
            if (word.endsWith("ance"))
                return word.substring(0, word.length() - 4);
            if (word.endsWith("ence"))
                return word.substring(0, word.length() - 4);
            if (word.endsWith("er"))
                return word.substring(0, word.length() - 2);
            if (word.endsWith("ic"))
                return word.substring(0, word.length() - 2);
            if (word.endsWith("able"))
                return word.substring(0, word.length() - 4);
            if (word.endsWith("ible"))
                return word.substring(0, word.length() - 4);
            if (word.endsWith("ant"))
                return word.substring(0, word.length() - 3);
            if (word.endsWith("ement"))
                return word.substring(0, word.length() - 5);
            if (word.endsWith("ment"))
                return word.substring(0, word.length() - 4);
            if (word.endsWith("ent"))
                return word.substring(0, word.length() - 3);
            if (word.endsWith("ou"))
                return word.substring(0, word.length() - 2);
        }
        return word;
    }

    public String step5(String word) {
        if (word.endsWith("e")) {
            if (measure(word.substring(0, word.length() - 1)) > 1) {
                return word.substring(0, word.length() - 1);
            }
            if (measure(word.substring(0, word.length() - 1)) == 1
                    && !endsWithCVC(word.substring(0, word.length() - 1))) {
                return word.substring(0, word.length() - 1);
            }
        }
        if (measure(word) > 1 && doubleConsonant(word) && word.endsWith("l")) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }

    public boolean endsWithCVC(String word) {
        if (word.length() < 3) {
            return false; // Minimal 3 huruf
        }

        int len = word.length();
        char c1 = word.charAt(len - 3); // Konsonan
        char c2 = word.charAt(len - 2); // Vokal
        char c3 = word.charAt(len - 1); // Konsonan terakhir

        if (!isVowel(c1) && isVowel(c2) && !isVowel(c3)) {
            if (c3 != 'w' && c3 != 'x' && c3 != 'y') {
                return true;
            }
        }

        return false;
    }

    public boolean doubleConsonant(String word) {
        if (word.length() < 2)
            return false;
        int len = word.length() - 1;
        return word.charAt(len) == word.charAt(len - 1);
    }

    public boolean containVowel(String word) {
        boolean containVowel = false;
        for (char c : word.toCharArray()) {
            if (isVowel(c)) {
                containVowel = true;
                break;
            }
        }
        return containVowel;
    }

    public boolean isVowel(char ch) {
        return ch == 'a' || ch == 'i' || ch == 'u' || ch == 'e' || ch == 'o';
    }

    public int measure(String word) {
        int m = 0;

        char[] cr = word.toCharArray();
        boolean prevIsVowel = false;

        for (int i = 0; i < cr.length; i++) {
            boolean currIsVowel;

            if (cr[i] == 'y') {
                currIsVowel = (i == 0) ? false : !isVowel(cr[i - 1]);
            } else {
                currIsVowel = isVowel(cr[i]);
            }

            if (prevIsVowel && !currIsVowel) {
                m++;
            }
            prevIsVowel = currIsVowel;
        }

        return m;

    }
}