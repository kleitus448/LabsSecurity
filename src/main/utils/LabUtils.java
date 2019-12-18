package main.utils;

import java.io.*;

public class LabUtils {
    public static String readFile(String path) {
        String s;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader (
                    new FileReader(path)
            );
            System.out.println("Чтение файла");
            while ((s = reader.readLine()) != null) {
                stringBuilder.append(s).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void writeFile(String text, String path) {
        try {
            BufferedWriter writer = new BufferedWriter (
                    new FileWriter(path)
            );
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
