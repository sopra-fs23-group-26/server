package ch.uzh.ifi.hase.soprafs23.constant;

public class WordSet {

    private String undercoverWord;
    private String detectiveWord;

    public WordSet(String undercoverWord, String detectiveWord) {
        this.undercoverWord = undercoverWord;
        this.detectiveWord = detectiveWord;
    }

    /*randomly get a word set, but it's incomplete for now*/
    public static WordSet generate() {
        return new WordSet("pig", "duck");
    }

    public String getUndercoverWord() {
        return undercoverWord;
    }

    public void setUndercoverWord(String undercoverWord) {
        this.undercoverWord = undercoverWord;
    }

    public String getDetectiveWord() {
        return detectiveWord;
    }

    public void setDetectiveWord(String detectiveWord) {
        this.detectiveWord = detectiveWord;
    }
}
