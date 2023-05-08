package ch.uzh.ifi.hase.soprafs23.constant;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class WordSet {

    private String undercoverWord;
    private String detectiveWord;

    private static final String[] OPTIONS = {"pets", "subjects", "zoo", "seafood"};

    public static String generateUrl() {
        Random random = new Random();
        int index = random.nextInt(OPTIONS.length);
        String option = OPTIONS[index];
        return "https://api.datamuse.com/words?rel_trg=" + option + "&md=p";
    }

    public WordSet(String undercoverWord, String detectiveWord) {
        this.undercoverWord = undercoverWord;
        this.detectiveWord = detectiveWord;
    }

    /*randomly get a word set, but it's incomplete for now*/
    public static WordSet generate() {
        List<String> words = generateOnce();
        while(words.size()<2){
            words = generateOnce();
        }
        return new WordSet(words.get(0), words.get(1));
    }

    public static List<String> generateOnce(){
        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Make a GET request to the API
        String url = generateUrl();
        List<Object> response = restTemplate.getForObject(url, List.class);

        // Extract two random words from the response and store them in a list of strings
        List<String> words = new ArrayList<>();
        int responseSize = response.size();
        Random random = new Random();
        for (int i = 0; i < responseSize; i++) {
            int k = random.nextInt(responseSize);
            List<String> tags = (List<String>) ((LinkedHashMap) response.get(k)).get("tags");
            if(containsOnlyLowercaseN(tags)){
                System.out.println(tags);
                String word = (String) ((LinkedHashMap) response.get(k)).get("word");
                words.add(word);
            }
        }
        return words;
    }

    public static boolean containsOnlyLowercaseN(List<String> strings) {
        if (strings.size() != 1) {
            return false; // list does not contain exactly one string
        }
        String str = strings.get(0);
        if (str.length() != 1) {
            return false; // string is not a single character
        }
        char c = str.charAt(0);
        return (c == 'n');
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
