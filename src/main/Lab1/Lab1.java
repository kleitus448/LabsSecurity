package main.Lab1;

import main.utils.LabUtils;
import java.util.*;
import java.util.stream.Collectors;

public class Lab1 {

    private static final String alphabet = ("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"+
                                            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя");

    private static String encryptText(String text, int k) {

        //Caesar cipher parameters
        int n = alphabet.length();
        StringBuilder encryptedText = new StringBuilder();

        //Encryption process
        for (int i = 0; i < text.length(); i++) {
            int x = alphabet.indexOf(text.charAt(i));
            if (x != -1) {
                int y = (x < n/2) ? (x+k) % (n/2) : (x-n/2+k) % (n/2) + n/2;
                encryptedText.append(alphabet.charAt(y));
            }
            else {
                encryptedText.append(text.charAt(i));
            }
        }
        return encryptedText.toString();
    }

    private static LinkedHashMap<String, Float> frequencyAnalysis(String text, int n) {
        HashMap<String, Float> freqMap = new HashMap<>();
        StringBuilder tempGram = new StringBuilder();
        int charCount = 0;
        for (int i = 0; i <= text.length()-n; i++) {
            if (alphabet.indexOf(text.charAt(i)) != -1)
                charCount++;
            for (int j = 0; j < n; j++) {
                char tempChar = text.charAt(i+j);
                if (alphabet.indexOf(tempChar) != -1)
                    tempGram.append(tempChar);
                else break;
            }
            String key = tempGram.toString();
            if (!key.isEmpty() && key.length() == n)
                if (freqMap.containsKey(key))
                    freqMap.put(key, freqMap.get(key) + 1.0f);
                else
                    freqMap.put(key, 1.0f);
            tempGram.delete(0,tempGram.length());
        }
        int finalCharCount = charCount;
        System.out.println("charCount " + charCount);
        freqMap.forEach((k, v) -> freqMap.put(k, v/finalCharCount));
        return freqMap.entrySet()
            .stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private static String replaceChars(String encryptedText, LinkedHashMap<String, String> newMatchHashMap) {
        StringBuilder encryptedTextBuilder = new StringBuilder(encryptedText);
        for (int i = 0; i < encryptedText.length(); i++) {
            String key = encryptedText.substring(i,i+1);
            if (newMatchHashMap.containsKey(key))
                encryptedTextBuilder.setCharAt(i, newMatchHashMap.get(key).charAt(0));
        }
        return encryptedTextBuilder.toString();
    }

    private static LinkedHashMap<String, String> matchOriginalEncrypted(String originalTextBook,
                                                                  String encryptedText,
                                                                  int gram_size) {
        LinkedHashMap<String, Float> encryptedFreqMap = frequencyAnalysis(encryptedText, gram_size);
        Iterator <Map.Entry<String, Float>> encryptedFreqIterator = encryptedFreqMap.entrySet().iterator();
        printHashMap(encryptedFreqMap, "encryptedFreqMap(N_GRAM = " + gram_size + ")");

        LinkedHashMap<String, Float> originalFreqMap = frequencyAnalysis(originalTextBook, gram_size);
        printHashMap(originalFreqMap, "originalFreqMap(N_GRAM = " + gram_size + ")");

        LinkedHashMap<String, String> matchHashMap = new LinkedHashMap<>();
        String temp_key = ""; float temp_difference = Integer.MAX_VALUE;
        while (encryptedFreqIterator.hasNext()) {
            String encryptedKey = encryptedFreqIterator.next().getKey();
            for (Map.Entry<String, Float> originalEntry : originalFreqMap.entrySet()) {
                String originalKey = originalEntry.getKey();
                float originalValue = originalEntry.getValue();
                float difference = Math.abs(encryptedFreqMap.get(encryptedKey) - originalValue);
                if (difference < temp_difference) {
                    temp_key = originalKey;
                    temp_difference = difference;
                }
            }
            matchHashMap.put(encryptedKey, temp_key);
            originalFreqMap.remove(temp_key);
            temp_difference = Integer.MAX_VALUE;
        }
        printHashMap(matchHashMap, "matchHashMap(N_GRAM = " + gram_size + ")");
        return matchHashMap;
    }

    private static void printHashMap(HashMap<String, ?> hashMap, String text) {
        System.out.println("\n"+text+":\n");
        hashMap.keySet().forEach
                (key -> System.out.println(key + " : " + hashMap.get(key)));
    }

    private static void decryptText(String originalTextBook, String originalTextChapter, String encryptedText, int top_n) {
        LinkedHashMap<String, String> matchHashMap = matchOriginalEncrypted(originalTextBook, encryptedText, 1);
        LinkedHashMap<String, String> matchBigramHashMap = matchOriginalEncrypted(originalTextBook, encryptedText, 2);
        LinkedHashMap<String, String> newMatchHashMap = new LinkedHashMap<>(matchHashMap);
        Iterator<Map.Entry<String,String>> matchBigramIterator = matchBigramHashMap.entrySet().iterator();
        for (int i = 0; i < top_n; i++) {
            Map.Entry<String, String> bigram = matchBigramIterator.next();
            String bigramKey = bigram.getKey(); String bigramValue = bigram.getValue();
            for (int j = 0; j < bigramKey.length(); j++) {
                String encryptedCharKey = bigramKey.substring(j, j+1);
                String originalCharValue = bigramValue.substring(j, j+1);
                if (!newMatchHashMap.get(encryptedCharKey).equals(originalCharValue)) {
                    newMatchHashMap.put(encryptedCharKey, originalCharValue);
                }
            }
        }
        printHashMap(newMatchHashMap, "matchHashMap after replacing");

        String decryptedText = replaceChars(encryptedText, matchHashMap);
        String decryptedTextGram = replaceChars(encryptedText, newMatchHashMap);
        int charCount = 0; int compCount = 0; int compGramCount = 0;
        for (int i = 0; i < decryptedText.length(); i++) {
            if (alphabet.indexOf(decryptedText.charAt(i)) != -1) {
                if (decryptedText.charAt(i) == originalTextChapter.charAt(i))
                    compCount++;
                if (decryptedTextGram.charAt(i) == originalTextChapter.charAt(i))
                    compGramCount++;
                charCount++;
            }
        }
        System.out.println(charCount);
        System.out.println(compCount);
        System.out.println(compGramCount);
        System.out.println("PERCENT FOR ONE CHAR: " + (float)compCount/charCount*100);
        System.out.println("PERCENT FOR GRAM CHAR: " + (float)compGramCount/charCount*100);
//        LabUtils.writeFile(replaceChars(encryptedText, matchHashMap), "src/main/Lab1/decryptedTextChapter.txt");
//        LabUtils.writeFile(replaceChars(encryptedText, newMatchHashMap), "src/main/Lab1/decryptedTextChapterBigram.txt");
    }

    public static void main(String[] args) {
        int shift = 4; int top_bigram = 5;
        String originalTextChapter = LabUtils.readFile("src/main/Lab1/WarAndPeaceChapter.txt");
        String originalTextBook = LabUtils.readFile("src/main/Lab1/WarAndPeaceBook.txt");
        String encryptedTextChapter = encryptText(originalTextChapter, shift);
        LabUtils.writeFile(encryptedTextChapter, "src/main/Lab1/encryptedTextChapter.txt");
        decryptText(originalTextBook, originalTextChapter, encryptedTextChapter, top_bigram);

    }
}


