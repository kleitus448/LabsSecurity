package main.Lab1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

public class Test {

    public static void main(String[] args) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("1", "Sosi");
        hashMap.put("2", "Pussylol");
        hashMap.put("3", "Sad Gusi");
        hashMap.put("4", "SAKA MAZAFAKA");

//        System.out.println(iterator.next() +"\n");
//        hashMap
//        iterator.forEachRemaining(System.out::println);
    }

    private static void getCombinations(int maxLength, char[] alphabet,
                                        StringBuilder curr, ArrayList<String> combList) {
        if(curr.toString().length() == maxLength) {
            combList.add(curr.toString());
        } else {
            String oldCurr;
            for (char c : alphabet) {
                oldCurr = curr.toString();
                curr.append(c);
                getCombinations(maxLength, alphabet, curr, combList);
                curr.replace(0, curr.toString().length(), oldCurr);
            }
        }
    }
}
