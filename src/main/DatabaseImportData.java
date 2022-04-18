package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseImportData {
    public static List<String> readFile(String path){
        List<String> fileData = new ArrayList<>();
        try (FileReader fr = new FileReader(path);
             BufferedReader br = new BufferedReader(fr))
        {
            String line = br.readLine();
            while (line != null){
                fileData.add(line);
                line = br.readLine();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return fileData;
    }

    public static void writeBinary(List<String> fileData){
        DatabaseEngine.writeBinaryFile("src/main/pokemon.data", fileData);
    }

    /*public static void main(String[] args) {
        writeBinary(readFile("src/main/Pokemon.csv"));
    }*/
}
